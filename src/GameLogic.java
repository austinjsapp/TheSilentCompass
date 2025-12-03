import java.util.Random;
import java.awt.Color;

public class GameLogic {
    private GameState state;
    private Random random;
    private GameUICallback uiCallback;

    // NEW: Color constants for visual feedback
    private static final Color COLOR_GOOD = new Color(100, 255, 100); // Bright green
    private static final Color COLOR_BAD = new Color(255, 100, 100); // Red
    private static final Color COLOR_SPECIAL = new Color(100, 200, 255); // Cyan
    private static final Color COLOR_WARNING = new Color(255, 200, 100); // Yellow/Orange

    public interface GameUICallback {
        void updateText(String text);
        void updateColoredText(String text, Color color); // NEW
        void setChoices(String c1, String c2, String c3, String c4);
        void updatePlayerHP(int hp);
        void updatePlayerWeapon(String weapon);
        void setBackground(String imageName);
        void showHPBonus(String text);
        void showDamageBonus(String text);
    }

    public GameLogic(GameState state, Random random, GameUICallback callback) {
        this.state = state;
        this.random = random;
        this.uiCallback = callback;
    }

    public void startGameNode() {
        state.setPosition(GameLocation.START);
        uiCallback.setBackground("black_screen");
        uiCallback.updateText("You wake up beside your shattered canoe on a river bank.\nThe dense jungle stretches before you. Survival is key.");
        uiCallback.setChoices("Continue", "", "", "");
    }

    public void decisionStart() {
        state.setPosition(GameLocation.DECISION_START);
        uiCallback.setBackground("black_screen");
        uiCallback.updateText("You stand up and check your surroundings. What is your first move?");
        // UPDATED HINTS
        uiCallback.setChoices(
                "Search wreckage (Supplies?)",
                "Follow river (Civilization?)",
                "Enter jungle (Shortcut?)",
                ""
        );
    }

    public void searchWreckage() {
        state.setPosition(GameLocation.SEARCH_WRECKAGE);
        uiCallback.setBackground("black_screen");
        uiCallback.updateText("The wreckage is unstable. You see a glint of metal under a heavy plank and a mostly-dry bag.");
        uiCallback.setChoices("Try for the metal (Risk Injury)", "Check the dry bag (Safe)", "Leave it alone", "");
    }

    public void wreckageDanger() {
        state.setPosition(GameLocation.WRECKAGE_DANGER);
        uiCallback.setBackground("black_screen");

        int chance = random.nextInt(100);

        if (chance < 40) {
            int damage = random.nextInt(3) + 8;
            state.getPlayer().takeDamage(damage);
            uiCallback.updatePlayerHP(state.getPlayer().getHp());

            if (!state.getPlayer().isAlive()) {
                lose("The plank shifts and crushes you. Game Over.");
            } else {
                uiCallback.updateColoredText("The plank shifts dangerously! You barely escape but get badly hurt.\n(You lost " + damage + " Health)", COLOR_BAD);
                uiCallback.setChoices("Continue", "", "", "");
            }
        } else if (chance < 70) {
            String newWeapon = "Rusty Pipe";
            if (state.getPlayer().getWeapon().equals(newWeapon)) {
                uiCallback.updateText("You carefully pull a... Rusty Pipe! But you already have one.");
                uiCallback.setChoices("Continue", "", "", "");
            } else {
                weaponChoice(newWeapon, GameLocation.AFTER_WRECKAGE_DECISION);
            }
        } else {
            uiCallback.updateColoredText("You cut your hand on a sharp edge but find nothing useful.\n(You lost 2 Health)", COLOR_BAD);
            state.getPlayer().takeDamage(2);
            uiCallback.updatePlayerHP(state.getPlayer().getHp());
            uiCallback.setChoices("Continue", "", "", "");
        }
    }

