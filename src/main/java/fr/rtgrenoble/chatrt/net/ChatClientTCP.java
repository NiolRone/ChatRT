package fr.rtgrenoble.chatrt.net;

import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatClientTCP extends ChatClient {
    /**
     * Create a new ChatClientTCP instance with the given host and port. The host and port are used to connect to the server.
     * The receivedMessageListener is called when a new message is received from the server.
     * Manage the connection to the server. The connection is established when the connect method is called.
     * The connection is closed when the disconnect method is called.
     * @param host
     */
    private Socket sock;
    private PrintStream out;
    private BufferedReader in;


    @Override
    public void send(Message message) throws IOException {
        /**
         * Send a message to the server
         * @param message
         * @throws IOException
         */
        out.print(message.getFrom() + "|" + message.getText()+"\n");
        if (out.checkError()) {
            throw new IOException("disconnected");
        }
    }

    @Override
    protected Message receive() throws IOException {
        /**
         * Receive a message from the server
         * @return
         * @throws IOException
         */
        String format = "^([-.a-zA-Z0-9 _]+)\\|([-.a-zA-Zé0-9- :',;?!()*=+_]+)$";
        Pattern pattern = Pattern.compile(format);
        String line = in.readLine();
        Matcher matcher = pattern.matcher(line);
        if (matcher.matches()) {
            return new Message(matcher.group(1), matcher.group(2));
        } else {
            throw new IOException("disconnected");
        }

    }

    public void disconnect() throws IOException {
        /**
         * Close the connection to the server
         * @throws IOException
         */
        this.sock.close();
        super.disconnect();
    }

    public void connect(String host, int port, InvalidationListener receivedMessageListener) throws IOException {
        /**
         * Connect to the server
         * @param host
         * @param port
         * @param receivedMessageListener
         * @throws IOException
         */
        this.sock = new Socket(host, port);
        this.out = new PrintStream(this.sock.getOutputStream());
        this.in = new BufferedReader(new InputStreamReader(this.sock.getInputStream()));
        super.connect(host, port, receivedMessageListener);
    }

}
