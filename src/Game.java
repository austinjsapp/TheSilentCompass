import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.CardLayout;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.swing.JLayeredPane;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import java.awt.Image;

/**
 * Main controller for The Silent Compass game.
 * Handles screen management, music, and fullscreen display.
 */
public class Game {
    // Screen dimension constants
    private static final int DEFAULT_WIDTH = 800;
    private static final int DEFAULT_HEIGHT = 600;
    private static final int MUTE_BUTTON_SIZE = 50;
    private static final int BUTTON_MARGIN = 30;

    // Loading screen delay constants
    private static final int MIN_LOADING_DELAY = 1500;
    private static final int MAX_LOADING_DELAY = 3000;

    private JFrame window;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JLayeredPane layeredPane;
    private JButton muteButton;
    private ImageIcon muteIcon;
    private ImageIcon unmuteIcon;
    private boolean isMuted = false;

    private MusicManager musicManager;
    private Map<String, String> musicMap;
    private String currentMusicPath = "";
    private Random random = new Random();

    // Screen dimensions for dynamic scaling
    private int screenWidth;
    private int screenHeight;

    public static void main(String[] args) {
        // Use invokeLater for thread safety
        javax.swing.SwingUtilities.invokeLater(() -> new Game());
    }

    public Game() {
        window = new JFrame("The Silent Compass");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setUndecorated(true); // Remove window decorations for fullscreen

        // Get screen dimensions
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

        if (gd.isFullScreenSupported()) {
            // Enter fullscreen exclusive mode
            gd.setFullScreenWindow(window);
            Dimension screenSize = window.getSize();
            screenWidth = screenSize.width;
            screenHeight = screenSize.height;
        } else {
            // Fallback to maximized window if fullscreen not supported
            window.setExtendedState(JFrame.MAXIMIZED_BOTH);
            Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
            screenWidth = screenSize.width;
            screenHeight = screenSize.height;
            window.setSize(screenWidth, screenHeight);
        }

        layeredPane = new JLayeredPane();
        layeredPane.setBounds(0, 0, screenWidth, screenHeight);
        window.add(layeredPane);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBounds(0, 0, screenWidth, screenHeight);
        layeredPane.add(mainPanel, JLayeredPane.DEFAULT_LAYER);

        // Setup mute button with scaled icons
        setupMuteButton();

        // Setup music manager
        setupMusicManager();

        // Create game panels with screen dimensions
        JPanel mainMenuScreen = new MainMenuPanel(this, screenWidth, screenHeight);
        JPanel gameScreen = new GameScreenPanel(this, screenWidth, screenHeight);
        JPanel aboutUsScreen = new AboutUsScreenPanel(this, screenWidth, screenHeight);
        JPanel loadingScreen = new LoadingScreenPanel(this, screenWidth, screenHeight);

        mainPanel.add(mainMenuScreen, "mainMenu");
        mainPanel.add(gameScreen, "gameScreen");
        mainPanel.add(aboutUsScreen, "aboutUsScreen");
        mainPanel.add(loadingScreen, "loadingScreen");

        // Start with main menu music
        currentMusicPath = musicMap.get("mainMenu");
        if (currentMusicPath != null) {
            musicManager.fadeIn(currentMusicPath);
        }

        cardLayout.show(mainPanel, "mainMenu");
        window.setVisible(true);
    }

    private void setupMuteButton() {
        // Load and scale icons
        ImageIcon originalUnmuteIcon = new ImageIcon(UIHelper.findImagePath("musiciconunmuted"));
        ImageIcon originalMuteIcon = new ImageIcon(UIHelper.findImagePath("musiciconmuted"));

        Image scaledUnmuteImg = originalUnmuteIcon.getImage()
                .getScaledInstance(MUTE_BUTTON_SIZE, MUTE_BUTTON_SIZE, Image.SCALE_SMOOTH);
        Image scaledMuteImg = originalMuteIcon.getImage()
                .getScaledInstance(MUTE_BUTTON_SIZE, MUTE_BUTTON_SIZE, Image.SCALE_SMOOTH);

        unmuteIcon = new ImageIcon(scaledUnmuteImg);
        muteIcon = new ImageIcon(scaledMuteImg);

        muteButton = new JButton();
        muteButton.setIcon(unmuteIcon);

        // Position button in bottom-right corner, scaled to screen size
        int buttonX = screenWidth - MUTE_BUTTON_SIZE - BUTTON_MARGIN;
        int buttonY = screenHeight - MUTE_BUTTON_SIZE - BUTTON_MARGIN;
        muteButton.setBounds(buttonX, buttonY, MUTE_BUTTON_SIZE, MUTE_BUTTON_SIZE);

        // Style the button
        muteButton.setOpaque(false);
        muteButton.setContentAreaFilled(false);
        muteButton.setBorderPainted(false);
        muteButton.setFocusPainted(false);

        muteButton.addActionListener(e -> toggleMute());

        layeredPane.add(muteButton, JLayeredPane.PALETTE_LAYER);
    }

    private void toggleMute() {
        isMuted = !isMuted;
        musicManager.setMuted(isMuted);
        muteButton.setIcon(isMuted ? muteIcon : unmuteIcon);
    }

    private void setupMusicManager() {
        musicManager = new MusicManager();
        musicMap = new HashMap<>();
        musicMap.put("mainMenu", UIHelper.findMusicPath("main_menu_music"));
        musicMap.put("gameScreen", UIHelper.findMusicPath("game_ambient_music"));
        musicMap.put("aboutUsScreen", UIHelper.findMusicPath("main_menu_music"));
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
            // Randomized loading delay
            int loadingDelay = random.nextInt(MAX_LOADING_DELAY - MIN_LOADING_DELAY) + MIN_LOADING_DELAY;

            Timer timer = new Timer(loadingDelay, e -> {
                cardLayout.show(mainPanel, targetScreenName);
                muteButton.setVisible(true);
            });
            timer.setRepeats(false);
            timer.start();
        }
    }

    // Getter methods for screen dimensions
    public int getScreenWidth() {
        return screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }
}