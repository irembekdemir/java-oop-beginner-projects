public class Terrain extends GameObject {

    public Terrain() {

        super("assets/Terrain(48x48x1).png", 48, 48
        );
    }

    @Override
    public void update() {
        // Terrain has no animation or behavior
    }
}