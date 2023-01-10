package fr.rtgrenoble.chatrt.net;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.io.IOException;

/**
 * Messagerie instantanée. Emission et réception d'objets {@link Message}.
 */
public abstract class ChatClient {

    /**
     * Etat connecté
     */
    protected boolean connected;

    /**
     * Propriété <b>observable</b> contenant le dernier objet {@link Message} reçu.
     */
    private ObjectProperty<Message> receivedMessage;

    /**
     * Objet notifié lorsque la propriété observable {@link #receivedMessage} est invalidée.
     * Lorsque qu'un message est réceptionné, il est stocké dans {@link #receivedMessage}
     * ce qui déclenche l'appel de la méthode <i>callback</i> {@link InvalidationListener#invalidated(Observable)}.
     */
    private InvalidationListener receivedMessageListener;

    /**
     *
     * @return état connecté de la messagerie instantanée
     */
    public boolean isConnected() {
        return this.connected;
    }

    /**
     * Connexion au serveur de messagerie {@code host}:{@code port}.
     * L'objet {@code receivedMessageListener} est notifié lors de la réception d'un message.
     * @param host nom ou adresse IP du serveur
     * @param port port d'écoute du serveur
     * @param receivedMessageListener objet notifié lors de la réception d'un message
     * @throws IOException
     */
    public void connect(String host, int port, InvalidationListener receivedMessageListener) throws IOException {
        this.receivedMessageListener = receivedMessageListener;
        this.receivedMessage = new SimpleObjectProperty<>();
        this.receivedMessage.addListener(receivedMessageListener);
        new Thread(this::receiveLoop).start();
        this.connected = true;
    }

    /**
     * Fermeture de la connexion au serveur
     * @throws IOException
     */
    public void disconnect() throws IOException {
        receivedMessage.removeListener(receivedMessageListener);
        this.connected = false;
    }

    /**
     * Emission d'un {@link Message}.
     * @param message message à envoyer
     * @throws IOException
     */
    public abstract void send(Message message) throws IOException;

    /**
     * Attente de la réception d'un message.
     * Appel bloquant jusqu'à ce qu'un message arrive.
     * @return message reçu
     * @throws IOException
     */
    protected abstract Message receive() throws IOException;

    /**
     * Boucle de réception des messages exécutée en tâche de fond grâce à un <i>thread</i>.
     * Un message reçu est stocké dans la propriété observable {@link #receivedMessage},
     * ce qui déclenche l'appel de la méthode <i>callback</i> {@link InvalidationListener#invalidated(Observable)}
     * de l'objet {@link #receivedMessageListener}.
     * @see #receive()
     */
    private void receiveLoop() {
        try {
            while (connected) {
                receivedMessage.set(receive());
            }
        } catch (IOException e) {
            receivedMessage.set(Message.QUIT);
            System.out.println("e = " + e);
        }
    }

    /**
     * Lecture du {@link Message} stocké dans la propriété observable {@link #receivedMessage}.
     * @return dernier message reçu
     */
    public Message getReceivedMessage() {
        return receivedMessage.get();
    }
}