import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import java.awt.FontMetrics;

public class GameScreenPanel extends JPanel implements GameLogic.GameUICallback {

    private Game game;
    private ImagePanel backgroundPanel;
    private JTextArea mainTextArea;
    private JButton choice1, choice2, choice3, choice4;
    private ShadowLabel hpLabelNumber, weaponLabelName;
    private Font gameFont = new Font("Serif", Font.PLAIN, 26);
    private Font smallerGameFont = new Font("Serif", Font.PLAIN, 22);

    private Random random;
    private GameState gameState;
    private GameLogic gameLogic;

    private ShadowLabel hpBonusLabel, damageBonusLabel;

    private final String DEFAULT_BACKGROUND = "black_screen";

    // Optimization: Track if repaint needed
    private boolean needsRepaint = false;
    private String currentBackground = null;

    public GameScreenPanel(Game game) {
        this.game = game;
        this.setLayout(null);
        this.random = new Random();
        this.gameState = new GameState();
        this.gameLogic = new GameLogic(gameState, random, this);

        final int HEADER_Y = 15;
        final int COMPONENT_HEIGHT = 50;

        JPanel playerPanel = new JPanel();
        playerPanel.setBounds(50, HEADER_Y, 700, COMPONENT_HEIGHT);
        playerPanel.setOpaque(false);
        playerPanel.setLayout(null);

        ShadowLabel hpLabel = new ShadowLabel("Health:");
        hpLabel.setForeground(Color.white);
        hpLabel.setFont(gameFont);
        hpLabel.setBounds(0, 0, 100, COMPONENT_HEIGHT);
        playerPanel.add(hpLabel);

        hpLabelNumber = new ShadowLabel("");
        hpLabelNumber.setForeground(Color.white);
        hpLabelNumber.setFont(gameFont);
        hpLabelNumber.setBounds(100, 0, 35, COMPONENT_HEIGHT);
        playerPanel.add(hpLabelNumber);

        hpBonusLabel = new ShadowLabel("");
        hpBonusLabel.setForeground(new Color(150, 255, 150));
        hpBonusLabel.setFont(gameFont);
        hpBonusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        hpBonusLabel.setBounds(135, 0, 100, COMPONENT_HEIGHT);
        playerPanel.add(hpBonusLabel);

        ShadowLabel weaponLabel = new ShadowLabel("Weapon:");
        weaponLabel.setForeground(Color.white);
        weaponLabel.setFont(gameFont);
        weaponLabel.setBounds(280, 0, 100, COMPONENT_HEIGHT);
        playerPanel.add(weaponLabel);

        weaponLabelName = new ShadowLabel("");
        weaponLabelName.setForeground(Color.white);
        weaponLabelName.setFont(gameFont);
        weaponLabelName.setBounds(380, 0, 170, COMPONENT_HEIGHT);
        playerPanel.add(weaponLabelName);

        damageBonusLabel = new ShadowLabel("");
        damageBonusLabel.setForeground(new Color(255, 150, 150));
        damageBonusLabel.setFont(gameFont);
        damageBonusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        damageBonusLabel.setBounds(555, 0, 150, COMPONENT_HEIGHT);
        playerPanel.add(damageBonusLabel);

        mainTextArea = new JTextArea("");
        mainTextArea.setFont(new Font("Serif", Font.PLAIN, 24));
        mainTextArea.setForeground(Color.white);
        mainTextArea.setOpaque(false);
        mainTextArea.setEditable(false);
        mainTextArea.setWrapStyleWord(true);
        mainTextArea.setLineWrap(true);
        mainTextArea.setFocusable(false);
        mainTextArea.setBounds(75, 80, 650, 270);

        JPanel choiceButtonPanel = new JPanel();
        choiceButtonPanel.setBounds(75, 360, 650, 200);
        choiceButtonPanel.setOpaque(false);
        choiceButtonPanel.setLayout(new GridLayout(4, 1, 0, 10));

        ChoiceHandler choiceHandler = new ChoiceHandler();
        choice1 = createGameButton("");
        choice1.setActionCommand("c1");
        choice1.addActionListener(choiceHandler);
        choiceButtonPanel.add(choice1);

        choice2 = createGameButton("");
        choice2.setActionCommand("c2");
        choice2.addActionListener(choiceHandler);
        choiceButtonPanel.add(choice2);

        choice3 = createGameButton("");
        choice3.setActionCommand("c3");
        choice3.addActionListener(choiceHandler);
        choiceButtonPanel.add(choice3);

        choice4 = createGameButton("");
        choice4.setActionCommand("c4");
        choice4.addActionListener(choiceHandler);
        choiceButtonPanel.add(choice4);

        this.add(playerPanel);
        this.add(mainTextArea);
        this.add(choiceButtonPanel);

        backgroundPanel = new ImagePanel(null);
        backgroundPanel.setBounds(0, 0, 800, 600);
        this.add(backgroundPanel);

        playerSetup();
    }

