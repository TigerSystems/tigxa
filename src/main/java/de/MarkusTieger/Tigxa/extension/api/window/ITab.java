package de.MarkusTieger.Tigxa.extension.api.window;

import javax.swing.*;

public interface ITab {

    String getTitle();

    Icon getIcon();

    boolean isActive();

    void remove();

    void setZoom(double factor);

    void print();
}
