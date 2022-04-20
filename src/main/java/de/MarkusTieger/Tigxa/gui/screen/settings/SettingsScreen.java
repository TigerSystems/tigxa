package de.MarkusTieger.Tigxa.gui.screen.settings;

import com.formdev.flatlaf.FlatLightLaf;
import com.yubico.client.v2.VerificationResponse;
import de.MarkusTieger.Tigxa.Browser;
import de.MarkusTieger.Tigxa.api.IAPI;
import de.MarkusTieger.Tigxa.api.gui.IScreen;
import de.MarkusTieger.Tigxa.gui.screen.settings.tree.TreeEntry;
import de.MarkusTieger.Tigxa.gui.screen.settings.tree.TreeFolder;
import de.MarkusTieger.Tigxa.gui.theme.Theme;
import de.MarkusTieger.Tigxa.gui.theme.ThemeCategory;
import de.MarkusTieger.Tigxa.gui.theme.ThemeManager;
import de.MarkusTieger.Tigxa.gui.window.PasswordWindow;
import de.MarkusTieger.Tigxa.http.cookie.CookieManager;
import de.MarkusTieger.Tigxa.lang.Translator;
import lombok.Getter;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@Getter
public class SettingsScreen {

    private static IScreen screen;
    private static final String ID = "settings";

    public static void initialize(IAPI api) {
        screen = api.getGUIManager().createScreen(Translator.translate(31), api.getNamespace() + "://" + ID);

        JPanel main = screen.getContentPane();

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT) {

            @Override
            public Dimension getPreferredSize() {
                return main.getSize();
            }

            @Override
            public Dimension getMinimumSize() {
                return getPreferredSize();
            }

            @Override
            public Dimension getMaximumSize() {
                return getPreferredSize();
            }
        };

        JTree tree = new JTree() {

            @Override
            public Dimension getPreferredSize() {
                Dimension max = getMaximumSize();
                max.width = 500;
                return max;
            }

            @Override
            public Dimension getMinimumSize() {
                Dimension max = getMaximumSize();
                max.width = 300;
                return max;
            }

            @Override
            public Dimension getMaximumSize() {
                return main.getSize();
            }

        };
        split.setLeftComponent(tree);

        JPanel content = new JPanel() {

            @Override
            public Dimension getPreferredSize() {
                Dimension max = getMaximumSize();
                max.width = max.width - 500;
                return max;
            }

            @Override
            public Dimension getMinimumSize() {
                Dimension max = getMaximumSize();
                max.width = 300;
                return max;
            }

            @Override
            public Dimension getMaximumSize() {
                return main.getSize();
            }

        };
        split.setRightComponent(content);

        DefaultTreeModel model = buildModel(tree, split::setRightComponent, main);

        tree.setModel(model);

