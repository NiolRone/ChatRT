package fr.rtgrenoble.chatrt;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class ServeurChoiceController implements Initializable {
    public TextField serverTextField;
    public Button razButton;
    public Button doneButton;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        serverTextField.setTextFormatter(new TextFormatter<>(change -> {
            change.setText(change.getText().replaceAll("[^a-zA-Z0-9:]", ""));
            return change;
        }));

        doneButton.disableProperty().bind(serverTextField.textProperty().isEmpty());

        razButton.setOnAction(this::handleRaz);
        doneButton.setOnAction(this::handleDone);

        // KeyEvent
        serverTextField.setOnKeyPressed(keyEvent -> {
            switch (keyEvent.getCode()) {
                case ENTER:
                    handleDone(new ActionEvent());
                    break;
                case ESCAPE:
                    handleRaz(new ActionEvent());
                    break;
            }
        });
    }

    public ServeurChoiceController() {
    }

    private void handleRaz(ActionEvent actionEvent) {
        serverTextField.clear();
    }

    private void handleDone(ActionEvent actionEvent) {
        onClose();
    }

    public String getServerText() {
        return serverTextField.getText();
    }

    public void onClose() {
        Stage stage = (Stage) serverTextField.getScene().getWindow();
        stage.close();
    }
}
