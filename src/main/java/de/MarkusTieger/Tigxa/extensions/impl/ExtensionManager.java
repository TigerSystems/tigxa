package de.MarkusTieger.Tigxa.extensions.impl;

import de.MarkusTieger.Tigxa.api.IAPI;
import de.MarkusTieger.Tigxa.api.action.IActionHandler;
import de.MarkusTieger.Tigxa.api.gui.IGUIWindow;
import de.MarkusTieger.Tigxa.api.impl.extension.ExtensionAPI;
import de.MarkusTieger.Tigxa.api.permission.Permission;
import de.MarkusTieger.Tigxa.extension.IExtension;
import de.MarkusTieger.Tigxa.extensions.impl.external.JavaScriptExtension;
import javafx.scene.web.WebEngine;
import netscape.javascript.JSObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class ExtensionManager {

    private static final String ITEM_SCRIPT = "var item = authors[index];";

    private final List<IExtension> extensionarray = new ArrayList<>();

    public void loadExtensions(IAPI api, File configRoot) throws IOException {

        File extensions = new File(configRoot, "extensions");
        if (!extensions.exists()) extensions.mkdirs();

        List<IExtension> localarray = new ArrayList<>();

        for (File ext : extensions.listFiles()) {

            if (ext.getName().toLowerCase().endsWith(".jar")) {
                loadJarExtension(ext);
            }

            if (ext.getName().toLowerCase().endsWith(".js")) {
                localarray.add(loadJSExtension(api, ext));
            }

        }

        extensionarray.addAll(localarray);

        localarray.forEach(IExtension::onLoad);


    }

    private void loadJarExtension(File ext) {
    }

    private IExtension loadJSExtension(IAPI parent, File ext) throws IOException {

        String script = null;
        FileInputStream in = new FileInputStream(ext);
        script = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        in.close();

        WebEngine engine = new WebEngine();
        engine.loadContent("<html><head><title>Tigxa Browser</title></head><body></body></html>");

        JSObject obj = (JSObject) engine.executeScript("window");

        obj.removeMember("name");

        obj.eval(script);

        Object name = obj.getMember("name");

        if (name == null) throw new IOException("Unknwon Name!");
        if (!(name instanceof String strname)) throw new IOException("Name can't load!");
        if (strname.isBlank()) throw new IOException("Name is blank!");
        if (strname.equals("undefined")) throw new IOException("Name is undefined!");

        Object version = obj.getMember("version");

        if ((version instanceof String strversion)) {
            if (strversion.isBlank()) version = null;
            if (strversion.equals("undefined")) version = null;
        } else version = null;

        List<String> authorarray = new ArrayList<>();
        Object authors = obj.getMember("authors");
        if ((authors instanceof String strauthors)) {
            authors = null;
        } else {
            if (authors instanceof JSObject js) {
                Object len_obj = js.getMember("length");
                if (len_obj instanceof Integer length) {
                    for (int i = 0; i < length.intValue(); i++) {
                        obj.setMember("index", i);
                        obj.eval(ITEM_SCRIPT);
                        Object item = obj.getMember("item");
                        if (item != null) {
                            if (item instanceof String author) {
                                authorarray.add(author);
                            }
                        }
                    }
                }
            }
        }

        Object icon = obj.getMember("icon");

        IActionHandler[] action = new IActionHandler[]{new IActionHandler() {
            @Override
            public void onAction(IGUIWindow window, String id) {
            }
        }};

        Supplier<IExtension>[] suparray = new Supplier[] {() -> null};
        Supplier<IExtension> sup = () -> suparray[0].get();
        ExtensionAPI api = new ExtensionAPI(sup, parent, new Permission[]{Permission.WINDOW, Permission.GUI}, new IActionHandler() {
            @Override
            public void onAction(IGUIWindow window, String id) {
                action[0].onAction(window, id);
            }
        });

        IExtension extension = new JavaScriptExtension(obj, api, (String) name, (String) version, authorarray.toArray(new String[0]), icon + "");
        action[0] = extension::onAction;
        suparray[0] = () -> extension;
        return extension;
    }


    public void enableExtensions() {
        extensionarray.forEach(IExtension::onEnable);
    }

    public void disableExtensions() {
        extensionarray.forEach(IExtension::onDisable);
    }

    public List<IExtension> getExtensions() {
        return Collections.unmodifiableList(extensionarray);
    }

    public IExtension loadExtension(IAPI parent, Function<IAPI, IExtension> constructor) {

        IActionHandler[] action = new IActionHandler[]{new IActionHandler() {
            @Override
            public void onAction(IGUIWindow window, String id) {
            }
        }};

        Supplier<IExtension>[] suparray = new Supplier[] {() -> null};
        Supplier<IExtension> sup = () -> suparray[0].get();
        ExtensionAPI api = new ExtensionAPI(sup, parent, new Permission[]{Permission.WINDOW, Permission.GUI}, new IActionHandler() {
            @Override
            public void onAction(IGUIWindow window, String id) {
                action[0].onAction(window, id);
            }
        });

        IExtension extension = constructor.apply(api);
        action[0] = extension::onAction;
        suparray[0] = () -> extension;
        extensionarray.add(extension);

        extension.onLoad();

        return extension;
    }
}
