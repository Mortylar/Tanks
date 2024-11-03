module edu.school21.app {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;

    opens edu.school21.game to javafx.fxml;
    exports edu.school21.game;

    opens edu.school21.view to javafx.fxml;
    exports edu.school21.view;

    opens edu.school21.controllers to javafx.fxml;
    exports edu.school21.controllers;

    // opens edu.school21.app to javafx.fxml;
    exports edu.school21.app to javafx.graphics;
}
