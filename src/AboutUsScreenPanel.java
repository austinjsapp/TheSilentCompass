import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * About Us screen with centered text.
 */
public class AboutUsScreenPanel extends JPanel {

    private int screenWidth;
    private int screenHeight;
    private double scale;

    private String[] textLines;
    private ImagePanel backgroundPanel;

    public AboutUsScreenPanel(Game game, int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        setLayout(null);

        double scaleX = screenWidth / 800.0;
        double scaleY = screenHeight / 600.0;
        this.scale = Math.min(scaleX, scaleY);

        backgroundPanel = new ImagePanel(UIHelper.findImagePath("about_us"));
        backgroundPanel.setBounds(0, 0, screenWidth, screenHeight);

        ShadowLabel titleLabel = new ShadowLabel("ABOUT US");
        titleLabel.setForeground(Color.white);
        titleLabel.setFont(new Font("Serif", Font.PLAIN, (int)(62 * scale)));
        titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        int titleWidth = (int)(700 * scaleX);
        int titleX = (screenWidth - titleWidth) / 2;
        titleLabel.setBounds(titleX, (int)(30 * scaleY), titleWidth, (int)(80 * scaleY));

        textLines = new String[] {
                "THE SILENT COMPASS",
                "",
                "A Text-Based Jungle Adventure Game",
                "",
                "Created by: Austin Sapp",
                "",
                "You awaken beside your shattered canoe on a mysterious riverbank.",
                "The dense jungle stretches endlessly before you.",
                "",
                "Make critical choices, battle dangerous creatures, collect powerful items,",
                "and discover multiple endings as you fight to survive and escape the wild.",
                "",
                "Will you find rescue, forge your own path, or become another lost soul",
                "claimed by the jungle?",
                "",
                "The compass is silent. The choice is yours."
        };

        int buttonWidth = (int)(300 * scaleX);
        int buttonHeight = (int)(50 * scaleY);
        int buttonX = (screenWidth - buttonWidth) / 2;

        JButton backButton = UIHelper.createMenuButton("BACK TO MENU", scale);
        backButton.setBounds(buttonX, (int)(530 * scaleY), buttonWidth, buttonHeight);
        backButton.addActionListener(e -> game.showScreen("mainMenu"));

        add(backgroundPanel);
        add(titleLabel);
        add(backButton);

        setComponentZOrder(backgroundPanel, getComponentCount() - 1);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g2d.setFont(new Font("Serif", Font.PLAIN, (int)(20 * scale)));
        int lineHeight = g2d.getFontMetrics().getHeight();
        int yPos = (int)(125 * screenHeight / 600.0);

        for (String line : textLines) {
            if (line.trim().isEmpty()) {
                yPos += lineHeight;
                continue;
            }

            int lineWidth = g2d.getFontMetrics().stringWidth(line);
            int xPos = (screenWidth - lineWidth) / 2;

            g2d.setColor(new Color(0, 0, 0, 150));
            g2d.drawString(line, xPos + 2, yPos + 2);

            g2d.setColor(Color.WHITE);
            g2d.drawString(line, xPos, yPos);

            yPos += lineHeight;
        }

        g2d.dispose();
    }
}