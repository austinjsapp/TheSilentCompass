import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import java.awt.FontMetrics;

public class GameScreenPanel extends JPanel {

    private Game game;

    private ImagePanel backgroundPanel;
    private JTextArea mainTextArea;
    private JPanel choiceButtonPanel;
    private JButton choice1, choice2, choice3, choice4;
    private ShadowLabel hpLabelNumber, weaponLabelName, levelXpLabel;
    private Font gameFont;
    private Font smallerGameFont;

    private Random random;

    private Player player;
    private Enemy currentEnemy;

    private ShadowLabel hpBonusLabel, damageBonusLabel;

    private String newWeaponFound;
    private GameLocation postWeaponChoicePosition;

    private int screenWidth;
    private int screenHeight;
    private double scale;

    private enum GameLocation {
        START, DECISION_START, SEARCH_WRECKAGE, WRECKAGE_DANGER, SEARCH_WRECKAGE_BAG, AFTER_WRECKAGE_DECISION,
        RIVER_PATH, DECISION_RIVER, RIVER_PATH_CONTINUED, WATERFALL_CAVE, INSIDE_CAVE, BOAR_ENCOUNTER,
        JUNGLE_PATH, FIND_SPEAR, DECISION_JUNGLE, GIANT_TREE, SPIDER_ENCOUNTER, AFTER_SPIDER_FIGHT,
        QUICKSAND_SWAMP, FIND_KIT_2, FIND_KIT_3,
        ENEMY_ENCOUNTER, FIGHT, PLAYER_ATTACK, PLAYER_HEAL, ENEMY_ATTACK,
        WIN_FIGHT, FLEE_RESULT, ENEMY_ATTACK_DISPLAY,
        DECISION_RIVER_DAMAGE, FIND_PENDANT, AFTER_PENDANT,
        DECISION_FINAL, BRIDGE_CROSS_SUCCESS, ENDING_ESCAPE, ENDING_RESCUE, ENDING_LOST,
        LOSE, THANK_YOU,
        WEAPON_CHOICE,

        MURKY_DELTA, DELTA_ISLAND, FIND_MAIN_CHANNEL, GIANT_CRAB_ENCOUNTER,
        ANCIENT_RUINS, RUINS_CRYPT, RUINS_BUILDING, STALKER_ENCOUNTER, AFTER_STALKER_FIGHT
    }

    private GameLocation position;
    private GameLocation postCombatPosition;

    private final String DEFAULT_BACKGROUND = "black_screen";

    public GameScreenPanel(Game game, int screenWidth, int screenHeight) {
        this.game = game;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.setLayout(null);
        this.random = new Random();

        double scaleX = screenWidth / 800.0;
        double scaleY = screenHeight / 600.0;
        this.scale = Math.min(scaleX, scaleY);

        this.gameFont = new Font("Serif", Font.PLAIN, (int)(26 * scale));
        this.smallerGameFont = new Font("Serif", Font.PLAIN, (int)(22 * scale));

        final int HEADER_Y = (int)(15 * scaleY);
        final int COMPONENT_HEIGHT = (int)(50 * scaleY);

        JPanel playerPanel = new JPanel();
        playerPanel.setBounds((int)(50 * scaleX), HEADER_Y, (int)(700 * scaleX), (int)(80 * scaleY));
        playerPanel.setOpaque(false);
        playerPanel.setLayout(null);

        ShadowLabel hpLabel = new ShadowLabel("Health:");
        hpLabel.setForeground(Color.white);
        hpLabel.setFont(gameFont);
        hpLabel.setBounds(0, 0, (int)(100 * scaleX), COMPONENT_HEIGHT);
        playerPanel.add(hpLabel);

        hpLabelNumber = new ShadowLabel("");
        hpLabelNumber.setForeground(Color.white);
        hpLabelNumber.setFont(gameFont);
        hpLabelNumber.setBounds((int)(100 * scaleX), 0, (int)(35 * scaleX), COMPONENT_HEIGHT);
        playerPanel.add(hpLabelNumber);

        hpBonusLabel = new ShadowLabel("");
        hpBonusLabel.setForeground(new Color(150, 255, 150));
        hpBonusLabel.setFont(gameFont);
        hpBonusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        hpBonusLabel.setBounds((int)(135 * scaleX), 0, (int)(100 * scaleX), COMPONENT_HEIGHT);
        playerPanel.add(hpBonusLabel);

        ShadowLabel weaponLabel = new ShadowLabel("Weapon:");
        weaponLabel.setForeground(Color.white);
        weaponLabel.setFont(gameFont);
        weaponLabel.setBounds((int)(280 * scaleX), 0, (int)(100 * scaleX), COMPONENT_HEIGHT);
        playerPanel.add(weaponLabel);

        weaponLabelName = new ShadowLabel("");
        weaponLabelName.setForeground(Color.white);
        weaponLabelName.setFont(gameFont);
        weaponLabelName.setBounds((int)(380 * scaleX), 0, (int)(170 * scaleX), COMPONENT_HEIGHT);
        playerPanel.add(weaponLabelName);

        damageBonusLabel = new ShadowLabel("");
        damageBonusLabel.setForeground(new Color(255, 150, 150));
        damageBonusLabel.setFont(gameFont);
        damageBonusLabel.setHorizontalAlignment(SwingConstants.LEFT);
        damageBonusLabel.setBounds((int)(555 * scaleX), 0, (int)(150 * scaleX), COMPONENT_HEIGHT);
        playerPanel.add(damageBonusLabel);

        levelXpLabel = new ShadowLabel("");
        levelXpLabel.setForeground(new Color(255, 215, 0)); // Gold color for level/XP
        levelXpLabel.setFont(gameFont);
        levelXpLabel.setHorizontalAlignment(SwingConstants.LEFT);
        levelXpLabel.setBounds(0, (int)(35 * scaleY), (int)(400 * scaleX), COMPONENT_HEIGHT);
        playerPanel.add(levelXpLabel);

        this.player = new Player(20, "Pocket Knife");

        mainTextArea = new JTextArea("");
        mainTextArea.setFont(new Font("Serif", Font.PLAIN, (int)(24 * scale)));
        mainTextArea.setForeground(Color.white);
        mainTextArea.setOpaque(false);
        mainTextArea.setEditable(false);
        mainTextArea.setWrapStyleWord(true);
        mainTextArea.setLineWrap(true);
        mainTextArea.setFocusable(false);
        mainTextArea.setBounds((int)(75 * scaleX), (int)(80 * scaleY), (int)(650 * scaleX), (int)(250 * scaleY));

        choiceButtonPanel = new JPanel();
        choiceButtonPanel.setBounds((int)(75 * scaleX), (int)(350 * scaleY), (int)(650 * scaleX), (int)(200 * scaleY));
        choiceButtonPanel.setOpaque(false);
        choiceButtonPanel.setLayout(new GridLayout(4, 1, 0, (int)(10 * scaleY)));

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
        backgroundPanel.setBounds(0, 0, screenWidth, screenHeight);
        this.add(backgroundPanel);

        playerSetup();
    }

