import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class LoadingScreenPanel extends JPanel {

    private int screenWidth;
    private int screenHeight;
    private double scale;

    public LoadingScreenPanel(Game game, int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        setLayout(null);
        setBackground(Color.BLACK);

        // Calculate scale
        double scaleX = screenWidth / 800.0;
        double scaleY = screenHeight / 600.0;
        this.scale = Math.min(scaleX, scaleY);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Draw "Loading..." text in center
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Serif", Font.PLAIN, (int)(48 * scale)));

        String text = "Loading...";
        java.awt.FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int x = (screenWidth - textWidth) / 2;
        int y = screenHeight / 2;

        g2d.drawString(text, x, y);
    }
}