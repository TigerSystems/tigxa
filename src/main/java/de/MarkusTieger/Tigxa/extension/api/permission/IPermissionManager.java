package de.MarkusTieger.Tigxa.extension.api.permission;

import netscape.javascript.JSObject;

import java.util.ArrayList;
import java.util.List;

public interface IPermissionManager {

    boolean hasPermission(Permission perm);

    default Permission getPermissionByName(String name) {
        return Permission.valueOf(name);
    }

    default Permission[] getPermissions() {
        return Permission.values();
    }

    IPermissionResult requestPermissions(List<Permission> permissions);

    default IPermissionResult requestPermissions(JSObject js) {

        List<Permission> array = new ArrayList<>();


        Object len_obj = js.getMember("length");
        if (len_obj instanceof Integer length) {
            for (int i = 0; i < length.intValue(); i++) {
                js.setMember("index", i);
                js.eval("var item = this[index];");
                Object item = js.getMember("item");
                if (item != null) {
                    if (item instanceof Permission perm) {
                        array.add(perm);
                    }
                }
            }
        }

        return requestPermissions(array);

    }

}
