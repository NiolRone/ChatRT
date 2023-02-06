package fr.rtgrenoble.chatrt;

import fr.rtgrenoble.chatrt.contacts.Avatar;
import fr.rtgrenoble.chatrt.contacts.Contact;
import fr.rtgrenoble.chatrt.contacts.ContactList;
import fr.rtgrenoble.chatrt.net.ChatClient;
import fr.rtgrenoble.chatrt.net.ChatClientTCP;
import fr.rtgrenoble.chatrt.net.Message;
import fr.rtgrenoble.chatrt.persistance.Persistance;
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
    /*
    Class that manage the GUI
     */

    // Attributes
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
    public MenuItem deleteConfiguration;
    public ListView contactListView;
    private ChatClient chatClient;
    private Image avatars;
    private ResourceBundle resourceBundle;
    public Chronometre chrono;
    public Persistance persistance;
    public ContactList contactList;

    // Methods
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        /**
         * Initialize the GUI
         * @param url
         * @param resourceBundle
         */

        // Resource Bundle for internationalization
        this.resourceBundle = resourceBundle;
        avatars = new Image(getClass().getResource("/img/avatars.png").toString());

        // Bindings
        sendButton.disableProperty().bind(connectionButton.selectedProperty().not());

        // Listeners
        sendButton.setOnAction(this::handleSendMessage);
        connectionButton.setOnAction(this::handleConnection);
        addServer.setOnAction(this::handleServerChoice);

        // MessageListCell
        chatListView.setCellFactory(lv -> new MessageListCell());

        // Clock
        Horloge horloge = new Horloge(clockLabel);
        Thread t = new Thread(horloge);
        t.start();

        // Status
        chronoLabel.setText(resourceBundle.getString("key.Disconnect"));

        // CloseMenu
        closeMenu.setOnAction(e -> {
            horloge.stop();
            if (chrono != null) {
                chrono.stop();
            }
            disconnect("key.Disconnect");
            Platform.exit();
        });

        // Listen when close window
        Platform.runLater(() -> chatListView.getScene().getWindow().setOnCloseRequest(e -> {
            horloge.stop(); // Stop the clock
            persistance = new Persistance("EssaiPrefs", nicknameTextField, serverComboBox); // Create a new configuration
            persistance.RAZ(); // Delete the configuration to not have doublons
            persistance.setPreference(); // Save the configuration
            if (chatClient != null && chatClient.isConnected()) {
                disconnect(resourceBundle.getString("key.Disconnect"));
            }
        }));

        // KeyEvent to send message
        messageTextField.setOnKeyPressed(e -> {
            if (e.getCode().toString().equals("ENTER")) {
                handleSendMessage(new ActionEvent());
            }
        });

        // Persistance of the configuration
        persistance = new Persistance("EssaiPrefs", nicknameTextField, serverComboBox);
        persistance.getPreference();

        // Delete configuration
        deleteConfiguration.setOnAction(e -> {
            serverComboBox.getItems().clear();
            nicknameTextField.clear();
            persistance.RAZ();
            contactList.setAllDeconnecte();
        });

        // ContactList
        contactList = new ContactList(contactListView, messageTextField);
        contactListView.setCellFactory(lv -> new ContactListCell());
    }

    private void handleConnection(ActionEvent actionEvent) {
        /**
         * Handle the connection
         * @param actionEvent
         */

        if (connectionButton.isSelected()) {
            // Check if the nickname is not empty
            if (nicknameTextField.getText().isBlank()) {
                Alert alert = new Alert(Alert.AlertType.ERROR, resourceBundle.getString("key.NicknameAlert"), ButtonType.OK);
                connectionButton.setSelected(false);
                alert.showAndWait();
                return;
            }
            // Initialize the chatClient
            chatClient = new ChatClientTCP();
            // Get the server address from the combo box
            String serverAddress = serverComboBox.getValue().toString();
            if (serverAddress.contains(":")) {
                String host = serverAddress.substring(0, serverAddress.indexOf(":"));
                int port = Integer.parseInt(serverAddress.substring(serverAddress.indexOf(":") + 1));
                this.connect(host, port);
            } else {
                this.connect(serverAddress, 2022);
            }
        } else {
            // Disconnect
            connectionButton.setText(resourceBundle.getString("key.ConnectionButtonLabel"));
            this.disconnect(resourceBundle.getString("key.Disconnect"));
        }
    }

    private void handleSendMessage(ActionEvent actionEvent) {
        /**
         * Handle the sending of a message
         * @param actionEvent
         */

        // Check if the message is not empty
        if (messageTextField.getText().isBlank()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, resourceBundle.getString("key.MessageAlert"), ButtonType.OK);
            alert.showAndWait();
        } else {
            // Send the message
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
        /**
         * Handle the choice of a server
         * @param actionEvent
         */
        try {
            // Load the FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("serveurchoice.fxml"), resourceBundle);
            // Create the window
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle(resourceBundle.getString("key.AddServer"));
            stage.showAndWait();
            ServeurChoiceController controller = loader.getController();

            // Check if the server is not already in the list
            if (serverComboBox.getItems().contains(controller.getServerText())) {
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
        /**
        * Append a message to the chat
        * @param message the message to append
        */
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
        /**
        Connect to the server
        @param host the host
        @param port the port
         */
        try {
            chatClient.connect(host, port, this::handleReceiveMessage);
            // Connection successful message
            this.appendMessage(new Message("system", resourceBundle.getString("key.Connection") + "%s:%d".formatted(host, port)));
            // Start the chronometer for the status
            chrono = new Chronometre(chronoLabel, String.format("%s:%d", host, port));
            Thread t = new Thread(chrono);
            t.start();
        } catch (IOException e) {
            this.disconnect("Connexion à %s:%d impossible".formatted(host, port));
        }
    }

    private void handleReceiveMessage(Observable observable) {
        /**
         * Handle the reception of a message
         * @param observable
         */
        System.out.println("<= " + chatClient.getReceivedMessage());
        System.out.println("<= " + chatClient.getReceivedMessage().getText());
        Platform.runLater(() -> this.appendMessage(chatClient.getReceivedMessage()));
        // Contacts
        // Create the contact
        Contact contact = new Contact(chatClient.getReceivedMessage().getFrom());
        // Add the contact to the list if it is not already in it
        Platform.runLater(() -> {
            if (!contactList.estContactConnu(contact)) {
                contactList.addContact(contact);
                contactList.rendVisible(contact);
            }
        });
    }

    private void disconnect(String infoMessage) {
        /**
        Disconnect from the server
        @param infoMessage the message to display
         */
        if (chatClient.isConnected()) {
            try {
                chrono.stop();
                chatClient.disconnect();
            } catch (IOException e) {
            }
        }
        // Disconnection message
        appendMessage(new Message("system", infoMessage));
        connectionButton.setSelected(false);
    }

    private ImageView getAvatar(String nickname) {
        /**
        Get the avatar of a user
        @param nickname the nickname of the user
         */
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
        /*
        * Custom cell for the chat
        * Put the nickname in blue and the message in black
        * Put the date in italic
        * Put the avatar on the left if the message is from the user and on the right if it is from another user
         */
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
    private class ContactListCell extends ListCell<Contact> {
        /*
        * Custom cell for the contact list
        * Put the nickname in blue
        * Put the avatar on the left
         */
        @Override
        protected void updateItem(Contact item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
            } else {
                ImageView avatar = getAvatar(item.getPseudo());
                Text pseudo = new Text(item.getPseudo());
                pseudo.setFont(Font.font(null, FontWeight.BOLD, 14));
                pseudo.setFill(Color.BLUEVIOLET);
                TextFlow tf = new TextFlow(avatar, pseudo);
                tf.maxWidthProperty().bind(getListView().widthProperty().multiply(0.8));
                HBox hBox = new HBox(tf);
                hBox.maxWidthProperty().bind(getListView().widthProperty());
                hBox.setAlignment(Pos.CENTER_LEFT);
                setGraphic(hBox);
                getListView().scrollTo(getListView().getItems().size() - 1);
            }
        }
    }
}