import javax.swing.*;
import java.awt.*;

// This class represents a JPanel with a background image.
public class ImagePanel extends JPanel {
    private Image backgroundImage;

    public ImagePanel(String imagePath) {
        // We now call the new setImage method
        setImage(imagePath);
    }

    /**
     * NEW METHOD: Sets the background image and repaints the panel.
     * @param imagePath The file path to the new image.
     */
    public void setImage(String imagePath) {
        if (imagePath != null) {
            this.backgroundImage = new ImageIcon(imagePath).getImage();
        } else {
            this.backgroundImage = null; // No image
        }
        repaint(); // Tell the panel to redraw itself
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, this.getWidth(), this.getHeight(), this);
        }
    }
}