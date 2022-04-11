package de.MarkusTieger.Tigxa.extension.api.permission;

import java.util.List;

public interface IPermissionResult {

    List<Permission> getAllowed();

    List<Permission> getDisallowed();

    default Permission[] getAllowedAsList() {
        return getAllowed().toArray(new Permission[0]);
    }

    default Permission[] getDisallowedAsList() {
        return getDisallowed().toArray(new Permission[0]);
    }

}
