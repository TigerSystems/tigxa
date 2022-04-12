package de.MarkusTieger.Tigxa.api.impl.main;

import de.MarkusTieger.Tigxa.api.impl.DefaultPermResult;
import de.MarkusTieger.Tigxa.api.permission.IPermissionManager;
import de.MarkusTieger.Tigxa.api.permission.IPermissionResult;
import de.MarkusTieger.Tigxa.api.permission.Permission;

import java.util.Collections;
import java.util.List;

public class MainPermManager implements IPermissionManager {

    @Override
    public boolean hasPermission(Permission perm) {
        return true;
    }

    @Override
    public IPermissionResult requestPermissions(List<Permission> permissions) {
        return new DefaultPermResult(permissions, Collections.emptyList());
    }
}