        main.add(split);
    }

    private static DefaultTreeModel buildModel(JTree tree, Consumer<JPanel> c, JPanel main) {
        TreeFolder root = new TreeFolder(Browser.NAME, null);
        DefaultTreeModel model = new DefaultTreeModel(root);

        TreeFolder general = new TreeFolder("General", root);
        root.getNodes().add(general);

        TreeEntry apperance = new TreeEntry("Apperance", general, buildApperancePanel(main));
        general.getNodes().add(apperance);

        TreeFolder web = new TreeFolder("Web", root);
        root.getNodes().add(web);

        TreeEntry homepage = new TreeEntry("HomePage", web, buildHomePage(main));
        web.getNodes().add(homepage);

        TreeEntry cookies = new TreeEntry("Cookies", web, buildCookiesPanel(main));
        web.getNodes().add(cookies);

        tree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                Object obj = tree.getLastSelectedPathComponent();
                if(obj != null){
                    if(obj instanceof TreeEntry te){
                        c.accept(te.getPanel());
                    }
                }
            }
        });

        return model;
    }

    private static JPanel buildCookiesPanel(JPanel main) {

        JPanel frame = new JPanel() {

            @Override
            public Dimension getPreferredSize() {
                Dimension max = getMaximumSize();
                max.width = max.width - 500;
                return max;
            }

            @Override
            public Dimension getMinimumSize() {
                Dimension max = getMaximumSize();
                max.width = 300;
                return max;
            }

            @Override
            public Dimension getMaximumSize() {
                return main.getSize();
            }

        };
        frame.setLayout(null);



        JButton pwd = new JButton(Translator.translate(33));
        pwd.setEnabled(Browser.SAVE_COOKIES);
        pwd.setBounds(25, 175 - 150, 150, 25);
        pwd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                new Thread(() -> {
                    char[] pwd = PasswordWindow.requestPWD(Translator.translate(34), (value) -> {
                        return value;
                    });

                    if (pwd != null) {
                        if (pwd.length == 0) {
                            CookieManager.getDef().getStore().setPwd(null);
                        } else {
                            CookieManager.getDef().getStore().setPwd(pwd);
                        }
                    }

                    VerificationResponse yubi = PasswordWindow.requestYUBI(Translator.translate(35), (value) -> {
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

        JCheckBox saveCookies = new JCheckBox(Translator.translate(36));
        saveCookies.setBounds(25, 225 - 150, 150, 25);
        saveCookies.setSelected(Browser.SAVE_COOKIES);
        saveCookies.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                Browser.SAVE_COOKIES = saveCookies.isSelected();
                Browser.saveConfig();
            }
        });
        frame.add(saveCookies);

        JButton erease = new JButton(Translator.translate(37));
        erease.setBounds(25, 275 - 150, 150, 25);
        erease.addActionListener((e) -> {

            CookieManager.getDef().getStore().erease();

        });
        frame.add(erease);


        return frame;
    }

    private static JPanel buildHomePage(JPanel main){

        JPanel frame = new JPanel() {

            @Override
            public Dimension getPreferredSize() {
                Dimension max = getMaximumSize();
                max.width = max.width - 500;
                return max;
            }

            @Override
            public Dimension getMinimumSize() {
                Dimension max = getMaximumSize();
                max.width = 300;
                return max;
            }

            @Override
            public Dimension getMaximumSize() {
                return main.getSize();
            }

        };
        frame.setLayout(null);


        JTextField homepage = new JTextField();
        homepage.setText(Browser.HOMEPAGE);
        homepage.setBounds(25, 325 - 300, 200, 25);
        homepage.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                update();
            }

            private void update(){
                Browser.HOMEPAGE = homepage.getText();
                Browser.saveConfig();
            }
        });
        frame.add(homepage);


        JTextField search = new JTextField();
        search.setText(Browser.SEARCH);
        search.setBounds(25, 375 - 300, 200, 25);
        search.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                update();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                update();
            }

            private void update(){
                Browser.SEARCH = search.getText();
                Browser.saveConfig();
            }
        });
        frame.add(search);


        return frame;
    }

    private static JPanel buildApperancePanel(JPanel main) {

        JPanel frame = new JPanel() {

            @Override
            public Dimension getPreferredSize() {
                Dimension max = getMaximumSize();
                max.width = max.width - 500;
                return max;
            }

            @Override
            public Dimension getMinimumSize() {
                Dimension max = getMaximumSize();
                max.width = 300;
                return max;
            }

            @Override
            public Dimension getMaximumSize() {
                return main.getSize();
            }

        };
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

        JButton btn = new JButton(Translator.translate(32));
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



        FontItem default_font = new FontItem(null);

        JComboBox<FontItem> item = new JComboBox<FontItem>();
        item.setBounds(25, 425 - 250, 150, 25);
        item.addItem(default_font);

        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();

        Map<String, FontItem> map = new HashMap<>();

        for(Font f : env.getAllFonts()){
            FontItem fi = new FontItem(f);
            item.addItem(fi);
            map.put(f.getName(), fi);
        }

        item.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(((FontItem)item.getSelectedItem()).font == null) {
                    Browser.FONT = null;
                    Browser.saveConfig();
                    return;
                }
                for(Map.Entry<String, FontItem> i : map.entrySet()){
                    if(i.getValue() == item.getSelectedItem()){
                        Browser.FONT = i.getKey();
                        Browser.saveConfig();
                    }
                }
            }
        });

        if(Browser.FONT != null){
            FontItem i = map.get(Browser.FONT);
            if(i != null) {
                item.setSelectedItem(i);
            }
        }

        frame.add(item);









        return frame;

    }

    public static class FontItem {

        @Getter
        private final Font font;

        public FontItem(Font font){
            this.font = font;
        }

        @Override
        public String toString() {
            return font == null ? Translator.translate(38) : font.getName();
        }
    }

    public static void register(IAPI api) {
        api.getGUIManager().getScreenRegistry().registerScreen(screen, ID);
    }

}
