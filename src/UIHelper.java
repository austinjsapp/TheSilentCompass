import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Helper class for UI creation and resource management.
 */
public class UIHelper {

    private static final SoundManager soundManager = new SoundManager();

    // Resource path finding methods
    public static String findImagePath(String keyword) {
        return findResourcePath("resources", keyword);
    }

    public static String findMusicPath(String keyword) {
        return findResourcePath("resources/music", keyword);
    }

    public static String findSfxPath(String keyword) {
        return findResourcePath("resources/sfx", keyword);
    }

    /**
     * Generic resource finder to reduce code duplication
     */
    private static String findResourcePath(String directory, String keyword) {
        String projectPath = System.getProperty("user.dir");
        File resourcesDir = new File(projectPath, directory);

        if (resourcesDir.exists() && resourcesDir.isDirectory()) {
            File[] files = resourcesDir.listFiles();
            if (files != null) {
                String lowerKeyword = keyword.toLowerCase();
                for (File file : files) {
                    if (file.getName().toLowerCase().contains(lowerKeyword)) {
                        return file.getAbsolutePath();
                    }
                }
            }
        }
        System.err.println("Could not find resource with keyword: " + keyword + " in " + directory);
        return null;
    }

    public static void playSound(String keyword) {
        String sfxPath = findSfxPath(keyword);
        if (sfxPath != null) {
            soundManager.playSfx(sfxPath);
        }
    }

    /**
     * Creates a styled menu button with optional scaling support
     */
    public static JButton createMenuButton(String text) {
        return createMenuButton(text, 1.0);
    }

    public static JButton createMenuButton(String text, double scale) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setFont(this.getFont());

                FontMetrics fm = g2.getFontMetrics();
                int stringWidth = fm.stringWidth(this.getText());
                int componentWidth = this.getWidth();

                int x = switch (this.getHorizontalAlignment()) {
                    case SwingConstants.CENTER -> (componentWidth - stringWidth) / 2;
                    case SwingConstants.RIGHT -> componentWidth - stringWidth;
                    default -> 0;
                };

                int y = (this.getHeight() - fm.getHeight()) / 2 + fm.getAscent();

                // Draw shadow
                g2.setColor(new Color(0, 0, 0, 150));
                g2.drawString(this.getText(), x + 2, y + 2);

                // Draw text
                g2.setColor(this.getForeground());
                g2.drawString(this.getText(), x, y);
                g2.dispose();
            }
        };

        button.setForeground(Color.white);
        button.setFont(new Font("Serif", Font.PLAIN, (int)(44 * scale)));
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.CENTER);

        final Font originalFont = button.getFont();
        final Font hoverFont = originalFont.deriveFont(Font.PLAIN, (float)(48 * scale));

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

        return button;
    }
}