    public void searchWreckageBag() {
        state.setPosition(GameLocation.SEARCH_WRECKAGE_BAG);
        uiCallback.setBackground("black_screen");
        uiCallback.updateColoredText("You find a soggy First Aid Kit! You use the remaining supplies.\n(Healed 5 Health)", COLOR_GOOD);
        state.getPlayer().heal(random, 5, 0);
        uiCallback.updatePlayerHP(state.getPlayer().getHp());
        uiCallback.setChoices("Continue", "", "", "");
    }

    public void afterWreckageDecision() {
        state.setPosition(GameLocation.AFTER_WRECKAGE_DECISION);
        uiCallback.setBackground("black_screen");
        uiCallback.updateText("You step away from the wreckage and survey the area again.");
        // UPDATED HINTS
        uiCallback.setChoices("Follow river (Civilization?)", "Head into jungle (Shortcut?)", "", "");
    }

    public void riverPath() {
        state.setPosition(GameLocation.RIVER_PATH);
        uiCallback.setBackground("black_screen");
        uiCallback.updateText("You decide to follow the river. The water looks murky, but it's a clear path.");
        uiCallback.setChoices("Continue", "", "", "");
    }

    public void decisionRiver() {
        state.setPosition(GameLocation.DECISION_RIVER);
        uiCallback.setBackground("black_screen");
        uiCallback.updateText("You walk for an hour. The riverbank is muddy and difficult.\nYou hear a loud snorting noise in the bushes nearby.");
        // UPDATED HINTS
        uiCallback.setChoices(
                "Investigate noise (Danger/Food?)",
                "Continue river (Civilization?)",
                "Wander into jungle (Unknown)",
                "Find a place to rest (Heal)"
        );
    }

    public void riverPathContinued() {
        state.setPosition(GameLocation.RIVER_PATH_CONTINUED);
        uiCallback.setBackground("black_screen");
        uiCallback.updateText("You continue along the main river. The bank is muddy. You see a small tributary breaking off to the left.");
        // UPDATED HINTS
        uiCallback.setChoices(
                "Follow main river (Snake Danger)",
                "Explore tributary (Hidden path?)",
                "Return to jungle",
                ""
        );
    }

    public void waterfallCave() {
        state.setPosition(GameLocation.WATERFALL_CAVE);
        uiCallback.setBackground("black_screen");
        uiCallback.updateText("The tributary leads to a small, beautiful waterfall. You notice a dark opening behind the curtain of water.");
        // UPDATED HINTS
        uiCallback.setChoices("Enter cave (Shiny object inside?)", "Return to river", "", "");
    }

    public void insideCave() {
        state.setPosition(GameLocation.INSIDE_CAVE);
        uiCallback.setBackground("black_screen");

        String newWeapon = "Machete";
        if (state.getPlayer().getWeapon().equals(newWeapon)) {
            uiCallback.updateText("You step inside. It's damp and empty. You already have a Machete.");
            uiCallback.setChoices("Return to the river", "", "", "");
        } else {
            weaponChoice(newWeapon, GameLocation.RIVER_PATH_CONTINUED);
        }
    }

    public void findKit2() {
        state.setPosition(GameLocation.FIND_KIT_2);
        uiCallback.setBackground("black_screen");
        int healAmount = state.getPlayer().heal(random, 3, 4);
        uiCallback.updatePlayerHP(state.getPlayer().getHp());
        uiCallback.updateColoredText("You find a sheltered spot under a large tree and rest. You find some edible berries.\n\n(Recovered " + healAmount + " Health)", COLOR_GOOD);
        uiCallback.setChoices("Continue", "", "", "");
    }

    public void junglePath() {
        state.setPosition(GameLocation.JUNGLE_PATH);
        uiCallback.setBackground("black_screen");
        uiCallback.updateText("You push your way into the dense, dark jungle. The sounds of insects and birds are deafening. You spot a long, sturdy-looking branch.");
        uiCallback.setChoices("Take the branch (Weapon)", "Leave it and continue", "", "");
    }

