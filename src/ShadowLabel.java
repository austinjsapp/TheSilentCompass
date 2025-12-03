import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.FontMetrics;

public class ShadowLabel extends JLabel {

    // This number controls how far the shadow is from the text
    private int shadowOffset;

    public ShadowLabel(String text) {
        // Default to a 2 pixel shadow if we don't say otherwise
        this(text, 2);
    }

    public ShadowLabel(String text, int offset) {
        super(text);
        this.shadowOffset = offset;
    }

    @Override
    protected void paintComponent(Graphics g) {
        // We do not call super.paintComponent(g) because we want to draw it ourselves

        Graphics2D g2 = (Graphics2D) g.create();

        // This makes the text look smooth and not jagged
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        FontMetrics fm = g2.getFontMetrics();
        int stringWidth = fm.stringWidth(getText());
        int componentWidth = getWidth();

        // Figure out where to start drawing based on if it's Center, Right, or Left
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

        // Calculate the Y position to center it vertically
        int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();

        // --- DRAWING THE SHADOW FIRST (BEHIND) ---
        // I changed this to solid black (no 150 for transparency) to make it hard like Minecraft
        g2.setColor(Color.black);
        g2.drawString(getText(), x + shadowOffset, y + shadowOffset);

        // --- DRAWING THE MAIN TEXT SECOND (ON TOP) ---
        g2.setColor(getForeground());
        g2.drawString(getText(), x, y);

        // Clean up the graphics object
        g2.dispose();
    }
}