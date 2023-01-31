package fr.rtgrenoble.chatrt;

import fr.rtgrenoble.chatrt.net.ChatClient;
import fr.rtgrenoble.chatrt.net.ChatClientTCP;
import fr.rtgrenoble.chatrt.net.Message;
import fr.rtgrenoble.chatrt.status.Chronometre;
import fr.rtgrenoble.chatrt.status.Horloge;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class ChatClientController implements Initializable {
    public TextField nicknameTextField;
    public ToggleButton connectionButton;
    public TextField messageTextField;
    public Button sendButton;
    public ListView chatListView;
    public Label chronoLabel;
    public Label clockLabel;
    public ComboBox serverComboBox;
    public MenuItem closeMenu;
    public MenuItem addServer;
    private ChatClient chatClient;
    private Image avatars;
    private ResourceBundle resourceBundle;
    public Chronometre chrono;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
        avatars = new Image(getClass().getResource("/img/avatars.png").toString());

        sendButton.disableProperty().bind(connectionButton.selectedProperty().not());

        sendButton.setOnAction(this::handleSendMessage);
        connectionButton.setOnAction(this::handleConnection);
        chatListView.setCellFactory(lv -> new MessageListCell());

        // Clock
        Horloge horloge = new Horloge(clockLabel);
        Thread t = new Thread(horloge);
        t.start();
        Platform.runLater(() -> clockLabel.getScene().getWindow().setOnCloseRequest(e -> horloge.stop()));

        // Status
        chronoLabel.setText(resourceBundle.getString("key.Disconnect"));

        // CloseMenu
        closeMenu.setOnAction(e -> {
            horloge.stop();
            if (chrono != null){
            chrono.stop();
            }
            Platform.exit();
        });

        //AddServer open serverchoice
        addServer.setOnAction(this::handleServerChoice);

        // KeyEvent to send message
        messageTextField.setOnKeyPressed(e -> {
            if (e.getCode().toString().equals("ENTER")) {
                handleSendMessage(new ActionEvent());
            }
        });
    }

    private void handleConnection(ActionEvent actionEvent) {

        if (connectionButton.isSelected()) {
            if (nicknameTextField.getText().isBlank()) {
                Alert alert = new Alert(Alert.AlertType.ERROR, resourceBundle.getString("key.NicknameAlert"), ButtonType.OK);
                connectionButton.setSelected(false);
                alert.showAndWait();
                return;
            }
            chatClient = new ChatClientTCP();

            // Get the server address from the combo box
            String serverAddress = serverComboBox.getValue().toString();
            if (serverAddress.contains(":")){
                String host = serverAddress.substring(0, serverAddress.indexOf(":"));
                int port = Integer.parseInt(serverAddress.substring(serverAddress.indexOf(":")+1));
                this.connect(host,port);
            } else {
                this.connect(serverAddress, 2022);
            }


        } else {
            connectionButton.setText(resourceBundle.getString("key.ConnectionButtonLabel"));
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

    private void handleServerChoice(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("serveurchoice.fxml"), resourceBundle);
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle(resourceBundle.getString("key.AddServer"));
            stage.showAndWait();
            ServeurChoiceController controller = loader.getController();

            //if controller.serverTextField contains ":" and ends with a letter
            if (controller.getServerText().contains(":") && !Pattern.compile("[0-9]").matcher(controller.getServerText().substring(controller.getServerText().indexOf(":")+1)).find()){
                Alert alert = new Alert(Alert.AlertType.ERROR, resourceBundle.getString("key.HostAlert"), ButtonType.OK);
                alert.showAndWait();
            } else if (controller.getServerText().endsWith(":")) {
                Alert alert = new Alert(Alert.AlertType.ERROR, resourceBundle.getString("key.HostAlert"), ButtonType.OK);
                alert.showAndWait();
            } else if (serverComboBox.getItems().contains(controller.getServerText())){
                Alert alert = new Alert(Alert.AlertType.ERROR, resourceBundle.getString("key.AddServerAlert"), ButtonType.OK);
                alert.showAndWait();
            } else {
                serverComboBox.getItems().add(controller.getServerText());
                serverComboBox.setValue(controller.getServerText());
            }
        } catch (IOException e) {
            e.printStackTrace();
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
