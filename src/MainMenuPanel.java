import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.Font;

// This class is a JPanel that represents ONLY the main menu screen.
public class MainMenuPanel extends JPanel {
    public MainMenuPanel(Game game) {
        setLayout(null);

        ImagePanel backgroundPanel = new ImagePanel(UIHelper.findImagePath("main_menu"));
        backgroundPanel.setBounds(0, 0, 800, 600);

        // We are changing this line to call the constructor that
        // only takes a String, which matches your ShadowLabel.java file.
        ShadowLabel titleLabel = new ShadowLabel("THE SILENT COMPASS");

        titleLabel.setForeground(Color.white);
        titleLabel.setFont(new Font("Serif", Font.PLAIN, 62));
        titleLabel.setBounds(50, 85, 700, 80);

        JButton newGameButton = UIHelper.createMenuButton("NEW GAME");
        newGameButton.setBounds(50, 200, 300, 50);

        newGameButton.addActionListener(e -> {
            game.showScreen("gameScreen");
        });

        JButton aboutUsButton = UIHelper.createMenuButton("ABOUT US");
        aboutUsButton.setBounds(50, 260, 300, 50);
        aboutUsButton.addActionListener(e -> game.showScreen("aboutUsScreen"));

        JButton exitButton = UIHelper.createMenuButton("EXIT");
        exitButton.setBounds(50, 320, 300, 50);
        exitButton.addActionListener(e -> System.exit(0));

        add(titleLabel);
        add(newGameButton);
        add(aboutUsButton);
        add(exitButton);
        add(backgroundPanel);
    }
}