public class GameState {
    private GameLocation position;
    private GameLocation postCombatPosition;
    private GameLocation postWeaponChoicePosition;
    private String newWeaponFound;

    private Player player;
    private Enemy currentEnemy;

    public GameState() {
        this.player = new Player(20, "Pocket Knife");
        this.position = GameLocation.START;
    }

    public void reset() {
        this.player.reset(20, "Pocket Knife");
        this.position = GameLocation.START;
        this.currentEnemy = null;
        this.postCombatPosition = null;
        this.postWeaponChoicePosition = null;
        this.newWeaponFound = null;
    }

    // Getters and Setters
    public GameLocation getPosition() { return position; }
    public void setPosition(GameLocation position) { this.position = position; }

    public GameLocation getPostCombatPosition() { return postCombatPosition; }
    public void setPostCombatPosition(GameLocation postCombatPosition) {
        this.postCombatPosition = postCombatPosition;
    }

    public GameLocation getPostWeaponChoicePosition() { return postWeaponChoicePosition; }
    public void setPostWeaponChoicePosition(GameLocation postWeaponChoicePosition) {
        this.postWeaponChoicePosition = postWeaponChoicePosition;
    }

    public String getNewWeaponFound() { return newWeaponFound; }
    public void setNewWeaponFound(String newWeaponFound) {
        this.newWeaponFound = newWeaponFound;
    }

    public Player getPlayer() { return player; }
    public Enemy getCurrentEnemy() { return currentEnemy; }
    public void setCurrentEnemy(Enemy enemy) { this.currentEnemy = enemy; }
}