    public void findSpear() {
        String newWeapon = "Makeshift Spear";
        if (state.getPlayer().getWeapon().equals(newWeapon)) {
            uiCallback.updateText("You already have a spear.");
            uiCallback.setChoices("Continue into the jungle", "", "", "");
            state.setPosition(GameLocation.FIND_SPEAR);
        } else {
            weaponChoice(newWeapon, GameLocation.DECISION_JUNGLE);
        }
    }

    public void decisionJungle() {
        state.setPosition(GameLocation.DECISION_JUNGLE);
        uiCallback.setBackground("black_screen");
        uiCallback.updateText("You find a fork in the faint animal trail you've been following.");
        // UPDATED HINTS
        uiCallback.setChoices(
                "Go left (Jaguar Danger!)",
                "Go right (Swamp Hazard)",
                "Follow noise (Giant Tree/Loot?)",
                "Rest (Heal)"
        );
    }

    public void weaponChoice(String newWeaponName, GameLocation nextPosition) {
        state.setPosition(GameLocation.WEAPON_CHOICE);
        state.setNewWeaponFound(newWeaponName);
        state.setPostWeaponChoicePosition(nextPosition);

        String currentWeaponName = state.getPlayer().getWeapon();
        String currentDamage = state.getPlayer().getAttackDamageRange();
        String newDamage = state.getPlayer().getAttackDamageRange(newWeaponName);

        uiCallback.updateColoredText("You found a " + newWeaponName + "!", COLOR_GOOD);
        uiCallback.setChoices("Switch to " + newWeaponName + " (Damage: " + newDamage + ")",
                "Keep " + currentWeaponName + " (Damage: " + currentDamage + ")",
                "", "");
    }

    public void giantTree() {
        state.setPosition(GameLocation.GIANT_TREE);
        uiCallback.setBackground("black_screen");
        uiCallback.updateText("You follow the skittering noise to a massive, ancient tree. A Large Spider descends from a thick web!");
        uiCallback.setChoices("Fight the spider", "Run back to the fork", "", "");
    }

    public void afterSpiderFight() {
        state.setPosition(GameLocation.AFTER_SPIDER_FIGHT);
        uiCallback.setBackground("black_screen");
        uiCallback.updateColoredText("The spider is defeated. You find a small, tough pouch in its web... It contains an old Compass!\n\n(You found a Compass)", COLOR_SPECIAL);
        state.getPlayer().setHasCompass(true);
        state.getPlayer().applyInspired(3);
        uiCallback.setChoices("Return to jungle fork", "", "", "");
    }

    public void findKit3() {
        state.setPosition(GameLocation.FIND_KIT_3);
        uiCallback.setBackground("black_screen");
        int healAmount = state.getPlayer().heal(random, 2, 3);
        uiCallback.updatePlayerHP(state.getPlayer().getHp());
        uiCallback.updateColoredText("You sit down to rest. You find a 'First Aid Leaf' and apply it to your scratches.\n\n(Recovered " + healAmount + " Health)", COLOR_GOOD);
        uiCallback.setChoices("Continue", "", "", "");
    }

    public void quicksandSwamp() {
        state.setPosition(GameLocation.QUICKSAND_SWAMP);
        uiCallback.setBackground("black_screen");

        int damage = random.nextInt(3) + 6;
        state.getPlayer().takeDamage(damage);
        uiCallback.updatePlayerHP(state.getPlayer().getHp());

        String currentText = "You head towards the swamp. The ground suddenly gives way! It's quicksand!\nYou frantically grab a vine and pull yourself out, exhausted and injured.\n(You lost " + damage + " Health)";

        if (!state.getPlayer().isAlive()) {
            lose("You pull yourself out, but the effort was too much. You die on the bank. Game Over.");
        } else {
            uiCallback.updateColoredText(currentText, COLOR_BAD);
            uiCallback.setChoices("Return to the fork", "", "", "");
        }
    }

