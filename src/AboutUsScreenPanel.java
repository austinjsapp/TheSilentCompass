import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.Font;

public class AboutUsScreenPanel extends JPanel {

    public AboutUsScreenPanel(Game game, int screenWidth, int screenHeight) {
        setLayout(null);

        // Calculate scale factors
        double scaleX = screenWidth / 800.0;
        double scaleY = screenHeight / 600.0;
        double scale = Math.min(scaleX, scaleY);

        ImagePanel backgroundPanel = new ImagePanel(UIHelper.findImagePath("about_us"));
        backgroundPanel.setBounds(0, 0, screenWidth, screenHeight);

        ShadowLabel titleLabel = new ShadowLabel("ABOUT US");
        titleLabel.setForeground(Color.white);
        titleLabel.setFont(new Font("Serif", Font.PLAIN, (int)(62 * scale)));

        int titleWidth = (int)(700 * scaleX);
        int titleX = (screenWidth - titleWidth) / 2;
        titleLabel.setBounds(titleX, (int)(50 * scaleY), titleWidth, (int)(80 * scaleY));

        ShadowLabel infoLabel = new ShadowLabel("<html><center>Created by: [Your Name]<br>A text-based adventure game<br>Navigate the jungle and survive!</center></html>");
        infoLabel.setForeground(Color.white);
        infoLabel.setFont(new Font("Serif", Font.PLAIN, (int)(24 * scale)));
        infoLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        int infoWidth = (int)(600 * scaleX);
        int infoX = (screenWidth - infoWidth) / 2;
        infoLabel.setBounds(infoX, (int)(150 * scaleY), infoWidth, (int)(200 * scaleY));

        int buttonWidth = (int)(300 * scaleX);
        int buttonHeight = (int)(50 * scaleY);
        int buttonX = (screenWidth - buttonWidth) / 2;

        JButton backButton = UIHelper.createMenuButton("BACK TO MENU", scale);
        backButton.setBounds(buttonX, (int)(400 * scaleY), buttonWidth, buttonHeight);
        backButton.addActionListener(e -> game.showScreen("mainMenu"));

        add(titleLabel);
        add(infoLabel);
        add(backButton);
        add(backgroundPanel);
    }
}