package de.MarkusTieger.Tigxa.gui.screen;

import com.formdev.flatlaf.FlatLightLaf;
import com.yubico.client.v2.VerificationResponse;
import de.MarkusTieger.Tigxa.Browser;
import de.MarkusTieger.Tigxa.api.IAPI;
import de.MarkusTieger.Tigxa.api.gui.IScreen;
import de.MarkusTieger.Tigxa.api.gui.registry.IScreenRegistry;
import de.MarkusTieger.Tigxa.gui.theme.Theme;
import de.MarkusTieger.Tigxa.gui.theme.ThemeCategory;
import de.MarkusTieger.Tigxa.gui.theme.ThemeManager;
import de.MarkusTieger.Tigxa.gui.window.PasswordWindow;
import de.MarkusTieger.Tigxa.http.cookie.CookieManager;

import javax.speech.Central;
import javax.speech.synthesis.Synthesizer;
import javax.speech.synthesis.SynthesizerModeDesc;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class InternalScreenRegistry {

    private final HashMap<String, IScreen> map = new HashMap<>();
    private final IAPI api;

    private IScreen about_browser;
    private IScreen about;
    private IScreen settings;

    public InternalScreenRegistry(IAPI api){
        this.api = api;
    }

    public void init(){

        initAboutTigxa();
        initSettings();

    }

    public void apply(){
        api.getGUIManager().getScreenRegistry().registerScreen(about_browser, "about");
        api.getGUIManager().getScreenRegistry().registerScreen(settings, "settings");
    }

    private void initAboutTigxa(){

        about_browser = api.getGUIManager().createScreen("About", api.getNamespace() + "://about");

        about_browser.getContentPane().setLayout(null);

        JLabel label = new JLabel() {

            private boolean rendered = false;

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                if(rendered) return;
                rendered = true;
                new Thread(this::start, "Starter").start();
            }

            private void start(){
                try
                {

                    InputStream in = Browser.class.getResourceAsStream("/res/gui/about/text");
                    String text = new String(in.readAllBytes(), StandardCharsets.UTF_8);
                    in.close();

                    System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us" + ".cmu_us_kal.KevinVoiceDirectory");
                    Central.registerEngineCentral("com.sun.speech.freetts" + ".jsapi.FreeTTSEngineCentral");
                    Synthesizer synthesizer = Central.createSynthesizer(new SynthesizerModeDesc(Locale.US));
                    synthesizer.allocate();
                    synthesizer.resume();

                    while (true){
                        synthesizer.speakPlainText(text, null);
                        synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);
                    }

                    // synthesizer.deallocate();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        };
        label.setBounds(0, 0, 20, 20);
        about_browser.getContentPane().add(label);
    }

    private void initSettings() {
        settings = api.getGUIManager().createScreen("Settings", api.getNamespace() + "://settings");

        JPanel frame = settings.getContentPane();
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
    }

}
