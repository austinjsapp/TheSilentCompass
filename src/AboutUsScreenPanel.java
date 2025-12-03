import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.JTextArea;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;

public class AboutUsScreenPanel extends JPanel {

    private JTextArea descriptionTextArea;

    public AboutUsScreenPanel(Game game) {

        this.setLayout(null);

        ImagePanel backgroundPanel = new ImagePanel(UIHelper.findImagePath("about_us_background"));
        backgroundPanel.setBounds(0, 0, 800, 600);

        ShadowLabel line1 = new ShadowLabel("About Us");
        line1.setForeground(Color.white);
        line1.setFont(new Font("Serif", Font.PLAIN, 44));
        line1.setBounds(50, 50, 700, 60);
        line1.setHorizontalAlignment(SwingConstants.CENTER);

        ShadowLabel line2 = new ShadowLabel("A First-Person Survival Text Adventure");
        line2.setForeground(Color.white);
        line2.setFont(new Font("Serif", Font.PLAIN, 28));
        line2.setBounds(50, 120, 700, 35);
        line2.setHorizontalAlignment(SwingConstants.CENTER);

        String bodyText = "Built by beginner developers Austin Sapp and Caden Saiza " +
                "using our custom Java engine, The Silent Compass is a passion " +
                "project designed to test the limits of text-based survival, " +
                "where every decision determines your fate.\n\n" +

                "Your journey begins when your canoe is destroyed in rough " +
                "river rapids, leaving you stranded deep within a dense, " +
                "uncharted tropical jungle. You are alone, and your only " +
                "goal is simple: find civilization.\n\n" +

                "The game runs entirely in IntelliJ. You interact with " +
                "the world by selecting any of the choices displayed.";

        // --- UPDATED TEXT AREA WITH SHADOW LOGIC ---
        descriptionTextArea = new JTextArea(bodyText) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;

                // 1. Draw the Shadow (Black, shifted +2, +2)
                g2.translate(2, 2);
                Color originalColor = getForeground();
                setForeground(Color.BLACK); // Force shadow color
                super.paintComponent(g2);   // Paint the text

                // 2. Draw the Main Text (White, shifted back)
                g2.translate(-2, -2);
                setForeground(originalColor); // Restore white color
                super.paintComponent(g2);     // Paint the text again
            }
        };

        descriptionTextArea.setFont(new Font("Serif", Font.PLAIN, 20));
        descriptionTextArea.setForeground(Color.white);
        descriptionTextArea.setOpaque(false); // Make sure it's transparent!
        descriptionTextArea.setEditable(false);
        descriptionTextArea.setWrapStyleWord(true);
        descriptionTextArea.setLineWrap(true);
        descriptionTextArea.setFocusable(false);

        descriptionTextArea.setBounds(75, 175, 650, 310);

        JButton backButton = UIHelper.createMenuButton("BACK TO MENU");
        backButton.setBounds(25, 500, 400, 50);
        backButton.addActionListener(e -> game.showScreen("mainMenu"));

        this.add(line1);
        this.add(line2);
        this.add(descriptionTextArea);
        this.add(backButton);
        this.add(backgroundPanel);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // This draws the darkened box behind the text
        if (descriptionTextArea != null) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setColor(new Color(0, 0, 0, 220));
            g2d.fillRoundRect(descriptionTextArea.getX() - 10,
                    descriptionTextArea.getY() - 10,
                    descriptionTextArea.getWidth() + 20,
                    descriptionTextArea.getHeight() + 20,
                    15, 15);
            g2d.dispose();
        }
    }
}