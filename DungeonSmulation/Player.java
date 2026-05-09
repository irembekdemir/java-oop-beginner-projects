/**
 * Player class represents the main character.
 * Keeps track of the remaining health and inventory information of the character (key, shield).
 * @author irem bekdemir
 * @verison 1.0
 */
public class Player {
    private int remainingHealth;
    private boolean haveKey;
    private int shield;
    private int maxHealth;

    /**
    * Creates a new player object w a specified initial health 
    * Starts without a shield nor a key.
    * @param remainingHealth is the amount of health that the character have.
    */
    public Player(int remainingHealth) {
        this.remainingHealth = remainingHealth;
        this.maxHealth = remainingHealth;
        this.haveKey = false;
        this.shield = 0;
    }

    /**
    * Responsible for damage taking. if there is any shield, damage will be decreased from the shield first.
    * Shield is reset after a damage.
    * Health amount cannot be below zero.
    * @param damage amount of raw damage recieved
    */
    public void takeDamage(int damage) {
        int effectiveDamage = Math.max(0, damage - shield); // damage cannot be negative, at least zero.
        remainingHealth = Math.max(0, remainingHealth - effectiveDamage);
        shield = 0;
    }

    /**
     * Increases the amount of remaining health.
     * @param cure amount of healing.
     */
    public void heal(int cure) { 
        remainingHealth += cure;
    }

    public boolean haveKey() {
        return haveKey;
    }

    public void setHaveKey(boolean haveKey) { //reachability
        this.haveKey = haveKey;
    }

    /**
     * Adds a temporary shield to the charachter
     * Shield cannot be negative
     * @param protect amount of protection that shield provides.
     */
    public void setShield(int protect) {  //reachability
        shield = Math.max(0, protect);   
    }
    public void setHealth(int health) {   //reachability
        remainingHealth = health;                
    }

    /**
     * @return Getters returns the specific function parameters
     */
    public int getHealth() {
        return remainingHealth;
    }
    public int getShield() {
        return shield;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    /**
     * Function related to the Princess Room
     */
    public void fullHeal() {
        remainingHealth = maxHealth;
    }

    /**
     * Prevents the corruption of DFS process caused by share state effects on unexplored paths.
     * @return
     */
    public Player copy() {
        Player p = new Player(this.maxHealth);
        p.setHealth(this.remainingHealth);
        p.setShield(this.shield);
        p.setHaveKey(this.haveKey);
        return p;
    }
}
