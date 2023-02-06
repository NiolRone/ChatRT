package fr.rtgrenoble.chatrt.net;

public class Message {
    /**
     * Class to create a message
     * @param from
     * @param text
     */
    private String from;
    private String text;

    public static final Message QUIT = new Message("system", "QUIT"); // Message when disconnecting

    public Message(String from, String text) {
        /**
         * Constructor
         * @param from
         * @param text
         */
        this.from = from;
        this.text = text;

    }

    public String getFrom() {
        /**
         * Get the sender
         * @return
         */
        return from;
    }
    public String getText() {
        /**
         * Get the text
         * @return
         */
        return text;
    }




}
