import javax.swing.*;
import java.awt.*;

/**
 * Settings panel with black/white theme and blue sliders.
 */
public class SettingsPanel extends JPanel {

    private Game game;
    private int screenWidth;
    private int screenHeight;
    private double scale;

    private JSlider musicVolumeSlider;
    private JSlider sfxVolumeSlider;
    private ShadowLabel musicLabel;
    private ShadowLabel sfxLabel;

    public SettingsPanel(Game game, int screenWidth, int screenHeight) {
        this.game = game;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;

        setLayout(null);
        setBackground(Color.BLACK);

        double scaleX = screenWidth / 800.0;
        double scaleY = screenHeight / 600.0;
        this.scale = Math.min(scaleX, scaleY);

        int centerX = screenWidth / 2;

        // Title
        ShadowLabel titleLabel = new ShadowLabel("SETTINGS");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Serif", Font.PLAIN, (int)(56 * scale)));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBounds(0, (int)(50 * scaleY), screenWidth, (int)(80 * scaleY));

        // Music Volume Label with value
        musicLabel = new ShadowLabel("Music Volume: 1.00");
        musicLabel.setForeground(Color.WHITE);
        musicLabel.setFont(new Font("Serif", Font.PLAIN, (int)(28 * scale)));
        musicLabel.setHorizontalAlignment(SwingConstants.CENTER);
        musicLabel.setBounds(0, (int)(170 * scaleY), screenWidth, (int)(40 * scaleY));

        // Music slider
        musicVolumeSlider = createStyledSlider();
        int sliderWidth = (int)(500 * scaleX);
        musicVolumeSlider.setBounds(centerX - sliderWidth / 2, (int)(220 * scaleY), sliderWidth, (int)(40 * scaleY));
        musicVolumeSlider.addChangeListener(e -> {
            float volume = musicVolumeSlider.getValue() / 100.0f;
            float adjustedVolume = (float)(Math.pow(volume, 2));
            game.setMusicVolume(adjustedVolume);
            musicLabel.setText(String.format("Music Volume: %.2f", volume));
            musicVolumeSlider.repaint();
        });

        // SFX Volume Label with value
        sfxLabel = new ShadowLabel("Sound Effects Volume: 1.00");
        sfxLabel.setForeground(Color.WHITE);
        sfxLabel.setFont(new Font("Serif", Font.PLAIN, (int)(28 * scale)));
        sfxLabel.setHorizontalAlignment(SwingConstants.CENTER);
        sfxLabel.setBounds(0, (int)(290 * scaleY), screenWidth, (int)(40 * scaleY));

        // SFX slider
        sfxVolumeSlider = createStyledSlider();
        sfxVolumeSlider.setBounds(centerX - sliderWidth / 2, (int)(340 * scaleY), sliderWidth, (int)(40 * scaleY));
        sfxVolumeSlider.addChangeListener(e -> {
            float volume = sfxVolumeSlider.getValue() / 100.0f;
            float adjustedVolume = (float)(Math.pow(volume, 2));
            game.setSfxVolume(adjustedVolume);
            sfxLabel.setText(String.format("Sound Effects Volume: %.2f", volume));
            sfxVolumeSlider.repaint();
        });

        // Buttons
        int buttonWidth = (int)(250 * scaleX);
        int buttonHeight = (int)(50 * scaleY);

        JButton resumeButton = UIHelper.createMenuButton("RESUME GAME", scale);
        resumeButton.setBounds(centerX - buttonWidth / 2, (int)(420 * scaleY), buttonWidth, buttonHeight);
        resumeButton.addActionListener(e -> game.resumeFromSettings());

        JButton mainMenuButton = UIHelper.createMenuButton("MAIN MENU", scale);
        mainMenuButton.setBounds(centerX - buttonWidth / 2, (int)(480 * scaleY), buttonWidth, buttonHeight);
        mainMenuButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(this, "Return to main menu?\n(Unsaved progress will be lost)", "Confirm", JOptionPane.YES_NO_OPTION);
            if (result == JOptionPane.YES_OPTION) {
                game.showScreen("mainMenu");
            }
        });

        add(titleLabel);
        add(musicLabel);
        add(musicVolumeSlider);
        add(sfxLabel);
        add(sfxVolumeSlider);
        add(resumeButton);
        add(mainMenuButton);
    }

    private JSlider createStyledSlider() {
        JSlider slider = new JSlider(0, 100, 100);
        slider.setOpaque(true);
        slider.setBackground(Color.BLACK);
        slider.setFocusable(false);

        slider.setUI(new javax.swing.plaf.basic.BasicSliderUI(slider) {
            @Override
            public void paintTrack(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();

                // Clear the entire area first
                g2d.setColor(Color.BLACK);
                g2d.fillRect(0, 0, slider.getWidth(), slider.getHeight());

                int trackHeight = 10;
                int trackTop = (trackRect.height - trackHeight) / 2 + trackRect.y;

                // Draw complete background track first (light gray)
                g2d.setColor(new Color(200, 200, 200));
                g2d.fillRect(trackRect.x, trackTop, trackRect.width, trackHeight);

                // Draw filled portion on top (blue)
                int fillWidth = (int) ((slider.getValue() / 100.0) * trackRect.width);
                g2d.setColor(new Color(70, 130, 255));
                g2d.fillRect(trackRect.x, trackTop, fillWidth, trackHeight);

                g2d.dispose();
            }

            @Override
            public void paintThumb(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();

                int thumbWidth = 20;
                int thumbHeight = 20;
                int x = thumbRect.x + (thumbRect.width - thumbWidth) / 2;
                int y = thumbRect.y + (thumbRect.height - thumbHeight) / 2;

                // White square thumb
                g2d.setColor(Color.WHITE);
                g2d.fillRect(x, y, thumbWidth, thumbHeight);

                // Dark border
                g2d.setColor(new Color(100, 100, 100));
                g2d.drawRect(x, y, thumbWidth - 1, thumbHeight - 1);

                g2d.dispose();
            }
        });

        return slider;
    }

    public void refreshSettings() {
        float musicLinear = (float)Math.sqrt(game.getMusicVolume());
        float sfxLinear = (float)Math.sqrt(game.getSfxVolume());

        musicVolumeSlider.setValue((int)(musicLinear * 100));
        sfxVolumeSlider.setValue((int)(sfxLinear * 100));

        musicLabel.setText(String.format("Music Volume: %.2f", musicLinear));
        sfxLabel.setText(String.format("Sound Effects Volume: %.2f", sfxLinear));
    }
}