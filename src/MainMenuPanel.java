import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.Font;

/**
 * Main menu panel with centered title and proper button layout.
 */
public class MainMenuPanel extends JPanel {

    public MainMenuPanel(Game game, int screenWidth, int screenHeight) {
        setLayout(null);

        double scaleX = screenWidth / 800.0;
        double scaleY = screenHeight / 600.0;
        double scale = Math.min(scaleX, scaleY);

        ImagePanel backgroundPanel = new ImagePanel(UIHelper.findImagePath("main_menu"));
        backgroundPanel.setBounds(0, 0, screenWidth, screenHeight);

        // Centered title - SAME FONT AS BUTTONS (Serif, PLAIN)
        ShadowLabel titleLabel = new ShadowLabel("THE SILENT COMPASS");
        titleLabel.setForeground(Color.white);
        titleLabel.setFont(new Font("Serif", Font.PLAIN, (int)(62 * scale)));
        titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        int titleWidth = (int)(700 * scaleX);
        int titleX = (screenWidth - titleWidth) / 2;
        titleLabel.setBounds(titleX, (int)(85 * scaleY), titleWidth, (int)(80 * scaleY));

        // Center buttons horizontally
        int buttonWidth = (int)(300 * scaleX);
        int buttonHeight = (int)(50 * scaleY);
        int buttonX = (screenWidth - buttonWidth) / 2;

        JButton newGameButton = UIHelper.createMenuButton("NEW GAME", scale);
        newGameButton.setBounds(buttonX, (int)(220 * scaleY), buttonWidth, buttonHeight);
        newGameButton.addActionListener(e -> game.showScreen("gameScreen"));

        JButton aboutUsButton = UIHelper.createMenuButton("ABOUT US", scale);
        aboutUsButton.setBounds(buttonX, (int)(280 * scaleY), buttonWidth, buttonHeight);
        aboutUsButton.addActionListener(e -> game.showScreen("aboutUsScreen"));

        JButton exitButton = UIHelper.createMenuButton("EXIT", scale);
        exitButton.setBounds(buttonX, (int)(340 * scaleY), buttonWidth, buttonHeight);
        exitButton.addActionListener(e -> System.exit(0));

        add(titleLabel);
        add(newGameButton);
        add(aboutUsButton);
        add(exitButton);
        add(backgroundPanel);
    }
}