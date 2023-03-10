package fr.rtgrenoble.chatrt.net;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Vector;

/**
 * Programme serveur qui renvoie les chaines de caractères lues jusqu'à recevoir le message "fin"
 */
public class ChatServerTCP {
    // Attributes
    private ResourceBundle resourceBundle = ResourceBundle.getBundle("fr.rtgrenoble.chatrt.i18nBundle");

    private Vector<ClientConnection> clientList;

    private ServerSocket passiveSock;

    public static void main(String[] args) throws IOException {
        /**
         * Main
         * @param args
         * @throws IOException
         */
        ChatServerTCP server = new ChatServerTCP(2022);
        server.acceptClients();
    }

    public ChatServerTCP(int port) throws IOException {
        /**
         * Constructor
         * @param port
         * @throws IOException
         */
        passiveSock = new ServerSocket(port);
        System.out.println("Serveur en écoute " + passiveSock);
        this.clientList = new Vector<ClientConnection>();
    }

    private void sendAllOtherClients(ClientConnection from, String message) {
        /**
         * Send a message to all clients except the sender
         * @param from
         * @param message
         */
        for (ClientConnection c : clientList) {
            if (c != from) {
                c.send(message);
            }
        }
    }
    /**
     * Boucle d'attente des clients
     */
    public void acceptClients() {
        while (true) {
            try {
                System.out.println("Attente d'un client");
                Socket sock = passiveSock.accept();
                System.out.println("connexion établie : " + sock);

                ClientConnection client = new ClientConnection(sock);
                clientList.add(client);
                // Send a message to all clients to inform that a new client is connected
                sendAllOtherClients(client,"Serveur|"+ this.resourceBundle.getString("key.AnotherConnection") + client.myIpPort);

                Thread t = new Thread(client);
                t.start();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * Une instance de cette classe interne gère une connexion
     * TCP avec un client
     */
    private class ClientConnection implements Runnable {
        /**
         * Socket connecté au client
         */
        private Socket sock;
        /**
         * Flux de caractères en sortie
         */
        private PrintStream out;
        /**
         * Flux de caractères en entrée
         */
        private BufferedReader in;
        /**
         * Chaine de caractères "ip:port" du client
         */
        private String myIpPort;

        private void send(String message) {
            out.println(message);
            System.out.printf("Émission vers %s : %s\n", myIpPort, message);
        }

        /**
         * Initialise les attributs {@link #sock} (socket connecté au client),
         * {@link #out} (flux de caractères UTF-8 en sortie) et
         * {@link #in} (flux de caractères UTF-8 en entrée).
         *
         * @param sock socket connecté au client
         * @throws IOException
         */
        public ClientConnection(Socket sock) throws IOException {
            this.myIpPort = sock.getInetAddress().getHostAddress() + ":" + sock.getPort();
            this.sock = sock;

            OutputStream os = sock.getOutputStream();
            InputStream is = sock.getInputStream();

            out = new PrintStream(os, true, StandardCharsets.UTF_8);
            in = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8), 2048);
        }

        /**
         * Boucle écho : renvoie tous les messages reçus.
         */

        public void echoLoop() {
            /**
             * Echo loop : send all received messages
             */
            try {
                String message = "";
                while (true) {
                    // Lecture d'un message
                    message = in.readLine();
                    System.out.printf("Réception de %s : %s\n", myIpPort, message);
                    // Si le message est null ou "QUIT", on sort de la boucle
                    if (message==null || message.equals(Message.QUIT.getText())) {
                        break;
                    }
                    // Envoi du message à tous les clients
                    sendAllOtherClients(this, message);
                }
            } catch (Exception e) {
                System.out.println("Erreur de communication");
            }
            System.out.println("remove myIpPort = " + myIpPort);
            clientList.remove(this);
            System.out.println("Client déconnecté : " + this.myIpPort);
            sendAllOtherClients(this,"Serveur|" + resourceBundle.getString("key.AnotherDisconnection") + this.myIpPort);
            try {
                sock.close();
            } catch (IOException e) {
                System.out.println("Impossible de fermer la connection");
            }

        }
        @Override
        public void run(){
            echoLoop();
        }
    }
}
