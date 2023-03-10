package fr.rtgrenoble.chatrt.status;

import fr.rtgrenoble.chatrt.ChatClientController;
import javafx.application.Platform;
import javafx.scene.control.Label;

import java.util.ResourceBundle;

public class Chronometre implements Runnable{
    /*
    * Class to manage the chronometer
     */
    // Attributes
    public Label chronoLabel;
    public Long startTime;
    public String status;
    public boolean stop = false;

    public Chronometre(Label chronoLabel, String status) {
        /**
        * Constructor
        * @param chronoLabel Label to display the chronometer
        * @param status Status of the user
         */
        this.chronoLabel = chronoLabel;
        this.status = status;
    }

    public void start() {
        /**
         * Method to start the chronometer
         * @return void
         */
        ResourceBundle resourceBundle = ResourceBundle.getBundle("fr.rtgrenoble.chatrt.i18nBundle");
        startTime = System.currentTimeMillis();
        while (!stop) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            long elapsedTime = System.currentTimeMillis() - startTime;
            long hours = elapsedTime / 3600000;
            long minutes = (elapsedTime % 3600000) / 60000;
            long seconds = (elapsedTime % 60000) / 1000;
            String statusChrono = String.format("%s %s %s %02d:%02d:%02d",resourceBundle.getString("key.Connection") ,status,resourceBundle.getString("key.Since"), hours, minutes, seconds);
            Platform.runLater(() -> chronoLabel.setText(statusChrono));
        }
        Platform.runLater(() -> chronoLabel.setText(resourceBundle.getString("key.Disconnect")));
    }

    public void stop() {
        /**
         * Method to stop the chronometer
         * @return void
         */
        stop = true;
    }


    @Override
    public void run() {
        /**
         * Method to run the chronometer
         * @return void
         */
        start();
    }
}
