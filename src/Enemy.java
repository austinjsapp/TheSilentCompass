import java.util.Random;

public class Enemy {
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
            return rand.nextInt(6) + 1;
        } else if (name.equals("Giant Crab")) {
            return rand.nextInt(4) + 2;
        } else if (name.equals("Giant Snake")) {
            return rand.nextInt(4) + 1;
        } else if (name.equals("Large Spider")) {
            return rand.nextInt(3) + 1;
        }
        return 1;
    }

    public int getFleeSuccessBonus() {
        if (name.equals("Large Spider")) {
            return 10;
        } else if (name.equals("Jungle Stalker")) {
            return -10;
        } else if (name.equals("Jaguar")) {
            return -5;
        }
        return 0;
    }

    // NEW: Check if enemy can apply status effect (30% chance)
    public String tryApplyStatusEffect(Random rand, Player player) {
        if (rand.nextInt(100) < 30) { // 30% chance
            if (name.equals("Giant Snake")) {
                player.applyPoison(3);
                return "\nThe snake's venom courses through your veins! [POISONED for 3 turns]";
            } else if (name.equals("Jaguar")) {
                player.applyBleeding(2);
                return "\nThe jaguar's claws leave deep wounds! [BLEEDING for 2 turns]";
            } else if (name.equals("Giant Crab")) {
                player.applyStun();
                return "\nThe crab's pincers crush your arm! [STUNNED - next attack skipped]";
            }
        }
        return "";
    }

    public boolean isAlive() { return this.hp > 0; }
    public String getName() { return this.name; }
    public int getHp() { return this.hp; }
}