    // Enemy encounters
    public void boarEncounter() {
        state.setPosition(GameLocation.ENEMY_ENCOUNTER);
        uiCallback.setBackground("black_screen");

        state.setCurrentEnemy(new Enemy("Wild Boar", 16 + random.nextInt(5)));
        state.setPostCombatPosition(GameLocation.RIVER_PATH_CONTINUED);

        uiCallback.updateText("You push aside the bushes and a " + state.getCurrentEnemy().getName() + " charges at you!");
        uiCallback.setChoices("Fight", "Try to flee", "", "");
    }

    public void riverEncounter() {
        state.setPosition(GameLocation.ENEMY_ENCOUNTER);
        uiCallback.setBackground("black_screen");

        state.setCurrentEnemy(new Enemy("Giant Snake", 12 + random.nextInt(5)));
        state.setPostCombatPosition(GameLocation.DECISION_RIVER_DAMAGE);

        uiCallback.updateText("A " + state.getCurrentEnemy().getName() + " slithers out from the mud! It looks aggressive.");
        uiCallback.setChoices("Fight", "Try to flee", "", "");
    }

    public void jungleEncounter() {
        state.setPosition(GameLocation.ENEMY_ENCOUNTER);
        uiCallback.setBackground("black_screen");

        state.setCurrentEnemy(new Enemy("Jaguar", 20 + random.nextInt(6)));
        state.setPostCombatPosition(GameLocation.FIND_PENDANT);

        uiCallback.updateText("You round a boulder and startle a " + state.getCurrentEnemy().getName() + "! It prepares to pounce!");
        uiCallback.setChoices("Fight", "Try to flee", "", "");
    }

    public void spiderEncounter() {
        state.setPosition(GameLocation.ENEMY_ENCOUNTER);
        uiCallback.setBackground("black_screen");

        state.setCurrentEnemy(new Enemy("Large Spider", 8 + random.nextInt(3)));
        state.setPostCombatPosition(GameLocation.AFTER_SPIDER_FIGHT);

        uiCallback.updateText("You ready your " + state.getPlayer().getWeapon() + " against the " + state.getCurrentEnemy().getName() + "!");
        uiCallback.setChoices("Fight", "Try to flee", "", "");
    }

    public void decisionRiverDamage() {
        state.setPosition(GameLocation.DECISION_RIVER_DAMAGE);
        uiCallback.setBackground("black_screen");
        uiCallback.updateText("After the fight with the snake, you are tired. The path ahead looks long.");
        // UPDATED HINTS
        uiCallback.setChoices("Continue to delta (Civilization?)", "Go back to jungle", "", "");
    }

    public void murkyDelta() {
        state.setPosition(GameLocation.MURKY_DELTA);
        uiCallback.setBackground("black_screen");
        uiCallback.updateText("The river splits into a maze of small, murky channels. You see a glint on a small island, but also hear a strange clicking sound from the mangroves.");
        // UPDATED HINTS
        uiCallback.setChoices(
                "Swim to island (Movement seen)",
                "Follow clicking (Danger)",
                "Find main channel (Exit?)",
                ""
        );
    }

    public void deltaIsland() {
        state.setPosition(GameLocation.DELTA_ISLAND);
        uiCallback.setBackground("black_screen");
        int chance = random.nextInt(100);

        if (chance < 40 && !state.getPlayer().hasWhetstone()) {
            uiCallback.updateColoredText("You find a smooth Whetstone on the shore!\n\n(Your weapons will now do +1 minimum damage!)", COLOR_GOOD);
            state.getPlayer().setHasWhetstone(true);
            uiCallback.showDamageBonus("(+1 Damage)");
            uiCallback.setChoices("Return to the delta", "", "", "");
        } else if (chance < 70 && !state.getPlayer().hasToughHide()) {
            uiCallback.updateColoredText("You find a thick, leathery hide on the island. It looks durable.\n\n(Your Max HP has permanently increased by 5!)", COLOR_GOOD);
            state.getPlayer().setHasToughHide(true);
            uiCallback.updatePlayerHP(state.getPlayer().getHp());
            uiCallback.showHPBonus("(+5 HP)");
            uiCallback.setChoices("Return to the delta", "", "", "");
        } else {
            uiCallback.updateText("You step onto the island and a Giant Crab bursts from the sand!");
            uiCallback.setChoices("Fight!", "", "", "");
        }
    }

