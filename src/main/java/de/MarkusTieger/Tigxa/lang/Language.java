package de.MarkusTieger.Tigxa.lang;

import java.util.Properties;

public record Language(Properties properties) {

    @Override
    public String toString() {
        return properties.getProperty("name", "Unknown");
    }
}
