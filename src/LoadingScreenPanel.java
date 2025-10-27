import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.Color;
import java.awt.Font;
import java.util.Random;

// This is our new, "smart" loading screen panel.
public class LoadingScreenPanel extends JPanel {

    private ShadowLabel loadingText;
    private ShadowLabel tipText;
    private Timer animationTimer;
    private Random random;

    private final String[] loadingFrames = {"Loading.", "Loading..", "Loading..."};

    private final String[] survivalTips = {
            "Keep an eye on your hunger and thirst.",
            "Not all plants are safe to eat.",
            "A sharp tool is a friend.",
            "Listen for the sounds of the jungle; they can warn you of danger.",
            "Shelter is your first priority before nightfall."
    };

    private int currentFrame = 0;

    public LoadingScreenPanel(Game game) {

        this.setLayout(null);
        this.random = new Random();

        loadingText = new ShadowLabel("Loading...");
        loadingText.setForeground(Color.white);
        loadingText.setFont(new Font("Serif", Font.PLAIN, 52)); // Was 36
        loadingText.setBounds(100, 225, 600, 100);
        loadingText.setHorizontalAlignment(SwingConstants.CENTER);

        // SET UP THE "TIP" TEXT ---
        tipText = new ShadowLabel("Tip: Keep an eye on your hunger and thirst.");
        tipText.setForeground(Color.white);
        // FONT SIZE CHANGED
        tipText.setFont(new Font("Serif", Font.PLAIN, 24)); // Was 20
        tipText.setBounds(50, 325, 700, 50);
        tipText.setHorizontalAlignment(SwingConstants.CENTER);

        // SET UP THE ANIMATION TIMER
        animationTimer = new Timer(300, e -> {
            currentFrame = (currentFrame + 1) % loadingFrames.length;
            loadingText.setText(loadingFrames[currentFrame]);
        });
        animationTimer.setRepeats(true);

        // SET UP THE BACKGROUND
        ImagePanel backgroundPanel = new ImagePanel(UIHelper.findImagePath("loading_screen_background"));
        backgroundPanel.setBounds(0, 0, 800, 600);

        // SET UP THE "SMART" LISTENER
        this.addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                // This runs when the panel becomes VISIBLE
                selectRandomTip();
                resetAnimation();
                animationTimer.start();
            }

            @Override
            public void ancestorRemoved(AncestorEvent event) {
                // This runs when the panel becomes HIDDEN
                animationTimer.stop();
            }

            @Override
            public void ancestorMoved(AncestorEvent event) {
                // We don't need this one
            }
        });

        this.add(loadingText);
        this.add(tipText);
        this.add(backgroundPanel);
    }

    // (Helper methods are unchanged)
    private void selectRandomTip() {
        int index = random.nextInt(survivalTips.length);
        tipText.setText(survivalTips[index]);
    }

    private void resetAnimation() {
        currentFrame = 0;
        loadingText.setText(loadingFrames[0]);
    }
}