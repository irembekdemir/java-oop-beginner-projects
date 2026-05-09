
/**
 * An abstrac class that forms the base of room types of the dungeon simulation.
 * Defines the effets of each room on player.
 * @author irem bekdemir
 * @version 1.0
 */

public abstract class Room {
    /**
     * Applies the specific room's logic/effect to the player.
     * @param player The character currently entering the room.
     */
    public abstract void effectOnPlayer(Player player);

    // Subclasses and their effects.
    public static class HealingRoom extends Room{
        @Override
        public void effectOnPlayer(Player player){
            player.heal(15);
        }
    }

    public static class TrapRoom extends Room {
        @Override
        public void effectOnPlayer(Player player) {
            player.takeDamage(20);
        }
    }

    public static class MonsterRoom extends Room {
        @Override
        public void effectOnPlayer(Player player){
            player.takeDamage(30);
        }
    }

    public static class KeyRoom extends Room {
        @Override
        public void effectOnPlayer(Player player){
            player.setHaveKey(true);
        }
    }

    public static class EmptyRoom extends Room{
        @Override
        public void effectOnPlayer(Player player){ 
            //no effect
        }
    }

    public static class ExitRoom extends Room {
        @Override
        public void effectOnPlayer(Player player){
        }
    }

    public static class WitchRoom extends Room {
        @Override
        if (takeWitch = true) {
            public void effectOnPlayer (Player player){
                player.heal(50);
            }
        } else {
            public void effectOnPlayer (Player player) {
                player.takeDamage(25);
            }
        }
    }
      
    /**
    * A room containing artifacts that provide defensive shields to the player.
    */
    public static class ArtifactRoom extends Room {
        private char artifactType;

        /**
        * Construction of ArtifactRoom with a specific type.
        * @param type is the character representing the artifact ('A', 'B', or 'L').
        */
        public ArtifactRoom(char type) {
            this.artifactType = type;
        }

        @Override
        public void effectOnPlayer (Player player) {

            switch (artifactType) {
                case 'A':
                    player.setShield(6);
                    break;

                case 'B':
                    player.setShield(4);
                    break;

                case 'L':
                    player.setShield(10);
                    break;
            }
        }
    }
}


