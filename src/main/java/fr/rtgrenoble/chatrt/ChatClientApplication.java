package fr.rtgrenoble.chatrt;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class ChatClientApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        ResourceBundle i18nBundle = ResourceBundle.getBundle("fr.rtgrenoble.chatrt.i18nBundle", Locale.getDefault());
        FXMLLoader fxmlLoader = new FXMLLoader(ChatClientApplication.class.getResource("chatclient-view.fxml"), i18nBundle);
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("ChatRT");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}