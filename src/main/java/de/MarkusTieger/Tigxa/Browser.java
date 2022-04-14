package de.MarkusTieger.Tigxa;

import com.formdev.flatlaf.FlatLightLaf;
import de.MarkusTieger.Tigxa.api.IAPI;
import de.MarkusTieger.Tigxa.api.impl.main.MainAPI;
import de.MarkusTieger.Tigxa.extension.IExtension;
import de.MarkusTieger.Tigxa.extensions.impl.ExtensionManager;
import de.MarkusTieger.Tigxa.extensions.impl.internal.AdblockerExtension;
import de.MarkusTieger.Tigxa.extensions.impl.internal.SettingsExtension;
import de.MarkusTieger.Tigxa.gui.screen.InternalScreenRegistry;
import de.MarkusTieger.Tigxa.gui.theme.ThemeManager;
import de.MarkusTieger.Tigxa.gui.window.BrowserWindow;
import de.MarkusTieger.Tigxa.http.cookie.CookieManager;
import de.MarkusTieger.Tigxa.update.Updater;
import de.MarkusTieger.Tigxa.update.Version;
import de.MarkusTieger.Tigxa.web.TrustManager;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;


import javax.speech.Central;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;

public class Browser {

    public static final String DEFAULT_HOMEPAGE = "https://google.com";
    public static final String DEFAULT_SEARCH = "https://google.com/search?q=%s";

    public static final String NAME;
    public static final String FULL_NAME;
    public static final String VERSION;
    public static final String BUILD;
    public static final String FULL_VERSION;
    public static final String COMMIT_HASH;
    public static final String AUTHOR;

    public static String HOMEPAGE;
    public static String SEARCH;
    public static boolean SAVE_COOKIES;
    public static String FONT;

    static {

        String[] args = Browser.class.getPackageName().split("\\.");

        String name = Browser.class.getPackage().getSpecificationTitle();
        if (name == null) {
            name = args[args.length - 1];
        }

        NAME = name;
        FULL_NAME = NAME + " Browser";

        String version = Browser.class.getPackage().getSpecificationVersion();
        if (version == null) {
            version = "0.0.0";
        }

        VERSION = version;

        String build = Browser.class.getPackage().getImplementationVersion();
        if (build == null) {
            build = "-";
        }

        BUILD = build;

        String hash = Browser.class.getPackage().getImplementationTitle();
        if (hash == null) {
            hash = "-";
        }
        COMMIT_HASH = hash;

        FULL_VERSION = VERSION + (BUILD.equalsIgnoreCase("-") ? "" : ("-" + BUILD)) + (COMMIT_HASH.equalsIgnoreCase("-") ? "" : ("-" + COMMIT_HASH));


        String author = Browser.class.getPackage().getImplementationVendor();

        if(author == null){
            author = args[1];
        }

        AUTHOR = author;


    }

    @Getter
    private static final List<BrowserWindow> windows = Collections.synchronizedList(new ArrayList<>());
    @Getter
    private static final List<JFrame> frames = Collections.synchronizedList(new ArrayList<>());


    private static File configRoot;
    private static Properties config;

    @Getter
    private static ExtensionManager extmanager;

    @Getter
    private static IAPI mainAPI;

    @Getter
    private static Updater updater = new Updater();

    @Getter
    private static List<Consumer<Version>> updateListener = new ArrayList<>();

    @Getter
    private static Version latest = null;

    @Setter
    private static Function<IAPI, IExtension> injectedExtension;

    public static void start() {

        TrustManager.initialize();

        configRoot = initializeConfigRoot();

        config = loadConfig();

        HOMEPAGE = config.getProperty("homepage", DEFAULT_HOMEPAGE);
        SEARCH = config.getProperty("search", DEFAULT_SEARCH);
        SAVE_COOKIES = !config.getProperty("save_cookies", "true").equalsIgnoreCase("false");
        FONT = config.getProperty("font", "-");
        if(FONT.equalsIgnoreCase("-")) FONT = null;

        if (!ThemeManager.setTheme(config)) {
            ThemeManager.setTheme(FlatLightLaf.class);
        }

        ThemeManager.applyFontByConfig(config);

        if(SAVE_COOKIES) CookieManager.initialize(configRoot);

        mainAPI = new MainAPI(configRoot);

        InternalScreenRegistry registry = new InternalScreenRegistry(mainAPI);
        registry.init();
        registry.apply();

        updateListener.add((ver) -> latest = ver);

        checkUpdates();

        extmanager = new ExtensionManager();
        try {
            extmanager.loadExtensions(mainAPI, configRoot);

            if(injectedExtension != null){
                extmanager.loadExtension(mainAPI, injectedExtension);
            }

            extmanager.loadExtension(mainAPI, AdblockerExtension::new);
            extmanager.loadExtension(mainAPI, SettingsExtension::new);
        } catch (IOException e) {
            e.printStackTrace();
        }

        createWindowWithDefaultHomePage(mainAPI);

        extmanager.enableExtensions();

        storeConfig(config);

    }

    private static void checkUpdates() {
        if(!updater.checkJar()) return;
        if(updater.isUpdated()) return;
        if(updater.isDebugBuild()) return;

        Version latest = updater.getLatestVersion();
        boolean update = false;
        if(!latest.version().equalsIgnoreCase(Browser.VERSION)) update = true;
        if(!latest.build().equalsIgnoreCase(Browser.BUILD)) update = true;
        if(!latest.commit().equalsIgnoreCase(Browser.COMMIT_HASH)) update = true;

        if(update) {
            updateListener.forEach((listener) -> listener.accept(latest));
        }
    }

    public static void updateUI() {
        synchronized (windows) {
            windows.forEach(BrowserWindow::updateUI);
        }
        synchronized (frames) {
            frames.forEach(SwingUtilities::updateComponentTreeUI);
        }
    }

    private static Properties loadConfig() {
        File file = new File(configRoot, "tigxa.properties");
        if (file.exists()) {
            Properties prop = new Properties();
            try {
                FileReader reader = new FileReader(file, StandardCharsets.UTF_8);
                prop.load(reader);
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return prop;
        } else {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new Properties();
    }

    private static void storeConfig(Properties prop) {
        File file = new File(configRoot, "tigxa.properties");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8);
            prop.store(writer, "Tigxa Configuration File");
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createWindowWithDefaultHomePage(IAPI api) {
        BrowserWindow window = new BrowserWindow();
        window.initWindow(api, configRoot);
        window.newTab((String) null, true);
        if(latest != null) {
            window.newTab(api.getGUIManager().getScreenRegistry().getRegistredScreen(api.getNamespace(), "update"), true);
        }
    }

    private static File initializeConfigRoot() {
        File configRoot = new File(System.getProperty("user.home", "."));
        if (!configRoot.exists()) configRoot.mkdirs();
        return configRoot;
    }

    public static void saveConfig() {

        ThemeManager.saveConfig(config);

        config.setProperty("homepage", HOMEPAGE);
        config.setProperty("search", SEARCH);
        config.setProperty("save_cookies", SAVE_COOKIES ? "true" : "false");
        config.setProperty("font", FONT == null ? "-" : FONT);

        storeConfig(config);
    }
}