    public void findMainChannel() {
        state.setPosition(GameLocation.FIND_MAIN_CHANNEL);
        uiCallback.setBackground("black_screen");
        if (random.nextInt(100) < 50) {
            uiCallback.updateText("You navigate the maze and find the main river channel again. It leads to a high ridge.");
            uiCallback.setChoices("Continue to the ridge", "", "", "");
        } else {
            uiCallback.updateColoredText("You get lost in the mangroves and cut yourself on sharp shells.\n(You lost 2 Health)", COLOR_BAD);
            state.getPlayer().takeDamage(2);
            uiCallback.updatePlayerHP(state.getPlayer().getHp());
            if (!state.getPlayer().isAlive()) {
                lose("You succumb to your injuries in the delta. Game Over.");
            } else {
                uiCallback.setChoices("Try again", "", "", "");
            }
        }
    }

    public void giantCrabEncounter() {
        state.setPosition(GameLocation.ENEMY_ENCOUNTER);
        uiCallback.setBackground("black_screen");
        state.setCurrentEnemy(new Enemy("Giant Crab", 15 + random.nextInt(4)));
        state.setPostCombatPosition(GameLocation.MURKY_DELTA);
        uiCallback.updateText("A " + state.getCurrentEnemy().getName() + " emerges, snapping its huge claws!");
        uiCallback.setChoices("Fight", "Try to flee", "", "");
    }

    public void findPendant() {
        state.setPosition(GameLocation.FIND_PENDANT);
        uiCallback.setBackground("black_screen");
        String text = "With the " + state.getCurrentEnemy().getName() + " defeated, you continue up the rocky hill. You find a small, abandoned camp.\n";

        if (!state.getPlayer().hasPendant()) {
            text += "Inside a rotted bag, you find an ornate Pendant!";
            state.getPlayer().setHasPendant(true);
            state.getPlayer().applyInspired(3);
            uiCallback.updateColoredText(text, COLOR_SPECIAL);
        } else {
            text += "You already found the pendant. There is nothing else here.";
            uiCallback.updateText(text);
        }

        uiCallback.setChoices("Continue", "", "", "");
    }

    public void afterPendant() {
        state.setPosition(GameLocation.AFTER_PENDANT);
        uiCallback.setBackground("black_screen");
        uiCallback.updateText("You pocket the pendant. It feels strangely important.\nThe path from the camp leads to crumbling stone ruins covered in vines.");
        uiCallback.setChoices("Explore the ruins", "", "", "");
    }

    public void ancientRuins() {
        state.setPosition(GameLocation.ANCIENT_RUINS);
        uiCallback.setBackground("black_screen");
        uiCallback.updateText("You stand in a vine-choked courtyard. There is a dark stairway leading down, a large stone building, and a path continuing up the hill.");
        // UPDATED HINTS
        uiCallback.setChoices(
                "Explore stairway (Stalker Danger)",
                "Search building (Supplies?)",
                "Follow path up (Exit)",
                ""
        );
    }

    public void ruinsCrypt() {
        state.setPosition(GameLocation.RUINS_CRYPT);
        uiCallback.setBackground("black_screen");
        uiCallback.updateText("You descend into the darkness. A pair of glowing eyes snaps open in the shadows... A Jungle Stalker attacks!");
        uiCallback.setChoices("Fight!", "", "", "");
    }

