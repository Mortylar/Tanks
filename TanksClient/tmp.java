import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Test extends Application {

    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) {

        VBox root = new VBox(5);
        HBox vhidDann = new HBox(5);
        root.getChildren().add(vhidDann);

        Label lbl = new Label("Total Error");
        Label lblOut = new Label("Out");
        ProgressBar bar = new ProgressBar();

        Button btn = new Button("Train");
        btn.setOnAction(act -> { trainNetwork(lblOut); });

        root.getChildren().addAll(lbl, btn, lblOut);
        Scene scene = new Scene(root, 450, 250);

        primaryStage.setTitle("Hello World!");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void trainNetwork(Label label) {
        Task task = new Task<Void>() {
            @Override
            public Void call() throws InterruptedException {
                final int max = 1000;
                return null;
            }
        };

        label.textProperty().bind(task.messageProperty());
        new Thread(task).start();
    }
}
