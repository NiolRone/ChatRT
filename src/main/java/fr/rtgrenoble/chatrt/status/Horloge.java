package fr.rtgrenoble.chatrt.status;

import fr.rtgrenoble.chatrt.ChatClientController;
import javafx.application.Platform;
import javafx.scene.control.Label;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Horloge implements Runnable{
    /**
     * Class to manage the clock
     */
    // Attributes
    public Label ClockLabel;

    public boolean stop = false;
    public Horloge(Label ClockLabel) {
        /**
         * Constructor
         * @param ClockLabel Label to display the clock
         */
        this.ClockLabel = ClockLabel;
    }

    public void stop() {
        /**
         * Method to stop the clock
         * @return void
         */
        stop = true;
    }
    @Override
    public void run() {
        /**
         * Method to start the clock
         * @return void
         */
        while (!stop) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");
            Platform.runLater(() -> ClockLabel.setText(simpleDateFormat.format(new Date())));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