    public void stalkerEncounter() {
        state.setPosition(GameLocation.ENEMY_ENCOUNTER);
        uiCallback.setBackground("black_screen");
        state.setCurrentEnemy(new Enemy("Jungle Stalker", 25 + random.nextInt(6)));
        state.setPostCombatPosition(GameLocation.AFTER_STALKER_FIGHT);
        uiCallback.updateText("The " + state.getCurrentEnemy().getName() + " lunges from the shadows!");
        uiCallback.setChoices("Fight", "Try to flee", "", "");
    }

    public void afterStalkerFight() {
        state.setPosition(GameLocation.AFTER_STALKER_FIGHT);
        uiCallback.setBackground("black_screen");
        String text = "The stalker dissolves into shadows. It dropped a sharp Obsidian Dagger!";

        String newWeapon = "Obsidian Dagger";
        if (state.getPlayer().getWeapon().equals(newWeapon)) {
            text += "\n...But you already have one.";
            uiCallback.updateText(text);
            uiCallback.setChoices("Return to courtyard", "", "", "");
        } else {
            weaponChoice(newWeapon, GameLocation.ANCIENT_RUINS);
        }
    }

    public void ruinsBuilding() {
        state.setPosition(GameLocation.RUINS_BUILDING);
        uiCallback.setBackground("black_screen");

        if (state.getPlayer().getWeapon().equals("Obsidian Dagger")) {
            uiCallback.updateColoredText("You search the crumbling building and find a pristine First Aid Kit!\n\n(Healed 10 Health)", COLOR_GOOD);
            state.getPlayer().heal(random, 10, 0);
            uiCallback.updatePlayerHP(state.getPlayer().getHp());
        } else if (!state.getPlayer().hasWhetstone()) {
            uiCallback.updateColoredText("You search the building and find a strange, smooth Whetstone!\n\n(Your weapons will now do +1 minimum damage!)", COLOR_GOOD);
            state.getPlayer().setHasWhetstone(true);
            uiCallback.showDamageBonus("(+1 Damage)");
        } else if (!state.getPlayer().hasToughHide()) {
            uiCallback.updateColoredText("You search the building and find a tough, leathery hide.\n\n(Your Max HP has permanently increased by 5!)", COLOR_GOOD);
            state.getPlayer().setHasToughHide(true);
            uiCallback.updatePlayerHP(state.getPlayer().getHp());
            uiCallback.showHPBonus("(+5 HP)");
        } else {
            uiCallback.updateText("The building is empty, save for dust and echoes.");
        }
        uiCallback.setChoices("Return to courtyard", "", "", "");
    }

    public void decisionFinal() {
        state.setPosition(GameLocation.DECISION_FINAL);
        uiCallback.setBackground("black_screen");
        uiCallback.updateText("You've been walking for what feels like days. You are weak and tired.\nYou reach a high ridge and see smoke in the distance, but also a dilapidated rope bridge across a chasm.");
        // UPDATED HINTS
        uiCallback.setChoices(
                "Head to smoke (Rescue?)",
                "Cross bridge (Requires Compass/Luck)",
                "Give up",
                ""
        );
    }

    // Combat methods with combo, status effects, and colors
    public void fight() {
        state.setPosition(GameLocation.FIGHT);
        uiCallback.setBackground("black_screen");

        StringBuilder fightText = new StringBuilder();
        fightText.append(state.getCurrentEnemy().getName()).append(" Health: ").append(state.getCurrentEnemy().getHp());

        if (state.getPlayer().hasActiveStatusEffects()) {
            fightText.append("\n\nStatus: ").append(state.getPlayer().getStatusEffectSummary());
        }

        if (state.getPlayer().getComboCount() > 0) {
            fightText.append("\nCombo: x").append(state.getPlayer().getComboCount());
        }

        fightText.append("\n\nWhat do you do?");

        uiCallback.updateText(fightText.toString());

        if (state.getPlayer().isStunned()) {
            uiCallback.setChoices("(Stunned - Cannot Attack)", "Heal (costs a turn)", "Attempt to Flee", "");
        } else {
            uiCallback.setChoices("Attack with " + state.getPlayer().getWeapon(), "Heal (costs a turn)", "Attempt to Flee", "");
        }
    }

