import javax.swing.*;
import java.awt.*;

public class CutscenePanel extends JPanel {
    private Game game;
    private Timer timer;
    private int currentImageIndex = 0;
    private JLabel displayLabel;

    // The names of your images in the resources folder
    private String[] imageNames = { "cutscene1", "cutscene2", "cutscene3", "cutscene4" };

    public CutscenePanel(Game game) {
        this.game = game;
        this.setLayout(new BorderLayout());
        this.setBackground(Color.BLACK);

        displayLabel = new JLabel();
        displayLabel.setHorizontalAlignment(JLabel.CENTER);
        this.add(displayLabel, BorderLayout.CENTER);
    }

    public void startCutscene() {
        currentImageIndex = 0;
        showNextImage();

        // Stop any existing timer to avoid conflicts
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }

        // Change image every 3 seconds (3000 milliseconds)
        timer = new Timer(3000, e -> showNextImage());
        timer.start();
    }

    private void showNextImage() {
        if (currentImageIndex < imageNames.length) {
            // Load the image using your UIHelper
            String path = UIHelper.findImagePath(imageNames[currentImageIndex]);

            if (path != null) {
                ImageIcon originalIcon = new ImageIcon(path);
                // Scale to fit the 800x600 game window
                Image scaledImage = originalIcon.getImage().getScaledInstance(800, 600, Image.SCALE_SMOOTH);
                displayLabel.setIcon(new ImageIcon(scaledImage));
            } else {
                System.out.println("Could not find image: " + imageNames[currentImageIndex]);
            }

            currentImageIndex++;
        } else {
            // When images run out, stop timer and start the actual game
            timer.stop();
            game.showGameScreenDirectly();
        }
    }
}