    private JButton createGameButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
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
                }
                else if (alignment == SwingConstants.RIGHT) {
                    x = componentWidth - stringWidth;
                }
                else {
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
        final Font hoverFont = originalFont.deriveFont(Font.PLAIN, (float)(24 * scale));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setFont(hoverFont);
                UIHelper.playSound("button_hover_sfx");
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setFont(originalFont);
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
        if (mainTextArea != null && choiceButtonPanel != null) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setColor(new Color(0, 0, 0, 220));
            // Calculate the combined bounds to cover both text area and button panel
            int bgX = mainTextArea.getX() - 10;
            int bgY = mainTextArea.getY() - 10;
            int bgWidth = mainTextArea.getWidth() + 20;
            int bgHeight = (choiceButtonPanel.getY() + choiceButtonPanel.getHeight()) - mainTextArea.getY() + 10;
            g2d.fillRoundRect(bgX, bgY, bgWidth, bgHeight, 15, 15);
            g2d.dispose();
        }
    }

    public void playerSetup() {
        player.reset(20, "Pocket Knife");
        weaponLabelName.setText(player.getWeapon());
        hpLabelNumber.setText("" + player.getHp());
        hpLabelNumber.setFont(gameFont);
        updateLevelXpDisplay();

        startGameNode();
    }

    private void updateLevelXpDisplay() {
        levelXpLabel.setText("Level: " + player.getLevel() + " | XP: " + player.getXp() + "/" + player.getXpToNextLevel());
    }

    private void setChoices(String text1, String text2, String text3, String text4) {
        choice1.setText(text1); choice1.setVisible(!text1.isEmpty());
        choice2.setText(text2); choice2.setVisible(!text2.isEmpty());
        choice3.setText(text3); choice3.setVisible(!text3.isEmpty());
        choice4.setText(text4); choice4.setVisible(!text4.isEmpty());
    }

    private void resumePath(GameLocation path) {
        switch (path) {
            case DECISION_RIVER_DAMAGE:
                decisionRiverDamage();
                break;
            case FIND_PENDANT:
                findPendant();
                break;
            case RIVER_PATH_CONTINUED:
                riverPathContinued();
                break;
            case AFTER_SPIDER_FIGHT:
                afterSpiderFight();
                break;
            case AFTER_WRECKAGE_DECISION:
                afterWreckageDecision();
                break;
            case DECISION_JUNGLE:
                decisionJungle();
                break;
            case MURKY_DELTA:
                murkyDelta();
                break;
            case ANCIENT_RUINS:
                ancientRuins();
                break;
            case AFTER_STALKER_FIGHT:
                afterStalkerFight();
                break;
            default:
                decisionStart();
                break;
        }
    }

    public void startGameNode() {
        position = GameLocation.START;
        backgroundPanel.setImage(UIHelper.findImagePath(DEFAULT_BACKGROUND));
        mainTextArea.setText("You wake up beside your shattered canoe on a river bank.\nThe dense jungle stretches before you. Survival is key.");
        setChoices("Continue", "", "", "");
    }

    public void decisionStart() {
        position = GameLocation.DECISION_START;
        backgroundPanel.setImage(UIHelper.findImagePath(DEFAULT_BACKGROUND));
        mainTextArea.setText("You stand up and check your surroundings. What is your first move?");
        setChoices("Search wreckage for supplies", "Follow the riverbank", "Head into the jungle", "");
    }

    public void searchWreckage() {
        position = GameLocation.SEARCH_WRECKAGE;
        backgroundPanel.setImage(UIHelper.findImagePath(DEFAULT_BACKGROUND));
        mainTextArea.setText("The wreckage is unstable. You see a glint of metal under a heavy plank and a mostly-dry bag.");
        setChoices("Try for the metal (Dangerous)", "Check the dry bag", "Leave it alone", "");
    }

    public void wreckageDanger() {
        position = GameLocation.WRECKAGE_DANGER;
        backgroundPanel.setImage(UIHelper.findImagePath(DEFAULT_BACKGROUND));

        int chance = random.nextInt(100);

        if (chance < 30) {
            lose("You try to move the plank, but the whole pile shifts and crushes you. Game Over.");
        } else if (chance < 70) {
            String newWeapon = "Rusty Pipe";
            if (player.getWeapon().equals(newWeapon)) {
                player.gainXP(5);
                mainTextArea.setText("You carefully pull a... Rusty Pipe! But you already have one.\n\n++5 XP for survival++");
                setChoices("Continue", "", "", "");
            } else {
                weaponChoice(newWeapon, GameLocation.AFTER_WRECKAGE_DECISION);
            }
        } else {
            player.gainXP(5);
            player.takeDamage(2);
            hpLabelNumber.setText("" + player.getHp());
            mainTextArea.setText("You cut your hand on a sharp edge but find nothing useful.\n(You lost 2 Health)\n\n++5 XP for survival++");
            setChoices("Continue", "", "", "");
        }
    }

    public void searchWreckageBag() {
        position = GameLocation.SEARCH_WRECKAGE_BAG;
        backgroundPanel.setImage(UIHelper.findImagePath(DEFAULT_BACKGROUND));
        player.heal(random, 5, 0);
        player.gainXP(5);
        hpLabelNumber.setText("" + player.getHp());
        mainTextArea.setText("You find a soggy First Aid Kit! You use the remaining supplies.\n(Healed 5 Health)\n\n++5 XP++");
        setChoices("Continue", "", "", "");
    }

    public void afterWreckageDecision() {
        position = GameLocation.AFTER_WRECKAGE_DECISION;
        backgroundPanel.setImage(UIHelper.findImagePath(DEFAULT_BACKGROUND));
        mainTextArea.setText("You step away from the wreckage and survey the area again.");
        setChoices("Follow the riverbank", "Head into the jungle", "", "");
    }

    public void riverPath() {
        position = GameLocation.RIVER_PATH;
        backgroundPanel.setImage(UIHelper.findImagePath(DEFAULT_BACKGROUND));
        mainTextArea.setText("You decide to follow the river. The water looks murky, but it's a clear path.");
        setChoices("Continue", "", "", "");
    }

    public void decisionRiver() {
        position = GameLocation.DECISION_RIVER;
        backgroundPanel.setImage(UIHelper.findImagePath(DEFAULT_BACKGROUND));
        mainTextArea.setText("You walk for an hour. The riverbank is muddy and difficult.\nYou hear a loud snorting noise in the bushes nearby.");
        setChoices("Investigate the noise (Boar)", "Continue along riverbank", "Wander off into the jungle", "Find a place to rest");
    }

    public void riverPathContinued() {
        position = GameLocation.RIVER_PATH_CONTINUED;
        backgroundPanel.setImage(UIHelper.findImagePath(DEFAULT_BACKGROUND));
        mainTextArea.setText("You continue along the main river. The bank is muddy. You see a small tributary breaking off to the left.");
        setChoices("Follow main river (Snake)", "Explore the tributary", "Go back to the jungle path", "");
    }

    public void waterfallCave() {
        position = GameLocation.WATERFALL_CAVE;
        backgroundPanel.setImage(UIHelper.findImagePath(DEFAULT_BACKGROUND));
        mainTextArea.setText("The tributary leads to a small, beautiful waterfall. You notice a dark opening behind the curtain of water.");
        setChoices("Investigate the cave", "Return to the river", "", "");
    }

    public void insideCave() {
        position = GameLocation.INSIDE_CAVE;
        backgroundPanel.setImage(UIHelper.findImagePath(DEFAULT_BACKGROUND));

        String newWeapon = "Machete";
        if (player.getWeapon().equals(newWeapon)) {
            mainTextArea.setText("You step inside. It's damp and empty. You already have a Machete.");
            setChoices("Return to the river", "", "", "");
        } else {
            weaponChoice(newWeapon, GameLocation.RIVER_PATH_CONTINUED);
        }
    }

    public void findKit2() {
        position = GameLocation.FIND_KIT_2;
        backgroundPanel.setImage(UIHelper.findImagePath(DEFAULT_BACKGROUND));
        int healAmount = player.heal(random, 3, 4);
        hpLabelNumber.setText("" + player.getHp());
        mainTextArea.setText("You find a sheltered spot under a large tree and rest. You find some edible berries.\n\n(Recovered " + healAmount + " Health)");
        setChoices("Continue", "", "", "");
    }

    public void junglePath() {
        position = GameLocation.JUNGLE_PATH;
        backgroundPanel.setImage(UIHelper.findImagePath(DEFAULT_BACKGROUND));
        mainTextArea.setText("You push your way into the dense, dark jungle. The sounds of insects and birds are deafening. You spot a long, sturdy-looking branch.");
        setChoices("Take the branch", "Leave it and continue", "", "");
    }

    public void findSpear() {
        String newWeapon = "Makeshift Spear";
        if (player.getWeapon().equals(newWeapon)) {
            mainTextArea.setText("You already have a spear.");
            setChoices("Continue into the jungle", "", "", "");
            position = GameLocation.FIND_SPEAR;
        } else {
            weaponChoice(newWeapon, GameLocation.DECISION_JUNGLE);
        }
    }

    public void decisionJungle() {
        position = GameLocation.DECISION_JUNGLE;
        backgroundPanel.setImage(UIHelper.findImagePath(DEFAULT_BACKGROUND));
        mainTextArea.setText("You find a fork in the faint animal trail you've been following.");
        setChoices("Go left, towards a rocky hill (Jaguar)", "Go right, towards a swamp (Quicksand)", "Follow a strange noise (Giant Tree)", "Find a place to rest");
    }

    public void weaponChoice(String newWeaponName, GameLocation nextPosition) {
        position = GameLocation.WEAPON_CHOICE;
        this.newWeaponFound = newWeaponName;
        this.postWeaponChoicePosition = nextPosition;

        String currentWeaponName = player.getWeapon();
        String currentDamage = player.getAttackDamageRange();
        String newDamage = player.getAttackDamageRange(newWeaponName);

        player.gainXP(5); // Award XP for finding items
        mainTextArea.setText("You found a " + newWeaponName + "!\n\n++5 XP++");

        setChoices("Switch to " + newWeaponName + " (Damage: " + newDamage + ")",
                "Keep " + currentWeaponName + " (Damage: " + currentDamage + ")",
                "", "");
    }

    public void giantTree() {
        position = GameLocation.GIANT_TREE;
        backgroundPanel.setImage(UIHelper.findImagePath(DEFAULT_BACKGROUND));
        mainTextArea.setText("You follow the skittering noise to a massive, ancient tree. A Large Spider descends from a thick web!");
        setChoices("Fight the spider", "Run back to the fork", "", "");
    }

    public void afterSpiderFight() {
        position = GameLocation.AFTER_SPIDER_FIGHT;
        backgroundPanel.setImage(UIHelper.findImagePath(DEFAULT_BACKGROUND));
        player.gainXP(5);
        player.setHasCompass(true);
        mainTextArea.setText("The spider is defeated. You find a small, tough pouch in its web... It contains an old Compass!\n\n(You found a Compass)\n\n++5 XP++");
        setChoices("Return to the jungle fork", "", "", "");
    }

    public void findKit3() {
        position = GameLocation.FIND_KIT_3;
        backgroundPanel.setImage(UIHelper.findImagePath(DEFAULT_BACKGROUND));
        int healAmount = player.heal(random, 2, 3);
        hpLabelNumber.setText("" + player.getHp());
        mainTextArea.setText("You sit down to rest. You find a 'First Aid Leaf' and apply it to your scratches.\n\n(Recovered " + healAmount + " Health)");
        setChoices("Continue", "", "", "");
    }

    public void quicksandSwamp() {
        position = GameLocation.QUICKSAND_SWAMP;
        backgroundPanel.setImage(UIHelper.findImagePath(DEFAULT_BACKGROUND));
        mainTextArea.setText("You head towards the swamp. The ground suddenly gives way! It's quicksand!");

        if (random.nextInt(100) < 50) {
            lose("You struggle, but the thick mud pulls you under. Game Over.");
        } else {
            int damage = random.nextInt(4) + 2;
            player.takeDamage(damage);
            player.gainXP(10); // XP for surviving dangerous encounter
            hpLabelNumber.setText("" + player.getHp());

            mainTextArea.append("\nYou frantically grab a vine and pull yourself out, exhausted.\n(You lost 2 Health)\n\n++10 XP for survival++");

            if (!player.isAlive()) {
                lose("You pull yourself out, but the effort was too much. You die on the bank. Game Over.");
            } else {
                setChoices("Return to the fork", "", "", "");
            }
        }
    }

    public void boarEncounter() {
        position = GameLocation.ENEMY_ENCOUNTER;
        backgroundPanel.setImage(UIHelper.findImagePath(DEFAULT_BACKGROUND));

        currentEnemy = new Enemy("Wild Boar", 16 + random.nextInt(5));
        postCombatPosition = GameLocation.RIVER_PATH_CONTINUED;

        mainTextArea.setText("You push aside the bushes and a " + currentEnemy.getName() + " charges at you!");
        setChoices("Fight", "Try to flee", "", "");
    }

    public void riverEncounter() {
        position = GameLocation.ENEMY_ENCOUNTER;
        backgroundPanel.setImage(UIHelper.findImagePath(DEFAULT_BACKGROUND));

        currentEnemy = new Enemy("Giant Snake", 12 + random.nextInt(5));
        postCombatPosition = GameLocation.DECISION_RIVER_DAMAGE;

        mainTextArea.setText("A " + currentEnemy.getName() + " slithers out from the mud! It looks aggressive.");
        setChoices("Fight", "Try to flee", "", "");
    }

    public void jungleEncounter() {
        position = GameLocation.ENEMY_ENCOUNTER;
        backgroundPanel.setImage(UIHelper.findImagePath(DEFAULT_BACKGROUND));

        currentEnemy = new Enemy("Jaguar", 20 + random.nextInt(6));
        postCombatPosition = GameLocation.FIND_PENDANT;

        mainTextArea.setText("You round a boulder and startle a " + currentEnemy.getName() + "! It prepares to pounce!");
        setChoices("Fight", "Try to flee", "", "");
    }

    public void spiderEncounter() {
        position = GameLocation.ENEMY_ENCOUNTER;
        backgroundPanel.setImage(UIHelper.findImagePath(DEFAULT_BACKGROUND));

        currentEnemy = new Enemy("Large Spider", 8 + random.nextInt(3));
        postCombatPosition = GameLocation.AFTER_SPIDER_FIGHT;

        mainTextArea.setText("You ready your " + player.getWeapon() + " against the " + currentEnemy.getName() + "!");
        setChoices("Fight", "Try to flee", "", "");
    }

    public void decisionRiverDamage() {
        position = GameLocation.DECISION_RIVER_DAMAGE;
        backgroundPanel.setImage(UIHelper.findImagePath(DEFAULT_BACKGROUND));
        mainTextArea.setText("After the fight with the snake, you are tired. The path ahead looks long.");
        setChoices("Continue into the murky delta", "Go back to the jungle path", "", "");
    }

    public void murkyDelta() {
        position = GameLocation.MURKY_DELTA;
        backgroundPanel.setImage(UIHelper.findImagePath(DEFAULT_BACKGROUND));
        mainTextArea.setText("The river splits into a maze of small, murky channels. You see a glint on a small island, but also hear a strange clicking sound from the mangroves.");
        setChoices("Try to reach the island (Risky)", "Follow the clicking sound (Crab)", "Try to find the main channel", "");
    }

    public void deltaIsland() {
        position = GameLocation.DELTA_ISLAND;
        backgroundPanel.setImage(UIHelper.findImagePath(DEFAULT_BACKGROUND));
        int chance = random.nextInt(100);

        if (chance < 40 && !player.hasWhetstone()) {
            player.gainXP(5);
            player.setHasWhetstone(true);
            mainTextArea.setText("You find a smooth Whetstone on the shore!\n\n(Your weapons will now do +1 minimum damage!)\n\n++5 XP++");
            setChoices("Return to the delta", "", "", "");
        } else if (chance < 70 && !player.hasToughHide()) {
            player.gainXP(5);
            player.setHasToughHide(true);
            mainTextArea.setText("You find a thick, leathery hide on the island. It looks durable.\n\n(Your Max HP has permanently increased by 5!)\n\n++5 XP++");
            setChoices("Return to the delta", "", "", "");
        } else {
            mainTextArea.setText("You step onto the island and a Giant Crab bursts from the sand!");
            setChoices("Fight!", "", "", "");
        }
    }

    public void findMainChannel() {
        position = GameLocation.FIND_MAIN_CHANNEL;
        backgroundPanel.setImage(UIHelper.findImagePath(DEFAULT_BACKGROUND));
        if (random.nextInt(100) < 50) {
            mainTextArea.setText("You navigate the maze and find the main river channel again. It leads to a high ridge.");
            setChoices("Continue to the ridge", "", "", "");
        } else {
            mainTextArea.setText("You get lost in the mangroves and cut yourself on sharp shells.\n(You lost 2 Health)");
            player.takeDamage(2);
            hpLabelNumber.setText("" + player.getHp());
            if (!player.isAlive()) {
                lose("You succumb to your injuries in the delta. Game Over.");
            } else {
                setChoices("Try again", "", "", "");
            }
        }
    }

    public void giantCrabEncounter() {
        position = GameLocation.ENEMY_ENCOUNTER;
        backgroundPanel.setImage(UIHelper.findImagePath(DEFAULT_BACKGROUND));
        currentEnemy = new Enemy("Giant Crab", 15 + random.nextInt(4));
        postCombatPosition = GameLocation.MURKY_DELTA;
        mainTextArea.setText("A " + currentEnemy.getName() + " emerges, snapping its huge claws!");
        setChoices("Fight", "Try to flee", "", "");
    }

    public void findPendant() {
        position = GameLocation.FIND_PENDANT;
        backgroundPanel.setImage(UIHelper.findImagePath(DEFAULT_BACKGROUND));
        mainTextArea.setText("With the " + currentEnemy.getName() + " defeated, you continue up the rocky hill. You find a small, abandoned camp.\n");

        if (!player.hasPendant()) {
            player.gainXP(5);
            player.setHasPendant(true);
            mainTextArea.append("Inside a rotted bag, you find an ornate Pendant!\n\n++5 XP++");
        } else {
            mainTextArea.append("You already found the pendant. There is nothing else here.");
        }

        setChoices("Continue", "", "", "");
    }

    public void afterPendant() {
        position = GameLocation.AFTER_PENDANT;
        backgroundPanel.setImage(UIHelper.findImagePath(DEFAULT_BACKGROUND));
        mainTextArea.setText("You pocket the pendant. It feels strangely important.\nThe path from the camp leads to crumbling stone ruins covered in vines.");
        setChoices("Explore the ruins", "", "", "");
    }

    public void ancientRuins() {
        position = GameLocation.ANCIENT_RUINS;
        backgroundPanel.setImage(UIHelper.findImagePath(DEFAULT_BACKGROUND));
        mainTextArea.setText("You stand in a vine-choked courtyard. There is a dark stairway leading down, a large stone building, and a path continuing up the hill.");
        setChoices("Explore the dark stairway (Stalker)", "Search the stone building", "Follow the path up the hill", "");
    }

    public void ruinsCrypt() {
        position = GameLocation.RUINS_CRYPT;
        backgroundPanel.setImage(UIHelper.findImagePath(DEFAULT_BACKGROUND));
        mainTextArea.setText("You descend into the darkness. A pair of glowing eyes snaps open in the shadows... A Jungle Stalker attacks!");
        setChoices("Fight!", "", "", "");
    }

    public void stalkerEncounter() {
        position = GameLocation.ENEMY_ENCOUNTER;
        backgroundPanel.setImage(UIHelper.findImagePath(DEFAULT_BACKGROUND));
        currentEnemy = new Enemy("Jungle Stalker", 25 + random.nextInt(6));
        postCombatPosition = GameLocation.AFTER_STALKER_FIGHT;
        mainTextArea.setText("The " + currentEnemy.getName() + " lunges from the shadows!");
        setChoices("Fight", "Try to flee", "", "");
    }

    public void afterStalkerFight() {
        position = GameLocation.AFTER_STALKER_FIGHT;
        backgroundPanel.setImage(UIHelper.findImagePath(DEFAULT_BACKGROUND));
        mainTextArea.setText("The stalker dissolves into shadows. It dropped a sharp Obsidian Dagger!");

        String newWeapon = "Obsidian Dagger";
        if (player.getWeapon().equals(newWeapon)) {
            mainTextArea.append("\n...But you already have one.");
            setChoices("Return to courtyard", "", "", "");
        } else {
            weaponChoice(newWeapon, GameLocation.ANCIENT_RUINS);
        }
    }

    public void ruinsBuilding() {
        position = GameLocation.RUINS_BUILDING;
        backgroundPanel.setImage(UIHelper.findImagePath(DEFAULT_BACKGROUND));

        if (player.getWeapon().equals("Obsidian Dagger")) {
            player.gainXP(5);
            player.heal(random, 10, 0);
            hpLabelNumber.setText("" + player.getHp());
            mainTextArea.setText("You search the crumbling building and find a pristine First Aid Kit!\n\n(Healed 10 Health)\n\n++5 XP++");
        } else if (!player.hasWhetstone()) {
            player.gainXP(5);
            player.setHasWhetstone(true);
            mainTextArea.setText("You search the building and find a strange, smooth Whetstone!\n\n(Your weapons will now do +1 minimum damage!)\n\n++5 XP++");
        } else if (!player.hasToughHide()) {
            player.gainXP(5);
            player.setHasToughHide(true);
            mainTextArea.setText("You search the building and find a tough, leathery hide.\n\n(Your Max HP has permanently increased by 5!)\n\n++5 XP++");
        } else {
            mainTextArea.setText("The building is empty, save for dust and echoes.");
        }
        setChoices("Return to courtyard", "", "", "");
    }

    public void decisionFinal() {
        position = GameLocation.DECISION_FINAL;
        backgroundPanel.setImage(UIHelper.findImagePath(DEFAULT_BACKGROUND));
        mainTextArea.setText("You've been walking for what feels like days. You are weak and tired.\nYou reach a high ridge and see smoke in the distance, but also a dilapidated rope bridge across a chasm.");
        setChoices("Head towards the smoke", "Try to cross the rope bridge (50% Fail)", "Give up and stay here", "");
    }

    public void bridgeCrossSuccess() {
        position = GameLocation.BRIDGE_CROSS_SUCCESS;
        backgroundPanel.setImage(UIHelper.findImagePath(DEFAULT_BACKGROUND));
        endingEscape();
    }

    public void fight() {
        position = GameLocation.FIGHT;
        backgroundPanel.setImage(UIHelper.findImagePath(DEFAULT_BACKGROUND));
        mainTextArea.setText(currentEnemy.getName() + " Health: " + currentEnemy.getHp() + "\n\nWhat do you do?");
        setChoices("Attack with " + player.getWeapon(), "Heal (costs a turn)", "Attempt to Flee", "");
    }

    public void playerAttack() {
        position = GameLocation.PLAYER_ATTACK;
        backgroundPanel.setImage(UIHelper.findImagePath(DEFAULT_BACKGROUND));

        int playerDamage = player.getAttackDamage(random);
        currentEnemy.takeDamage(playerDamage);

        mainTextArea.setText("You attack the " + currentEnemy.getName() + " for " + playerDamage + " damage!");
        setChoices("Continue", "", "", "");
    }

    public void playerHeal() {
        position = GameLocation.PLAYER_HEAL;
        backgroundPanel.setImage(UIHelper.findImagePath(DEFAULT_BACKGROUND));

        int healAmount = player.heal(random, 3, 5);
        hpLabelNumber.setText("" + player.getHp());
        mainTextArea.setText("You tend to your wounds, trying to recover.\n(Healed " + healAmount + " Health)");
        setChoices("Continue", "", "", "");
    }

    public void enemyAttack() {
        position = GameLocation.ENEMY_ATTACK;
        backgroundPanel.setImage(UIHelper.findImagePath(DEFAULT_BACKGROUND));

        int enemyDamage = currentEnemy.getAttackDamage(random);
        player.takeDamage(enemyDamage);
        hpLabelNumber.setText("" + player.getHp());

        mainTextArea.setText("The " + currentEnemy.getName() + " attacks you for " + enemyDamage + " damage!");
        setChoices("Continue", "", "", "");
    }

    public void winFight() {
        position = GameLocation.WIN_FIGHT;
        backgroundPanel.setImage(UIHelper.findImagePath(DEFAULT_BACKGROUND));
        
        // Award XP based on enemy difficulty (10-30 XP)
        int xpGained = getEnemyXP(currentEnemy.getName());
        player.gainXP(xpGained);
        
        mainTextArea.setText("You defeated the " + currentEnemy.getName() + "!\n\n++" + xpGained + " XP++");
        setChoices("Continue", "", "", "");
    }

    private int getEnemyXP(String enemyName) {
        // Award XP based on enemy difficulty (10-30 XP)
        if (enemyName.equals("Jungle Stalker")) {
            return 30; // Hardest enemy
        } else if (enemyName.equals("Jaguar")) {
            return 25;
        } else if (enemyName.equals("Wild Boar") || enemyName.equals("Giant Crab")) {
            return 20;
        } else if (enemyName.equals("Giant Snake")) {
            return 15;
        } else if (enemyName.equals("Large Spider")) {
            return 10; // Easiest enemy
        }
        return 10; // Default
    }

    public void fleeResult(boolean success) {
        position = GameLocation.FLEE_RESULT;
        backgroundPanel.setImage(UIHelper.findImagePath(DEFAULT_BACKGROUND));
        if (success) {
            mainTextArea.setText("You managed to escape!");
            setChoices("Continue", "", "", "");
        } else {
            mainTextArea.setText("You couldn't get away! The " + currentEnemy.getName() + " attacks!");

            int enemyDamage = currentEnemy.getAttackDamage(random);
            player.takeDamage(enemyDamage);
            hpLabelNumber.setText("" + player.getHp());

            mainTextArea.append("\n(Received " + enemyDamage + " damage)");

            setChoices("Continue", "", "", "");
            position = GameLocation.ENEMY_ATTACK_DISPLAY;
        }
    }

    public void lose(String message) {
        position = GameLocation.LOSE;
        backgroundPanel.setImage(UIHelper.findImagePath(DEFAULT_BACKGROUND));
        mainTextArea.setText(message + "\n\nGAME OVER");
        setChoices("Restart", "Main Menu", "", "");
    }

    public void endingEscape() {
        position = GameLocation.ENDING_ESCAPE;
        backgroundPanel.setImage(UIHelper.findImagePath(DEFAULT_BACKGROUND));
        mainTextArea.setText("On the other side, you find a clear trail that leads you out of the jungle to a small village.\n\n(Escape Ending)");
        setChoices("Continue", "", "", "");
    }

    public void endingRescue() {
        position = GameLocation.ENDING_RESCUE;
        backgroundPanel.setImage(UIHelper.findImagePath(DEFAULT_BACKGROUND));
        mainTextArea.setText("You head towards the smoke. A search party finds you! They were looking for you.\nYour Pendant seems to glow as they approach.\n\n(Rescue Ending)");
        setChoices("Continue", "", "", "");
    }

    public void endingLost() {
        position = GameLocation.ENDING_LOST;
        backgroundPanel.setImage(UIHelper.findImagePath(DEFAULT_BACKGROUND));
        mainTextArea.setText("You collapse, too weak to go on. The jungle... it wins.\nYou are never seen again.\n\n(Lost Forever Ending)");
        setChoices("Continue", "", "", "");
    }

    public void thankYou() {
        position = GameLocation.THANK_YOU;
        backgroundPanel.setImage(UIHelper.findImagePath(DEFAULT_BACKGROUND));
        mainTextArea.setText("Thank you for playing!");
        setChoices("Play Again?", "Main Menu", "", "");
    }

    public class ChoiceHandler implements ActionListener {

        public void actionPerformed(ActionEvent event) {
            String yourChoice = event.getActionCommand();

            if (position == GameLocation.LOSE || position == GameLocation.THANK_YOU) {
                if (yourChoice.equals("c1")) {
                    playerSetup();
                }
                else if (yourChoice.equals("c2")) {
                    game.showScreen("mainMenu");
                }
                return;
            }

            if (position == GameLocation.ENDING_ESCAPE || position == GameLocation.ENDING_RESCUE || position == GameLocation.ENDING_LOST) {
                if (yourChoice.equals("c1")) {
                    thankYou();
                }
                return;
            }

            if (!player.isAlive()) {
                if (position != GameLocation.LOSE) {
                    lose("You succumbed to your injuries. Game Over.");
                }
                return;
            }

            switch (position) {
                case START:
                    if (yourChoice.equals("c1")) { decisionStart(); }
                    break;

                case DECISION_START:
                    switch (yourChoice) {
                        case "c1": searchWreckage(); break;
                        case "c2": riverPath(); break;
                        case "c3": junglePath(); break;
                    }
                    break;

                case SEARCH_WRECKAGE:
                    switch (yourChoice) {
                        case "c1": wreckageDanger(); break;
                        case "c2": searchWreckageBag(); break;
                        case "c3": afterWreckageDecision(); break;
                    }
                    break;
                case WRECKAGE_DANGER:
                case SEARCH_WRECKAGE_BAG:
                    if (yourChoice.equals("c1")) { afterWreckageDecision(); }
                    break;
                case AFTER_WRECKAGE_DECISION:
                    if (yourChoice.equals("c1")) { riverPath(); }
                    else if (yourChoice.equals("c2")) { junglePath(); }
                    break;

                case RIVER_PATH:
                    if (yourChoice.equals("c1")) { decisionRiver(); }
                    break;
                case DECISION_RIVER:
                    switch (yourChoice) {
                        case "c1": boarEncounter(); break;
                        case "c2": riverPathContinued(); break;
                        case "c3": junglePath(); break;
                        case "c4": findKit2(); break;
                    }
                    break;
                case RIVER_PATH_CONTINUED:
                    switch (yourChoice) {
                        case "c1": riverEncounter(); break;
                        case "c2": waterfallCave(); break;
                        case "c3": junglePath(); break;
                    }
                    break;
                case WATERFALL_CAVE:
                    if (yourChoice.equals("c1")) { insideCave(); }
                    else if (yourChoice.equals("c2")) { riverPathContinued(); }
                    break;
                case INSIDE_CAVE:
                    if (yourChoice.equals("c1")) { riverPathContinued(); }
                    break;
                case FIND_KIT_2:
                    if (yourChoice.equals("c1")) { decisionRiver(); }
                    break;

                case JUNGLE_PATH:
                    if (yourChoice.equals("c1")) { findSpear(); }
                    else if (yourChoice.equals("c2")) { decisionJungle(); }
                    break;
                case FIND_SPEAR:
                    if (yourChoice.equals("c1")) { decisionJungle(); }
                    break;
                case DECISION_JUNGLE:
                    switch (yourChoice) {
                        case "c1": jungleEncounter(); break;
                        case "c2": quicksandSwamp(); break;
                        case "c3": giantTree(); break;
                        case "c4": findKit3(); break;
                    }
                    break;
                case GIANT_TREE:
                    if (yourChoice.equals("c1")) { spiderEncounter(); }
                    else if (yourChoice.equals("c2")) { decisionJungle(); }
                    break;
                case AFTER_SPIDER_FIGHT:
                    if (yourChoice.equals("c1")) { decisionJungle(); }
                    break;
                case FIND_KIT_3:
                    if (yourChoice.equals("c1")) { decisionJungle(); }
                    break;
                case QUICKSAND_SWAMP:
                    if (yourChoice.equals("c1")) { decisionJungle(); }
                    break;

                case DECISION_RIVER_DAMAGE:
                    switch (yourChoice) {
                        case "c1": murkyDelta(); break;
                        case "c2": junglePath(); break;
                    }
                    break;

                case MURKY_DELTA:
                    switch (yourChoice) {
                        case "c1": deltaIsland(); break;
                        case "c2": giantCrabEncounter(); break;
                        case "c3": findMainChannel(); break;
                    }
                    break;
                case DELTA_ISLAND:
                    if (yourChoice.equals("c1")) {
                        if (mainTextArea.getText().contains("bursts from the sand")) {
                            giantCrabEncounter();
                        } else {
                            murkyDelta();
                        }
                    }
                    break;
                case FIND_MAIN_CHANNEL:
                    if (yourChoice.equals("c1")) {
                        if (mainTextArea.getText().contains("ridge")) {
                            decisionFinal();
                        } else {
                            murkyDelta();
                        }
                    }
                    break;
                case FIND_PENDANT:
                    if (yourChoice.equals("c1")) { afterPendant(); }
                    break;
                case AFTER_PENDANT:
                    if (yourChoice.equals("c1")) { ancientRuins(); }
                    break;
                case ANCIENT_RUINS:
                    switch (yourChoice) {
                        case "c1": ruinsCrypt(); break;
                        case "c2": ruinsBuilding(); break;
                        case "c3": decisionFinal(); break;
                    }
                    break;
                case RUINS_CRYPT:
                    if (yourChoice.equals("c1")) { stalkerEncounter(); }
                    break;
                case RUINS_BUILDING:
                    if (yourChoice.equals("c1")) { ancientRuins(); }
                    break;
                case AFTER_STALKER_FIGHT:
                    if (yourChoice.equals("c1")) { ancientRuins(); }
                    break;

                case DECISION_FINAL:
                    switch (yourChoice) {
                        case "c1":
                            if (player.hasPendant()) {
                                endingRescue();
                            } else {
                                endingLost();
                            }
                            break;
                        case "c2":
                            if (player.hasCompass()) {
                                player.gainXP(15);
                                mainTextArea.setText("You check your compass... it seems to waver, pointing to the left side of the bridge. You cross carefully, avoiding the weak planks on the right!\n\n++15 XP for survival++");
                                setChoices("Continue", "", "", "");
                                position = GameLocation.BRIDGE_CROSS_SUCCESS;
                            } else if (random.nextInt(100) < 50) {
                                lose("You step on a rotten plank. It snaps, and you fall into the chasm. Game Over.");
                            } else {
                                player.gainXP(15);
                                mainTextArea.setText("You test every plank, moving slowly. Your heart pounds. After an eternity, you reach the other side!\n\n++15 XP for survival++");
                                setChoices("Continue", "", "", "");
                                position = GameLocation.BRIDGE_CROSS_SUCCESS;
                            }
                            break;
                        case "c3":
                            endingLost();
                            break;
                    }
                    break;

                case BRIDGE_CROSS_SUCCESS:
                    if (yourChoice.equals("c1")) { endingEscape(); }
                    break;

                case WEAPON_CHOICE:
                    if (yourChoice.equals("c1")) {
                        player.setWeapon(newWeaponFound);
                        weaponLabelName.setText(player.getWeapon());
                    }
                    resumePath(postWeaponChoicePosition);
                    break;

                case ENEMY_ENCOUNTER:
                    if (yourChoice.equals("c1")) { fight(); }
                    else if (yourChoice.equals("c2")) {
                        fleeResult(random.nextInt(100) < 25);
                    }
                    break;

                case FIGHT:
                    switch (yourChoice) {
                        case "c1": playerAttack(); break;
                        case "c2": playerHeal(); break;
                        case "c3":
                            fleeResult(random.nextInt(100) < 25);
                            break;
                    }
                    break;

                case PLAYER_ATTACK:
                    if (!currentEnemy.isAlive()) {
                        winFight();
                    } else {
                        enemyAttack();
                    }
                    break;

                case PLAYER_HEAL:
                    enemyAttack();
                    break;

                case ENEMY_ATTACK:
                case ENEMY_ATTACK_DISPLAY:
                    if (!player.isAlive()) {
                        lose("You were killed by the " + currentEnemy.getName() + ". Game Over.");
                    } else {
                        fight();
                    }
                    break;

                case WIN_FIGHT:
                case FLEE_RESULT:
                    if (yourChoice.equals("c1")) {
                        resumePath(postCombatPosition);
                    }
                    break;
            }
        }
    }

    private class Player {
        private int hp;
        private int maxHp;
        private int baseMaxHp;
        private String weapon;
        private boolean hasPendant;
        private boolean hasCompass;
        private boolean hasWhetstone;
        private boolean hasToughHide;
        private int level;
        private int xp;
        private int xpToNextLevel;

        public Player(int maxHp, String startWeapon) {
            this.baseMaxHp = maxHp;
            this.maxHp = baseMaxHp;
            this.weapon = startWeapon;
            this.level = 1;
            this.xp = 0;
            this.xpToNextLevel = 50;

            reset(maxHp, startWeapon);
        }

        public void reset(int maxHp, String startWeapon) {
            this.baseMaxHp = maxHp;
            this.maxHp = this.baseMaxHp;
            this.hp = this.maxHp;
            this.weapon = startWeapon;
            this.hasPendant = false;
            this.hasCompass = false;
            this.hasWhetstone = false;
            this.hasToughHide = false;
            this.level = 1;
            this.xp = 0;
            this.xpToNextLevel = 50;

            hpBonusLabel.setText("");
            damageBonusLabel.setText("");
        }

        public void takeDamage(int amount) {
            this.hp -= amount;
            if (this.hp < 0) {
                this.hp = 0;
            }
        }

        public int heal(Random rand, int base, int bonus) {
            int healAmount = base + rand.nextInt(bonus + 1);
            int oldHp = this.hp;
            this.hp += healAmount;
            if (this.hp > this.maxHp) {
                this.hp = this.maxHp;
            }
            return this.hp - oldHp;
        }

        public void gainXP(int amount) {
            this.xp += amount;
            while (this.xp >= this.xpToNextLevel) {
                levelUp();
            }
            updateLevelXpDisplay();
        }

        private void levelUp() {
            this.xp -= this.xpToNextLevel;
            this.level++;
            
            // Increase stats
            this.baseMaxHp += 5;
            this.maxHp = this.baseMaxHp;
            if (this.hasToughHide) {
                this.maxHp += 5;
            }
            this.hp = this.maxHp; // Full health restoration
            
            // Update XP requirement (constant 50 per level)
            this.xpToNextLevel = 50;
            
            // Show level up message
            mainTextArea.append("\n\n*** LEVEL UP! You are now Level " + this.level + "! ***");
            mainTextArea.append("\n+5 Max HP | +1 Damage | HP Fully Restored!");
            
            hpLabelNumber.setText("" + this.hp);
        }

        public int getAttackDamage(Random rand) {
            int minDamageBonus = hasWhetstone ? 1 : 0;
            int levelBonus = (this.level - 1); // +1 damage per level above 1

            if (weapon.equals("Machete")) {
                return rand.nextInt(8) + 3 + minDamageBonus + levelBonus;
            } else if (weapon.equals("Obsidian Dagger")) {
                return rand.nextInt(5) + 4 + minDamageBonus + levelBonus;
            } else if (weapon.equals("Makeshift Spear")) {
                return rand.nextInt(6) + 2 + minDamageBonus + levelBonus;
            } else if (weapon.equals("Rusty Pipe")) {
                return rand.nextInt(5) + 2 + minDamageBonus + levelBonus;
            } else {
                return rand.nextInt(4) + 1 + minDamageBonus + levelBonus;
            }
        }

        public String getAttackDamageRange(String weaponName) {
            int minBonus = this.hasWhetstone ? 1 : 0;
            if (weaponName.equals("Machete")) {
                return (3 + minBonus) + "-10";
            } else if (weaponName.equals("Obsidian Dagger")) {
                return (4 + minBonus) + "-8";
            } else if (weaponName.equals("Makeshift Spear")) {
                return (2 + minBonus) + "-7";
            } else if (weaponName.equals("Rusty Pipe")) {
                return (2 + minBonus) + "-6";
            } else if (weaponName.equals("Pocket Knife")) {
                return (1 + minBonus) + "-4";
            }
            return "0-0";
        }

        public String getAttackDamageRange() {
            return getAttackDamageRange(this.weapon);
        }

        public boolean isAlive() {
            return this.hp > 0;
        }

        public int getHp() { return this.hp; }
        public String getWeapon() { return this.weapon; }
        public void setWeapon(String weapon) { this.weapon = weapon; }
        public boolean hasPendant() { return this.hasPendant; }
        public void setHasPendant(boolean has) { this.hasPendant = has; }
        public boolean hasCompass() { return this.hasCompass; }
        public void setHasCompass(boolean has) { this.hasCompass = has; }
        public int getLevel() { return this.level; }
        public int getXp() { return this.xp; }
        public int getXpToNextLevel() { return this.xpToNextLevel; }

        public boolean hasWhetstone() { return this.hasWhetstone; }
        public void setHasWhetstone(boolean has) {
            this.hasWhetstone = has;
            if (has) {
                damageBonusLabel.setText("(+1 Damage)");
            } else {
                damageBonusLabel.setText("");
            }
        }

        public boolean hasToughHide() { return this.hasToughHide; }
        public void setHasToughHide(boolean has) {
            if (has && !this.hasToughHide) {
                this.hasToughHide = true;
                this.baseMaxHp += 5;
                this.maxHp = this.baseMaxHp;
                this.hp += 5;
                hpBonusLabel.setText("(+5 HP)");
            } else if (!has && this.hasToughHide) {
                this.hasToughHide = false;
                this.baseMaxHp -= 5;
                this.maxHp = this.baseMaxHp;
                hpBonusLabel.setText("");
            }

            if (this.hp > this.maxHp) { this.hp = this.maxHp; }
            hpLabelNumber.setText("" + this.hp);
        }
    }

    private class Enemy {
        private String name;
        private int hp;

        public Enemy(String name, int hp) {
            this.name = name;
            this.hp = hp;
        }

        public void takeDamage(int amount) {
            this.hp -= amount;
            if (this.hp < 0) {
                this.hp = 0;
            }
        }

        public int getAttackDamage(Random rand) {
            if (name.equals("Jungle Stalker")) {
                return rand.nextInt(4) + 3;
            } else if (name.equals("Jaguar")) {
                return rand.nextInt(5) + 2;
            } else if (name.equals("Wild Boar")) {
                return rand.nextInt(4) + 2;
            } else if (name.equals("Giant Crab")) {
                return rand.nextInt(4) + 2;
            } else if (name.equals("Giant Snake")) {
                return rand.nextInt(4) + 1;
            } else if (name.equals("Large Spider")) {
                return rand.nextInt(3) + 1;
            }
            return 1;
        }

        public boolean isAlive() { return this.hp > 0; }
        public String getName() { return this.name; }
        public int getHp() { return this.hp; }
    }
}