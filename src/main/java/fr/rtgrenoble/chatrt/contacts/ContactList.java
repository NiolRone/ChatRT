package fr.rtgrenoble.chatrt.contacts;

import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ContactList {
    protected ListView<Contact> connectesListView;
    protected ArrayList<Contact> contactsArrayList;
    protected TextField messageTextField;

    public ContactList(ListView<Contact> connectesListView, TextField messageTextField) {
        this.connectesListView = connectesListView;
        this.messageTextField = messageTextField;
        this.contactsArrayList = new ArrayList<>();
        contactsArrayList.add(Contact.SYSTEME);
        contactsArrayList.add(Contact.SERVEUR);
    }

    public void addContact(Contact contact) {
        contactsArrayList.add(contact);
        connectesListView.getItems().add(contact);
    }

    public boolean estContactConnu(Contact contact) {
        for (Contact contact1 : contactsArrayList) {
            if (contact1.equals(contact)) {
                return true;
            }
        }
        return false;
    }
    public boolean estPseudoConnu(String pseudo) {
        for (Contact contact1 : contactsArrayList) {
            if (contact1.getPseudo().equals(pseudo)) {
                return true;
            }
        }
        return false;
    }
    public boolean estVisible(Contact contact) {
        return connectesListView.getItems().contains(contact);
    }

    public Contact getContactAvecPseudo(String pseudo) {
        for (Contact contact : contactsArrayList) {
            if (contact.getPseudo().equals(pseudo)) {
                return contact;
            }
        }
        return null;
    }

    public void rendVisible(Contact contact) {
        if (!estVisible(contact)) {
            connectesListView.getItems().add(contact);
        }
    }

    public void rendInvisible(Contact contact) {
        if (estVisible(contact)) {
            connectesListView.getItems().remove(contact);
        }
    }

    public void setConnecte(Contact contact) {
        if (estContactConnu(contact)) {
            rendVisible(contact);
        } else {
            addContact(contact);
        }
    }

    public void setDeconnecte(Contact contact) {
        rendInvisible(contact);
    }

    public void setAllDeconnecte() {
        for (Contact contact : contactsArrayList) {
            if (contact != Contact.SYSTEME) {
                rendInvisible(contact);
            }
        }
    }
}
