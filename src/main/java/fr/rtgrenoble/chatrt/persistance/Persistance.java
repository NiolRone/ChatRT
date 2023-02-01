package fr.rtgrenoble.chatrt.persistance;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import java.util.prefs.Preferences;


public class Persistance {
    private Preferences prefs;
    private String fichierPreferences;
    private TextField nicknameTextField;
    private ComboBox serveursComboBox;


    public Persistance(String fichierPreferences, TextField nicknameTextField, ComboBox serveursComboBox){
        this.fichierPreferences = fichierPreferences;
        this.nicknameTextField = nicknameTextField;
        this.serveursComboBox = serveursComboBox;
    }
    public void getPreference(){
        // Get preferences
        this.prefs = Preferences.userRoot().node(fichierPreferences + ".prefs");
        // Get nickname
        nicknameTextField.setText(prefs.get("pseudo", ""));
        // Get serveurs
        String serveurs = prefs.get("serveurs", "");
        if (!serveurs.isEmpty()) {
            for (String serveur : serveurs.split("\\|")) {
                serveursComboBox.getItems().add(serveur);
            }
        }
        // Get the server in the comboBox
        serveursComboBox.setValue(prefs.get("serveur_selectionnee", ""));

    }
    public void RAZ(){
        // Get preferences
        this.prefs = Preferences.userRoot().node(fichierPreferences + ".prefs");
        // RAZ nickname
        prefs.remove("pseudo");
        // RAZ serveurs
        prefs.remove("serveurs");
        // RAZ the server in the comboBox
        prefs.remove("serveur_selectionnee");

    }

    public void setPreference(){
        // Get preferences
        this.prefs = Preferences.userRoot().node(fichierPreferences + ".prefs");
        // Save nickname
        prefs.put("pseudo",nicknameTextField.getText());
        // Create string serveurs with hote:port of serveurComboBox separated by |
        StringBuilder serveurs = new StringBuilder();
        for (Object serveur : serveursComboBox.getItems()) {
            serveurs.append(serveur.toString()).append("|");
        }
        prefs.put("serveurs", serveurs.toString());
        // Save the server in the comboBox
        if (serveursComboBox.getValue() != null) {
            prefs.put("serveur_selectionnee", serveursComboBox.getValue().toString());
        }
    }
}
