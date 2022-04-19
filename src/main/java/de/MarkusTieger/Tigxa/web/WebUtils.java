package de.MarkusTieger.Tigxa.web;

import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import chrriis.dj.nativeswing.swtimpl.components.*;
import de.MarkusTieger.Tigxa.Browser;
import de.MarkusTieger.Tigxa.api.engine.IEngine;
import de.MarkusTieger.Tigxa.api.gui.IScreen;
import de.MarkusTieger.Tigxa.api.web.IWebEngine;
import de.MarkusTieger.Tigxa.api.web.IWebHistory;
import de.MarkusTieger.Tigxa.gui.components.ModifiedTabbedPane;
import de.MarkusTieger.Tigxa.gui.window.BrowserWindow;
import de.MarkusTieger.Tigxa.lang.Translator;
import de.MarkusTieger.Tigxa.web.engine.djnatives.DJNativesWebEngine;
import de.MarkusTieger.Tigxa.web.engine.fx.FXContent;
import de.MarkusTieger.Tigxa.web.engine.none.NoneWebEngine;
import de.MarkusTieger.Tigxa.web.engine.swing.SwingWebEngine;

import de.MarkusTieger.Tigxa.web.engine.swt.SWTWebEngine;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.swt.browser.TitleEvent;
import org.eclipse.swt.browser.TitleListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class WebUtils {

    private static final Logger LOGGER = Logger.getLogger(WebUtils.class);

    public static void initialize(Browser.Mode mode){
        LOGGER.debug("Initializing...");
        if(mode == Browser.Mode.DJ_NATIVE_SWT){
            LOGGER.debug("Opening Native-Interface of \"" + mode.name() + "\"...");
            NativeInterface.open();
        }
    }

    public static void reload(BrowserWindow window) {

        Map<Component, IEngine> tabLinks = window.getTabLinks();

        synchronized (tabLinks) {
                        Component c = window.tabs.getSelectedComponent();
                        IEngine data = tabLinks.get(c);
                        if (data == null) return;
                        if(!(data instanceof IWebEngine web)) return;

                        web.reload();
                    }

    }

    public static void screenshot(BrowserWindow window) {
        Component c = window.tabs.getSelectedComponent();
        takeScreenshot(window, c);
    }

    public static void unloadTab(BrowserWindow window, Component c) {

        Map<Component, IEngine> tabLinks = window.getTabLinks();

        synchronized (tabLinks) {
            IEngine data = tabLinks.get(c);
            if (data == null) return;
            data.load(null);
        }

    }

    private static void syncExec(final Runnable r) {
        try {
            if (EventQueue.isDispatchThread()) r.run();
            else EventQueue.invokeAndWait(r);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    public static Component createPanel(ModifiedTabbedPane tabs, BrowserWindow window, JPanel nav, String url, IWebEngine[] enginearray, JPanel panel, List<Runnable> visibleHandler) {

        IWebEngine engine = null;
        Component component = null;

        if(window.getMode() == Browser.Mode.NONE){

            JLabel label = new JLabel();

            label.setText(Translator.translate(40));

            component = label;

            enginearray[0] = new NoneWebEngine();

        }

        if(window.getMode() == Browser.Mode.JAVAFX){

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

            FXContent.MainContentData data = FXContent.createContent(window, (title) -> {

                        int index = -1;

                        index = tabs.indexOfComponent(panel);

                        if (index == -1) return;

                        title(tabs, index, title);

                    }, (icon) -> {

                        int index = -1;

                        index = tabs.indexOfComponent(panel);

                        if (index == -1) return;

                        tabs.setIconAt(index, icon);

                    }
                    , (loc) -> window.newTab(loc, false)
                    , (link) -> {
                    }, change, screenshot, devtools);


            engine = data.apiEngine();

            final IWebEngine fe = engine;

            component = data.jfx();

            final Component finalc = component;

            screenshotarray[0] = () -> takeScreenshot(window, finalc);

            changearray[0] = window.buildNav(nav, () -> {

                // Backwards
                IWebHistory history = fe.getHistory();
                if(history.hasBackwards()){
                    history.backward();
                }

            }, () -> {

                // Forwards
                IWebHistory history = fe.getHistory();
                if(history.hasForwards()){
                    history.forward();
                }



            }, engine::reload, (loc) -> {

                try {
                    URI uri = new URI(loc);
                    IScreen sc = window.getMapi().getGUIManager().getScreenRegistry().getRegistredScreen(uri.getScheme(), uri.getHost());
                    if(sc == null){
                        fe.load(loc);
                    } else {
                        window.newTab(sc, true);
                        try {
                            changearray[0].accept(fe.getLocation());
                        } catch(Throwable e) {}
                    }
                } catch(Throwable e){
                    fe.load(loc);
                }

            }, engine);
            if (d[0] != null) {
                changearray[0].accept(d[0]);
            }

            // devtoolsarray[0] = () -> DevWindow.create(data);

            if(url != null) engine.load(url);

            enginearray[0] = engine;

        }

        if(window.getMode() == Browser.Mode.DJ_NATIVE_SWT){


            Component[] comps = new Component[1];
            syncExec(() -> comps[0] = createDJNativesPanel(tabs, window, nav, url, enginearray, panel, visibleHandler));
            component = comps[0];


        }

        if(window.getMode() == Browser.Mode.SWING){


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


            JEditorPane editor = new JEditorPane() {

                @Override
                public void setPage(URL page) throws IOException {
                    super.setPage(page);
                    changearray[0].accept(page.toString());
                }
            };
            editor.setEditable(false);
            component = editor;

            engine = new SwingWebEngine(editor);

            final Component finalc = component;
            final IWebEngine fe = engine;

            screenshotarray[0] = () -> takeScreenshot(window, finalc);

            changearray[0] = window.buildNav(nav, () -> {

                // Backwards
                IWebHistory history = fe.getHistory();
                if(history.hasBackwards()){
                    history.backward();
                }

            }, () -> {

                // Forwards
                IWebHistory history = fe.getHistory();
                if(history.hasForwards()){
                    history.forward();
                }



            }, engine::reload, (loc) -> {

                try {
                    URI uri = new URI(loc);
                    IScreen sc = window.getMapi().getGUIManager().getScreenRegistry().getRegistredScreen(uri.getScheme(), uri.getHost());
                    if(sc == null){
                        fe.load(loc);
                    } else {
                        window.newTab(sc, true);
                        try {
                            changearray[0].accept(fe.getLocation());
                        } catch(Throwable e) {}
                    }
                } catch(Throwable e){
                    fe.load(loc);
                }

            }, engine);
            if (d[0] != null) {
                changearray[0].accept(d[0]);
            }

            // devtoolsarray[0] = () -> DevWindow.create(data);

            editor.addHyperlinkListener(new HyperlinkListener() {
                @Override
                public void hyperlinkUpdate(HyperlinkEvent e) {
                    try {
                        editor.setPage(e.getURL());
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            });

            engine.load(url);

            enginearray[0] = engine;


        }

        if(window.getMode() == Browser.Mode.SWT) {


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


            Consumer<Dimension>[] sizearray = new Consumer[]{(dim) -> {
            }};

            Canvas canvas = new Canvas() {

                @Override
                public Dimension getSize() {
                    Dimension size = super.getSize();
                    sizearray[0].accept(size);
                    return size;
                }

                @Override
                public void setSize(Dimension d) {
                    sizearray[0].accept(d);
                    super.setSize(d);
                }

                @Override
                public void setSize(int width, int height) {
                    sizearray[0].accept(new Dimension(width, height));
                    super.setSize(width, height);
                }

                @Override
                public void resize(Dimension d) {
                    getSize();
                    super.resize(d);
                }

                @Override
                public void resize(int width, int height) {
                    getSize();
                    super.resize(width, height);
                }
            };

            component = canvas;

            org.eclipse.swt.browser.Browser[] browserarray = new org.eclipse.swt.browser.Browser[1];

            Runnable run = () -> {


                IWebEngine engine_ = null;

                Display display = new Display();


                Shell shell = SWT_AWT.new_Shell(display, canvas);
                org.eclipse.swt.browser.Browser browser = new org.eclipse.swt.browser.Browser(shell, SWT.NONE);
                browser.setLayoutData(new GridData(GridData.FILL_BOTH));

                browserarray[0] = browser;
                enginearray[0] = new SWTWebEngine(browser);

                sizearray[0] = (dim) -> {
                    shell.setSize(dim.width, dim.height);
                    browser.setSize(dim.width, dim.height);
                };
                canvas.getSize();


                engine_ = enginearray[0];

                final IWebEngine fe = engine_;

                final Component finalc = canvas;

                screenshotarray[0] = () -> takeScreenshot(window, finalc);

                changearray[0] = window.buildNav(nav, () -> {

                    // Backwards
                    IWebHistory history = fe.getHistory();
                    if (history.hasBackwards()) {
                        history.backward();
                    }

                }, () -> {

                    // Forwards
                    IWebHistory history = fe.getHistory();
                    if (history.hasForwards()) {
                        history.forward();
                    }


                }, engine_::reload, (loc) -> {

                    try {
                        URI uri = new URI(loc);
                        IScreen sc = window.getMapi().getGUIManager().getScreenRegistry().getRegistredScreen(uri.getScheme(), uri.getHost());
                        if (sc == null) {
                            fe.load(loc);
                        } else {
                            window.newTab(sc, true);
                            try {
                                changearray[0].accept(fe.getLocation());
                            } catch (Throwable e) {
                            }
                        }
                    } catch (Throwable e) {
                        fe.load(loc);
                    }

                }, engine_);

                browserarray[0].addLocationListener(new LocationListener() {
                    @Override
                    public void changing(LocationEvent locationEvent) {

                    }

                    @Override
                    public void changed(LocationEvent locationEvent) {
                        changearray[0].accept(browserarray[0].getUrl());
                    }
                });
                browserarray[0].addTitleListener(new TitleListener() {
                    @Override
                    public void changed(TitleEvent titleEvent) {
                        int index = -1;

                        index = tabs.indexOfComponent(panel);

                        if (index == -1) return;

                        title(tabs, index, titleEvent.title);
                    }
                });
                // Authication-Listener

                if (d[0] != null) {
                    changearray[0].accept(d[0]);
                }

                // devtoolsarray[0] = () -> DevWindow.create(data);

                if (url != null) engine_.load(url);

                enginearray[0] = engine_;

                if (url != null) browserarray[0].setUrl(url);

                SwingUtilities.invokeLater(() -> {

                    shell.open();
                    while (!shell.isDisposed()) {
                        if (!display.readAndDispatch())
                            display.sleep();
                    }
                    display.dispose();

                });

            };

            Runnable invokeRun = () -> {

                try {
                    SwingUtilities.invokeAndWait(run);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }

            };

            visibleHandler.add(invokeRun);
        }

        Map<Component, IEngine> tabLinks = window.getTabLinks();
        synchronized (tabLinks) {
            tabLinks.put(component, engine);
        }

        return component;
    }

    private static void title(JTabbedPane tabs, int index, String title) {
        String sh = "";
        int width = 0;
        if(title == null) return;
        for(char c : title.toCharArray()){
            width += tabs.getFontMetrics(tabs.getFont()).charWidth(c);
            if(width <= 150){
                sh += c;
            } else {
                sh += Translator.translate(39);
                break;
            }
        }

        tabs.setTitleAt(index, sh);
        tabs.setToolTipTextAt(index, title);
    }

    private static Component createDJNativesPanel(ModifiedTabbedPane tabs, BrowserWindow window, JPanel nav, String url, IWebEngine[] enginearray, JPanel panel, List<Runnable> visibleHandler) {

        Component component = null;
        IWebEngine engine = null;


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


        JWebBrowser browser = new JWebBrowser();
        engine = new DJNativesWebEngine(browser);

        browser.setBarsVisible(false);
        browser.setButtonBarVisible(false);
        browser.setLocationBarVisible(false);
        browser.setMenuBarVisible(false);
        browser.setStatusBarVisible(false);

        final IWebEngine fe = engine;

        component = browser;

        final Component finalc = component;

        screenshotarray[0] = () -> takeScreenshot(window, finalc);

        changearray[0] = window.buildNav(nav, () -> {

            // Backwards
            IWebHistory history = fe.getHistory();
            if (history.hasBackwards()) {
                history.backward();
            }

        }, () -> {

            // Forwards
            IWebHistory history = fe.getHistory();
            if (history.hasForwards()) {
                history.forward();
            }


        }, engine::reload, (loc) -> {

            try {
                URI uri = new URI(loc);
                IScreen sc = window.getMapi().getGUIManager().getScreenRegistry().getRegistredScreen(uri.getScheme(), uri.getHost());
                if (sc == null) {
                    syncExec(() -> fe.load(loc));
                } else {
                    window.newTab(sc, true);
                    try {
                        changearray[0].accept(fe.getLocation());
                    } catch (Throwable e) {
                    }
                }
            } catch (Throwable e) {
                syncExec(() -> fe.load(loc));
            }

        }, engine);
        if (d[0] != null) {
            changearray[0].accept(d[0]);
        }

        browser.addWebBrowserListener(new WebBrowserListener() {
            @Override
            public void windowWillOpen(WebBrowserWindowWillOpenEvent webBrowserWindowWillOpenEvent) {

            }

            @Override
            public void windowOpening(WebBrowserWindowOpeningEvent webBrowserWindowOpeningEvent) {

            }

            @Override
            public void windowClosing(WebBrowserEvent webBrowserEvent) {

            }

            @Override
            public void locationChanging(WebBrowserNavigationEvent webBrowserNavigationEvent) {

            }

            @Override
            public void locationChanged(WebBrowserNavigationEvent webBrowserNavigationEvent) {
                changearray[0].accept(webBrowserNavigationEvent.getNewResourceLocation());
            }

            @Override
            public void locationChangeCanceled(WebBrowserNavigationEvent webBrowserNavigationEvent) {

            }

            @Override
            public void loadingProgressChanged(WebBrowserEvent webBrowserEvent) {

            }

            @Override
            public void titleChanged(WebBrowserEvent webBrowserEvent) {
                int index = -1;

                index = tabs.indexOfComponent(panel);

                if (index == -1) return;

                title(tabs, index, webBrowserEvent.getWebBrowser().getPageTitle());

            }

            @Override
            public void statusChanged(WebBrowserEvent webBrowserEvent) {

            }

            @Override
            public void commandReceived(WebBrowserCommandEvent webBrowserCommandEvent) {

            }
        });

        // devtoolsarray[0] = () -> DevWindow.create(data);

        if (url != null) engine.load(url);

        enginearray[0] = engine;

        System.out.println(browser.getBrowserType());

        return component;
    }

    public static void takeScreenshot(BrowserWindow window, Component main) {

        try {
            BufferedImage image = new BufferedImage(main.getWidth(), main.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics g = image.getGraphics();
            main.print(g);

            File screenshot = new File(window.screenshots, System.currentTimeMillis() + ".png");
            if (!screenshot.exists()) screenshot.createNewFile();

            ImageIO.write(image, "png", screenshot);

            g.dispose();
            image.flush();

            window.newTab(screenshot.toURI().toString(), true);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