    public void playerAttack() {
        state.setPosition(GameLocation.PLAYER_ATTACK);
        uiCallback.setBackground("black_screen");

        if (state.getPlayer().isStunned()) {
            state.getPlayer().clearStun();
            uiCallback.updateText("You shake off the stun and prepare to fight!");
            uiCallback.setChoices("Continue", "", "", "");
            return;
        }

        int baseDamage = state.getPlayer().getAttackDamage(random);
        boolean isCritical = state.getPlayer().isCriticalHit(random);

        int finalDamage = isCritical ? (int)(baseDamage * 1.5) : baseDamage;
        state.getCurrentEnemy().takeDamage(finalDamage);
        state.getPlayer().incrementCombo();

        StringBuilder attackText = new StringBuilder();
        attackText.append("You attack the ").append(state.getCurrentEnemy().getName());
        attackText.append(" for ").append(finalDamage).append(" damage!");

        if (isCritical) {
            attackText.append("\n\nCRITICAL HIT!");
        }

        String comboMsg = state.getPlayer().getComboMessage();
        if (!comboMsg.isEmpty()) {
            attackText.append("\n").append(comboMsg);
        }

        if (isCritical || state.getPlayer().getComboCount() >= 2) {
            uiCallback.updateColoredText(attackText.toString(), COLOR_GOOD);
        } else {
            uiCallback.updateText(attackText.toString());
        }

        uiCallback.setChoices("Continue", "", "", "");
    }

    public void playerHeal() {
        state.setPosition(GameLocation.PLAYER_HEAL);
        uiCallback.setBackground("black_screen");

        int healAmount = state.getPlayer().heal(random, 6, 6);
        uiCallback.updatePlayerHP(state.getPlayer().getHp());
        uiCallback.updateColoredText("You tend to your wounds, bandaging and resting briefly.\n(Healed " + healAmount + " Health)", COLOR_GOOD);
        uiCallback.setChoices("Continue", "", "", "");
    }

    public void enemyAttack() {
        state.setPosition(GameLocation.ENEMY_ATTACK);
        uiCallback.setBackground("black_screen");

        StringBuilder attackText = new StringBuilder();
        boolean tookDamage = false;

        if (state.getPlayer().canDodge(random)) {
            attackText.append("The ").append(state.getCurrentEnemy().getName());
            attackText.append(" attacks, but you dodge out of the way!\n\n(Damage avoided!)");
        } else {
            tookDamage = true;
            int enemyDamage = state.getCurrentEnemy().getAttackDamage(random);
            state.getPlayer().takeDamage(enemyDamage);
            uiCallback.updatePlayerHP(state.getPlayer().getHp());

            attackText.append("The ").append(state.getCurrentEnemy().getName());
            attackText.append(" attacks you for ").append(enemyDamage).append(" damage!");

            String statusEffect = state.getCurrentEnemy().tryApplyStatusEffect(random, state.getPlayer());
            attackText.append(statusEffect);
        }

        String statusDamage = state.getPlayer().processStatusEffects();
        if (!statusDamage.isEmpty()) {
            tookDamage = true;
            attackText.append("\n\n").append(statusDamage);
            uiCallback.updatePlayerHP(state.getPlayer().getHp());
        }

        if (tookDamage) {
            uiCallback.updateColoredText(attackText.toString(), COLOR_BAD);
        } else {
            uiCallback.updateColoredText(attackText.toString(), COLOR_GOOD);
        }

        uiCallback.setChoices("Continue", "", "", "");
    }

    public void winFight() {
        state.setPosition(GameLocation.WIN_FIGHT);
        uiCallback.setBackground("black_screen");

        StringBuilder winText = new StringBuilder();
        winText.append("You defeated the ").append(state.getCurrentEnemy().getName()).append("!");

        if (state.getPlayer().getComboCount() >= 3) {
            winText.append("\n\nYou fought with exceptional skill!");
        }

        uiCallback.updateColoredText(winText.toString(), COLOR_GOOD);
        uiCallback.setChoices("Continue", "", "", "");
    }

