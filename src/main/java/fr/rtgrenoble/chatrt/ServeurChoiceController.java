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
    /**
     * Controller for the server choice window
     * @param url
     * @param resourceBundle
     */
    // Attributes
    public TextField serverTextField;
    public Button razButton;
    public Button doneButton;
    private static final String HOST_PORT_REGEX = "^([-.a-zA-Z0-9]+)(?::([0-9]{1,5}))?$";
    private final Pattern hostPortPattern = Pattern.compile(HOST_PORT_REGEX);



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        /**
         * Initialize the server choice window
         * @param url
         * @param resourceBundle
         */
        // TextFormatter
        // Check if the text is a valid host:port
        serverTextField.setTextFormatter(new TextFormatter<>(change -> {
            Matcher matcher = hostPortPattern.matcher(change.getControlNewText());
            return (matcher.matches() || matcher.hitEnd()) ? change : null;
        }));

        // Bindings
        doneButton.disableProperty().bind(serverTextField.textProperty().isEmpty());
        razButton.setOnAction(this::handleRaz);
        doneButton.setOnAction(this::handleDone);

        // KeyEvent handler
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
        /**
         * Clear the text field
         * @param actionEvent
         */
        serverTextField.clear();
    }

    private void handleDone(ActionEvent actionEvent) {
        /**
         * Close the window
         * @param actionEvent
         */
        ResourceBundle resourceBundle = ResourceBundle.getBundle("fr.rtgrenoble.chatrt.i18nBundle");
        if (serverTextField.getText().endsWith(":")) {
            Alert alert = new Alert(Alert.AlertType.ERROR, resourceBundle.getString("key.ColonAlert"), ButtonType.OK);
            alert.showAndWait();
        } else {
        onClose();
        }
    }

    public String getServerText() {
        /**
         * Get the text from the text field
         * @return
         */
        return serverTextField.getText();
    }

    public void onClose() {
        /**
         * Close the window
         */
        Stage stage = (Stage) serverTextField.getScene().getWindow();
        stage.close();
    }
}