    // Optimized button with cached FontMetrics
    private JButton createGameButton(String text) {
        JButton button = new JButton(text) {
            private FontMetrics cachedFontMetrics = null;

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setFont(this.getFont());

                // Cache FontMetrics
                if (cachedFontMetrics == null || !cachedFontMetrics.getFont().equals(this.getFont())) {
                    cachedFontMetrics = g2.getFontMetrics();
                }

                FontMetrics fm = cachedFontMetrics;
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
                g2.drawString(this.getText(), x + 2, y + 2);

                g2.setColor(this.getForeground());
                g2.drawString(this.getText(), x, y);
                g2.dispose();
            }
        };

        button.setForeground(Color.white);
        button.setFont(smallerGameFont);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.CENTER);

        final Font originalFont = button.getFont();
        final Font hoverFont = originalFont.deriveFont(Font.PLAIN, 24f);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setFont(hoverFont);
                UIHelper.playSound("button_hover_sfx");
                scheduleRepaint();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setFont(originalFont);
                scheduleRepaint();
            }

            @Override
            public void mousePressed(java.awt.event.MouseEvent evt) {
                UIHelper.playSound("button_press_sfx");
            }
        });

        return button;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (mainTextArea != null) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setColor(new Color(0, 0, 0, 220));
            g2d.fillRoundRect(mainTextArea.getX() - 10, mainTextArea.getY() - 10,
                    mainTextArea.getWidth() + 20, mainTextArea.getHeight() + 20, 15, 15);
            g2d.dispose();
        }
        needsRepaint = false;
    }

    // Optimization: Only repaint when needed
    private void scheduleRepaint() {
        if (!needsRepaint) {
            needsRepaint = true;
            SwingUtilities.invokeLater(this::repaint);
        }
    }

    public void playerSetup() {
        gameState.reset();
        weaponLabelName.setText(gameState.getPlayer().getWeapon());
        hpLabelNumber.setText("" + gameState.getPlayer().getHp());
        hpLabelNumber.setFont(gameFont);
        hpBonusLabel.setText("");
        damageBonusLabel.setText("");

        gameLogic.startGameNode();
    }

    // GameUICallback implementations
    @Override
    public void updateText(String text) {
        mainTextArea.setText(text);
        scheduleRepaint();
    }

    @Override
    public void setChoices(String text1, String text2, String text3, String text4) {
        choice1.setText(text1);
        choice1.setVisible(!text1.isEmpty());
        choice2.setText(text2);
        choice2.setVisible(!text2.isEmpty());
        choice3.setText(text3);
        choice3.setVisible(!text3.isEmpty());
        choice4.setText(text4);
        choice4.setVisible(!text4.isEmpty());
        scheduleRepaint();
    }

    @Override
    public void updatePlayerHP(int hp) {
        hpLabelNumber.setText("" + hp);
        scheduleRepaint();
    }

    @Override
    public void updatePlayerWeapon(String weapon) {
        weaponLabelName.setText(weapon);
        scheduleRepaint();
    }

    @Override
    public void setBackground(String imageName) {
        if (currentBackground == null || !currentBackground.equals(imageName)) {
            currentBackground = imageName;
            backgroundPanel.setImage(UIHelper.findImagePath(imageName));
            scheduleRepaint();
        }
    }

    @Override
    public void showHPBonus(String text) {
        hpBonusLabel.setText(text);
        scheduleRepaint();
    }

    @Override
    public void showDamageBonus(String text) {
        damageBonusLabel.setText(text);
        scheduleRepaint();
    }

    // Choice handler delegates to GameLogic
    public class ChoiceHandler implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            String yourChoice = event.getActionCommand();
            GameLocation position = gameState.getPosition();

            if (position == GameLocation.LOSE || position == GameLocation.THANK_YOU) {
                if (yourChoice.equals("c1")) {
                    playerSetup();
                } else if (yourChoice.equals("c2")) {
                    game.showScreen("mainMenu");
                }
                return;
            }

            if (position == GameLocation.ENDING_ESCAPE ||
                    position == GameLocation.ENDING_RESCUE ||
                    position == GameLocation.ENDING_LOST) {
                if (yourChoice.equals("c1")) {
                    gameLogic.thankYou();
                }
                return;
            }

            if (!gameState.getPlayer().isAlive()) {
                if (position != GameLocation.LOSE) {
                    gameLogic.lose("You succumbed to your injuries. Game Over.");
                }
                return;
            }

            handleChoice(yourChoice, position);
        }

        private void handleChoice(String choice, GameLocation position) {
            switch (position) {
                case START:
                    if (choice.equals("c1")) gameLogic.decisionStart();
                    break;

                case DECISION_START:
                    switch (choice) {
                        case "c1": gameLogic.searchWreckage(); break;
                        case "c2": gameLogic.riverPath(); break;
                        case "c3": gameLogic.junglePath(); break;
                    }
                    break;

                case SEARCH_WRECKAGE:
                    switch (choice) {
                        case "c1": gameLogic.wreckageDanger(); break;
                        case "c2": gameLogic.searchWreckageBag(); break;
                        case "c3": gameLogic.afterWreckageDecision(); break;
                    }
                    break;

                case WRECKAGE_DANGER:
                case SEARCH_WRECKAGE_BAG:
                    if (choice.equals("c1")) gameLogic.afterWreckageDecision();
                    break;

                case AFTER_WRECKAGE_DECISION:
                    if (choice.equals("c1")) gameLogic.riverPath();
                    else if (choice.equals("c2")) gameLogic.junglePath();
                    break;

                case RIVER_PATH:
                    if (choice.equals("c1")) gameLogic.decisionRiver();
                    break;

                case DECISION_RIVER:
                    switch (choice) {
                        case "c1": gameLogic.boarEncounter(); break;
                        case "c2": gameLogic.riverPathContinued(); break;
                        case "c3": gameLogic.junglePath(); break;
                        case "c4": gameLogic.findKit2(); break;
                    }
                    break;

                case RIVER_PATH_CONTINUED:
                    switch (choice) {
                        case "c1": gameLogic.riverEncounter(); break;
                        case "c2": gameLogic.waterfallCave(); break;
                        case "c3": gameLogic.junglePath(); break;
                    }
                    break;

                case WATERFALL_CAVE:
                    if (choice.equals("c1")) gameLogic.insideCave();
                    else if (choice.equals("c2")) gameLogic.riverPathContinued();
                    break;

                case INSIDE_CAVE:
                    if (choice.equals("c1")) gameLogic.riverPathContinued();
                    break;

                case FIND_KIT_2:
                    if (choice.equals("c1")) gameLogic.decisionRiver();
                    break;

                case JUNGLE_PATH:
                    if (choice.equals("c1")) gameLogic.findSpear();
                    else if (choice.equals("c2")) gameLogic.decisionJungle();
                    break;

                case FIND_SPEAR:
                    if (choice.equals("c1")) gameLogic.decisionJungle();
                    break;

                case DECISION_JUNGLE:
                    switch (choice) {
                        case "c1": gameLogic.jungleEncounter(); break;
                        case "c2": gameLogic.quicksandSwamp(); break;
                        case "c3": gameLogic.giantTree(); break;
                        case "c4": gameLogic.findKit3(); break;
                    }
                    break;

                case GIANT_TREE:
                    if (choice.equals("c1")) gameLogic.spiderEncounter();
                    else if (choice.equals("c2")) gameLogic.decisionJungle();
                    break;

                case AFTER_SPIDER_FIGHT:
                    if (choice.equals("c1")) gameLogic.decisionJungle();
                    break;

                case FIND_KIT_3:
                    if (choice.equals("c1")) gameLogic.decisionJungle();
                    break;

                case QUICKSAND_SWAMP:
                    if (choice.equals("c1")) gameLogic.decisionJungle();
                    break;

                case DECISION_RIVER_DAMAGE:
                    switch (choice) {
                        case "c1": gameLogic.murkyDelta(); break;
                        case "c2": gameLogic.junglePath(); break;
                    }
                    break;

                case MURKY_DELTA:
                    switch (choice) {
                        case "c1": gameLogic.deltaIsland(); break;
                        case "c2": gameLogic.giantCrabEncounter(); break;
                        case "c3": gameLogic.findMainChannel(); break;
                    }
                    break;

                case DELTA_ISLAND:
                    if (choice.equals("c1")) {
                        if (mainTextArea.getText().contains("bursts from the sand")) {
                            gameLogic.giantCrabEncounter();
                        } else {
                            gameLogic.murkyDelta();
                        }
                    }
                    break;

                case FIND_MAIN_CHANNEL:
                    if (choice.equals("c1")) {
                        if (mainTextArea.getText().contains("ridge")) {
                            gameLogic.decisionFinal();
                        } else {
                            gameLogic.murkyDelta();
                        }
                    }
                    break;

                case FIND_PENDANT:
                    if (choice.equals("c1")) gameLogic.afterPendant();
                    break;

                case AFTER_PENDANT:
                    if (choice.equals("c1")) gameLogic.ancientRuins();
                    break;

                case ANCIENT_RUINS:
                    switch (choice) {
                        case "c1": gameLogic.ruinsCrypt(); break;
                        case "c2": gameLogic.ruinsBuilding(); break;
                        case "c3": gameLogic.decisionFinal(); break;
                    }
                    break;

                case RUINS_CRYPT:
                    if (choice.equals("c1")) gameLogic.stalkerEncounter();
                    break;

                case RUINS_BUILDING:
                    if (choice.equals("c1")) gameLogic.ancientRuins();
                    break;

                case AFTER_STALKER_FIGHT:
                    if (choice.equals("c1")) gameLogic.ancientRuins();
                    break;

                case DECISION_FINAL:
                    handleFinalDecision(choice);
                    break;

                case BRIDGE_CROSS_SUCCESS:
                    if (choice.equals("c1")) gameLogic.endingEscape();
                    break;

                case WEAPON_CHOICE:
                    if (choice.equals("c1")) {
                        gameState.getPlayer().setWeapon(gameState.getNewWeaponFound());
                        updatePlayerWeapon(gameState.getPlayer().getWeapon());
                    }
                    gameLogic.resumePath(gameState.getPostWeaponChoicePosition());
                    break;

                // IMPROVED: Enemy encounters with flee bonus
                case ENEMY_ENCOUNTER:
                    if (choice.equals("c1")) gameLogic.fight();
                    else if (choice.equals("c2")) {
                        // IMPROVED: 30% base flee chance + enemy-specific bonus
                        int fleeChance = 30 + gameState.getCurrentEnemy().getFleeSuccessBonus();
                        gameLogic.fleeResult(random.nextInt(100) < fleeChance);
                    }
                    break;

                // IMPROVED: Combat with flee bonus
                case FIGHT:
                    switch (choice) {
                        case "c1": gameLogic.playerAttack(); break;
                        case "c2": gameLogic.playerHeal(); break;
                        case "c3":
                            // IMPROVED: 30% base flee chance + enemy-specific bonus
                            int fleeChance = 30 + gameState.getCurrentEnemy().getFleeSuccessBonus();
                            gameLogic.fleeResult(random.nextInt(100) < fleeChance);
                            break;
                    }
                    break;

                case PLAYER_ATTACK:
                    if (!gameState.getCurrentEnemy().isAlive()) {
                        gameLogic.winFight();
                    } else {
                        gameLogic.enemyAttack();
                    }
                    break;

                case PLAYER_HEAL:
                    gameLogic.enemyAttack();
                    break;

                case ENEMY_ATTACK:
                case ENEMY_ATTACK_DISPLAY:
                    if (!gameState.getPlayer().isAlive()) {
                        gameLogic.lose("You were killed by the " + gameState.getCurrentEnemy().getName() + ". Game Over.");
                    } else {
                        gameLogic.fight();
                    }
                    break;

                case WIN_FIGHT:
                case FLEE_RESULT:
                    if (choice.equals("c1")) {
                        gameLogic.resumePath(gameState.getPostCombatPosition());
                    }
                    break;
            }
        }

        // IMPROVED: Better bridge crossing odds
        private void handleFinalDecision(String choice) {
            switch (choice) {
                case "c1":
                    if (gameState.getPlayer().hasPendant()) {
                        gameLogic.endingRescue();
                    } else {
                        gameLogic.endingLost();
                    }
                    break;
                case "c2":
                    if (gameState.getPlayer().hasCompass()) {
                        updateText("You check your compass... it seems to waver, pointing to the left side of the bridge. You cross carefully, avoiding the weak planks on the right.");
                        setChoices("Continue", "", "", "");
                        gameState.setPosition(GameLocation.BRIDGE_CROSS_SUCCESS);
                    } else if (random.nextInt(100) < 60) { // IMPROVED: 60% success instead of 50%
                        updateText("You test every plank, moving slowly. Your heart pounds. After an eternity, you reach the other side!");
                        setChoices("Continue", "", "", "");
                        gameState.setPosition(GameLocation.BRIDGE_CROSS_SUCCESS);
                    } else {
                        gameLogic.lose("You step on a rotten plank. It snaps, and you fall into the chasm. Game Over.");
                    }
                    break;
                case "c3":
                    gameLogic.endingLost();
                    break;
            }
        }
    }
}