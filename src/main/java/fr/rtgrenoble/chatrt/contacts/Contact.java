package fr.rtgrenoble.chatrt.contacts;

import javafx.scene.image.ImageView;
import javafx.scene.image.Image;

public class Contact {
    /*
    Class that memorize the contact's name and his avatar in base64
     */
    // Attributes
    public static Contact ALL = new Contact("all");
    protected String base64;
    protected String pseudo;
    public static Contact SERVEUR = new Contact("serveur");
    public static Contact SYSTEME = new Contact("systeme");

    public Contact(String pseudo, String base64) {
        /*
        Constructor of the class Contact
        @param pseudo: the pseudo of the contact
        @param base64: the avatar of the contact in base64
         */
        this.pseudo = pseudo;
        this.base64 = base64;
    }
    public Contact(String pseudo) {
        /*
        Constructor of the class Contact
        @param pseudo: the pseudo of the contact
         */
        this.pseudo = pseudo;
        this.base64 = null;
    }

    public String getBase64() {
        /*
        Get the avatar of the contact in base64
        @return: the avatar of the contact in base64
         */
        return base64;
    }
    public String getPseudo() {
        /*
        Get the pseudo of the contact
        @return: the pseudo of the contact
         */
        return pseudo;
    }
    public ImageView getImageViewAvatar() {
        /*
        Get the avatar of the contact in ImageView
        @return: the avatar of the contact in ImageView
         */
        return new ImageView(getImageFromBase64());
    }
    public Image getImageFromBase64() {
        /*
        Get the avatar of the contact in Image
        @return: the avatar of the contact in Image
         */
        return new Image(base64);
    }
    public boolean equals(Contact contact) {
        /*
        Check if two contacts are the same
        @param contact: the contact to compare
        @return: true if the two contacts are the same, false otherwise
         */
        return this.pseudo.equals(contact.pseudo);
    }
    public void setPseudo(String pseudo, boolean changeAvatar) {
        /*
        Set the pseudo of the contact
        @param pseudo: the new pseudo of the contact
        @param changeAvatar: true if the avatar must be changed, false otherwise
         */
        this.pseudo = pseudo;
        if (changeAvatar) {
            this.base64 = null;
        }
    }
}
