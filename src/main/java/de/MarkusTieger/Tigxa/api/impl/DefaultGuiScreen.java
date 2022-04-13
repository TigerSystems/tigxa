package de.MarkusTieger.Tigxa.api.impl;

import de.MarkusTieger.Tigxa.api.gui.IScreen;
import lombok.Setter;

import javax.swing.*;
import java.util.function.Supplier;

public class DefaultGuiScreen implements IScreen {

    @Setter
    private Supplier<String> titleSupplier, locationSupplier;
    private final JPanel content = new JPanel();

    public DefaultGuiScreen(String title, String location) {
        titleSupplier = () -> title;
        locationSupplier = () -> location;
    }

    @Override
    public String getTitle() {
        return titleSupplier.get();
    }

    @Override
    public String getLocation() {
        return locationSupplier.get();
    }

    @Override
    public JPanel getContentPane() {
        return content;
    }
}
