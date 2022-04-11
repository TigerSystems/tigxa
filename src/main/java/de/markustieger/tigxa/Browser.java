package de.markustieger.tigxa;

import com.formdev.flatlaf.FlatLightLaf;
import de.markustieger.tigxa.extension.ExtensionManager;
import de.markustieger.tigxa.extension.api.IAPI;
import de.markustieger.tigxa.extension.api.impl.main.MainAPI;
import de.markustieger.tigxa.extension.impl.internal.SettingsExtension;
import de.markustieger.tigxa.gui.theme.ThemeManager;
import de.markustieger.tigxa.gui.window.BrowserWindow;
import de.markustieger.tigxa.http.cookie.CookieManager;
import de.markustieger.tigxa.web.TrustManager;
import lombok.Getter;

import javax.swing.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

public class Browser {

    public static final String DEFAULT_HOMEPAGE = "https://google.com";
    public static final String NAME;
    public static final String FULL_NAME;
    public static final String VERSION;

    static {
        NAME = Browser.class.getPackage().getSpecificationTitle();
        FULL_NAME = NAME + " Browser";
        VERSION = Browser.class.getPackage().getSpecificationVersion();
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

    public static void start() {

        TrustManager.initialize();

        configRoot = initializeConfigRoot();

        config = loadConfig();

        if (!ThemeManager.setTheme(config)) {
            ThemeManager.setTheme(FlatLightLaf.class);
        }

        CookieManager.initialize(configRoot);

        mainAPI = new MainAPI(configRoot);

        extmanager = new ExtensionManager();
        try {
            extmanager.loadExtensions(mainAPI, configRoot);

            extmanager.loadExtension(mainAPI, SettingsExtension::new);
        } catch (IOException e) {
            e.printStackTrace();
        }

        createWindowWithDefaultHomePage(mainAPI);

        extmanager.enableExtensions();

        storeConfig(config);

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
        window.newTab(null, true);
    }

    private static File initializeConfigRoot() {
        File configRoot = new File(System.getProperty("user.home", "."));
        if (!configRoot.exists()) configRoot.mkdirs();
        return configRoot;
    }

    public static void saveConfig() {

        ThemeManager.saveConfig(config);

        storeConfig(config);
    }
}
