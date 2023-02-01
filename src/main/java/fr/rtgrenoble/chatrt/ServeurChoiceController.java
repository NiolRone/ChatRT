package fr.rtgrenoble.chatrt;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServeurChoiceController implements Initializable {
    public TextField serverTextField;
    public Button razButton;
    public Button doneButton;
    private static final String HOST_PORT_REGEX = "^([-.a-zA-Z0-9]+)(?::([0-9]{1,5}))?$";
    private final Pattern hostPortPattern = Pattern.compile(HOST_PORT_REGEX);



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        serverTextField.setTextFormatter(new TextFormatter<>(change -> {
            Matcher matcher = hostPortPattern.matcher(change.getControlNewText());
            return (matcher.matches() || matcher.hitEnd()) ? change : null;
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
        ResourceBundle resourceBundle = ResourceBundle.getBundle("fr.rtgrenoble.chatrt.i18nBundle");
        if (serverTextField.getText().endsWith(":")) {
            Alert alert = new Alert(Alert.AlertType.ERROR, resourceBundle.getString("key.ColonAlert"), ButtonType.OK);
            alert.showAndWait();
        } else {
        onClose();
        }
    }

    public String getServerText() {
        return serverTextField.getText();
    }

    public void onClose() {
        Stage stage = (Stage) serverTextField.getScene().getWindow();
        stage.close();
    }
}
