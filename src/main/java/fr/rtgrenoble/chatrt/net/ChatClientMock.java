package fr.rtgrenoble.chatrt.net;

import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;
import javafx.beans.InvalidationListener;

import java.io.IOException;
import java.util.Random;

public class ChatClientMock extends ChatClient {

    private Random random;
    private Lorem lorem;

    public ChatClientMock() {
        this.random = new Random();
        this.lorem = LoremIpsum.getInstance();
    }

    @Override
    public void connect(String host, int port, InvalidationListener receivedMessageListener) throws IOException {
        if (!"mock".equals(host)) {
            throw new IOException("Connectez-vous à mock");
        }
        super.connect(host, port, receivedMessageListener);
    }

    @Override
    public void send(Message message) throws IOException {
        if (this.connected) {
            if(message.getText().equalsIgnoreCase("KO")) {
                throw new IOException("Error");
            }
            System.out.println(message);
        } else {
            throw new IOException("Not connected");
        }
    }

    @Override
    protected Message receive() throws IOException {
        try {
            Thread.sleep(random.nextInt(2000, 10_000));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return new Message(lorem.getFirstName(), lorem.getWords(3, 15));
    }
}
