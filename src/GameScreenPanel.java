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
    private JButton choice1, choice2, choice3, choice4;
    private ShadowLabel hpLabelNumber, weaponLabelName;
    private Font gameFont = new Font("Serif", Font.PLAIN, 26);
    private Font smallerGameFont = new Font("Serif", Font.PLAIN, 22);

    private Random random;

    private Player player;
    private Enemy currentEnemy;

    private enum GameLocation {
        START, DECISION_START, SEARCH_WRECKAGE, AFTER_WRECKAGE_DECISION,
        RIVER_PATH, DECISION_RIVER, JUNGLE_PATH, DECISION_JUNGLE,
        SWAMP_PATH_DAMAGE, FIND_KIT_2, FIND_KIT_3,
        ENEMY_ENCOUNTER, FIGHT, PLAYER_ATTACK, PLAYER_HEAL, ENEMY_ATTACK,
        WIN_FIGHT, FLEE_RESULT, ENEMY_ATTACK_DISPLAY,
        DECISION_RIVER_DAMAGE, FIND_PENDANT, AFTER_PENDANT,
        DECISION_FINAL, ENDING_ESCAPE, ENDING_RESCUE, ENDING_LOST,
        LOSE, THANK_YOU, TEMP_TRIBUTARY
    }

    private GameLocation position;
    private GameLocation postCombatPosition;

    private final String DEFAULT_BACKGROUND = "black_screen";

    public GameScreenPanel(Game game) {
        this.game = game;
        this.setLayout(null);
        this.random = new Random();

        this.player = new Player(20, "Pocket Knife");

        backgroundPanel = new ImagePanel(null);
        backgroundPanel.setBounds(0, 0, 800, 600);

        JPanel playerPanel = new JPanel();
        playerPanel.setBounds(50, 15, 700, 50);
        playerPanel.setOpaque(false);
        playerPanel.setLayout(new GridLayout(1, 4));

        ShadowLabel hpLabel = new ShadowLabel("Health:");
        hpLabel.setForeground(Color.white); hpLabel.setFont(gameFont);
        playerPanel.add(hpLabel);

        hpLabelNumber = new ShadowLabel("");
        hpLabelNumber.setForeground(Color.white); hpLabelNumber.setFont(gameFont);
        playerPanel.add(hpLabelNumber);

        ShadowLabel weaponLabel = new ShadowLabel("Weapon:");
        weaponLabel.setForeground(Color.white); weaponLabel.setFont(gameFont);
        playerPanel.add(weaponLabel);

        weaponLabelName = new ShadowLabel("");
        weaponLabelName.setForeground(Color.white); weaponLabelName.setFont(gameFont);
        playerPanel.add(weaponLabelName);

        mainTextArea = new JTextArea("");
        mainTextArea.setFont(new Font("Serif", Font.PLAIN, 24));
        mainTextArea.setForeground(Color.white);
        mainTextArea.setOpaque(false); mainTextArea.setEditable(false);
        mainTextArea.setWrapStyleWord(true); mainTextArea.setLineWrap(true);
        mainTextArea.setFocusable(false);
        mainTextArea.setBounds(75, 80, 650, 250);

        JPanel choiceButtonPanel = new JPanel();
        choiceButtonPanel.setBounds(75, 340, 650, 200);
        choiceButtonPanel.setOpaque(false);
        choiceButtonPanel.setLayout(new GridLayout(4, 1, 0, 10));

        ChoiceHandler choiceHandler = new ChoiceHandler();
        choice1 = createGameButton(""); choice1.setActionCommand("c1"); choice1.addActionListener(choiceHandler); choiceButtonPanel.add(choice1);
        choice2 = createGameButton(""); choice2.setActionCommand("c2"); choice2.addActionListener(choiceHandler); choiceButtonPanel.add(choice2);
        choice3 = createGameButton(""); choice3.setActionCommand("c3"); choice3.addActionListener(choiceHandler); choiceButtonPanel.add(choice3);
        choice4 = createGameButton(""); choice4.setActionCommand("c4"); choice4.addActionListener(choiceHandler); choiceButtonPanel.add(choice4);

        this.add(playerPanel);
        this.add(mainTextArea);
        this.add(choiceButtonPanel);
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
        final Font hoverFont = originalFont.deriveFont(Font.PLAIN, 24f);

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
        if (mainTextArea != null) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setColor(new Color(0, 0, 0, 220));
            g2d.fillRoundRect(mainTextArea.getX() - 10, mainTextArea.getY() - 10, mainTextArea.getWidth() + 20, mainTextArea.getHeight() + 20, 15, 15);
            g2d.dispose();
        }
    }

    public void playerSetup() {
        player.reset(20, "Pocket Knife");
        weaponLabelName.setText(player.getWeapon());
        hpLabelNumber.setText("" + player.getHp());
        startGameNode();
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

        if (player.getWeapon().equals("Pocket Knife")) {
            mainTextArea.setText("Amidst the debris, you find a sturdy Machete!\n\n(You obtained a Machete)");
            player.setWeapon("Machete");
            weaponLabelName.setText(player.getWeapon());
        } else {
            mainTextArea.setText("You find some soggy supplies, nothing else useful.");
        }

        setChoices("Continue", "", "", "");
    }

    public void afterWreckageDecision() {
        position = GameLocation.AFTER_WRECKAGE_DECISION;
        backgroundPanel.setImage(UIHelper.findImagePath(DEFAULT_BACKGROUND));
        mainTextArea.setText("You stash your findings and survey the area again.");
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
        mainTextArea.setText("You walk for an hour. The riverbank is muddy and difficult.\nYou hear a noise in the bushes.");
        setChoices("Continue on riverbank (Encounter)", "Wander off into the jungle", "Find a place to rest", "");
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
        mainTextArea.setText("You push your way into the dense, dark jungle. The sounds of insects and birds are deafening.");
        setChoices("Continue", "", "", "");
    }

    public void decisionJungle() {
        position = GameLocation.DECISION_JUNGLE;
        backgroundPanel.setImage(UIHelper.findImagePath(DEFAULT_BACKGROUND));
        mainTextArea.setText("You find a fork in the faint animal trail you've been following.");
        setChoices("Go left, towards a rocky hill (Encounter)", "Go right, towards a swamp (Damage)", "Find a place to rest", "");
    }

    public void findKit3() {
        position = GameLocation.FIND_KIT_3;
        backgroundPanel.setImage(UIHelper.findImagePath(DEFAULT_BACKGROUND));
        int healAmount = player.heal(random, 2, 3);
        hpLabelNumber.setText("" + player.getHp());
        mainTextArea.setText("You sit down to rest. You find a 'First Aid Leaf' and apply it to your scratches.\n\n(Recovered " + healAmount + " Health)");
        setChoices("Continue", "", "", "");
    }

    public void swampPathDamage() {
        position = GameLocation.SWAMP_PATH_DAMAGE;
        backgroundPanel.setImage(UIHelper.findImagePath(DEFAULT_BACKGROUND));

        int damage = random.nextInt(3) + 2;
        player.takeDamage(damage);
        hpLabelNumber.setText("" + player.getHp());

        mainTextArea.setText("You head towards the swamp. The ground is soft and you twist your ankle struggling through the mud.\n\n(You lost " + damage + " Health)");

        if (!player.isAlive()) {
            lose("You succumbed to your injuries in the swamp. Game Over.");
        } else {
            mainTextArea.append("\nYou decide this is a bad idea and return to the fork.");
            setChoices("Continue", "", "", "");
        }
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

    public void decisionRiverDamage() {
        position = GameLocation.DECISION_RIVER_DAMAGE;
        backgroundPanel.setImage(UIHelper.findImagePath(DEFAULT_BACKGROUND));
        mainTextArea.setText("You nurse your wounds from the fight. The river seems to be widening. You see a small tributary breaking off to the left.");
        setChoices("Follow the main river", "Explore the tributary", "Go back to the jungle path", "");
    }

    public void findPendant() {
        position = GameLocation.FIND_PENDANT;
        backgroundPanel.setImage(UIHelper.findImagePath(DEFAULT_BACKGROUND));
        mainTextArea.setText("With the " + currentEnemy.getName() + " defeated, you continue up the rocky hill. You find a small, abandoned camp.\n");

        if (!player.hasPendant()) {
            mainTextArea.append("Inside a rotted bag, you find an ornate Pendant!");
            player.setHasPendant(true);
        } else {
            mainTextArea.append("You already found the pendant. There is nothing else here.");
        }

        setChoices("Continue", "", "", "");
    }

    public void afterPendant() {
        position = GameLocation.AFTER_PENDANT;
        backgroundPanel.setImage(UIHelper.findImagePath(DEFAULT_BACKGROUND));
        mainTextArea.setText("You pocket the pendant. It feels strangely important.\nThe path continues from the camp, up a steep hill.");
        setChoices("Continue", "", "", "");
    }

    public void decisionFinal() {
        position = GameLocation.DECISION_FINAL;
        backgroundPanel.setImage(UIHelper.findImagePath(DEFAULT_BACKGROUND));
        mainTextArea.setText("You've been walking for what feels like days. You are weak and tired.\nYou reach a high ridge and see smoke in the distance, but also a dilapidated rope bridge across a chasm.");
        setChoices("Head towards the smoke", "Try to cross the rope bridge", "Give up and stay here", "");
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
        mainTextArea.setText("You defeated the " + currentEnemy.getName() + "!");
        setChoices("Continue", "", "", "");
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
        setChoices("Restart", "", "", "");
    }

    public void endingEscape() {
        position = GameLocation.ENDING_ESCAPE;
        backgroundPanel.setImage(UIHelper.findImagePath(DEFAULT_BACKGROUND));
        mainTextArea.setText("You cross the bridge carefully. On the other side, you find a clear trail that leads you out of the jungle to a small village.\n\n(Escape Ending)");
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
        setChoices("Play Again?", "", "", "");
    }

    public class ChoiceHandler implements ActionListener {

        public void actionPerformed(ActionEvent event) {
            String yourChoice = event.getActionCommand();

            if (position == GameLocation.LOSE || position == GameLocation.THANK_YOU) {
                if (yourChoice.equals("c1")) {
                    playerSetup();
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
                        case "c1": riverEncounter(); break;
                        case "c2": junglePath(); break;
                        case "c3": findKit2(); break;
                    }
                    break;
                case FIND_KIT_2:
                    if (yourChoice.equals("c1")) { decisionRiver(); }
                    break;

                case JUNGLE_PATH:
                    if (yourChoice.equals("c1")) { decisionJungle(); }
                    break;
                case DECISION_JUNGLE:
                    switch (yourChoice) {
                        case "c1": jungleEncounter(); break;
                        case "c2": swampPathDamage(); break;
                        case "c3": findKit3(); break;
                    }
                    break;
                case FIND_KIT_3:
                    if (yourChoice.equals("c1")) { decisionJungle(); }
                    break;
                case SWAMP_PATH_DAMAGE:
                    if (yourChoice.equals("c1")) { decisionJungle(); }
                    break;

                case DECISION_RIVER_DAMAGE:
                    switch (yourChoice) {
                        case "c1": decisionFinal(); break;
                        case "c2":
                            mainTextArea.setText("The tributary leads to a small, stagnant pond. Nothing of interest. You return to the main river.");
                            setChoices("Continue", "", "", "");
                            position = GameLocation.TEMP_TRIBUTARY;
                            break;
                        case "c3": junglePath(); break;
                    }
                    break;
                case TEMP_TRIBUTARY:
                    if (yourChoice.equals("c1")) { decisionRiverDamage(); }
                    break;

                case FIND_PENDANT:
                    if (yourChoice.equals("c1")) { afterPendant(); }
                    break;
                case AFTER_PENDANT:
                    if (yourChoice.equals("c1")) { decisionFinal(); }
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
                            if (player.getHp() > 5) {
                                endingEscape();
                            } else {
                                lose("You are too weak to maintain your grip on the swaying bridge and fall into the chasm. Game Over.");
                            }
                            break;
                        case "c3":
                            endingLost();
                            break;
                    }
                    break;

                case ENEMY_ENCOUNTER:
                    if (yourChoice.equals("c1")) { fight(); }
                    else if (yourChoice.equals("c2")) {
                        fleeResult(random.nextInt(100) < 40);
                    }
                    break;

                case FIGHT:
                    switch (yourChoice) {
                        case "c1": playerAttack(); break;
                        case "c2": playerHeal(); break;
                        case "c3": fleeResult(random.nextInt(100) < 40); break;
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
        private String weapon;
        private boolean hasPendant;

        public Player(int maxHp, String startWeapon) {
            this.maxHp = maxHp;
            this.weapon = startWeapon;
            reset(maxHp, startWeapon);
        }

        public void reset(int maxHp, String startWeapon) {
            this.maxHp = maxHp;
            this.hp = this.maxHp;
            this.weapon = startWeapon;
            this.hasPendant = false;
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

        public int getAttackDamage(Random rand) {
            if (weapon.equals("Machete")) {
                return rand.nextInt(8) + 3;
            } else {
                return rand.nextInt(4) + 1;
            }
        }

        public boolean isAlive() {
            return this.hp > 0;
        }

        public int getHp() { return this.hp; }
        public String getWeapon() { return this.weapon; }
        public void setWeapon(String weapon) { this.weapon = weapon; }
        public boolean hasPendant() { return this.hasPendant; }
        public void setHasPendant(boolean has) { this.hasPendant = has; }
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
            if (name.equals("Jaguar")) {
                return rand.nextInt(5) + 2;
            } else if (name.equals("Giant Snake")) {
                return rand.nextInt(4) + 1;
            }
            return 1;
        }

        public boolean isAlive() { return this.hp > 0; }
        public String getName() { return this.name; }
        public int getHp() { return this.hp; }
    }
}