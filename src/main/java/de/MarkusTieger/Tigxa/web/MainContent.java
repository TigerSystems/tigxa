package de.MarkusTieger.Tigxa.web;

import de.MarkusTieger.Tigxa.gui.image.ImageLoader;
import de.MarkusTieger.Tigxa.gui.window.BrowserWindow;
import de.MarkusTieger.Tigxa.http.HttpUtils;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.embed.swing.JFXPanel;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.web.*;
import javafx.util.Callback;
import netscape.javascript.JSObject;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.InputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

public class MainContent {

    public record MainContentData(JFXPanel jfx, WebView webView, WebEngine webEngine,
                                  Scene scene, WebHistory history, Runnable screenshot) {
    }

    private static final String CLICK_CODE = "" +
            "function get_click(tigxa_x, tigxa_y) {" +
            "  var doc = document.elementFromPoint(tigxa_x, tigxa_y);" +
            "  var pos = 0;" +
            "  while (doc != null && pos != 10) {" +
            "    if (doc.tagName === \"A\" || doc.tagName === \"a\") { return doc.href; }" +
            "    doc = doc.parentElement;" +
            "    pos++;" +
            "  }" +
            "  return null;" +
            "}";

    public static void loadFavicon(String location, Consumer<ImageIcon> c) {

        try {
            URI l = new URI(location);
            if (!l.getScheme().equalsIgnoreCase("https") && !l.getScheme().equalsIgnoreCase("http")) {
                return;
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        String faviconUrl = String.format("http://www.google.com/s2/favicons?domain_url=%s", URLEncoder.encode(location, StandardCharsets.UTF_8));
        try {
            URL url = new URL(faviconUrl);

            new Thread(() -> {

                c.accept(ImageLoader.loadHTTPImageAsIcon(url));

            }, "Favicon-Downloader").start();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public static MainContentData createContent(BrowserWindow window, Consumer<String> title, Consumer<ImageIcon> icon,
                                                Consumer<String> newTabURL, Consumer<String> newWindowURL, Consumer<String> urlChange,
                                                Runnable screenshot, Runnable devtools) {
        // Create the WebView
        WebView webView = new WebView();

        final WebEngine webEngine = webView.getEngine();

        webEngine.setUserAgent(HttpUtils.AGENT);

        webEngine.setConfirmHandler(new Callback<String, Boolean>() {
            @Override
            public Boolean call(String param) {
                int data = JOptionPane.showConfirmDialog(null, param, "Confirm", JOptionPane.YES_NO_OPTION);
                return Boolean.valueOf(data == JOptionPane.YES_OPTION);
            }
        });

        webEngine.setOnAlert(new EventHandler<WebEvent<String>>() {
            @Override
            public void handle(WebEvent<String> event) {
                JOptionPane.showMessageDialog(null, event.getData(), "Alert", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        webEngine.locationProperty().addListener(new ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

                urlChange.accept(newValue);
            }
        });

        webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
            public void changed(ObservableValue<? extends Worker.State> ov, Worker.State oldState, Worker.State newState) {
                if (newState == Worker.State.SUCCEEDED) {

                    try {
                        URI loc = new URI(webEngine.getLocation());
                        if (loc.getScheme().equalsIgnoreCase("file")) {
                            String[] path = loc.normalize().getPath().split("/");
                            if (path.length > 0) {
                                title.accept(path[path.length - 1]);
                            }
                        } else {
                            title.accept(webEngine.getTitle());
                            loadFavicon(webEngine.getLocation(), icon);
                        }
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        webEngine.setCreatePopupHandler(new Callback<PopupFeatures, WebEngine>() {
            @Override
            public WebEngine call(PopupFeatures param) {
                return window.newTab(true).webEngine();
            }
        });

        final JPopupMenu m = new JPopupMenu();

        addDefaults(m, screenshot, webEngine, devtools);

        webView.setContextMenuEnabled(false);

        JFXPanel jfx = new JFXPanel();

        webView.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
            @Override
            public void handle(ContextMenuEvent event) {

                JSObject obj = (JSObject) webEngine.executeScript("window");
                obj.eval(CLICK_CODE);

                Object result = obj.call("get_click", (int) event.getSceneX(), (int) event.getSceneY());

                if (result == null || (result + "").isBlank()) {
                    m.show(jfx, (int) event.getX(), (int) event.getY());
                } else {

                    JMenuItem new_tab = new JMenuItem("Open in new Tab");
                    new_tab.addActionListener((e) -> {
                        Platform.runLater(() -> {
                            window.newTab((result + ""), true);
                        });
                    });

                    JMenuItem new_window = new JMenuItem("Open in new Window");
                    new_window.addActionListener((e) -> {
                        Platform.runLater(() -> {
                            BrowserWindow w = new BrowserWindow();
                            w.initWindow(window.getMapi(), window.getConfigRoot());
                            w.newTab((result + ""), true);
                        });
                    });

                    JMenuItem copy = new JMenuItem("Copy to Clipboard");
                    copy.addActionListener((e) -> {
                        Toolkit toolkit = Toolkit.getDefaultToolkit();
                        toolkit.getSystemClipboard().setContents(new StringSelection(result + ""), null);
                    });

                    JPopupMenu context = new JPopupMenu();

                    context.add(new_tab);
                    context.add(new_window);
                    context.add(copy);
                    context.addSeparator();

                    addDefaults(context, screenshot, webEngine, devtools);

                    context.show(jfx, (int) event.getX(), (int) event.getY());
                }

            }
        });

        StackPane root = new StackPane();

        root.getChildren().add(webView);

        Scene scene = new Scene(root);

        jfx.setScene(scene);

        return new MainContentData(jfx, webView, webEngine, scene, webEngine.getHistory(), screenshot);
    }

    private static void addDefaults(JPopupMenu m, Runnable screenshot, WebEngine webEngine, Runnable devtools) {
        JMenuItem createScreenshot = new JMenuItem("Create Screenshot");
        createScreenshot.addActionListener((e) -> screenshot.run());

        JMenuItem reloadItem = new JMenuItem("Reload");
        reloadItem.addActionListener((e) -> webEngine.reload());

        JMenuItem openDevTools = new JMenuItem("Open Dev Tools");
        openDevTools.addActionListener((e) -> devtools.run());

        m.add(createScreenshot);
        m.add(reloadItem);
        m.add(openDevTools);
    }

    private static void showSource(BrowserWindow window, WebEngine engine) {
        try {
            URL url = new URL(engine.getLocation());
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("User-Agent", engine.getUserAgent());
            InputStream in = con.getInputStream();
            byte[] bytes = in.readAllBytes();
            in.close();

            MainContentData data = window.newTab(true);

            data.webEngine().loadContent(new String(bytes, StandardCharsets.UTF_8), "plain/text");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createContextMenu(WebView webView) {
        ContextMenu contextMenu = new ContextMenu();
        /*MenuItem reload = new MenuItem("Reload");
        reload.setOnAction(e -> webView.getEngine().reload());
        MenuItem savePage = new MenuItem("Save Page");
        savePage.setOnAction(e -> System.out.println("Save page..."));
        MenuItem hideImages = new MenuItem("Hide Images");
        hideImages.setOnAction(e -> System.out.println("Hide Images..."));
        contextMenu.getItems().addAll(reload, savePage, hideImages);*/

        webView.setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                contextMenu.show(webView, e.getScreenX(), e.getScreenY());
            } else {
                contextMenu.hide();
            }
        });
    }

}
