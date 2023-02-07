package fr.rtgrenoble.chatrt.net;

import fr.rtgrenoble.chatrt.contacts.Contact;

import java.util.Date;

public class MessageJSON extends Message{
    /**
     * Transform a message to JSON format
     * @param from
     * @param text
     */

    // Attributes
    static final String CLE_BASE64 = "base64";
    static final String CLE_DATE = "date";
    static final String CLE_FROM = "from";
    static final String CLE_IDENTIFIANT = "identifiant";
    static final String CLE_TEXT = "text";
    static final String CLE_TO = "to";
    protected Date date;
    protected long identifiant;
    protected String to;
    protected String base64;

    // Herited attributes
    protected String from;
    protected String text;
    protected Message QUIT = new Message("system", "QUIT");

    public MessageJSON(Contact contact, Contact destinataire, String text, Date date) {
        super(contact.getPseudo(), text);
        this.to = destinataire.getPseudo();
        this.date = date;
    }
    public MessageJSON(Contact contact, Contact destinataire, String text, Date date, long identifiant) {
        super(contact.getPseudo(), text);
        this.to = destinataire.getPseudo();
        this.date = date;
        this.identifiant = identifiant;
    }
    public MessageJSON(String from, String base64, String to, String text, Date date, long identifiant){
        super(from, text);
        this.to = to;
        this.date = date;
        this.identifiant = identifiant;
        this.base64 = base64;
    }

    public String getTo() {
        /**
         * Get the recipient
         * @return
         */
        return to;
    }
    public Date getDate() {
        /**
         * Get the date
         * @return
         */
        return date;
    }
    public String getText() {
        /**
         * Get the text, overrides the method from Message
         */
        return text;
    }

    public long getIdentifiant() {
        /**
         * Get the identifiant
         * @return
         */
        return identifiant;
    }
    public org.json.JSONObject toJSON() {
        /**
         * Transform the message to JSON format
         * @return
         */
        org.json.JSONObject json = new org.json.JSONObject();
        json.put(CLE_FROM, from);
        json.put(CLE_TO, to);
        json.put(CLE_TEXT, text);
        json.put(CLE_DATE, date);
        json.put(CLE_IDENTIFIANT, identifiant);
        return json;
    }

    public String toString() {
        /**
         * Transform the message to String format
         * @return
         */
        return "From: " + from + " To: " + to + " Text: " + text + " Date: " + date + " Identifiant: " + identifiant;
    }

    public String toJSONString() {
        /**
         * Transform the message to JSON String format
         * Take care of \n
         * @return
         */
        return toJSON().toString().replace("\\n", "");
    }

    public void setIdentifiant(long identifiant) {
        this.identifiant = identifiant;
    }
}
