import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Loading screen with background image and animated loading text.
 */
public class LoadingScreenPanel extends JPanel {

    private int screenWidth;
    private int screenHeight;
    private double scale;
    private ImagePanel backgroundPanel;
    private JLabel loadingLabel;

    private int dotCount = 0;
    private Timer animationTimer;

    public LoadingScreenPanel(Game game, int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        setLayout(null);

        double scaleX = screenWidth / 800.0;
        double scaleY = screenHeight / 600.0;
        this.scale = Math.min(scaleX, scaleY);

        backgroundPanel = new ImagePanel(UIHelper.findImagePath("loading_screen_background"));
        backgroundPanel.setBounds(0, 0, screenWidth, screenHeight);

        loadingLabel = new JLabel("Loading", JLabel.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

                g2d.setFont(getFont());
                java.awt.FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() / 2) + (fm.getAscent() / 2);

                // Shadow
                g2d.setColor(new Color(0, 0, 0, 150));
                g2d.drawString(getText(), x + 3, y + 3);

                // Text
                g2d.setColor(getForeground());
                g2d.drawString(getText(), x, y);

                g2d.dispose();
            }
        };
        loadingLabel.setFont(new Font("Serif", Font.PLAIN, (int)(48 * scale)));
        loadingLabel.setForeground(Color.WHITE);
        loadingLabel.setBounds(0, (screenHeight / 2) - (int)(100 * scale), screenWidth, (int)(200 * scale));

        animationTimer = new Timer(500, e -> {
            dotCount = (dotCount + 1) % 4;
            String dots = ".".repeat(dotCount);
            loadingLabel.setText("Loading" + dots);
        });
        animationTimer.start();

        this.add(backgroundPanel);
        this.add(loadingLabel);
        this.setComponentZOrder(loadingLabel, 0);
        this.setComponentZOrder(backgroundPanel, 1);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            dotCount = 0;
            loadingLabel.setText("Loading");
            if (animationTimer != null) {
                animationTimer.restart();
            }
        } else {
            if (animationTimer != null) {
                animationTimer.stop();
            }
        }
    }
}