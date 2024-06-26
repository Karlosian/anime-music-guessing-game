module com.animeguessinggame.animeguessinggame {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;
    requires Mal4J;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;
    requires javafx.web;
    requires javafx.media;
    requires java.desktop;
    requires uk.co.caprica.vlcj;
    requires javafx.swing;
    requires uk.co.caprica.vlcj.javafx;

    opens com.animeguessinggame.animeguessinggame to com.fasterxml.jackson.databind, javafx.fxml;
    exports com.animeguessinggame.animeguessinggame;
}
