package de.MarkusTieger.Tigxa.api.impl.extension;

import de.MarkusTieger.Tigxa.api.IAPI;
import de.MarkusTieger.Tigxa.api.action.IActionHandler;
import de.MarkusTieger.Tigxa.api.event.IEventManager;
import de.MarkusTieger.Tigxa.api.gui.IGUIManager;
import de.MarkusTieger.Tigxa.api.media.IMediaManager;
import de.MarkusTieger.Tigxa.api.permission.IPermissionManager;
import de.MarkusTieger.Tigxa.api.permission.Permission;
import de.MarkusTieger.Tigxa.api.web.IWebManager;
import de.MarkusTieger.Tigxa.api.window.IWindowManager;
import de.MarkusTieger.Tigxa.extension.IExtension;

import java.util.function.Supplier;

public class ExtensionAPI implements IAPI {

    private final Supplier<String> namespace;
    private final Supplier<IExtension> ext;
    private final IAPI parent;
    private final IPermissionManager permManager;
    private final IActionHandler action;
    private final IGUIManager gui;

    public ExtensionAPI(Supplier<String> namespace, Supplier<IExtension> ext, IAPI parent, Permission[] perms, IActionHandler action) {
        this.namespace = namespace;
        this.ext = ext;
        this.parent = parent;
        this.permManager = new ExtensionPermManager(perms);
        this.action = action;
        this.gui = new ExtensionGuiManager(this, parent);
    }

    @Override
    public IWindowManager getWindowManager() {
        if (!getPermissionManager().hasPermission(Permission.WINDOW))
            throw new RuntimeException("The Plugin doesn't have the Window permission!");
        return parent.getWindowManager();
    }

    @Override
    public IEventManager getEventManager() {
        if (!getPermissionManager().hasPermission(Permission.EVENTS))
            throw new RuntimeException("The Plugin doesn't have the Event permission!");
        return parent.getEventManager();
    }

    @Override
    public IWebManager getWebManager() {
        if (!getPermissionManager().hasPermission(Permission.WEB))
            throw new RuntimeException("The Plugin doesn't have the Web permission!");
        return parent.getWebManager();
    }

    @Override
    public IMediaManager getMediaManager() {
        if (!getPermissionManager().hasPermission(Permission.MEDIA))
            throw new RuntimeException("The Plugin doesn't have the Media permission!");
        return parent.getMediaManager();
    }

    @Override
    public IGUIManager getGUIManager() {
        if (!getPermissionManager().hasPermission(Permission.GUI))
            throw new RuntimeException("The Plugin doesn't have the Gui permission!");
        return gui;
    }

    @Override
    public IActionHandler getActionHandler() {
        return action;
    }

    @Override
    public IPermissionManager getPermissionManager() {
        return permManager;
    }

    @Override
    public IExtension getExtension() {
        return ext.get();
    }

    @Override
    public String getNamespace() {
        return namespace.get();
    }

}
