public class Player extends GameObject {

    public Player() {

        super("assets/Player(22x26x11).png", 22, 26);
    }

    @Override
    public void update() {
        // Player animation (ex: 6 frame, med. speed)
        animate(11, 7);
    }
}