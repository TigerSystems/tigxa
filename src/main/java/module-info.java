open module de.MarkusTieger.Tigxa {

    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.swing;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;

    requires java.desktop;

    requires com.formdev.flatlaf;
    requires com.formdev.flatlaf.intellijthemes;
    requires jdk.jsobject;

    requires com.google.gson;
    requires static lombok;

    requires yubico.validation.client2;


    requires tigxa.api;
    requires tigxa.events;
    requires freetts;

    exports de.MarkusTieger.Tigxa;
}