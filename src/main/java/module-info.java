module fr.rtgrenoble.chatrt {
    requires javafx.controls;
    requires javafx.fxml;
    requires lorem;
    requires org.controlsfx.controls;
    requires java.prefs;

    opens fr.rtgrenoble.chatrt to javafx.fxml;
    exports fr.rtgrenoble.chatrt;
}