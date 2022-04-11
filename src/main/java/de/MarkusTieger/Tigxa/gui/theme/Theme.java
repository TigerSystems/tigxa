package de.MarkusTieger.Tigxa.gui.theme;

public record Theme(String name, Class<?> clazz) {

    @Override
    public Class<?> clazz() {
        return clazz;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
}
