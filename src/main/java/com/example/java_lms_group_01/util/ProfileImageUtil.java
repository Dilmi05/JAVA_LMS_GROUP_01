package com.example.java_lms_group_01.util;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.nio.file.Path;

public final class ProfileImageUtil {

    private ProfileImageUtil() {
    }

    // Load image only from local file path
    public static void loadImage(ImageView imageView, String imagePath) {

        // If image view is null, do nothing
        if (imageView == null) {
            return;
        }

        // Clear current image
        imageView.setImage(null);

        // If path is empty, stop
        if (imagePath == null || imagePath.trim().isEmpty()) {
            return;
        }

        try {
            // Convert local file path to URI
            String path = imagePath.trim();
            String source = Path.of(path).toUri().toString();

            // Load image
            Image image = new Image(source, true);

            // Set image if no error
            if (!image.isError()) {
                imageView.setImage(image);
            }

        } catch (Exception e) {
            // If error, clear image
            imageView.setImage(null);
        }
    }
}