package de.markustieger.tigxa.extension.api.impl.plugin;

import de.markustieger.tigxa.extension.api.impl.DefaultPermResult;
import de.markustieger.tigxa.extension.api.permission.IPermissionManager;
import de.markustieger.tigxa.extension.api.permission.IPermissionResult;
import de.markustieger.tigxa.extension.api.permission.Permission;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PluginPermManager implements IPermissionManager {

    private final List<Permission> perms;

    public PluginPermManager(Permission[] perms) {
        this.perms = Arrays.asList(perms);
    }

    @Override
    public boolean hasPermission(Permission perm) {
        return perms.contains(perm);
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
