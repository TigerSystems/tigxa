package de.MarkusTieger.Tigxa.gui.window;

import de.MarkusTieger.Tigxa.Browser;
import de.MarkusTieger.Tigxa.extension.IExtension;
import de.MarkusTieger.Tigxa.api.IAPI;
import de.MarkusTieger.Tigxa.api.gui.IGUIWindow;
import de.MarkusTieger.Tigxa.api.impl.DefaultGUIWindow;
import de.MarkusTieger.Tigxa.api.impl.main.gui.window.MainWindowManager;
import de.MarkusTieger.Tigxa.api.window.IWindowManager;
import de.MarkusTieger.Tigxa.gui.components.DraggableTabbedPane;
import de.MarkusTieger.Tigxa.gui.image.ImageLoader;
import de.MarkusTieger.Tigxa.gui.theme.ThemeManager;
import de.MarkusTieger.Tigxa.web.MainContent;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.web.WebHistory;
import lombok.Getter;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ComponentUI;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class BrowserWindow {

    public JTabbedPane tabs = null;
    private KeyListener key = null;

    @Getter
    private File configRoot = new File(".");

    private File pictures = new File(configRoot, "pictures"),
            screenshots = new File(pictures, "screenshots");

    @Getter
    private final JFrame frame = new JFrame(Browser.FULL_NAME + " v." + Browser.FULL_VERSION);
    private final JLabel addpanel = new JLabel("Nice to see you");


    private IGUIWindow api;

    public void updateUI() {
        SwingUtilities.updateComponentTreeUI(frame);

    }

    public void update() {

    }

    @Getter
    private IAPI mapi;

    public void initWindow(IAPI mapi, File configRoot) {

        this.mapi = mapi;

        this.configRoot = configRoot;

        pictures = new File(configRoot, "pictures");
        if (!pictures.exists()) pictures.mkdirs();

        screenshots = new File(pictures, "screenshots");
        if (!screenshots.exists()) screenshots.mkdirs();


        BufferedImage image = ImageLoader.loadInternalImage("/res/gui/logo.png");

        frame.setSize(1600, 900);
        frame.setResizable(true);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
            }

            @Override
            public void windowClosing(WindowEvent e) {

                List<BrowserWindow> windows = Browser.getWindows();
                synchronized (windows) {
                    windows.remove(BrowserWindow.this);
                    if (windows.size() == 0) System.exit(0);
                }

                for (int i = 0; i < tabs.getTabCount(); i++) {
                    Component c = tabs.getComponent(i);
                    unloadTab(c);
                }
            }

            @Override
            public void windowClosed(WindowEvent e) {
            }

            @Override
            public void windowIconified(WindowEvent e) {
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
            }

            @Override
            public void windowActivated(WindowEvent e) {
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
            }
        });
        frame.setFocusable(true);
        if (image != null) frame.setIconImage(image);

        JTabbedPane tabs = new DraggableTabbedPane();

        /*if(theme.tabBG() != null) tabs.setBackground(theme.tabBG());
        if(theme.tabFG() != null) tabs.setForeground(theme.tabFG());*/

        tabs.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (tabs.getSelectedComponent() == addpanel) {
                    Platform.runLater(() -> newTab(null, true));
                }
            }
        });

        frame.add(tabs);

        key = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {


                if (e.getKeyCode() == KeyEvent.VK_T && e.isControlDown()) {
                    Platform.runLater(() -> newTab(null, true));
                }
                if (e.getKeyCode() == KeyEvent.VK_W && e.isControlDown()) {

                    int index = tabs.getSelectedIndex();

                    Component c = tabs.getSelectedComponent();
                    tabs.removeTabAt(index);
                    if (tabs.getTabCount() < 1) {
                        frame.setVisible(false);

                        List<BrowserWindow> windows = Browser.getWindows();
                        synchronized (windows) {
                            windows.remove(BrowserWindow.this);
                            if (windows.size() == 0) System.exit(0);
                        }
                    }
                    unloadTab(c);
                    update();

                }

                if (e.getKeyCode() == KeyEvent.VK_F2) {

                    synchronized (tabLinks) {
                        Component c = tabs.getSelectedComponent();
                        MainContent.MainContentData data = tabLinks.get(c);
                        if (data == null) return;
                        Platform.runLater(data.screenshot());
                    }

                }

                if (e.getKeyCode() == KeyEvent.VK_F5) {

                    synchronized (tabLinks) {
                        Component c = tabs.getSelectedComponent();
                        MainContent.MainContentData data = tabLinks.get(c);
                        if (data == null) return;
                        Platform.runLater(data.webEngine()::reload);
                    }

                }


            }

            @Override
            public void keyReleased(KeyEvent e) {


            }
        };
        frame.addKeyListener(key);

        this.tabs = tabs;

        IWindowManager wm = mapi.getWindowManager();
        if (wm instanceof MainWindowManager mwm) {
            api = new DefaultGUIWindow(mwm.fromBW(this));
        }

        List<BrowserWindow> windows = Browser.getWindows();
        synchronized (windows) {
            windows.add(this);
        }

        frame.setVisible(true);
    }

    @Getter
    private final Map<Component, MainContent.MainContentData> tabLinks = Collections.synchronizedMap(new HashMap<>());

    public void unloadTab(Component c) {

        synchronized (tabLinks) {
            MainContent.MainContentData data = tabLinks.get(c);
            if (data == null) return;
            Platform.runLater(() -> data.webEngine().load(null));
        }

    }

    public Component newTab(String url, boolean autoselect) {

        if (tabs == null) throw new RuntimeException("GUI is not initialized!");

        if (url == null) url = Browser.DEFAULT_HOMEPAGE;

        JPanel panel = new JPanel();

        JPanel nav = new JPanel() {

            @Override
            public Dimension getPreferredSize() {
                Dimension dim = panel.getSize();
                dim.height = 30;
                return dim;
            }

            @Override
            public Dimension getMaximumSize() {
                return getPreferredSize();
            }

            @Override
            public Dimension getMinimumSize() {
                return getPreferredSize();
            }
        };
        nav.setLayout(null);

        String[] d = new String[]{null};
        final Consumer<String>[] changearray = new Consumer[]{
                (Consumer<String>) (c) -> {
                    d[0] = c;
                }
        };
        Consumer<String> change = (c) -> changearray[0].accept(c);


        Runnable[] screenshotarray = new Runnable[]{() -> {
        }};
        Runnable screenshot = () -> screenshotarray[0].run();

        Runnable[] devtoolsarray = new Runnable[]{() -> {
        }};
        Runnable devtools = () -> devtoolsarray[0].run();

        MainContent.MainContentData data = MainContent.createContent(this, (title) -> {

                    int index = -1;

                    index = tabs.indexOfComponent(panel);

                    if (index == -1) return;

                    tabs.setTitleAt(index, title);

                }, (icon) -> {

                    int index = -1;

                    index = tabs.indexOfComponent(panel);

                    if (index == -1) return;

                    tabs.setIconAt(index, icon);

                }
                , (loc) -> newTab(loc, false)
                , (link) -> {
                }, change, screenshot, devtools);


        JFXPanel main = data.jfx();

        screenshotarray[0] = () -> Platform.runLater(() -> this.takeScreenshot(main));

        changearray[0] = buildNav(nav, () -> {

            // Backwards
            WebHistory history = data.history();

            if (history.getCurrentIndex() > 0) {
                Platform.runLater(() -> {
                    data.webEngine().load(history.getEntries().get(history.getCurrentIndex() - 1).getUrl());
                    history.go(-1);
                });
            }

        }, () -> {

            // Forwards
            WebHistory history = data.history();

            if ((history.getCurrentIndex() + 1) < history.getEntries().size()) {
                Platform.runLater(() -> {
                    data.webEngine().load(history.getEntries().get(history.getCurrentIndex() + 1).getUrl());
                    history.go(1);
                });
            }


        }, () -> Platform.runLater(data.webEngine()::reload), (loc) -> Platform.runLater(() -> data.webEngine().load(loc)), data);
        if (d[0] != null) {
            changearray[0].accept(d[0]);
        }
        devtoolsarray[0] = () -> DevWindow.create(data);

        data.webEngine().load(url);

        JPanel content = new JPanel();

        content.add(nav);

        JPanel mainContent = new JPanel() {

            @Override
            public Dimension getPreferredSize() {
                Dimension dim = panel.getSize();
                dim.height -= 30;
                return dim;
            }

            @Override
            public Dimension getMaximumSize() {
                return getPreferredSize();
            }

            @Override
            public Dimension getMinimumSize() {
                return getPreferredSize();
            }
        };
        mainContent.setLayout(new GridLayout(1, 1));
        mainContent.add(main);
        content.add(mainContent);

        panel.setLayout(new GridLayout(1, 1));
        panel.add(content);

        addKeyListener(panel);

        synchronized (tabLinks) {
            tabLinks.put(panel, data);
        }
        tabs.addTab("Tab", panel);

        update();

        if (autoselect) tabs.setSelectedComponent(panel);

        return panel;
    }

    private void takeScreenshot(JFXPanel main) {

        try {
            BufferedImage image = new BufferedImage(main.getWidth(), main.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics g = image.getGraphics();
            main.print(g);

            File screenshot = new File(screenshots, System.currentTimeMillis() + ".png");
            if (!screenshot.exists()) screenshot.createNewFile();

            ImageIO.write(image, "png", screenshot);

            g.dispose();
            image.flush();

            newTab(screenshot.toURI().toString(), true);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public MainContent.MainContentData newTab(boolean autoselect) {

        if (tabs == null) throw new RuntimeException("GUI is not initialized!");

        JPanel panel = new JPanel();

        JPanel nav = new JPanel() {

            @Override
            public Dimension getPreferredSize() {
                Dimension dim = panel.getSize();
                dim.height = 30;
                return dim;
            }

            @Override
            public Dimension getMaximumSize() {
                return getPreferredSize();
            }

            @Override
            public Dimension getMinimumSize() {
                return getPreferredSize();
            }
        };
        nav.setLayout(null);

        String[] d = new String[]{null};
        final Consumer<String>[] changearray = new Consumer[]{
                (Consumer<String>) (c) -> {
                    d[0] = c;
                }
        };
        Consumer<String> change = (c) -> changearray[0].accept(c);

        Runnable[] devtoolsarray = new Runnable[]{() -> {
        }};
        Runnable devtools = () -> devtoolsarray[0].run();

        Runnable[] screenshotarray = new Runnable[]{() -> {
        }};
        Runnable screenshot = () -> screenshotarray[0].run();

        MainContent.MainContentData data = MainContent.createContent(this, (title) -> {
                    int index = -1;

                    index = tabs.indexOfComponent(panel);

                    if (index == -1) return;

                    tabs.setTitleAt(index, title);

                }, (icon) -> {

                    int index = -1;

                    index = tabs.indexOfComponent(panel);

                    if (index == -1) return;

                    tabs.setIconAt(index, icon);

                }
                , (loc) -> newTab(loc, false)
                , (link) -> {
                }, change, screenshot, devtools);

        JFXPanel main = data.jfx();

        screenshotarray[0] = () -> this.takeScreenshot(main);

        changearray[0] = buildNav(nav, () -> {

            // Backwards
            WebHistory history = data.history();

            if (history.getCurrentIndex() > 0) {
                Platform.runLater(() -> {
                    data.webEngine().load(history.getEntries().get(history.getCurrentIndex() - 1).getUrl());
                    history.go(-1);
                });
            }

        }, () -> {

            // Forwards
            WebHistory history = data.history();

            if ((history.getCurrentIndex() + 1) < history.getEntries().size()) {
                Platform.runLater(() -> {
                    data.webEngine().load(history.getEntries().get(history.getCurrentIndex() + 1).getUrl());
                    history.go(1);
                });
            }


        }, () -> Platform.runLater(data.webEngine()::reload), (loc) -> Platform.runLater(() -> data.webEngine().load(loc)), data);
        if (d[0] != null) {
            changearray[0].accept(d[0]);
        }
        devtoolsarray[0] = () -> DevWindow.create(data);

        main.setScene(data.scene());

        JPanel content = new JPanel();
        content.add(nav);

        JPanel mainContent = new JPanel() {

            @Override
            public Dimension getPreferredSize() {
                Dimension dim = panel.getSize();
                dim.height -= 30;
                return dim;
            }

            @Override
            public Dimension getMaximumSize() {
                return getPreferredSize();
            }

            @Override
            public Dimension getMinimumSize() {
                return getPreferredSize();
            }
        };
        mainContent.setLayout(new GridLayout(1, 1));
        mainContent.add(main);
        content.add(mainContent);

        panel.setLayout(new GridLayout(1, 1));
        panel.add(content);

        addKeyListener(panel);

        synchronized (tabLinks) {
            tabLinks.put(panel, data);
        }
        tabs.addTab("Tab", panel);

        update();

        if (autoselect) tabs.setSelectedComponent(panel);

        return data;
    }

    private void addKeyListener(Container c) {
        for (Component c_ : c.getComponents()) {
            c_.addKeyListener(key);
            if (c_ instanceof Container c__) addKeyListener(c__);
        }
    }

    private Consumer<String> buildNav(JPanel nav, Runnable backwards, Runnable forwards, Runnable reload, Consumer<String> location, MainContent.MainContentData mcd) {

        ArrayList<BiConsumer<MainContent.MainContentData, String>> handlers = new ArrayList<>();

        final int webFunctionCalls = buildFunctionCalls(nav, backwards, forwards, reload, mcd.history(), handlers::add);
        final int extensionSymbols = buildExtensionBar(nav, webFunctionCalls);

        buildLocationBar(nav, location, webFunctionCalls, extensionSymbols, handlers::add);

        return (loc) -> handlers.forEach((consumer) -> consumer.accept(mcd, loc));
    }

    private int buildFunctionCalls(JPanel nav, Runnable backwards, Runnable forwards, Runnable reload, WebHistory history, Consumer<BiConsumer<MainContent.MainContentData, String>> add) {

        JButton backwardsBtn = new JButton("<-") {

            @Override
            public boolean isEnabled() {
                boolean enabled = history.getCurrentIndex() > 0;
                if (!enabled && model.isRollover()) {
                    model.setRollover(false);
                }
                model.setEnabled(enabled);
                return enabled;
            }
        };
        JButton forwardsBtn = new JButton("->") {

            @Override
            public boolean isEnabled() {
                boolean enabled = (history.getCurrentIndex() + 1) < history.getEntries().size();

                if (!enabled && model.isRollover()) {
                    model.setRollover(false);
                }
                model.setEnabled(enabled);

                return enabled;
            }
        };
        JButton reloadBtn = new JButton("F5");

        backwardsBtn.setBounds(5, 2, 45, 26);
        forwardsBtn.setBounds((5 + 45) + 5 + 2, 2, 45, 26);
        reloadBtn.setBounds(((5 + 45) + 5 + 2) + 45 + 5 + 2, 2, 45, 26);

        backwardsBtn.setFocusable(false);
        forwardsBtn.setFocusable(false);
        reloadBtn.setFocusable(false);

        add.accept((mcd, loc) -> backwardsBtn.isEnabled());
        add.accept((mcd, loc) -> forwardsBtn.isEnabled());

        backwardsBtn.addActionListener((e) -> backwards.run());
        forwardsBtn.addActionListener((e) -> forwards.run());
        reloadBtn.addActionListener((e) -> reload.run());

        nav.add(backwardsBtn);
        nav.add(forwardsBtn);
        nav.add(reloadBtn);

        return 200;
    }

    private int buildExtensionBar(JPanel nav, int webFunctionCalls) {

        int amount = Browser.getExtmanager().getExtensions().size();
        int pos = 0;
        for (IExtension ext : Browser.getExtmanager().getExtensions()) {
            pos++;

            final int current_pos = pos;
            JButton btn = new JButton() {

                @Override
                public int getX() {
                    updateBounds();
                    return super.getX();
                }

                @Override
                public int getY() {
                    updateBounds();
                    return super.getY();
                }

                @Override
                public int getHeight() {
                    updateBounds();
                    return super.getHeight();
                }

                @Override
                public int getWidth() {
                    updateBounds();
                    return super.getWidth();
                }

                public void updateBounds() {
                    int bar = nav.getWidth() - webFunctionCalls;
                    bar -= (amount * 30);
                    bar -= 30;
                    bar += current_pos * 30;

                    int x = webFunctionCalls + bar;

                    x += 2;

                    int y = 2;
                    int width = 26;
                    int height = 26;

                    boolean update = x != super.getX();

                    if (y != super.getY()) update = true;
                    if (width != super.getWidth()) update = true;
                    if (height != super.getHeight()) update = true;

                    if (update) setBounds(x, y, width, height);
                }

                @Override
                public void updateUI() {
                    super.updateUI();
                    ui();
                }

                @Override
                protected void setUI(ComponentUI newUI) {
                    super.setUI(newUI);
                    ui();
                }

                private void ui() {
                    Image icon = ThemeManager.isDark() ? ext.getDarkIcon() : ext.getIcon();
                    if (icon != null) setIcon(new ImageIcon(icon));
                }
            };

            Image icon = ThemeManager.isDark() ? ext.getDarkIcon() : ext.getIcon();
            if (icon != null) {
                ImageIcon ii = new ImageIcon(icon);
                btn.setIcon(ii);
            }

            btn.setFocusPainted(false);
            btn.setContentAreaFilled(false);

            btn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Point p = btn.getLocationOnScreen();

                    Point p_ = frame.getLocation();

                    Point difference = new Point();
                    difference.x = p.x - p_.x;
                    difference.y = p.y - p_.y;

                    Point n = difference;

                    ext.onAction(api,
                            n.x + (btn.getWidth() / 2), n.y + (btn.getHeight() / 2),
                            p.x, p.y
                    );

                }
            });

            nav.add(btn);

        }

        return 30 + (amount * 30);

    }

    private void buildLocationBar(JPanel nav, Consumer<String> location, int webFunctionCalls, int extensionSymbols, Consumer<BiConsumer<MainContent.MainContentData, String>> add) {

        final int y = 2;
        final int height = 26;

        ImageIcon security_secure = ImageLoader.loadInternalImageAsIcon("/res/gui/nav/security_secure.png");
        ImageIcon security_secure_hover = ImageLoader.loadInternalImageAsIcon("/res/gui/nav/security_secure_hover.png");

        ImageIcon security_insecure = ImageLoader.loadInternalImageAsIcon("/res/gui/nav/security_insecure.png");
        ImageIcon security_insecure_hover = ImageLoader.loadInternalImageAsIcon("/res/gui/nav/security_insecure_hover.png");

        JButton security = new JButton() {

            @Override
            public int getX() {
                updateBounds();
                return super.getX();
            }

            @Override
            public int getY() {
                updateBounds();
                return super.getY();
            }

            @Override
            public int getHeight() {
                updateBounds();
                return super.getHeight();
            }

            @Override
            public int getWidth() {
                updateBounds();
                return super.getWidth();
            }

            public void updateBounds() {
                int x = webFunctionCalls + 5;
                boolean update = false;

                int width = 26;

                if (x != super.getX()) update = true;
                if (y != super.getY()) update = true;
                if (width != super.getWidth()) update = true;
                if (height != super.getHeight()) update = true;

                if (update) setBounds(x, y, width, height);
            }
        };

        // int[] secure = new int[] {-1};

        security.setFocusPainted(false);
        security.setContentAreaFilled(false);
        /*security.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {}

            @Override
            public void mousePressed(MouseEvent e) {}

            @Override
            public void mouseReleased(MouseEvent e) {}

            @Override
            public void mouseEntered(MouseEvent e) {
                if(secure[0] == -1) return;
                ImageIcon icon = secure[0] == 1 ? security_secure_hover : security_insecure_hover;
                if(icon != null) security.setIcon(icon);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if(secure[0] == -1) return;
                ImageIcon icon = secure[0] == 1 ? security_secure : security_insecure;
                if(icon != null) security.setIcon(icon);
            }
        });*/

        security.setRolloverEnabled(true);

        add.accept((bcd, loc) -> {

            try {
                URI uri = new URI(loc);
                if (uri.getScheme().equalsIgnoreCase("https")) {
                    security.setIcon(security_secure);
                    security.setRolloverIcon(security_secure_hover);
                } else if (uri.getScheme().equalsIgnoreCase("http")) {
                    security.setIcon(security_insecure);
                    security.setRolloverIcon(security_insecure_hover);
                } else {
                    security.setIcon(null);
                    security.setRolloverIcon(null);
                }

            } catch (URISyntaxException e) {
                e.printStackTrace();
            }

        });

        nav.add(security);

        JTextField field = new JTextField() {

            @Override
            public int getX() {
                updateBounds();
                return super.getX();
            }

            @Override
            public int getY() {
                updateBounds();
                return super.getY();
            }

            @Override
            public int getHeight() {
                updateBounds();
                return super.getHeight();
            }

            @Override
            public int getWidth() {
                updateBounds();
                return super.getWidth();
            }

            public void updateBounds() {
                int x = webFunctionCalls + 35; // + 5
                int width = nav.getWidth() - ((webFunctionCalls) + 20 + extensionSymbols);
                boolean update = x != super.getX();

                if (y != super.getY()) update = true;
                if (width != super.getWidth()) update = true;
                if (height != super.getHeight()) update = true;

                if (update) setBounds(x, y, width, height);
            }
        };

        field.setText("about:blank");
        field.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    URI uri = new URI(field.getText());
                    if (uri.getScheme() == null) throw new URISyntaxException(field.getText(), "No Scheme set!");
                    location.accept(uri.toString());
                } catch (URISyntaxException ex) {
                    try {
                        URI uri = new URI("https://" + field.getText());
                        try {
                            InetAddress.getByName(uri.getHost());
                            location.accept(uri.toString());
                        } catch (UnknownHostException exc) {
                            throw new URISyntaxException(field.getText(), "Unknown Host!");
                        }
                    } catch (URISyntaxException exc) {
                        String query = field.getText();
                        try {
                            URI uri = new URI("https://google.com/search?q=" + URLEncoder.encode(query, StandardCharsets.UTF_8));
                            location.accept(uri.toString());
                        } catch (URISyntaxException uriSyntaxException) {
                            uriSyntaxException.printStackTrace();
                        }
                    }
                }
            }
        });
        field.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().isEmpty()) return;
                field.setSelectionStart(0);
                field.setSelectionEnd(field.getText().length());
            }

            @Override
            public void focusLost(FocusEvent e) {

            }
        });
        nav.add(field);

        add.accept((mcd, text) -> {
            field.setText(text);
        });
    }

    public void close() {
        /*
                List<BrowserWindow> windows = Browser.getWindows();
        synchronized (windows){
            windows.remove(this);
            if(windows.size() == 0) System.exit(0);
        }
         */
        frame.setVisible(false);
    }

}
