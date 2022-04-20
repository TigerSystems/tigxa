open module de.MarkusTieger.Tigxa {

    // Logging
    requires log4j;

    // Technical
    requires jdk.jsobject;

    requires com.google.gson;
    requires static lombok;
    // requires java.discord.rpc;

    // Web
    requires swt.all;

    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.swing;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;

    // GUI

    requires java.desktop;

    requires com.formdev.flatlaf;
    requires com.formdev.flatlaf.intellijthemes;

    requires yubico.validation.client2;

    // API
    requires tigxa.api;
    requires tigxa.events;

    // Media
    requires uk.co.caprica.vlcj;
    requires vlcj.natives;
    requires uk.co.caprica.vlcj.javafx;

    // Music
    requires freetts;
    requires jedit;
    requires com.sun.jna;

    exports de.MarkusTieger.Tigxa;
}