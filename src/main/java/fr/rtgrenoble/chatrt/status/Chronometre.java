package fr.rtgrenoble.chatrt.status;

import fr.rtgrenoble.chatrt.ChatClientController;
import javafx.application.Platform;
import javafx.scene.control.Label;

public class Chronometre implements Runnable{
    public Label ChronoLabel;
    public Long startTime;
    public String status;

    public boolean stop = false;

    public Chronometre(Label ChronoLabel, String status) {
        this.ChronoLabel = ChronoLabel;
        this.startTime = System.currentTimeMillis();
        this.status = status;
    }


    @Override
    public void run() {
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
            String statusChrono = String.format("Connecté à %s depuis %02d:%02d:%02d", status, hours, minutes, seconds);
            Platform.runLater(() -> ChronoLabel.setText(statusChrono));

        }
    }
}
