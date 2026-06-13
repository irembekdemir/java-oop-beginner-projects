# 2D Platform Game

This project is a dynamic 2D side-scrolling platform game developed using the JavaFX library, built upon solid Object-Oriented Programming (OOP) principles. The player navigates a character through a grid of platforms loaded directly from a `level.txt` file, dodging hazards to reach the ultimate treasure chest at the end.

> **💡 Note on Implementation:** This is an assignment based project. Hence the core JavaFX graphical engine architecture, base game loop structure, level file parsing template, and basic collision placeholders were provided as a starter framework by the instructor. The advanced gameplay mechanics, real-time item spawning loops, active target-destruction mechanics, and the automated countdown victory state were custom-implemented to complete the assignment specifications.

---

## 🎮 Game Objectives & Rules

* **Primary Objective:** Navigate through the generated platforms to reach the **Golden Treasure Box** located near the end of the map (hardcoded at coordinates X: 880, Y: 400).
* **Victory State:** Upon touching the treasure box, the gameplay physics freeze instantly. A stylish victory overlay appears, and a dynamic **3-second countdown** triggers to automatically reset and restart the game.
* **Collecting Apples:** Gathering apples scattered across the map rewards the player. Every two apples collected increases the overall score by 1 and magically obliterates one random dangerous trap (`Spike`) from the current map.
* **Dynamic Spawning:** To maintain an arcade-like challenge, the game engine automatically spawns new apples and spikes at random valid platform positions every 3 seconds (180 animation frames).
* **Hazards & Game Over:** Colliding with any `Spike` instantly kills the player, prompting a "Game Over" screen. Players can press the `R` key to cleanly reset all parameters and try again.
* **Special Ability (Spike Destroyer):** If your current score is **5 or higher**, pressing the `SPACE` key triggers a devastating blast that target-destroys the single closest spike to the player. This active ability consumes 5 score points.

---

## ⌨️ Controls

| Key | Action |
| :--- | :--- |
| **A / Left Arrow Key** | Walk Left (Flips character sprite symmetry to face left) |
| **D / Right Arrow Key** | Walk Right (Flips character sprite symmetry to face right) |
| **W / Up Arrow Key** | Jump (Calculated via velocity physics to create natural parabolic curves) |
| **SPACEBAR** | Destroy Closest Spike (Requires a minimum score of 5) |
| **R Key** | Manually Reset / Restart Game (Available during Game Over or Victory screens) |

---

## 🏗️ Architecture & Class Structure

The game engine utilizes JavaFX's native `AnimationTimer`, running a continuous frame-loop at approximately 60 FPS. It heavily takes advantage of polymorphism and class inheritance:

### 1. `GameObject` (Abstract Base Class)
The foundational blueprint class from which all physical game objects (`Player`, `Block`, `Spike`, `Terrain`, `Apple`) inherit. It encapsulates width, height, structural X-Y layout positioning, and contains an overridable `update()` method for handling unique frame-by-frame asset animations.

### 2. `Player`
The main interactive character class controlled by the user. It manipulates the `scaleX` transform matrix property to handle smooth directional turns without splitting the spritesheet asset.

### 3. `Block` & `Terrain`
Solid, unyielding environmental structures that provide physical surfaces for entities to stand on. Collision detection engines (`isInvalid` & `checkActualIntersection`) employ a `0.1` pixel bounding tolerance to reliably prevent entities from passing through these solid barriers.

### 4. `Apple` & `Spike`
Represent the pickable scoring items and hazardous static traps respectively, utilizing automated lifecycle tracking arrays.

### 5. `GameEngine` (Core Framework)
* **Camera Viewport System:** As the player advances horizontally, the engine calculates an `offsetX` based on the predefined `VIEWPORT` bounds and shifts the entire `gameRoot` canvas layer. This effectively keeps the camera perfectly centered on the moving player.
* **Physics Framework:** Implements a steady `GRAVITY` coefficient (0.1) that handles falling mechanics and simulates realistic aerial acceleration vectors.
* **UI Layer Overlay:** Utilizes a `StackPane` arrangement to seamlessly superimpose the HUD labels (Score, Apples, Spikes counters) and modal containers (Game Over, Victory countdown alerts) over the actively rendering background graphics.

---

## 📂 Project Directory Structure

For the application to read map layouts and render textures successfully, ensure your workspace directory matches the layout below:

```text
├── src/
│   ├── GameEngine.java
│   ├── GameObject.java
│   ├── Player.java
│   ├── Block.java
│   ├── Terrain.java
│   ├── Apple.java
│   └── Spike.java
├── assets/
│   └── Background(960x480x1).png    # Environmental background image
└── level.txt                        # Initial structural map configuration
```

## level.txt File Format Standard:
```bash
player,50,400
block,200,350
terrain,0,450
apple,300,200
spike,400,420
```

## 🚀 Execution & Compilation Instructions
Because this project relies heavily on JavaFX components, make sure the JavaFX SDK modules are properly linked in your compile arguments if you are utilizing terminal commands:
``` bash
# 1. Compile all source files into a binary directory
javac --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls,javafx.fxml -d bin src/*.java

# 2. Launch the application
java --module-path /path/to/javafx-sdk/lib --add-modules javafx.controls -cp bin GameEngine
```

## Author 
Developed by [irem bekdemir](https://github.com/irembekdemir)
