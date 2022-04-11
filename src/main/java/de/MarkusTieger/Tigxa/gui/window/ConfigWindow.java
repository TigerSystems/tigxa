package de.MarkusTieger.Tigxa.gui.window;

import com.formdev.flatlaf.FlatLightLaf;
import com.yubico.client.v2.VerificationResponse;
import de.MarkusTieger.Tigxa.Browser;
import de.MarkusTieger.Tigxa.gui.image.ImageLoader;
import de.MarkusTieger.Tigxa.gui.theme.Theme;
import de.MarkusTieger.Tigxa.gui.theme.ThemeCategory;
import de.MarkusTieger.Tigxa.gui.theme.ThemeManager;
import de.MarkusTieger.Tigxa.http.cookie.CookieManager;

import javax.swing.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;

public class ConfigWindow {

    public static void create() {
        ConfigWindow window = new ConfigWindow();
        window.init();
    }

    public void init() {
        BufferedImage image = ImageLoader.loadInternalImage("/res/gui/logo.png");

        JFrame frame = new JFrame();
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        if (image != null) frame.setIconImage(image);
        frame.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {
                List<JFrame> frames = Browser.getFrames();
                synchronized (frames) {
                    frames.add(frame);
                }
            }

            @Override
            public void windowClosing(WindowEvent e) {
                List<JFrame> frames = Browser.getFrames();
                synchronized (frames) {
                    frames.remove(frame);
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

        frame.setResizable(false);
        frame.setLayout(null);

        Class<?> current = ThemeManager.getTheme();
        ThemeCategory current_category = ThemeManager.getCategory(current);

        if (current_category == null || current == null) {
            current_category = ThemeCategory.FLATLAF;
            current = FlatLightLaf.class;
        }

        JComboBox<ThemeCategory> categoryCombo = new JComboBox<>();
        categoryCombo.setBounds(25, 25, 150, 25);

        for (ThemeCategory category : ThemeCategory.values()) {
            categoryCombo.addItem(category);
        }
        categoryCombo.setSelectedItem(current_category);

        frame.add(categoryCombo);

        JComboBox<Theme> themeCombo = new JComboBox<>();
        themeCombo.setBounds(25, 75, 150, 25);

        Theme selected = null;
        for (Map.Entry<String, Class<?>> e : ThemeManager.getThemesByCategory(current_category).entrySet()) {
            if (e.getValue() == current) {
                themeCombo.addItem(selected = new Theme(e.getKey(), e.getValue()));
            } else {
                themeCombo.addItem(new Theme(e.getKey(), e.getValue()));
            }
        }
        if (selected != null) themeCombo.setSelectedItem(selected);

        frame.add(themeCombo);


        categoryCombo.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent event) {
                themeCombo.removeAllItems();
                for (Map.Entry<String, Class<?>> e : ThemeManager.getThemesByCategory((ThemeCategory) categoryCombo.getSelectedItem()).entrySet()) {
                    themeCombo.addItem(new Theme(e.getKey(), e.getValue()));
                }
            }
        });

        JButton btn = new JButton("Apply");
        btn.setBounds(25, 125, 150, 25);
        btn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ThemeManager.setTheme(((Theme) themeCombo.getSelectedItem()).clazz());
                Browser.updateUI();
                Browser.saveConfig();
            }
        });
        frame.add(btn);

        JButton pwd = new JButton("Cookie-PWD");
        pwd.setBounds(25, 175, 150, 25);
        pwd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                new Thread(() -> {
                    char[] pwd = PasswordWindow.requestPWD("Cookie Password-Change", (value) -> {
                        return value;
                    });

                    if (pwd != null) {
                        if (pwd.length == 0) {
                            CookieManager.getDef().getStore().setPwd(null);
                        } else {
                            CookieManager.getDef().getStore().setPwd(pwd);
                        }
                    }

                    VerificationResponse yubi = PasswordWindow.requestYUBI("Cookie Yubi-Change", (value) -> {
                        return value;
                    });

                    if (yubi != null) {
                        if (yubi.isOk()) {
                            CookieManager.getDef().getStore().setYubi(yubi);
                        } else {
                            CookieManager.getDef().getStore().setYubi(null);
                        }
                    }

                    CookieManager.getDef().getStore().save();
                }, "Cookie-Change-Window-Thread").start();
            }
        });
        frame.add(pwd);

        frame.setVisible(true);

    }

}
