package fr.rtgrenoble.chatrt.contacts;

import javafx.scene.image.ImageView;
import javafx.scene.image.Image;

public class Contact {
    /*
    Class that memorize the contact's name and his avatar in base64
     */
    public static Contact ALL = new Contact("all");
    protected String base64;
    protected String pseudo;
    public static Contact SERVEUR = new Contact("serveur");
    public static Contact SYSTEME = new Contact("systeme");

    public Contact(String pseudo, String base64) {
        this.pseudo = pseudo;
        this.base64 = base64;
    }
    public Contact(String pseudo) {
        this.pseudo = pseudo;
        this.base64 = null;
    }

    public String getBase64() {
        return base64;
    }
    public String getPseudo() {
        return pseudo;
    }
    public ImageView getImageViewAvatar() {
        return new ImageView(getImageFromBase64());
    }
    public Image getImageFromBase64() {
        return new Image(base64);
    }
    public boolean equals(Contact contact) {
        return this.pseudo.equals(contact.pseudo);
    }
    public void setPseudo(String pseudo, boolean changeAvatar) {
        this.pseudo = pseudo;
        if (changeAvatar) {
            this.base64 = null;
        }
    }
}
