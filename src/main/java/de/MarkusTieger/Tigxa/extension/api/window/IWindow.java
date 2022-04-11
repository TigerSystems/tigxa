package de.MarkusTieger.Tigxa.extension.api.window;

import java.util.List;

public interface IWindow {

    ITab add(String url, boolean autoselect);

    List<ITab> listTabs();

    void remove();

    boolean isActive();

    ITab getSelectedTab();

    ITab setSelectedTab(ITab tab);

}
