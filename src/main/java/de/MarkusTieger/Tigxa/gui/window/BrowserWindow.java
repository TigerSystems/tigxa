package de.MarkusTieger.Tigxa.gui.window;

import de.MarkusTieger.Tigxa.Browser;
import de.MarkusTieger.Tigxa.api.IAPI;
import de.MarkusTieger.Tigxa.api.engine.IEngine;
import de.MarkusTieger.Tigxa.api.gui.IScreen;
import de.MarkusTieger.Tigxa.api.impl.main.gui.window.MainWindowManager;
import de.MarkusTieger.Tigxa.api.media.IMediaEngine;
import de.MarkusTieger.Tigxa.api.permission.Permission;
import de.MarkusTieger.Tigxa.api.web.IWebEngine;
import de.MarkusTieger.Tigxa.api.web.IWebHistory;
import de.MarkusTieger.Tigxa.api.window.IWindow;
import de.MarkusTieger.Tigxa.api.window.IWindowManager;
import de.MarkusTieger.Tigxa.extension.IExtension;
import de.MarkusTieger.Tigxa.gui.components.ModifiedTabbedPane;
import de.MarkusTieger.Tigxa.gui.image.ImageLoader;
import de.MarkusTieger.Tigxa.gui.theme.ThemeManager;
import de.MarkusTieger.Tigxa.media.MediaUtils;
import de.MarkusTieger.Tigxa.web.WebUtils;
import de.MarkusTieger.Tigxa.web.search.PrefixSearch;
import javafx.application.Platform;
import lombok.Getter;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ComponentUI;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class BrowserWindow {

    public ModifiedTabbedPane tabs = null;
    private KeyListener key = null;

    @Getter
    private File configRoot = new File(".");

    private File pictures = new File(configRoot, "pictures");
    public File screenshots = new File(pictures, "screenshots");

    @Getter
    private final JFrame frame = new JFrame(Browser.FULL_NAME + " v." + Browser.FULL_VERSION);

    private int lineHeight = -1;

    private IWindow api;

    public void updateUI() {
        SwingUtilities.updateComponentTreeUI(frame);

    }

    public void update() {}

    @Getter
    private IAPI mapi;

    @Getter
    private Browser.Mode mode;



    private int selectedTabPage = 0;
    private int maxTabPage = 5;
    private Map<Integer, ModifiedTabbedPane> tabPages = Collections.synchronizedMap(new HashMap<>());
    private Map<Integer, JToggleButton> tabButtons = Collections.synchronizedMap(new HashMap<>());


    public void initWindow(Browser.Mode mode, IAPI mapi, File configRoot) {

        this.mode = mode;
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
                    WebUtils.unloadTab(BrowserWindow.this, c);
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

        ModifiedTabbedPane tabs = new ModifiedTabbedPane();



        JPanel side = new JPanel() {

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(30, frame.getContentPane().getHeight());
            }

            @Override
            public Dimension getMinimumSize() {
                return getPreferredSize();
            }

            @Override
            public Dimension getMaximumSize() {
                return getPreferredSize();
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                if(lineHeight != -1){

                    try {
                        Point p = getLocationOnScreen();

                        g.setColor(ThemeManager.getAccentColor());
                        g.drawLine(29, lineHeight - p.y, 29, getHeight());
                    } catch (Throwable e){}

                }

                repaint();
            }
        };
        side.setLayout(null);
        frame.add(side, BorderLayout.WEST);

        JPanel tabPanel = new JPanel();
        CardLayout cardLayout = new CardLayout();

        synchronized (tabPages){
            tabPages.put(Integer.valueOf(selectedTabPage), tabs);
        }

        applyTabHandler(selectedTabPage, tabPanel, cardLayout, tabs);

        tabPanel.setLayout(cardLayout);

        for(int i = 0; i < maxTabPage; i++){
            final int k = i;
            JToggleButton btn = new JToggleButton((i + 1) + "");

            btn.setBounds(2, 100 + 2 + (k * 30), 26, 26);

            tabButtons.put(Integer.valueOf(i), btn);

            final int btnselect = i;
            btn.addActionListener((e) -> {

                if(!btn.isSelected()){
                    btn.setSelected(true);
                    return;
                }

                if(btnselect == selectedTabPage) return;

                final ModifiedTabbedPane current = tabs;

                synchronized (tabPages){

                    if(tabPages.containsKey(Integer.valueOf(selectedTabPage))){
                        tabPages.replace(Integer.valueOf(selectedTabPage), current);
                    } else {
                        tabPages.put(Integer.valueOf(selectedTabPage), current);
                    }

                    if(tabPages.containsKey(Integer.valueOf(btnselect))){
                        ModifiedTabbedPane p = tabPages.get(Integer.valueOf(btnselect));
                        this.tabs = p;

                        cardLayout.show(tabPanel, "tab_" + btnselect);
                    } else {
                        ModifiedTabbedPane p = new ModifiedTabbedPane();

                        applyTabHandler(btnselect, tabPanel, cardLayout, p);

                        this.tabs = p;
                        tabPanel.add(p, "tab_" + btnselect);

                        Platform.runLater(() -> newTab((String)null, true));
                    }

                }

                selectedTabPage = btnselect;
                for(int j = 0; j < maxTabPage; j++){
                    JToggleButton b = tabButtons.get(Integer.valueOf(j));
                    b.setSelected(j == btnselect);
                }

            });
            side.add(btn);
        }
        tabButtons.get(Integer.valueOf(selectedTabPage)).setSelected(true);


        /*if(theme.tabBG() != null) tabs.setBackground(theme.tabBG());
        if(theme.tabFG() != null) tabs.setForeground(theme.tabFG());*/

        int btnselect = selectedTabPage;

        synchronized (tabPages){
            tabPages.put(Integer.valueOf(btnselect), tabs);
        }
        tabPanel.add(tabs, "tab_" + btnselect);

        frame.add(tabPanel);

        key = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {


                if (e.getKeyCode() == KeyEvent.VK_T && e.isControlDown()) {
                    Platform.runLater(() -> newTab((String) null, true));
                }
                if (e.getKeyCode() == KeyEvent.VK_W && e.isControlDown()) {

                    int index = tabs.getSelectedIndex();

                    Component c = tabs.getSelectedComponent();

                    rmTab(selectedTabPage, tabPanel, cardLayout, index, c);

                }

                if (e.getKeyCode() == KeyEvent.VK_F2) {

                    WebUtils.screenshot(BrowserWindow.this);

                }

                if (e.getKeyCode() == KeyEvent.VK_F5) {

                    WebUtils.reload(BrowserWindow.this);

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
            api = mwm.fromBW(this);
        }

        List<BrowserWindow> windows = Browser.getWindows();
        synchronized (windows) {
            windows.add(this);
        }

        frame.setVisible(true);
    }

    private void applyTabHandler(int i, JPanel tabPanel, CardLayout cardLayout, ModifiedTabbedPane tabs) {
        tabs.setHandler((index, c) -> rmTab(i, tabPanel, cardLayout, index, c), () -> {

            Platform.runLater(() -> newTab((String)null, true));

        } );
    }

    private void rmTab(int i, JPanel tabPanel, CardLayout cardLayout, int index, Component c){
        tabs.removeTabAt(index);
        if (tabs.getTabCount() < 2) {
            synchronized (tabPages){
                tabPages.remove(Integer.valueOf(i));
            }
            findTab(tabPanel, cardLayout);
        }
        WebUtils.unloadTab(BrowserWindow.this, c);
        update();
    }

    private void findTab(JPanel tabPanel, CardLayout cardLayout) {

        synchronized (tabPages){
            for(Map.Entry<Integer, ModifiedTabbedPane> e : tabPages.entrySet()){
                applyTab(e.getKey().intValue(), tabPanel, cardLayout, e.getValue());
                return;
            }
        }

        rmwindow();
    }

    private void applyTab(int btnselect, JPanel tabPanel, CardLayout cardLayout, ModifiedTabbedPane value) {
        final ModifiedTabbedPane current = tabs;

        cardLayout.show(tabPanel, "tab_" + btnselect);

        this.tabs = value;

        selectedTabPage = btnselect;
        for(int j = 0; j < maxTabPage; j++){
            JToggleButton b = tabButtons.get(Integer.valueOf(j));
            b.setSelected(j == btnselect);
        }
    }

    private void rmwindow(){
        frame.setVisible(false);

        List<BrowserWindow> windows = Browser.getWindows();
        synchronized (windows) {
            windows.remove(BrowserWindow.this);
            if (windows.size() == 0) System.exit(0);
        }
    }

    @Getter
    private final Map<Component, IEngine> tabLinks = Collections.synchronizedMap(new HashMap<>());







    public Component newMediaTab(String url, boolean autoselect) {

        if (tabs == null) throw new RuntimeException("GUI is not initialized!");

        final ModifiedTabbedPane tabs = this.tabs;

        if (url == null) url = Browser.HOMEPAGE;

        JPanel panel = new JPanel();

        JPanel nav = new JPanel() {

            @Override
            public Dimension getPreferredSize() {
                Dimension dim = panel.getSize();
                dim.height = 34;
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

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                g.setColor(ThemeManager.getAccentColor());
                g.drawLine(0, 33, getWidth(), 33);

                try {
                    lineHeight = getLocationOnScreen().y + 33;
                } catch (Throwable e){
                }
            }

        };
        nav.setLayout(null);


        List<Runnable> handlers = new ArrayList<>();

        Component main = MediaUtils.createPanel(BrowserWindow.this, nav, url, new IMediaEngine[1], panel, handlers);

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

        tabs.addTab("Tab", panel);

        update();

        handlers.forEach(Runnable::run);

        if (autoselect) tabs.setSelectedComponent(panel);

        return panel;
    }


    public Component newTab(String url, boolean autoselect) {

        if (tabs == null) throw new RuntimeException("GUI is not initialized!");

        final ModifiedTabbedPane tabs = this.tabs;

        if (url == null) url = Browser.HOMEPAGE;

        JPanel panel = new JPanel();

        JPanel nav = new JPanel() {

            @Override
            public Dimension getPreferredSize() {
                Dimension dim = panel.getSize();
                dim.height = 34;
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


            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                g.setColor(ThemeManager.getAccentColor());
                g.drawLine(0, 33, getWidth(), 33);

                try {
                    lineHeight = getLocationOnScreen().y + 33;
                } catch (Throwable e){
                }
            }
        };
        nav.setLayout(null);


        List<Runnable> handlers = new ArrayList<>();

        Component main = WebUtils.createPanel(tabs, BrowserWindow.this, nav, url, new IWebEngine[1], panel, handlers);

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

        tabs.addTab("Tab", panel);

        update();

        handlers.forEach(Runnable::run);

        if (autoselect) tabs.setSelectedComponent(panel);

        return panel;
    }

    public IWebEngine newTab(boolean autoselect) {

        if (tabs == null) throw new RuntimeException("GUI is not initialized!");

        final ModifiedTabbedPane tabs = this.tabs;

        JPanel panel = new JPanel();

        JPanel nav = new JPanel() {

            @Override
            public Dimension getPreferredSize() {
                Dimension dim = panel.getSize();
                dim.height = 34;
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

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                g.setColor(ThemeManager.getAccentColor());
                g.drawLine(0, 33, getWidth(), 33);

                try {
                    lineHeight = getLocationOnScreen().y + 33;
                } catch (Throwable e){
                }
            }
        };
        nav.setLayout(null);

        IWebEngine[] enginearray = new IWebEngine[] {null};

        List<Runnable> handlers = new ArrayList<>();

        Component main = WebUtils.createPanel(tabs, BrowserWindow.this, nav,null, enginearray, panel, handlers);

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

        tabs.addTab("Tab", panel);

        update();

        handlers.forEach(Runnable::run);

        if (autoselect) tabs.setSelectedComponent(panel);

        return enginearray[0];
    }


    public Component newTab(IScreen screen, boolean autoselect) {

        if (tabs == null) throw new RuntimeException("GUI is not initialized!");

        final ModifiedTabbedPane tabs = this.tabs;

        JPanel main = screen.getContentPane();

        JPanel panel = new JPanel();

        JPanel nav = new JPanel() {

            @Override
            public Dimension getPreferredSize() {
                Dimension dim = panel.getSize();
                dim.height = 34;
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

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                g.setColor(ThemeManager.getAccentColor());
                g.drawLine(0, 33, getWidth(), 33);

                try {
                    lineHeight = getLocationOnScreen().y + 33;
                } catch (Throwable e){
                }
            }
        };
        nav.setLayout(null);

        Component[] c = new Component[] {screen.getContentPane()};

        Consumer<String>[] changearray = new Consumer[] {(str) -> {}};
        Consumer<String> change = buildNav(nav, () -> {


        }, () -> {


        }, () -> {
        }, (loc) -> {

            try {
                URI uri = new URI(loc);
                IScreen sc = mapi.getGUIManager().getScreenRegistry().getRegistredScreen(uri.getScheme(), uri.getHost());
                if(sc == null){
                    newTab(loc, true);
                    changearray[0].accept(screen.getLocation());
                } else {
                    int index = tabs.indexOfComponent(c[0]);
                    c[0] = sc.getContentPane();
                    tabs.setComponentAt(index, c[0]);
                    tabs.setTitleAt(index, sc.getTitle());
                }
            } catch (Throwable e) {
                newTab(loc, true);
                changearray[0].accept(screen.getLocation());
            }

        }, null);

        changearray[0] = change;

        change.accept(screen.getLocation());

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

        tabs.addTab(screen.getTitle(), panel);

        update();

        if (autoselect) tabs.setSelectedComponent(panel);

        return panel;
    }


    private void addKeyListener(Container c) {
        for (Component c_ : c.getComponents()) {
            c_.addKeyListener(key);
            if (c_ instanceof Container c__) addKeyListener(c__);
        }
    }

    public Consumer<String> buildNav(JPanel nav, Runnable backwards, Runnable forwards, Runnable reload, Consumer<String> location, IEngine mcd) {

        ArrayList<BiConsumer<IEngine, String>> handlers = new ArrayList<>();

        final int webFunctionCalls = buildFunctionCalls(nav, backwards, forwards, reload, mcd == null ? null : (mcd instanceof IWebEngine web ? web.getHistory() : null), handlers::add);
        final int extensionSymbols = buildExtensionBar(nav, webFunctionCalls);

        buildLocationBar(nav, location, webFunctionCalls, extensionSymbols, handlers::add);

        return (loc) -> handlers.forEach((consumer) -> consumer.accept(mcd, loc));
    }

    private int buildFunctionCalls(JPanel nav, Runnable backwards, Runnable forwards, Runnable reload, IWebHistory history, Consumer<BiConsumer<IEngine, String>> add) {

        JButton backwardsBtn = new JButton("<-") {

            @Override
            public boolean isEnabled() {
                if(history == null){
                    model.setEnabled(false);
                    return false;
                }
                boolean enabled = history.hasBackwards();
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

                if(history == null){
                    model.setEnabled(false);
                    return false;
                }

                boolean enabled = history.hasForwards();

                if (!enabled && model.isRollover()) {
                    model.setRollover(false);
                }
                model.setEnabled(enabled);

                return enabled;
            }
        };
        JButton reloadBtn = new JButton("F5") {

            @Override
            public boolean isEnabled() {
                if(history == null){
                    model.setEnabled(false);
                    return false;
                }
                return true;
            }
        };

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

                    IWindow w = null;

                    if (ext.hasPermission(new Permission[]{Permission.WINDOW})) w = api;

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

    private void buildLocationBar(JPanel nav, Consumer<String> location, int webFunctionCalls, int extensionSymbols, Consumer<BiConsumer<IEngine, String>> add) {

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

        security.addActionListener((e) -> this.cps());

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
                PrefixSearch.search(location, field.getText());
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

    private long start = -1L;
    private int click = 0;
    private boolean result = false;

    private void cps() {
        if (start == -1L || (start + 15000L) < System.currentTimeMillis()) {
            start = System.currentTimeMillis();
            result = false;
            click = 0;
        }
        if ((start + 10000L) > System.currentTimeMillis()) {
            click++;
        } else {
            if (result) return;
            result = true;
            JOptionPane.showMessageDialog(null, "Easter-EGG found:\nYour CPS are: " + (((double) click) / 10D), "Easter-EGG...", JOptionPane.INFORMATION_MESSAGE);
        }
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

    public boolean isFullscreen() {
        return frame.isUndecorated();
    }

    public void enterFullscreen() {
        Toolkit kit = Toolkit.getDefaultToolkit();
        frame.setVisible(false);
        frame.setSize(kit.getScreenSize());
        frame.setUndecorated(true);
        frame.setAlwaysOnTop(true);
        frame.setVisible(true);
    }

    public void exitFullscreen() {
        frame.setVisible(false);
        frame.setUndecorated(false);
        frame.setAlwaysOnTop(false);
        frame.setVisible(true);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    }
}