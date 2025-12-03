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
import java.awt.Image;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;

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

    // 1. Add reference to CutscenePanel
    private CutscenePanel cutscenePanel;

    public static void main(String[] args) {
        new Game();
    }

    public Game() {
        window = new JFrame("The Silent Compass");
        window.setExtendedState(JFrame.MAXIMIZED_BOTH);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        window.getContentPane().setBackground(Color.BLACK);
        window.setLayout(new GridBagLayout());

        layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(800, 600));
        window.add(layeredPane);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        mainPanel.setBounds(0, 0, 800, 600);
        layeredPane.add(mainPanel, JLayeredPane.DEFAULT_LAYER);

        // --- Icons ---
        int iconSize = 50;
        ImageIcon originalUnmuteIcon = new ImageIcon(UIHelper.findImagePath("musiciconunmuted"));
        ImageIcon originalMuteIcon = new ImageIcon(UIHelper.findImagePath("musiciconmuted"));

        Image scaledUnmuteImg = originalUnmuteIcon.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH);
        Image scaledMuteImg = originalMuteIcon.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH);

        unmuteIcon = new ImageIcon(scaledUnmuteImg);
        muteIcon = new ImageIcon(scaledMuteImg);

        muteButton = new JButton();
        muteButton.setIcon(unmuteIcon);
        muteButton.setBounds(720, 500, iconSize, iconSize);
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

        // 2. Initialize CutscenePanel
        cutscenePanel = new CutscenePanel(this);

        mainPanel.add(mainMenuScreen, "mainMenu");
        mainPanel.add(gameScreen, "gameScreen");
        mainPanel.add(aboutUsScreen, "aboutUsScreen");
        mainPanel.add(loadingScreen, "loadingScreen");

        // 3. Add CutscenePanel to CardLayout
        mainPanel.add(cutscenePanel, "cutscene");

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

        // Created a helper to run after the delay/fade is done
        Runnable onTransitionComplete = () -> {
            cardLayout.show(mainPanel, targetScreenName);
            muteButton.setVisible(true);

            // If we just switched to the cutscene, start the timer logic!
            if (targetScreenName.equals("cutscene")) {
                cutscenePanel.startCutscene();
                muteButton.setVisible(false);
            }
        };

        if (musicNeedsToChange) {
            currentMusicPath = newMusicPath;

            musicManager.fadeOut(() -> {
                musicManager.fadeIn(newMusicPath);
                if (isMuted) {
                    musicManager.setMuted(true);
                }
                onTransitionComplete.run();
            });

        } else {
            int loadingDelay = random.nextInt(1500) + 1500;
            Timer timer = new Timer(loadingDelay, e -> {
                onTransitionComplete.run();
            });
            timer.setRepeats(false);
            timer.start();
        }
    }

    // 4. Helper for CutscenePanel to call when finished
    public void showGameScreenDirectly() {
        cardLayout.show(mainPanel, "gameScreen");
        muteButton.setVisible(true);

        String gameMusic = musicMap.get("gameScreen");
        if (gameMusic != null && !gameMusic.equals(currentMusicPath)) {
            // FIXED: Changed playMusic() to fadeIn()
            musicManager.fadeIn(gameMusic);
            currentMusicPath = gameMusic;
        }
    }
}