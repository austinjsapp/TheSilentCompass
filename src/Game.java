import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.CardLayout;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.swing.JLayeredPane;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import java.awt.Image; // <-- Import for scaling images

// This is the main controller/director for our entire application.
public class Game {

    JFrame window;
    CardLayout cardLayout;
    JPanel mainPanel;

    private JLayeredPane layeredPane;
    private JButton muteButton;
    private ImageIcon muteIcon;
    private ImageIcon unmuteIcon;
    private boolean isMuted = false;

    private MusicManager musicManager;
    private Map<String, String> musicMap;
    private String currentMusicPath = "";
    private Random random = new Random();

    public static void main(String[] args) {
        new Game();
    }

    public Game() {
        window = new JFrame("The Silent Compass");
        window.setSize(800, 600);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        layeredPane = new JLayeredPane();
        layeredPane.setBounds(0, 0, 800, 600);
        window.add(layeredPane);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBounds(0, 0, 800, 600);
        layeredPane.add(mainPanel, JLayeredPane.DEFAULT_LAYER);


        // Define the button size
        int iconSize = 50;

        // Load the *original* images first
        ImageIcon originalUnmuteIcon = new ImageIcon(UIHelper.findImagePath("musiciconunmuted"));
        ImageIcon originalMuteIcon = new ImageIcon(UIHelper.findImagePath("musiciconmuted"));

        // Get the 'Image' object from the icon, scale it, and make a new icon
        Image scaledUnmuteImg = originalUnmuteIcon.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH);

        // It was 'originalMMuteIcon', now it's 'originalMuteIcon'
        Image scaledMuteImg = originalMuteIcon.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH);

        // Create the final, scaled icons
        unmuteIcon = new ImageIcon(scaledUnmuteImg);
        muteIcon = new ImageIcon(scaledMuteImg);

        muteButton = new JButton();
        muteButton.setIcon(unmuteIcon); // Use the new *scaled* icon


        // X = 720 (800 - 50 - 30 for safety/border)
        // Y = 500 (600 - 50 - 50 for safety/border)
        muteButton.setBounds(720, 500, iconSize, iconSize);

        // Style the button
        muteButton.setOpaque(false);
        muteButton.setContentAreaFilled(false);
        muteButton.setBorderPainted(false);
        muteButton.setFocusPainted(false);

        muteButton.addActionListener(e -> {
            isMuted = !isMuted;
            musicManager.setMuted(isMuted);

            if (isMuted) {
                muteButton.setIcon(muteIcon);
            } else {
                muteButton.setIcon(unmuteIcon);
            }
        });

        layeredPane.add(muteButton, JLayeredPane.PALETTE_LAYER);


        //  MUSIC MANAGER SETUP
        musicManager = new MusicManager();
        musicMap = new HashMap<>();
        musicMap.put("mainMenu", UIHelper.findMusicPath("main_menu_music"));
        musicMap.put("gameScreen", UIHelper.findMusicPath("game_ambient_music"));
        musicMap.put("aboutUsScreen", UIHelper.findMusicPath("main_menu_music"));

        JPanel mainMenuScreen = new MainMenuPanel(this);
        JPanel gameScreen = new GameScreenPanel(this);
        JPanel aboutUsScreen = new AboutUsScreenPanel(this);
        JPanel loadingScreen = new LoadingScreenPanel(this);
        // --- End Panel Creation ---

        mainPanel.add(mainMenuScreen, "mainMenu");
        mainPanel.add(gameScreen, "gameScreen");
        mainPanel.add(aboutUsScreen, "aboutUsScreen");
        mainPanel.add(loadingScreen, "loadingScreen");

        currentMusicPath = musicMap.get("mainMenu");
        if (currentMusicPath != null) {
            musicManager.fadeIn(currentMusicPath);
        }

        cardLayout.show(mainPanel, "mainMenu");
        window.setVisible(true);
    }

    public void showScreen(String targetScreenName) {

        cardLayout.show(mainPanel, "loadingScreen");
        muteButton.setVisible(false);

        String newMusicPath = musicMap.get(targetScreenName);
        boolean musicNeedsToChange = (newMusicPath != null && !newMusicPath.equals(currentMusicPath));

        if (musicNeedsToChange) {
            currentMusicPath = newMusicPath;

            musicManager.fadeOut(() -> {
                musicManager.fadeIn(newMusicPath);

                if (isMuted) {
                    musicManager.setMuted(true);
                }

                cardLayout.show(mainPanel, targetScreenName);
                muteButton.setVisible(true);
            });

        } else {
            // Use the longer, randomized delay
            int loadingDelay = random.nextInt(1500) + 1500;

            Timer timer = new Timer(loadingDelay, e -> {
                cardLayout.show(mainPanel, targetScreenName);
                muteButton.setVisible(true);
            });
            timer.setRepeats(false);
            timer.start();
        }
    }
}