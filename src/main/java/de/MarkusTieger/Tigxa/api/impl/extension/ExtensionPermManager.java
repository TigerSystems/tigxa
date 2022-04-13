package de.MarkusTieger.Tigxa.api.impl.extension;

import de.MarkusTieger.Tigxa.api.impl.DefaultPermResult;
import de.MarkusTieger.Tigxa.api.permission.IPermissionManager;
import de.MarkusTieger.Tigxa.api.permission.IPermissionResult;
import de.MarkusTieger.Tigxa.api.permission.Permission;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExtensionPermManager implements IPermissionManager {

    private final List<Permission> perms;

    public ExtensionPermManager(Permission[] perms) {
        this.perms = Arrays.asList(perms);
    }

    public boolean hasPermission0(Permission perm) {
        return perms.contains(perm);
    }

    @Override
    public boolean hasPermission(Permission... permissions) {
        boolean value = true;
        for(Permission perm : permissions){
            value = hasPermission0(perm);
            if(!value) break;
        }
        return value;
    }

    @Override
    public IPermissionResult requestPermissions(List<Permission> permissions) {
        List<Permission> allowed = new ArrayList<>(),
                disallowed = new ArrayList<>();

        for (Permission perm : permissions) {
            if (hasPermission(perm)) {
                allowed.add(perm);
            } else {
                disallowed.add(perm);
            }
        }

        return new DefaultPermResult(allowed, disallowed);
    }
}