    public void fleeResult(boolean success) {
        state.setPosition(GameLocation.FLEE_RESULT);
        uiCallback.setBackground("black_screen");

        state.getPlayer().resetCombo();

        if (success) {
            uiCallback.updateColoredText("You managed to escape!", COLOR_GOOD);
            uiCallback.setChoices("Continue", "", "", "");
        } else {
            if (state.getPlayer().canDodge(random)) {
                uiCallback.updateColoredText("You couldn't get away, but you dodge the " + state.getCurrentEnemy().getName() + "'s attack as you retreat!", COLOR_GOOD);
                uiCallback.setChoices("Continue", "", "", "");
                state.setPosition(GameLocation.ENEMY_ATTACK_DISPLAY);
            } else {
                int enemyDamage = state.getCurrentEnemy().getAttackDamage(random);
                state.getPlayer().takeDamage(enemyDamage);
                uiCallback.updatePlayerHP(state.getPlayer().getHp());

                uiCallback.updateColoredText("You couldn't get away! The " + state.getCurrentEnemy().getName() + " attacks!\n(Received " + enemyDamage + " damage)", COLOR_BAD);
                uiCallback.setChoices("Continue", "", "", "");
                state.setPosition(GameLocation.ENEMY_ATTACK_DISPLAY);
            }
        }
    }

    // Endings
    public void lose(String message) {
        state.setPosition(GameLocation.LOSE);
        uiCallback.setBackground("black_screen");
        uiCallback.updateColoredText(message + "\n\nGAME OVER", COLOR_BAD);
        uiCallback.setChoices("Restart", "Main Menu", "", "");
    }

    public void endingEscape() {
        state.setPosition(GameLocation.ENDING_ESCAPE);
        uiCallback.setBackground("black_screen");
        uiCallback.updateColoredText("On the other side, you find a clear trail that leads you out of the jungle to a small village.\n\n(Escape Ending)", COLOR_GOOD);
        uiCallback.setChoices("Continue", "", "", "");
    }

    public void endingRescue() {
        state.setPosition(GameLocation.ENDING_RESCUE);
        uiCallback.setBackground("black_screen");
        uiCallback.updateColoredText("You head towards the smoke. A search party finds you! They were looking for you.\nYour Pendant seems to glow as they approach.\n\n(Rescue Ending)", COLOR_SPECIAL);
        uiCallback.setChoices("Continue", "", "", "");
    }

    public void endingLost() {
        state.setPosition(GameLocation.ENDING_LOST);
        uiCallback.setBackground("black_screen");
        uiCallback.updateColoredText("You collapse, too weak to go on. The jungle... it wins.\nYou are never seen again.\n\n(Lost Forever Ending)", COLOR_BAD);
        uiCallback.setChoices("Continue", "", "", "");
    }

    public void thankYou() {
        state.setPosition(GameLocation.THANK_YOU);
        uiCallback.setBackground("black_screen");
        uiCallback.updateText("Thank you for playing!");
        uiCallback.setChoices("Play Again?", "Main Menu", "", "");
    }

    public void resumePath(GameLocation path) {
        switch (path) {
            case DECISION_RIVER_DAMAGE: decisionRiverDamage(); break;
            case FIND_PENDANT: findPendant(); break;
            case RIVER_PATH_CONTINUED: riverPathContinued(); break;
            case AFTER_SPIDER_FIGHT: afterSpiderFight(); break;
            case AFTER_WRECKAGE_DECISION: afterWreckageDecision(); break;
            case DECISION_JUNGLE: decisionJungle(); break;
            case MURKY_DELTA: murkyDelta(); break;
            case ANCIENT_RUINS: ancientRuins(); break;
            case AFTER_STALKER_FIGHT: afterStalkerFight(); break;
            default: decisionStart(); break;
        }
    }
}