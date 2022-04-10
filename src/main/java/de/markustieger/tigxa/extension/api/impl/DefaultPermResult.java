package de.markustieger.tigxa.extension.api.impl;

import de.markustieger.tigxa.extension.api.permission.IPermissionResult;
import de.markustieger.tigxa.extension.api.permission.Permission;

import java.util.List;

public class DefaultPermResult implements IPermissionResult {

    private final List<Permission> allowed, disallowed;

    public DefaultPermResult(List<Permission> allowed, List<Permission> disallowed) {
        this.allowed = allowed;
        this.disallowed = disallowed;
    }

    @Override
    public List<Permission> getAllowed() {
        return allowed;
    }

    @Override
    public List<Permission> getDisallowed() {
        return disallowed;
    }
}
