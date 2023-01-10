package fr.rtgrenoble.chatrt.net;

public class Message {
    private String from;
    private String text;

    public static final Message QUIT = new Message("system", "QUIT");

    public Message(String from, String text) {
        this.from = from;
        this.text = text;

    }

    public String getFrom() {
        return from;
    }
    public String getText() {
        return text;
    }




}
