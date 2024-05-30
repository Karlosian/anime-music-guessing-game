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

    opens com.animeguessinggame.animeguessinggame to javafx.fxml;
    exports com.animeguessinggame.animeguessinggame;
}