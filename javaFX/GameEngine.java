import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;

public class GameEngine extends Application {
    private final Pane gameRoot = new Pane();
    private final StackPane mainRoot = new StackPane();
    private static final int GAME_WIDTH = 960, GAME_HEIGHT = 480;
    private static final int VIEWPORT_WIDTH = 480, VIEWPORT_HEIGHT = 480;

    private Label scoreLabel;
    private Label spikesLabel;
    private Label applesLabel;

    private Label countdownLabel; // to show the countdown to restart after reaching the treasure chest

    //Game Over
    private VBox gameOverContainer;
    private boolean isGameOver = false;

    //Key control variables

    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean jumpPressed = false;
    private boolean spacePressed = false;

    private final double GRAVITY = 0.1;
    private boolean isJumping = false;
    private double velocityY = 0;

    private int frameCounter = 0;

    private Rectangle treasureBox;
    private VBox victoryContainer;
    private boolean isVictory = false;

    //Game objects
    private Player player;

    private final ArrayList<GameObject> apples = new ArrayList<>();
    private final ArrayList<GameObject> blocks = new ArrayList<>();
    private final ArrayList<GameObject> spikes = new ArrayList<>();
    private final ArrayList<GameObject> terrains = new ArrayList<>();

    // Counters & Tİmers

    private int appleCount = 0;
    private int spikeCount = 0;
    private int currentScore = 0;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {

        // VICTORY screen design
        victoryContainer = new VBox(15);
        victoryContainer.setAlignment(Pos.CENTER);
        victoryContainer.setBackground(new Background(new BackgroundFill(Color.web("#000000", 0.8), CornerRadii.EMPTY, Insets.EMPTY)));

        Label victoryLabel = new Label("* V I C T O R Y ! *");
        victoryLabel.setStyle("-fx-font-size: 40px; -fx-text-fill: gold; -fx-font-weight: bold;");

        countdownLabel = new Label("Restarting in 3...");
        countdownLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: white; -fx-font-weight: bold;");

        victoryContainer.getChildren().addAll(victoryLabel, countdownLabel);
        mainRoot.getChildren().add(victoryContainer);

        // Initializes the background image for the game environment.
        // gameRoot -> mainRoot change implied to prevent unwanted whitenings in the background
        mainRoot.setBackground(new Background(new BackgroundImage(
                new Image(new File("assets/Background(960x480x1).png").toURI().toString()), null, null, null, null)));

        // Uses a StackPane to overlay the UI components atop the game world.
        mainRoot.getChildren().add(gameRoot);


        //Game Over screen design
        gameOverContainer = new VBox(10);
        gameOverContainer.setAlignment(Pos.CENTER);
        gameOverContainer.setBackground(new Background(new BackgroundFill(Color.web("#000000", 0.7), CornerRadii.EMPTY, Insets.EMPTY)));

        Label gameOverLabel = new Label("GAME OVER :( ");
        gameOverLabel.setStyle("-fx-font-size: 40px; -fx-text-fill: red; -fx-font-weight: bold;");

        Label restartGLabel = new Label("Press 'R' to Try Again");
        restartGLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: violet;");

        gameOverContainer.getChildren().addAll(gameOverLabel, restartGLabel);
        mainRoot.getChildren().add(gameOverContainer);

        Scene scene = new Scene(mainRoot, VIEWPORT_WIDTH, VIEWPORT_HEIGHT);

        reset();

        //Movement commands for the keyboard
        scene.setOnKeyPressed(e -> {
            KeyCode code = e.getCode();

            if ((isGameOver || isVictory) && code == KeyCode.R) {
                reset();
                return;
            }

            if (isGameOver || isVictory) return;

            if (isGameOver) return;

            if (code == KeyCode.A || e.getCode() == KeyCode.LEFT) {
                leftPressed = true;
            }
            if (code == KeyCode.D || e.getCode() == KeyCode.RIGHT) {
                rightPressed = true;
            }
            if (code == KeyCode.W || e.getCode() == KeyCode.UP) {
                jumpPressed = true;
            }
            if (code == KeyCode.SPACE) {
                spacePressed = true;
            }
        });

        scene.setOnKeyReleased(e -> {
            KeyCode code = e.getCode();
            if (code == KeyCode.A || e.getCode() == KeyCode.LEFT) {
                leftPressed = false;
            }
            if (code == KeyCode.D || e.getCode() == KeyCode.RIGHT) {
                rightPressed = false;
            }
            if (code == KeyCode.W || e.getCode() == KeyCode.UP) {
                jumpPressed = false;
            }
            if (code == KeyCode.SPACE) {
                spacePressed = false;
            }
        });

        // Configures stage properties and displays the primary scene.
        stage.setTitle("BBM104 Assignment 3 Platform Game");
        stage.setScene(scene);
        stage.show();
        gameRoot.requestFocus();

        // Initializes and starts the primary game loop.
        new GameLoop().start();

    }

