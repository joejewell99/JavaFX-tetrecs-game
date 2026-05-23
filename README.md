# JavaFX TetrECS Game

TetrECS is a JavaFX puzzle game inspired by Tetris. Instead of falling pieces, the player places randomly generated pieces onto a 5x5 board. The aim is to clear complete rows and columns, build score multipliers, and survive for as long as possible before running out of lives.

It used to connect to a university server, but that server appears to be unavailable, so the websocket setup is currently disabled. Multiplayer is still in development.

## Features

- JavaFX desktop interface with separate menu, game, instructions, and score scenes.
- 5x5 playable board with mouse-based piece placement.
- 15 different 3x3 game pieces, including lines, corners, squares, diagonals, plus pieces, and other shapes.
- Current-piece and next-piece previews.
- Piece rotation using the keyboard, right mouse button, or current-piece preview.
- Current and next piece swapping.
- Timed turns with a visual time bar that changes from green to red.
- Lives system: if the timer expires, a life is lost and the game moves to the next piece.
- Score, high score, level, and multiplier display.
- Line and column clearing with fade animations.
- Local score saving through `scores.txt`.
- Background music and sound effects for menu, gameplay, rotation, placement, clearing, invalid moves, life loss, and game over.
- Home-screen master volume control for adjusting all music and sound effects.
- Instruction screen showing controls and all available pieces.

## How The Game Works

The game creates a 5x5 grid and randomly selects a current piece and a following piece. Each piece is represented as a 3x3 shape. To play, click a square on the board; the selected square acts as the centre of the 3x3 piece area.

A move is valid only if every occupied block in the piece fits inside the board and lands on an empty grid square. If the move is valid, the piece is placed, a new piece is generated, and the turn timer resets. If the move is invalid, the game plays an error sound and the board is unchanged.

After each valid placement, the game checks every row and column. Any full row or column is cleared. Score is calculated using:

```text
lines cleared * blocks cleared * 10 * multiplier
```

Clearing at least one line increases the multiplier. If no line is cleared, the multiplier resets to 1. The level is based on score, increasing every 1000 points. Higher levels reduce the turn timer, down to a minimum delay.

When the timer runs out, the player loses one life, the current piece changes, and the multiplier resets. When lives reach 0, the game moves to the score screen, where the player can enter a name and save their score locally.

## Controls

| Action | Control |
| --- | --- |
| Place piece | Left click on the board |
| Rotate right | Right click, `E`, `C`, `]`, or click the current-piece preview |
| Rotate left | `Q`, `Z`, or `[` |
| Swap current and next piece | `R` or `Space` |
| Adjust master volume | Click `VOL` on the menu, then drag the slider |
| Leave game / go back | `Esc` |
| Exit from main menu | `Esc` or the Exit button |

The arrow keys and `WASD` are currently wired up in the scene, but the movement methods in `Grid.java` are placeholders, so they do not move pieces yet.

## Requirements

- Java Development Kit 17
- Apache Maven
- A desktop environment capable of opening JavaFX windows

Project dependencies are managed by Maven:

- JavaFX Controls, FXML, and Media, version `21-ea+5`
- Log4j API and Core, version `2.20.0`
- nv-websocket-client, version `2.14`

The websocket dependency is present for online score/network features, but the communicator code is currently commented out in the game window and scores scene.

## Running The Game

From the project root, run:

```powershell
mvn javafx:run
```

To compile and check the project without launching the JavaFX window, run:

```powershell
mvn test
```

There are currently no automated tests, so this command mainly verifies that the project compiles successfully.

## Building A Jar

The project includes a Maven shade profile for creating a shaded jar:

```powershell
mvn package -Pshade
```

The shaded jar uses `uk.ac.soton.comp1206.Launcher` as its main class so JavaFX can start correctly.

## Project Notes

- The main JavaFX application class is `uk.ac.soton.comp1206.App`.
- The Maven JavaFX plugin is configured to run `uk.ac.soton.comp1206/uk.ac.soton.comp1206.App`.
- Local scores are read from and appended to `scores.txt` in the project root.
- The `VOL` button on the menu adjusts a shared master volume used by both background music and sound effects.
- Several image assets are loaded using absolute paths such as `C:\Comp1206\coursework\src\main\resources\images\TetrECS.png`. If the project folder is moved or renamed, those image paths may need to be updated or converted to classpath resource loading.
