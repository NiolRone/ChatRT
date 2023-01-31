package fr.rtgrenoble.chatrt;

import fr.rtgrenoble.chatrt.net.ChatClient;
import fr.rtgrenoble.chatrt.net.ChatClientTCP;
import fr.rtgrenoble.chatrt.net.Message;
import fr.rtgrenoble.chatrt.status.Chronometre;
import fr.rtgrenoble.chatrt.status.Horloge;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import org.controlsfx.control.textfield.TextFields;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatClientController implements Initializable {
    public TextField hostPortTextField;
    public TextField nicknameTextField;
    public ToggleButton connectionButton;
    public TextField messageTextField;
    public Button sendButton;
    public ListView chatListView;
    public Label chronoLabel;
    public Label ClockLabel;
    private ChatClient chatClient;
    private static final String HOST_PORT_REGEX = "^([-.a-zA-Z0-9]+)(?::([0-9]{1,5}))?$";
    private final Pattern hostPortPattern = Pattern.compile(HOST_PORT_REGEX);
    private Image avatars;
    private ResourceBundle resourceBundle;

    public Chronometre chrono;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
        hostPortTextField.disableProperty().bind(connectionButton.selectedProperty());
        sendButton.disableProperty().bind(connectionButton.selectedProperty().not());

        hostPortTextField.setTextFormatter(new TextFormatter<>(change -> {
            Matcher matcher = hostPortPattern.matcher(change.getControlNewText());
            return (matcher.matches() || matcher.hitEnd()) ? change : null;
        }));

        connectionButton.setOnAction(this::handleConnection);
        sendButton.setOnAction(this::handleSendMessage);

        avatars = new Image(getClass().getResource("/img/avatars.png").toString());

        TextFields.bindAutoCompletion(hostPortTextField, "mock", "localhost", "127.0.0.1:2022");
        chatListView.setCellFactory(lv -> new MessageListCell());

        // Clock
        Horloge horloge = new Horloge(ClockLabel);
        Thread t = new Thread(horloge);
        t.start();

        // Clock stop thread
        Platform.runLater(() -> ClockLabel.getScene().getWindow().setOnCloseRequest(e -> horloge.stop()));

        // Status
        chronoLabel.setText(resourceBundle.getString("key.Disconnect"));

        // Deconnection if close window
        Platform.runLater(() -> ClockLabel.getScene().getWindow().setOnCloseRequest(e -> {
            if (chatClient != null && chatClient.isConnected()) {
                horloge.stop();
                disconnect(resourceBundle.getString("key.Disconnect"));
            }
        }));


    }

    private void handleConnection(ActionEvent actionEvent) {

        if (connectionButton.isSelected()) {
            Matcher matcher = hostPortPattern.matcher(hostPortTextField.getText());
            if (nicknameTextField.getText().isBlank()) {
                Alert alert = new Alert(Alert.AlertType.ERROR, resourceBundle.getString("key.NicknameAlert"), ButtonType.OK);
                connectionButton.setSelected(false);
                alert.showAndWait();
                return;
            }
            if (!matcher.matches()) {
                Alert alert = new Alert(Alert.AlertType.ERROR, resourceBundle.getString("key.HostAlert"), ButtonType.OK);
                connectionButton.setSelected(false);
                alert.showAndWait();
                return;
            }

            String host = matcher.group(1);
            int port = (matcher.group(2) != null) ? Integer.parseInt(matcher.group(2)) : 2022;
            System.out.printf("hôte: %s ; port: %d\n", host, port);
            chatClient = new ChatClientTCP();
            this.connect(host, port);

        } else {
            connectionButton.setText("Connexion");
            this.disconnect(resourceBundle.getString("key.Disconnect"));

        }


    }

    private void handleSendMessage(ActionEvent actionEvent) {
        if (messageTextField.getText().isBlank()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, resourceBundle.getString("key.MessageAlert"), ButtonType.OK);
            alert.showAndWait();
        } else {
            Message message = new Message(nicknameTextField.getText(), messageTextField.getText());
            this.appendMessage(message);
            try {
                chatClient.send(message);
            } catch (IOException e) {
                this.disconnect(resourceBundle.getString("key.Disconnect"));
            }
            messageTextField.clear();
        }
    }

    private void appendMessage(Message message) {
        Text datetimeText = new Text("\n%1$td/%1$tm/%1$tY %1$tH:%1$tM:%1$tS\n".formatted(new Date()));
        datetimeText.setFont(Font.font(null, FontPosture.ITALIC, 8));
        Text nicknameText = new Text(message.getFrom() + ": ");
        nicknameText.setFont(Font.font(null, FontWeight.BOLD, 14));
        nicknameText.setFill(Color.BLUEVIOLET);
        Text msgText = new Text(message.getText() + "\n");
        ImageView avatar = getAvatar(message.getFrom());
        chatListView.getItems().add(message);
    }

    private void connect(String host, int port) {
        try {
            chatClient.connect(host, port, this::handleReceiveMessage);
            this.appendMessage(new Message("system", resourceBundle.getString("key.Connection") +"%s:%d".formatted(host, port)));
            chrono = new Chronometre(chronoLabel, String.format("%s:%d", host, port));
            Thread t = new Thread(chrono);
            t.start();
        } catch (IOException e) {
            this.disconnect("Connexion à %s:%d impossible".formatted(host, port));
        }
    }

    private void handleReceiveMessage(Observable observable) {
        System.out.println("<= " + chatClient.getReceivedMessage());
        Platform.runLater(() -> this.appendMessage(chatClient.getReceivedMessage()));
    }

    private void disconnect(String infoMessage) {
        if (chatClient.isConnected()) {
            try {
                chrono.stop();
                chatClient.disconnect();
            } catch (IOException e) {}
        }
        appendMessage(new Message("system", infoMessage));
        connectionButton.setSelected(false);

    }

    private ImageView getAvatar(String nickname) {
        ImageView imageView = new ImageView(avatars);
        double imgWidth = avatars.getWidth() / 9;
        double imgHeight = avatars.getHeight();
        int n = Integer.remainderUnsigned(nickname.hashCode(), 9);
        imageView.setViewport(new Rectangle2D(imgWidth * n, 0, imgWidth, imgHeight));
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(32);
        return imageView;
    }


    private class MessageListCell extends ListCell<Message> {

        @Override
        protected void updateItem(Message item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
            } else {
                Text datetimeText = new Text("\n%1$td/%1$tm/%1$tY %1$tH:%1$tM:%1$tS\n".formatted(new Date()));
                datetimeText.setFont(Font.font(null, FontPosture.ITALIC, 8));
                Text nicknameText = new Text(item.getFrom() + ": ");
                nicknameText.setFill(Color.RED);
                nicknameText.setFont(Font.font(null, FontWeight.BOLD, 14));
                nicknameText.setFill(Color.BLUEVIOLET);
                Text msgText = new Text(item.getText());
                ImageView avatar = getAvatar(item.getFrom());
                TextFlow tf = new TextFlow(datetimeText, avatar, nicknameText, msgText);
                tf.maxWidthProperty().bind(getListView().widthProperty().multiply(0.8));
                HBox hBox = new HBox(tf);
                hBox.maxWidthProperty().bind(getListView().widthProperty());
                if (item.getFrom().equals(nicknameTextField.getText())) {
                    tf.setBackground(Background.fill(Color.web("#EEF")));
                    hBox.setAlignment(Pos.CENTER_RIGHT);
                } else {
                    tf.setBackground(Background.fill(Color.web("#FEE")));
                    hBox.setAlignment(Pos.CENTER_LEFT);
                }
                setGraphic(hBox);
                getListView().scrollTo(getListView().getItems().size() - 1);
            }
        }
    }
}
