import java.util.Random;

public class Player {
    private int hp;
    private int maxHp;
    private int baseMaxHp;
    private String weapon;
    private boolean hasPendant;
    private boolean hasCompass;
    private boolean hasWhetstone;
    private boolean hasToughHide;

    // NEW: Combo system
    private int comboCount;

    // NEW: Status effects
    private int poisonTurns;
    private int bleedingTurns;
    private boolean isStunned;
    private int inspiredTurns;

    public Player(int maxHp, String startWeapon) {
        this.baseMaxHp = maxHp;
        this.maxHp = baseMaxHp;
        this.weapon = startWeapon;
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

        // Reset combo and status effects
        this.comboCount = 0;
        this.poisonTurns = 0;
        this.bleedingTurns = 0;
        this.isStunned = false;
        this.inspiredTurns = 0;
    }

    public void takeDamage(int amount) {
        this.hp -= amount;
        if (this.hp < 0) {
            this.hp = 0;
        }
        // Taking damage breaks combo
        resetCombo();
    }

    public int heal(Random rand, int base, int bonus) {
        int healAmount = Math.max(6, base + rand.nextInt(bonus + 1));
        int oldHp = this.hp;
        this.hp += healAmount;
        if (this.hp > this.maxHp) {
            this.hp = this.maxHp;
        }
        // Healing breaks combo
        resetCombo();
        return this.hp - oldHp;
    }

    public int getAttackDamage(Random rand) {
        int minDamageBonus = hasWhetstone ? 1 : 0;
        int baseDamage;

        if (weapon.equals("Machete")) {
            baseDamage = rand.nextInt(8) + 3 + minDamageBonus;
        } else if (weapon.equals("Obsidian Dagger")) {
            baseDamage = rand.nextInt(5) + 4 + minDamageBonus;
        } else if (weapon.equals("Makeshift Spear")) {
            baseDamage = rand.nextInt(6) + 2 + minDamageBonus;
        } else if (weapon.equals("Rusty Pipe")) {
            baseDamage = rand.nextInt(5) + 2 + minDamageBonus;
        } else { // Pocket Knife
            baseDamage = rand.nextInt(4) + 1 + minDamageBonus;
        }

        // NEW: Add combo bonus
        int comboBonus = getComboBonus();

        // NEW: Add inspired bonus
        int inspiredBonus = (inspiredTurns > 0) ? 2 : 0;

        return baseDamage + comboBonus + inspiredBonus;
    }

    public boolean isCriticalHit(Random rand) {
        return rand.nextInt(100) < 15;
    }

    public boolean canDodge(Random rand) {
        return rand.nextInt(100) < 10;
    }

    // NEW: Combo system methods
    public void incrementCombo() {
        comboCount++;
    }

    public void resetCombo() {
        comboCount = 0;
    }

    public int getComboCount() {
        return comboCount;
    }

    public int getComboBonus() {
        if (comboCount >= 4) return 3;
        if (comboCount >= 3) return 2;
        if (comboCount >= 2) return 1;
        return 0;
    }

    public String getComboMessage() {
        if (comboCount >= 4) return "UNSTOPPABLE!";
        if (comboCount == 3) return "ON FIRE!";
        if (comboCount == 2) return "Combo x2!";
        return "";
    }

    // NEW: Status effect methods
    public void applyPoison(int turns) {
        this.poisonTurns = Math.max(this.poisonTurns, turns);
    }

    public void applyBleeding(int turns) {
        this.bleedingTurns = Math.max(this.bleedingTurns, turns);
    }

    public void applyStun() {
        this.isStunned = true;
    }

    public void applyInspired(int turns) {
        this.inspiredTurns = Math.max(this.inspiredTurns, turns);
    }

    public String processStatusEffects() {
        StringBuilder message = new StringBuilder();
        int totalDamage = 0;

        // Process poison
        if (poisonTurns > 0) {
            totalDamage += 1;
            poisonTurns--;
            message.append("You feel sick from poison (-1 HP). ");
            if (poisonTurns > 0) {
                message.append("[").append(poisonTurns).append(" turns left] ");
            }
        }

        // Process bleeding
        if (bleedingTurns > 0) {
            totalDamage += 2;
            bleedingTurns--;
            message.append("You're bleeding (-2 HP). ");
            if (bleedingTurns > 0) {
                message.append("[").append(bleedingTurns).append(" turns left] ");
            }
        }

        // Process inspired
        if (inspiredTurns > 0) {
            inspiredTurns--;
            if (message.length() > 0) message.append("\n");
            message.append("You feel inspired! (+2 damage) ");
            if (inspiredTurns > 0) {
                message.append("[").append(inspiredTurns).append(" turns left]");
            }
        }

        // Apply damage
        if (totalDamage > 0) {
            this.hp -= totalDamage;
            if (this.hp < 0) this.hp = 0;
        }

        return message.toString().trim();
    }

    public boolean isStunned() {
        return isStunned;
    }

    public void clearStun() {
        isStunned = false;
    }

    public boolean hasActiveStatusEffects() {
        return poisonTurns > 0 || bleedingTurns > 0 || isStunned || inspiredTurns > 0;
    }

    public String getStatusEffectSummary() {
        StringBuilder summary = new StringBuilder();
        if (poisonTurns > 0) summary.append("[Poisoned] ");
        if (bleedingTurns > 0) summary.append("[Bleeding] ");
        if (isStunned) summary.append("[Stunned] ");
        if (inspiredTurns > 0) summary.append("[Inspired] ");
        return summary.toString().trim();
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
    public int getMaxHp() { return this.maxHp; }
    public String getWeapon() { return this.weapon; }
    public void setWeapon(String weapon) { this.weapon = weapon; }
    public boolean hasPendant() { return this.hasPendant; }
    public void setHasPendant(boolean has) { this.hasPendant = has; }
    public boolean hasCompass() { return this.hasCompass; }
    public void setHasCompass(boolean has) { this.hasCompass = has; }
    public boolean hasWhetstone() { return this.hasWhetstone; }
    public void setHasWhetstone(boolean has) { this.hasWhetstone = has; }
    public boolean hasToughHide() { return this.hasToughHide; }

    public void setHasToughHide(boolean has) {
        if (has && !this.hasToughHide) {
            this.hasToughHide = true;
            this.baseMaxHp += 5;
            this.maxHp = this.baseMaxHp;
            this.hp += 5;
        } else if (!has && this.hasToughHide) {
            this.hasToughHide = false;
            this.baseMaxHp -= 5;
            this.maxHp = this.baseMaxHp;
        }

        if (this.hp > this.maxHp) {
            this.hp = this.maxHp;
        }
    }
}