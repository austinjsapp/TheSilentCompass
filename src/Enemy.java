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