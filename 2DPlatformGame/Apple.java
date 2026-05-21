public class Apple extends GameObject {

    public Apple() {

        super("assets/Apple(16x18x17).png", 32, 32);
    }

    @Override
    public void update() {
        animate(17, 7);
    }
}