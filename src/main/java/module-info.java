module com.animeguessinggame.animeguessinggame {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;

    opens com.animeguessinggame.animeguessinggame to javafx.fxml;
    exports com.animeguessinggame.animeguessinggame;
}