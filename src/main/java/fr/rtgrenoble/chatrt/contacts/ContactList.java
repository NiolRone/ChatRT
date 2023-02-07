package fr.rtgrenoble.chatrt.contacts;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ContactList {
    /*
    Class that memorize the list of contacts
     */
    // Attributes
    protected ListView<Contact> connectesListView;
    protected ArrayList<Contact> contactsArrayList;
    protected TextField messageTextField;

    public ContactList(ListView<Contact> connectesListView, TextField messageTextField) {
        /*
        Constructor of the class ContactList
         */
        this.connectesListView = connectesListView;
        this.messageTextField = messageTextField;
        this.contactsArrayList = new ArrayList<>();
        contactsArrayList.add(Contact.SYSTEME);
        contactsArrayList.add(Contact.SERVEUR);
    }

    public void addContact(Contact contact) {
        /**
        Add a contact to the list of contacts
        @param contact: the contact to add
         */
        contactsArrayList.add(contact);
    }

    public boolean estContactConnu(Contact contact) {
        /**
        Check if a contact is in the list of contacts
        @param contact: the contact to check
        @return: true if the contact is in the list of contacts, false otherwise
         */
        for (Contact contact1 : contactsArrayList) {
            if (contact1.equals(contact)) {
                return true;
            }
        }
        return false;
    }
    public boolean estPseudoConnu(String pseudo) {
        /**
        Check if a pseudo is in the list of contacts
        @param pseudo: the pseudo to check
        @return: true if the pseudo is in the list of contacts, false otherwise
         */
        for (Contact contact1 : contactsArrayList) {
            if (contact1.getPseudo().equals(pseudo)) {
                return true;
            }
        }
        return false;
    }
    public boolean estVisible(Contact contact) {
        /**
        Check if a contact is visible in the list of contacts
        @param contact: the contact to check
        @return: true if the contact is visible in the list of contacts, false otherwise
         */
        return connectesListView.getItems().contains(contact);
    }

    public Contact getContactAvecPseudo(String pseudo) {
        /**
        Get a contact with his pseudo
        @param pseudo: the pseudo of the contact
        @return: the contact with the pseudo given in parameter
         */
        for (Contact contact : contactsArrayList) {
            if (contact.getPseudo().equals(pseudo)) {
                return contact;
            }
        }
        return null;
    }

    public void rendVisible(Contact contact) {
        /**
        Make a contact visible in the list of contacts
        @param contact: the contact to make visible
         */
        if (!estVisible(contact)) {
            // Affiche l'avatar du contact et son pseudo
            connectesListView.getItems().add(contact);
        }
    }

    public void rendInvisible(Contact contact) {
        /**
        Make a contact invisible in the list of contacts
        @param contact: the contact to make invisible
         */
        if (estVisible(contact)) {
            connectesListView.getItems().remove(contact);
        }
    }

    public void setConnecte(Contact contact) {
        /**
        Make a contact visible in the list of contacts
        @param contact: the contact to make visible
         */
        if (estContactConnu(contact)) {
            rendVisible(contact);
        } else {
            addContact(contact);
        }
    }

    public void setDeconnecte(Contact contact) {
        /**
        Make a contact invisible in the list of contacts
        @param contact: the contact to make invisible
         */
        rendInvisible(contact);
    }

    public void setAllDeconnecte() {
        /*
        Make all contacts invisible in the list of contacts
         */
        for (Contact contact : contactsArrayList) {
            if (contact != Contact.SYSTEME) {
                rendInvisible(contact);
            }
        }
    }
}
