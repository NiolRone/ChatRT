package fr.rtgrenoble.chatrt.contacts;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

public class Avatar {
    public static final String DEFAULT_AVATAR = null;
    public static int PREFFERED_WIDTH;
    public static int PREFFERED_HEIGHT;

    public Avatar() {
        PREFFERED_WIDTH = 50;
        PREFFERED_HEIGHT = 50;
    }

    public static WritableImage getDefaultAvatarFromPseudo(String file, String pseudo) {
        ImageView imageView = new ImageView(file);
        double imgWidth = PREFFERED_WIDTH / 9;
        double imgHeight = PREFFERED_HEIGHT;
        int n = Integer.remainderUnsigned(pseudo.hashCode(), 9);
        imageView.setViewport(new Rectangle2D(imgWidth * n, 0, imgWidth, imgHeight));
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(32);
        return imageView.snapshot(null, null);
    }

    public static String getBase64WithImage (Image image) {
        return null;
    }

}
