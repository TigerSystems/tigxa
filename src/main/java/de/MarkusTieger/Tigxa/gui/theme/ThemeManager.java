package de.MarkusTieger.Tigxa.gui.theme;

import com.formdev.flatlaf.*;
import com.formdev.flatlaf.intellijthemes.FlatAllIJThemes;
import de.MarkusTieger.Tigxa.Browser;
import de.MarkusTieger.Tigxa.gui.screen.InternalScreenRegistry;
import de.MarkusTieger.Tigxa.gui.screen.settings.SettingsScreen;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ThemeManager {

    private static Class<?> theme;

    public static Map<ThemeCategory, Map<String, Class<?>>> getThemes() {
        Map<ThemeCategory, Map<String, Class<?>>> map = new HashMap<>();

        FlatAllIJThemes.FlatIJLookAndFeelInfo[] themes = FlatAllIJThemes.INFOS;

        Map<String, Class<?>> intellij = new HashMap<>();
        for (FlatAllIJThemes.FlatIJLookAndFeelInfo info : themes) {
            try {
                intellij.put(info.getName(), Class.forName(info.getClassName()));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                System.out.println(info.getClassName() + " can't load! (IntelliJ)");
            }
        }
        map.put(ThemeCategory.INTELLIJ, intellij);

        Map<String, Class<?>> flatlaf = new HashMap<>();

        flatlaf.put("Light", FlatLightLaf.class);
        flatlaf.put("Dark", FlatDarkLaf.class);
        flatlaf.put("IntelliJ", FlatIntelliJLaf.class);
        flatlaf.put("Darcula", FlatDarculaLaf.class);

        map.put(ThemeCategory.FLATLAF, flatlaf);

        return map;
    }

    public static boolean setTheme(Class<?> theme) {
        try {
            Method method = theme.getDeclaredMethod("setup");

            method.invoke(null);
            ThemeManager.theme = theme;
            updateAccent();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void setUIFont (Font f){
        java.util.Enumeration keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get (key);
            if (value instanceof javax.swing.plaf.FontUIResource v)
            {
                UIManager.put (key, new FontUIResource(f.deriveFont((float) v.getSize())));
            }
        }
    }

    public static void applyFontByConfig(Properties prop){

        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();

        Map<String, SettingsScreen.FontItem> map = new HashMap<>();

        for(Font f : env.getAllFonts()){
            SettingsScreen.FontItem fi = new SettingsScreen.FontItem(f);
            map.put(f.getName(), fi);
        }

        if(Browser.FONT != null){
            SettingsScreen.FontItem i = map.get(Browser.FONT);
            if(i != null) {
                if(i.getFont() != null) setUIFont(i.getFont());
            }
        }
    }

    public static boolean setTheme(Properties prop) {
        String classname = prop.getProperty(ThemeManager.class.getName() + ".theme");
        if (classname == null) {
            prop.setProperty(ThemeManager.class.getName() + ".theme", classname = FlatLightLaf.class.getName());
        }
        try {
            Class<?> clazz = Class.forName(classname);
            return setTheme(clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Class<?> getTheme() {
        return theme;
    }

    public static ThemeCategory getCategory(Class<?> current) {
        Map<ThemeCategory, Map<String, Class<?>>> themes = getThemes();
        for (Map.Entry<ThemeCategory, Map<String, Class<?>>> e : themes.entrySet()) {
            if (e.getValue().containsValue(current)) return e.getKey();
        }
        return null;
    }

    public static Map<String, Class<?>> getThemesByCategory(ThemeCategory category) {
        return getThemes().get(category);
    }

    public static void saveConfig(Properties config) {
        if(theme != null) config.setProperty(ThemeManager.class.getName() + ".theme", theme.getName());
    }

    public static boolean isDark() {
        Class<?> theme = getTheme();
        try {
            if (theme.getSuperclass() == FlatLaf.class) {
                Method m = theme.getDeclaredMethod("isDark");
                Object instance = theme.getDeclaredConstructor().newInstance();
                return m.invoke(instance) == Boolean.TRUE;
            }
            if (theme.getSuperclass() == IntelliJTheme.ThemeLaf.class) {
                FlatAllIJThemes.FlatIJLookAndFeelInfo[] themes = FlatAllIJThemes.INFOS;

                for (FlatAllIJThemes.FlatIJLookAndFeelInfo info : themes) {
                    if (info.getClassName().equalsIgnoreCase(theme.getName())) {
                        return info.isDark();
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    private static Color accent = Color.BLACK;

    public static void updateAccent(){
        JProgressBar bar = new JProgressBar();
        bar.setMinimum(0);
        bar.setMaximum(1);
        bar.setValue(1);
        bar.setSize(25, 25);

        BufferedImage img = new BufferedImage(25, 25, BufferedImage.TYPE_INT_ARGB);
        bar.paint(img.getGraphics());
        accent = new Color(img.getRGB(10, 10));
    }

    public static Color getAccentColor() {
        return accent;
    }
}
