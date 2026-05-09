# Dungeon Escape Simulation

A Java-based dungeon adventure simulation that uses a Depth-First Search (DFS) Backtracking Algorithm to find the optimal escape path through a dungeon grid.

The player must:
- survive traps and monsters,
- collect the key,
- interact with special rooms,
- and finally reach the exit with the highest remaining health possible.

---

## Features

- DFS + Backtracking pathfinding
- Health and shield system
- Inventory management (key collection)
- Multiple room types with unique effects
- Special interactive Witch Room
- Princess rescue mechanic
- Best-path selection based on:
  1. Maximum remaining health
  2. Minimum number of steps

---

## Room Types

| Symbol | Room Type               | Effect                                                                                                     |
| ------ | ----------------------- | ---------------------------------------------------------------------------------------------------------- |
| H      | Healing Room            | Restores 15 HP                                                                                             |
| T      | Trap Room               | Deals 20 damage                                                                                            |
| M      | Monster Room            | Deals 30 damage                                                                                            |
| K      | Key Room                | Gives the player a key                                                                                     |
| E      | Empty Room              | No effect                                                                                                  |
| X      | Exit Room               | Final destination                                                                                          |
| A      | Artifact Room A         | Gives shield +6                                                                                            |
| B      | Artifact Room B         | Gives shield +4                                                                                            |
| L      | Legendary Artifact Room | Gives shield +10                                                                                           |
| P      | Princess Room           | Fully restores health if player has at least 50 HP, otherwise deals 10 damage                              |
| W      | Witch Room              | Asks a math question. Correct answer teleports player to the exit, wrong answer kills the player instantly |

---

## Project Structure
```
├── Main.java
├── Logic.java
├── Dungeon.java
├── Room.java
├── Player.java
├── Position.java
├── input.txt
├── output.txt
```
---

## Class Responsibilities

### Main.java
Handles:
- file input/output
- dungeon initialization
- simulation startup

---

### Logic.java
Responsible for:
- DFS traversal
- backtracking
- path evaluation
- game rules
- best-path selection

---

### Dungeon.java

Represents the dungeon grid and manages room placement.

---

### Room.java

Abstract base class for all room types.

Contains subclasses:
- HealingRoom
- TrapRoom
- MonsterRoom
- KeyRoom
- EmptyRoom
- ExitRoom
- ArtifactRoom
- PrincessRoom
- WitchRoom

---

### Player.java

Stores player state:
- health
- shield
- key possession
- maximum health

---

### Position.java

Represents:
- row
- column
- remaining health at that position

Used for path tracking.

---

## DFS Backtracking Algorithm

The simulation explores all valid paths recursively using DFS.

For every move:
- The player state is copied
- Room effects are applied
- The algorithm recursively explores neighboring rooms
- Backtracking restores previous state safely

The best path is selected using:
- highest remaining health
- shortest path as tie-breaker

---

## Complexity Analysis

Worst-case time complexity:
*O(4^(n×m))*
because every cell may recursively explore up to 4 directions.

Space complexity:
*O(n×m)*
due to:
- recursion stack
- visited matrix
- path storage

---

## Input Format
```
rows cols initialHealth
startRow startCol
exitRow exitCol
[dungeon grid]
```
Example:
```
4 4 100
0 0
3 3

E H T M
A K E H
P W B T
E E L X
```

---

## Output Format

Example successful output:
```
RESULT: SUCCESS
MAX_HEALTH: 100
REMAINING_HEALTH: 72
STEPS: 6
PATH:
(0,0,100) -> (1,0,100) -> (1,1,100) -> ...
```
Example failed output:
```
RESULT: FAILURE
MAX_HEALTH: 100
REASON: Player died before reaching the exit. RIP
```
---

## Compilation

Compile:

```javac *.java```

Run:
```
java Main input.txt output.txt
```
---

## Special Mechanics
### Shield System

Artifacts provide temporary shields that reduce incoming damage.

After damage is taken:
-shield resets to zero

---

### Witch Room

The Witch asks the player a random math question.

Correct answer:
- player is teleported directly to the exit
- 
Wrong answer:
- player dies instantly
- 
---
 
### Princess Room

The player attempts to save the princess.

If HP ≥ 50:
- health fully restores

Otherwise:
- player loses 10 HP

---

## Object-Oriented Design Concepts Used

- Abstraction
- Inheritance
- Polymorphism
- Encapsulation
- Recursive Backtracking
- State Copying
- Dynamic Dispatch

# Author

Developed by [irem bekdemir](https://github.com/irembekdemir)

Dungeon Escape Simulation Project – Java DFS Backtracking System
