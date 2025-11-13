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
        int minDamageBonus = hasWhetstone ? 1 : 0;

        if (weapon.equals("Machete")) {
            return rand.nextInt(8) + 3 + minDamageBonus;
        } else if (weapon.equals("Obsidian Dagger")) {
            return rand.nextInt(5) + 4 + minDamageBonus;
        } else if (weapon.equals("Makeshift Spear")) {
            return rand.nextInt(6) + 2 + minDamageBonus;
        } else if (weapon.equals("Rusty Pipe")) {
            return rand.nextInt(5) + 2 + minDamageBonus;
        } else { // Pocket Knife
            return rand.nextInt(4) + 1 + minDamageBonus;
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