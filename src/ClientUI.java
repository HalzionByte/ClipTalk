package src;

import java.io.*;
import java.net.Socket;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.Popup;

public class ClientUI extends Application {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private ScrollPane scrollPane;
    private VBox messageArea;
    private BorderPane root;
    private Scene scene;
    private String displayName;
    private TextField messageField;
    private HBox inputArea;
    private Button emojiButton;
    private Button sendButton;
    private MenuButton optionsButton;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("AZURA - ClipTalk");

        TextInputDialog nameDialog = new TextInputDialog();
        nameDialog.setTitle("Enter Display Name");
        nameDialog.setHeaderText("Welcome to ClipTalk brought to you by Azura!");
        nameDialog.setContentText("Enter your name:");
        nameDialog.showAndWait().ifPresent(name -> displayName = name);

        root = new BorderPane();
        root.getStyleClass().add("root-pane");

        // Header top bar
        HBox header = new HBox();
        header.setId("header");
        header.setPadding(new Insets(10));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(10);

        Label nameLabel = new Label(displayName);
        nameLabel.setId("nameLabel");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        optionsButton = new MenuButton("☰");
        optionsButton.setId("optionsMenu");

        MenuItem darkMode = new MenuItem("Dark Theme");
        MenuItem lightMode = new MenuItem("Light Theme");
        MenuItem defaultbt = new MenuItem("Default Theme");
        MenuItem logout = new MenuItem("Logout");

        defaultbt.setOnAction(e -> switchTheme("style1.css"));
        darkMode.setOnAction(e -> switchTheme("dark.css"));
        lightMode.setOnAction(e -> switchTheme("light.css"));
        logout.setOnAction(e -> {
    try {
        if (socket != null) socket.close();
    } catch (IOException ex) {
        ex.printStackTrace();
    }
    Platform.exit();
});
        optionsButton.getItems().addAll(darkMode, lightMode, defaultbt, logout);

        header.getChildren().addAll(nameLabel, spacer, optionsButton);
        root.setTop(header);

        // Message area where all messages show
        messageArea = new VBox(10);
        messageArea.setId("messageArea");
        messageArea.setPadding(new Insets(10));

        scrollPane = new ScrollPane(messageArea);
        scrollPane.setId("scrollPane");
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");
        messageArea.setStyle("-fx-background-color: transparent;");
        scrollPane.setFitToHeight(true);
        root.setCenter(scrollPane);

        // Input area for messages
        inputArea = new HBox(10);
        inputArea.setId("inputArea");
        inputArea.setPadding(new Insets(10));
        inputArea.setAlignment(Pos.CENTER_LEFT);

        emojiButton = new Button("😊");
        emojiButton.setId("emojiButton");
        emojiButton.setOnAction(e -> showEmojiPicker());


        messageField = new TextField();
        messageField.setId("messageField");
        messageField.setPromptText("Type a message...");
        HBox.setHgrow(messageField, Priority.ALWAYS);

        sendButton = new Button("➤");
        sendButton.setId("sendButton");

        inputArea.getChildren().addAll(emojiButton, messageField, sendButton);
        root.setBottom(inputArea);

        // Scene & CSS front/ styling
        StackPane stackPane = new StackPane(root);
        scene = new Scene(stackPane, 400, 600);
        String cssPath = new File("C:\\Users\\user\\OneDrive\\Desktop\\p2p chat\\src\\style1.css").toURI().toString();
        scene.getStylesheets().add(cssPath);

        primaryStage.setScene(scene);
        primaryStage.show();

        connectToServer();
        setupListeners();
    }
    private void switchTheme(String themeFile) {
    // Clear all existing stylesheets except primary style1.css
    scene.getStylesheets().clear();
    String baseCss = new File("C:\\Users\\user\\OneDrive\\Desktop\\p2p chat\\src\\style1.css").toURI().toString();
    scene.getStylesheets().add(baseCss);

    // Add the selected theme CSS file
    String themeCss = new File("C:\\Users\\user\\OneDrive\\Desktop\\p2p chat\\src\\" + themeFile).toURI().toString();
    scene.getStylesheets().add(themeCss);
} 
private void showEmojiPicker() {
    // Create popup window
    Popup emojiPopup = new Popup();
    emojiPopup.setAutoHide(true);

    //container for emoji labels
    HBox emojiBar = new HBox(5);
    emojiBar.setPadding(new Insets(8));
    emojiBar.setStyle("-fx-background-color: rgb(255, 253, 253); -fx-border-color: #ccc; -fx-background-radius: 8; -fx-border-radius: 8;");

    // Labels and their emoji mappings
    String[][] emojiMap = {
        {"laughing", "😂"},
        {"heart_eyes", "😍"},
        {"heart", "❤️"},
        {"thumbsup", "👍"},
        {"angry", "😡"},
        {"thinking", "🤔"},
        {"confetti", "🎉"}
    };


    Font emojiFont = Font.font("Segoe UI Emoji", 26);

    for (String[] pair : emojiMap) {
        String label = pair[0];
        String actualEmoji = pair[1];

        Button emojiBtn = new Button(label);
        emojiBtn.setFont(Font.font(14));
        emojiBtn.setPrefSize(70, 42);
        emojiBtn.setStyle("-fx-background-color: white; -fx-border-color: #ccc; -fx-border-radius: 5;");

        // Button Action: insert real emoji into message field, and then close popup
        emojiBtn.setOnAction(e -> {
            messageField.appendText(actualEmoji);
            emojiPopup.hide();
        });
 
        emojiBar.getChildren().add(emojiBtn);
    }

    // Add emoji bar to popup
    emojiPopup.getContent().add(emojiBar);

    // Show popup just below emojiButton
    double x = emojiButton.localToScreen(0, 0).getX();
    double y = emojiButton.localToScreen(0, 0).getY() + emojiButton.getHeight();
    emojiPopup.show(emojiButton, x, y);
}

    private void connectToServer() {
        try {
            socket = new Socket("localhost", 12345);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println(displayName);

            new Thread(() -> {
                try {
                    String line;
                    while ((line = in.readLine()) != null) {
                        String finalLine = line;
                        Platform.runLater(() -> displayMessage(finalLine));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupListeners() {
        sendButton.setOnAction(e -> sendMessage());
        messageField.setOnAction(e -> sendMessage());
    }

    private void sendMessage() {
        String text = messageField.getText();
        if (!text.trim().isEmpty()) {
            out.println(text);
            messageField.clear();
        }
    }

    private void displayMessage(String text) {
        Label messageLabel = new Label(text);
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(300);

        HBox messageContainer = new HBox(messageLabel);

        if (text.startsWith(displayName)) {
            messageLabel.getStyleClass().add("self-message");
            messageContainer.setAlignment(Pos.CENTER_RIGHT);
        } else if (text.startsWith("Server:")) {
            messageLabel.getStyleClass().add("server-message");
            messageContainer.setAlignment(Pos.CENTER);
        } else {
            messageLabel.getStyleClass().add("received-message");
            messageContainer.setAlignment(Pos.CENTER_LEFT);
        }

        messageArea.getChildren().add(messageContainer);

        Platform.runLater(() -> {
    scrollPane.layout();
    scrollPane.setVvalue(1.1);
});
    }

    public static void main(String[] args) {
        launch(args);
    }
}