    private void reset() {

        isGameOver = false;
        gameOverContainer.setVisible(false);

        appleCount = 0;
        spikeCount = 0;
        currentScore = 0;
        velocityY = 0;
        isJumping = false;
        frameCounter = 0;

        //Reset the key commands to perevent the character's unwanted movement after restart.
        leftPressed = false;
        rightPressed = false;
        jumpPressed = false;
        spacePressed = false;

        apples.clear();
        blocks.clear();
        spikes.clear();
        terrains.clear();

        // Resets the game world state and reinitializes UI labels.
        gameRoot.getChildren().clear();
        gameRoot.setTranslateX(0);
        gameRoot.setTranslateY(0); //LOOK HERE

        if (scoreLabel == null) {
            scoreLabel = new Label("Score: 0");
            scoreLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: white; -fx-font-weight: bold;");
            StackPane.setAlignment(scoreLabel, Pos.BOTTOM_LEFT);
            StackPane.setMargin(scoreLabel, new Insets(0, 0, 5, 10));
            mainRoot.getChildren().add(scoreLabel);

            spikesLabel = new Label("Spikes: 0");
            spikesLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: white; -fx-font-weight: bold;");
            StackPane.setAlignment(spikesLabel, Pos.BOTTOM_RIGHT);
            StackPane.setMargin(spikesLabel, new Insets(0, 10, 5, 0));
            mainRoot.getChildren().add(spikesLabel);

            applesLabel = new Label("Apples: 0");
            applesLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: white; -fx-font-weight: bold;");
            StackPane.setAlignment(applesLabel, Pos.BOTTOM_CENTER);
            StackPane.setMargin(applesLabel, new Insets(0, 0, 5, 0));
            mainRoot.getChildren().add(applesLabel);
        }

        //reading the level.txt file
        try {
            Scanner scanner = new Scanner(new File("level.txt"));

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] p = line.split(",");

                if (p.length < 3) continue;

                String type = p[0].trim();

                double x = Double.parseDouble(p[1]);
                double y = Double.parseDouble(p[2]);

                if (type.equalsIgnoreCase("player")) {
                    player = new Player();
                    player.setLayoutX(x);
                    player.setLayoutY(y);
                    gameRoot.getChildren().add(player);

                } else if (type.equalsIgnoreCase("block")) {
                    Block b = new Block();
                    b.setLayoutX(x); b.setLayoutY(y);
                    blocks.add(b);
                    gameRoot.getChildren().add(b);

                } else if (type.equalsIgnoreCase("terrain")) {
                    Terrain t = new Terrain();
                    t.setLayoutX(x); t.setLayoutY(y);
                    terrains.add(t);
                    gameRoot.getChildren().add(t);

                } else if (type.equalsIgnoreCase("apple")) {
                    Apple a = new Apple();
                    a.setLayoutX(x); a.setLayoutY(y);
                    apples.add(a);
                    gameRoot.getChildren().add(a);
                    appleCount++;

                } else if (type.equalsIgnoreCase("spike")) {
                    Spike s = new Spike();
                    s.setLayoutX(x); s.setLayoutY(y);
                    spikes.add(s);
                    gameRoot.getChildren().add(s);
                    spikeCount++;
                }
            }

