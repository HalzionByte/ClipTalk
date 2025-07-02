/* 
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class appmy extends Application {
    public void start(Stage mysstage) {
        StackPane root = new StackPane();

        Text text = new Text("Hello , this is test");
        text.setFont(Font.font("Arial", 12));
        text.setTranslateX(text.getTranslateX() + 100);
        Stage mystage = new Stage();
        root.getChildren().add(text);
        Scene scene = new Scene(root, 320, 240);
        mystage.setScene(scene);
        mystage.setTitle("Me");
        mystage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
} */