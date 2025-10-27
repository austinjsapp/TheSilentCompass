import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.FontMetrics;

public class ShadowLabel extends JLabel {

    // A field to hold the shadow distance
    private int shadowOffset;

    /**
     * Creates a ShadowLabel with the default shadow offset (2 pixels).
     * @param text The text to display.
     */
    public ShadowLabel(String text) {
        // This calls the other constructor with a default value of 2.
        this(text, 2);
    }

    /**
     * Creates a ShadowLabel with a custom shadow offset.
     * @param text The text to display.
     * @param offset The distance (in pixels) for the shadow.
     */
    public ShadowLabel(String text, int offset) {
        super(text); // Call the parent JLabel constructor
        this.shadowOffset = offset; // Store the custom offset
    }

    @Override
    protected void paintComponent(Graphics g) {

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        FontMetrics fm = g2.getFontMetrics();
        int stringWidth = fm.stringWidth(getText());
        int componentWidth = getWidth();

        // --- X Calculation ---
        int x;
        int alignment = getHorizontalAlignment();

        if (alignment == SwingConstants.CENTER) {
            x = (componentWidth - stringWidth) / 2;
        }
        else if (alignment == SwingConstants.RIGHT) {
            x = componentWidth - stringWidth;
        }
        else {
            x = 0;
        }

        // --- Y Calculation ---
        int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();

        // Draw the Shadow using the stored 'shadowOffset' value
        g2.setColor(new Color(0, 0, 0, 150));
        g2.drawString(getText(), x + shadowOffset, y + shadowOffset);

        // Draw the main text on top
        g2.setColor(getForeground());
        g2.drawString(getText(), x, y);

        g2.dispose();
    }
}