            scanner.close();

        } catch (FileNotFoundException e) {
            System.out.println("File name: 'level.txt' cannot be found! Try again later.");
        }

        updateLabels();

        //Remove current box and add new one after reset
        if (treasureBox != null) {
            gameRoot.getChildren().remove(treasureBox);
        }

        //form the treasure chest
        treasureBox = new javafx.scene.shape.Rectangle(32, 32);
        treasureBox.setFill(Color.GOLD); // Görsel imajın yoksa renk atayabilirsin
        treasureBox.setStroke(Color.BROWN);
        treasureBox.setStrokeWidth(2);

        //location of the trasure chest
        treasureBox.setLayoutX(880);
        treasureBox.setLayoutY(400);

        gameRoot.getChildren().add(treasureBox);

        isVictory = false;
        victoryContainer.setVisible(false);

        scoreLabel.toFront();
        spikesLabel.toFront();
        applesLabel.toFront();

    }

    private void updateLabels() {
        scoreLabel.setText("Score: " + currentScore);
        applesLabel.setText("Apples: " + appleCount);
        spikesLabel.setText("Spikes: " + spikeCount);
    }

    /**
     * To prevent go through solid objects
     * @param obj
     * @return boolean
     */
    private boolean isInvalid(GameObject obj) {

        if (obj.getLayoutX() < 0 || obj.getLayoutX() + obj.getObjectWidth() > GAME_WIDTH) {
            return true;
        }
        if (obj.getLayoutY() < 0 || obj.getLayoutY() + obj.getObjectHeight() > GAME_HEIGHT) {
            return true;
        }

        for (GameObject t : terrains) {
            if (obj != t && obj.getBoundsInParent().intersects(t.getBoundsInParent())) {
                if (checkActualIntersection(obj, t)) {
                    return true;
                }
            }
        }

        for (GameObject b : blocks) {
            if (obj != b && obj.getBoundsInParent().intersects(b.getBoundsInParent())) {
                if (checkActualIntersection(obj, b)) {
                    return true;
                }
            }
        }

        return false; //OR TRUE??? LOOK BACK HERE
    }

    /**
     * Applies 0.1 pixel tolerance to prevent unwanted default intersection perception
     * @param obj1
     * @param obj2
     */

    private boolean checkActualIntersection(GameObject obj1, GameObject obj2) {

        double tolerance = 0.1;

        return (obj1.getLayoutX() + obj1.getObjectWidth() - tolerance > obj2.getLayoutX() &&
                obj1.getLayoutX() + tolerance < obj2.getLayoutX() + obj2.getObjectWidth() &&
                obj1.getLayoutY() + obj1.getObjectHeight() - tolerance > obj2.getLayoutY() &&
                obj1.getLayoutY() + tolerance < obj2.getLayoutY() + obj2.getObjectHeight());
    }

    private void spawn() {

        Random r = new Random();

        GameObject platform = null;
        int attempts = 0;
        int totalPlatformsNo = terrains.size() + blocks.size();

        if (totalPlatformsNo > 0) {
            while (platform == null && attempts < 100) {
                attempts++;
                int randomIndex = r.nextInt(totalPlatformsNo);

                if (randomIndex < terrains.size()) {
                    platform = terrains.get(randomIndex);
                } else {
                    platform = blocks.get(randomIndex - terrains.size());
                }

                if (platform.getLayoutY() <= 0) {
                    platform = null;
                }
            }
        }

        if (platform != null) {
            Spike spike = new Spike();
            double x = platform.getLayoutX() + r.nextDouble() * (platform.getObjectWidth() - spike.getObjectWidth());
            double y = platform.getLayoutY() - spike.getObjectHeight();
            spike.setLayoutX(x);
            spike.setLayoutY(y);

            if (spike.getLayoutX() >= 0 && spike.getLayoutX() + spike.getObjectWidth() <= GAME_WIDTH) {
                spikes.add(spike);
                gameRoot.getChildren().add(spike);
                spikeCount++;
            }
        }

        for (int i = 0; i < 2; i++) {

            Apple apple = new Apple();
            boolean placed = false;
            int aptempts = 0; //apple+attempts

            while (!placed && aptempts < 30) {  //to prevent infinite loop (might be in/decreased)
                aptempts++;
                double ax = r.nextInt(GAME_WIDTH - 32);
                double ay = r.nextInt(GAME_HEIGHT / 2);

                apple.setLayoutX(ax);
                apple.setLayoutY(ay);

                if (!isInvalid(apple)) {
                    placed = true;
                }
            }

            if (placed) {
                apples.add(apple);
                gameRoot.getChildren().add(apple);
                appleCount++;
            }
        }
        updateLabels();
    }

    // --------------------------------------------------------------------
    // This update method is called automatically in every animation frame.
    // --------------------------------------------------------------------

    private void update() {

        if (isVictory || isGameOver) return;

        //triggering the animations of the objects
        for (javafx.scene.Node node : gameRoot.getChildren()) {
            if (node instanceof GameObject) {
                ((GameObject) node).update();
            }
        }

        if (player == null ) return;

        // Horizontal Movement (x-axis logic)

        double oldX = player.getLayoutX();

        if (leftPressed) {
            player.setLayoutX(player.getLayoutX() - 1); //move one pixel to the left
            player.setScaleX(-1);
        } else if (rightPressed) {
            player.setLayoutX(player.getLayoutX() + 1); //move one pixel to the right
            player.setScaleX(1);
        }

        if (isInvalid(player)) {
            player.setLayoutX(oldX);
        }

        // Jumping & GRAVITY (y-axis logic)

        double oldY = player.getLayoutY();

        player.setLayoutY(oldY + 1);
        boolean onTheGround = isInvalid(player);
        player.setLayoutY(oldY);

        if (onTheGround) {
            isJumping = false;
            velocityY = 0;
        } else {
            isJumping = true; //falling or jumping (on air)
        }

        if (jumpPressed && onTheGround) {
            velocityY = -4; //upward velocity against the gravity
            isJumping = true;
        }

        if (isJumping) {
            double GRAVITY = 0.1;
            velocityY += GRAVITY;                                   // accelerating towards to ground
            player.setLayoutY(player.getLayoutY() + velocityY);

            if (isInvalid(player)) {
                player.setLayoutY(oldY); //cancel vertical movement in case of collision

                if (velocityY > 0) {
                    isJumping = false; //fell onto the ground
                }
                velocityY = 0;
            }
        }

        // Spike Traps

        for (GameObject spike : spikes) {

            if (player.getBoundsInParent().intersects(spike.getBoundsInParent())) {
                isGameOver = true;
                gameOverContainer.setVisible(true); // Game Over stamp is visible now
                gameOverContainer.toFront(); // covers everything by coming very front
                return;
            }
        }

        // Apple Collection

        for (int i = apples.size() - 1; i >= 0; i--) {

            GameObject a = apples.get(i);

            if (player.getBoundsInParent().intersects(a.getBoundsInParent())) {

                apples.remove(a);
                gameRoot.getChildren().remove(a);
                appleCount--;

                if (appleCount % 2 == 0) {
                    currentScore += 1;

                    // Remove one random spike
                    if (!spikes.isEmpty()) {
                        Random rand = new Random();

                        int targetIndex = rand.nextInt(spikes.size());

                        GameObject removedSpike = spikes.remove(targetIndex);

                        gameRoot.getChildren().remove(removedSpike);
                        spikeCount--;
                    }
                }
                updateLabels();
                break;
            }
        }

        frameCounter++;
        if (frameCounter >= 180) { // spawn in every 3 seconds = 180 frames
            spawn();
            frameCounter = 0;
        }

        // ~ * Victory * ~

        if (treasureBox != null && player != null) {
            if (player.getBoundsInParent().intersects(treasureBox.getBoundsInParent())) {
                isVictory = true;
                victoryContainer.setVisible(true);
                victoryContainer.toFront();

                countdownLabel.setText("Restarting in 3...");

                final int[] timeLeft = {3};

                Timeline timeline = new Timeline(
                        new KeyFrame(Duration.seconds(1), event -> {
                            timeLeft[0]--; // goes back 1 second by 1

                            if (timeLeft[0] > 0) {
                                countdownLabel.setText("Restarting in " + timeLeft[0] + "...");
                            } else {
                                // time's out, reset the game.
                                reset();
                            }
                        })
                );

                timeline.setCycleCount(3);
                timeline.play();

                return;
            }
        }

        // Centering the player right in the middle of the screen

        double offsetX = VIEWPORT_WIDTH / 2.0 - (player.getLayoutX() + player.getObjectWidth() / 2.0);

        if (offsetX > 0) {
            offsetX = 0;
        }

        if (offsetX < VIEWPORT_WIDTH - GAME_WIDTH) {
            offsetX = VIEWPORT_WIDTH - GAME_WIDTH;
        }

        gameRoot.setTranslateX(offsetX);

        //Spike destroyer

        if (spacePressed) {
            // one time lock needed to prevent loop behavior
            spacePressed = false;

            if (currentScore >= 5 && !spikes.isEmpty()) {
                GameObject closestSpike = spikes.get(0);
                double minDistance = Math.abs(player.getLayoutX() - closestSpike.getLayoutX());

                // Find the closwst spike
                for (GameObject cs : spikes) {
                    double distance = Math.abs(player.getLayoutX() - cs.getLayoutX());
                    if (distance < minDistance) {
                        minDistance = distance;
                        closestSpike = cs;
                    }
                }

                if (closestSpike != null) {
                    spikes.remove(closestSpike);
                    gameRoot.getChildren().remove(closestSpike);
                    currentScore -= 5;
                    spikeCount--;
                    updateLabels();
                }
            }
        }
    }

    private class GameLoop extends AnimationTimer {
        @Override
        public void handle(long now) {
            update();
        }
    }
}
