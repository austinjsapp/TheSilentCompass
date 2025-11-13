import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.CardLayout;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.awt.KeyEventDispatcher;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import javax.swing.JLayeredPane;

/**
 * Main controller for The Silent Compass game.
 * Handles screen management, music, fullscreen, and settings.
 */
public class Game {
    // Screen dimension constants
    private static final int DEFAULT_WIDTH = 800;
    private static final int DEFAULT_HEIGHT = 600;

    // Loading screen delay constants
    private static final int MIN_LOADING_DELAY = 1500;
    private static final int MAX_LOADING_DELAY = 3000;

    private JFrame window;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JLayeredPane layeredPane;

    private MusicManager musicManager;
    private Map<String, String> musicMap;
    private String currentMusicPath = "";
    private String previousScreen = "mainMenu";
    private Random random = new Random();

    // Screen dimensions for dynamic scaling
    private int screenWidth;
    private int screenHeight;
    private boolean isFullscreen = true;
    private GraphicsDevice graphicsDevice;

    // Volume controls
    private float musicVolume = 1.0f;
    private float sfxVolume = 1.0f;

    // Panels
    private SettingsPanel settingsPanel;

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> new Game());
    }

    public Game() {
        window = new JFrame("The Silent Compass");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setUndecorated(true);

        graphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

        if (graphicsDevice.isFullScreenSupported()) {
            graphicsDevice.setFullScreenWindow(window);
            Dimension screenSize = window.getSize();
            screenWidth = screenSize.width;
            screenHeight = screenSize.height;
        } else {
            window.setExtendedState(JFrame.MAXIMIZED_BOTH);
            Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
            screenWidth = screenSize.width;
            screenHeight = screenSize.height;
            window.setSize(screenWidth, screenHeight);
        }

        setupKeyListener();

        layeredPane = new JLayeredPane();
        layeredPane.setBounds(0, 0, screenWidth, screenHeight);
        window.add(layeredPane);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBounds(0, 0, screenWidth, screenHeight);
        layeredPane.add(mainPanel, JLayeredPane.DEFAULT_LAYER);

        setupMusicManager();

        JPanel mainMenuScreen = new MainMenuPanel(this, screenWidth, screenHeight);
        JPanel gameScreen = new GameScreenPanel(this, screenWidth, screenHeight);
        JPanel aboutUsScreen = new AboutUsScreenPanel(this, screenWidth, screenHeight);
        JPanel loadingScreen = new LoadingScreenPanel(this, screenWidth, screenHeight);
        settingsPanel = new SettingsPanel(this, screenWidth, screenHeight);

        mainPanel.add(mainMenuScreen, "mainMenu");
        mainPanel.add(gameScreen, "gameScreen");
        mainPanel.add(aboutUsScreen, "aboutUsScreen");
        mainPanel.add(loadingScreen, "loadingScreen");
        mainPanel.add(settingsPanel, "settings");

        currentMusicPath = musicMap.get("mainMenu");
        if (currentMusicPath != null) {
            musicManager.fadeIn(currentMusicPath);
        }

        cardLayout.show(mainPanel, "mainMenu");
        window.setVisible(true);
    }

    private void setupKeyListener() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {
                if (e.getID() == KeyEvent.KEY_PRESSED && e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    openSettings();
                    return true;
                }
                return false;
            }
        });
    }

    public void openSettings() {
        settingsPanel.refreshSettings();
        cardLayout.show(mainPanel, "settings");
    }

    public void resumeFromSettings() {
        cardLayout.show(mainPanel, previousScreen);
    }

    private void setupMusicManager() {
        musicManager = new MusicManager();
        musicMap = new HashMap<>();
        musicMap.put("mainMenu", UIHelper.findMusicPath("main_menu_music"));
        musicMap.put("gameScreen", UIHelper.findMusicPath("game_ambient_music"));
        musicMap.put("aboutUsScreen", UIHelper.findMusicPath("main_menu_music"));
    }

    public void showScreen(String targetScreenName) {
        if (!targetScreenName.equals("settings") && !targetScreenName.equals("loadingScreen")) {
            previousScreen = targetScreenName;
        }

        cardLayout.show(mainPanel, "loadingScreen");

        String newMusicPath = musicMap.get(targetScreenName);
        boolean musicNeedsToChange = (newMusicPath != null && !newMusicPath.equals(currentMusicPath));

        if (musicNeedsToChange) {
            currentMusicPath = newMusicPath;

            musicManager.fadeOut(() -> {
                musicManager.fadeIn(newMusicPath);
                cardLayout.show(mainPanel, targetScreenName);
            });

        } else {
            int loadingDelay = random.nextInt(MAX_LOADING_DELAY - MIN_LOADING_DELAY) + MIN_LOADING_DELAY;

            Timer timer = new Timer(loadingDelay, e -> {
                cardLayout.show(mainPanel, targetScreenName);
            });
            timer.setRepeats(false);
            timer.start();
        }
    }

    public void setMusicVolume(float volume) {
        this.musicVolume = volume;
        musicManager.setVolume(volume);
    }

    public void setSfxVolume(float volume) {
        this.sfxVolume = volume;
        UIHelper.setSfxVolume(volume);
    }

    public float getMusicVolume() {
        return musicVolume;
    }

    public float getSfxVolume() {
        return sfxVolume;
    }

    public boolean isFullscreen() {
        return isFullscreen;
    }

    public void toggleFullscreen(boolean fullscreen) {
        this.isFullscreen = fullscreen;

        if (!fullscreen) {
            graphicsDevice.setFullScreenWindow(null);
            window.setUndecorated(false);
            window.setExtendedState(JFrame.MAXIMIZED_BOTH);
        } else {
            window.setUndecorated(true);
            if (graphicsDevice.isFullScreenSupported()) {
                graphicsDevice.setFullScreenWindow(window);
            }
        }
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }
}