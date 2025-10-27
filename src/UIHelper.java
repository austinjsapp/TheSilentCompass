import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.FontMetrics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

// A helper class with static methods for creating UI elements and finding files.
public class UIHelper {

    // Create a single instance of SoundManager
    private static final SoundManager soundManager = new SoundManager();

    // Helper method to find images reliably
    public static String findImagePath(String keyword) {
        String projectPath = System.getProperty("user.dir");
        File resourcesDir = new File(projectPath, "resources");
        if (resourcesDir.exists() && resourcesDir.isDirectory()) {
            File[] files = resourcesDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.getName().toLowerCase().contains(keyword)) {
                        return file.getAbsolutePath();
                    }
                }
            }
        }
        System.err.println("Could not find image with keyword: " + keyword);
        return null;
    }

    // Helper method to find music files reliably
    public static String findMusicPath(String keyword) {
        String projectPath = System.getProperty("user.dir");
        File resourcesDir = new File(projectPath, "resources/music");
        if (resourcesDir.exists() && resourcesDir.isDirectory()) {
            File[] files = resourcesDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.getName().toLowerCase().contains(keyword)) {
                        return file.getAbsolutePath();
                    }
                }
            }
        }
        System.err.println("Could not find music with keyword: " + keyword);
        return null;
    }

    // A method to find sound effects
    public static String findSfxPath(String keyword) {
        String projectPath = System.getProperty("user.dir");
        File resourcesDir = new File(projectPath, "resources/sfx");
        if (resourcesDir.exists() && resourcesDir.isDirectory()) {
            File[] files = resourcesDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.getName().toLowerCase().contains(keyword)) {
                        return file.getAbsolutePath();
                    }
                }
            }
        }
        System.err.println("Could not find SFX with keyword: " + keyword);
        return null;
    }

    // --- REUSABLE METHOD ---
    /**
     * Finds and plays a sound effect file from the 'sfx' folder.
     * @param keyword The keyword in the SFX file name (e.g., "button_hover_sfx")
     */
    public static void playSound(String keyword) {
        String sfxPath = findSfxPath(keyword);
        if (sfxPath != null) {
            soundManager.playSfx(sfxPath);
        }
    }
    // --- END NEW METHOD ---


    // Helper method to create a styled menu button
    public static JButton createMenuButton(String text) {

        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                // (Custom shadow painting code)
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setFont(this.getFont());
                FontMetrics fm = g2.getFontMetrics();
                int stringWidth = fm.stringWidth(this.getText());
                int componentWidth = this.getWidth();
                int x;
                int alignment = this.getHorizontalAlignment();
                if (alignment == SwingConstants.CENTER) {
                    x = (componentWidth - stringWidth) / 2;
                } else if (alignment == SwingConstants.RIGHT) {
                    x = componentWidth - stringWidth;
                } else {
                    x = 0;
                }
                int y = (this.getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.setColor(new Color(0, 0, 0, 150));
                g2.drawString(this.getText(), x + 2, y + 2); // Using 2px offset
                g2.setColor(this.getForeground());
                g2.drawString(this.getText(), x, y);
                g2.dispose();
            }
        };

        button.setForeground(Color.white);
        button.setFont(new Font("Serif", Font.PLAIN, 44)); // Larger font
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);

        final Font originalFont = button.getFont();
        final Font hoverFont = originalFont.deriveFont(Font.PLAIN, 48f); // Larger hover font

        // --- MOUSE LISTENER ---
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                button.setFont(hoverFont);
                playSound("button_hover_sfx");
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                button.setFont(originalFont);
            }

            @Override
            public void mousePressed(MouseEvent evt) {
                playSound("button_press_sfx");
            }
        });
        // --- END OF MOUSE LISTENER ---

        return button;
    }
}