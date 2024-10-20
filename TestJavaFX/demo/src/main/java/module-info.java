module com.example.app {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;

    opens com.example.game to javafx.fxml;
    exports com.example.game;
}
