# Burn (2025)

A narrative-driven 2D action game built from scratch in Java, developed over approximately one month as a final project for Grade 12 Computer Science.

## Overview
Burn is a story-focused 2D game that combines real-time action gameplay with scripted cutscenes to deliver a continuous narrative experience.  
Players progress through a linear sequence of levels, encountering enemies, bosses, and story moments that are tightly integrated into gameplay.

**No external game engine was used**, the project was built entirely from the ground up to explore core game programming concepts such as rendering, state management, entity systems, and narrative flow.

## How to Run the Game

1. Download or clone the repository
2. Make sure **Java 17 or newer** is installed
3. Extract the ZIP and either:
   - Double-click **Burn.jar**, or
   - Run the following command from the project folder:
     ```bash
     java -jar Burn.jar
     ```
4. Choose your difficulty and click the play button
5. Look at the current objective in the bottom right corner to figure out what to do
   **"Burn Legacy" is the version of the game made for the school project in the span of a month. "Burn" is the latest copy of the game.**

> The asset folders must remain in the same directory as `Burn.jar`.

## Controls
**W A S D** – Move  
**Mouse Move** - Aim  
**Left Click** – Attack  
**Scroll Wheel** – Switch weapon  
**R** – Reload  
**ESC** – Pause
## Gameplay & Story Structure
- Linear, level-based progression
- Story delivery through:
  - Scripted cutscenes
  - In-game events
  - Visual background and scene transitions
- Combat encounters are designed to pace and reinforce the narrative rather than exist in isolation

The game emphasizes atmosphere, progression, and storytelling alongside mechanical gameplay.

## Roles & Contributions

### Adam Shafronsky
**Lead Programmer & Game Designer**
- Implemented the majority of the codebase (~90%)
- Designed and implemented the core game architecture
- Built the custom game loop, rendering pipeline, and input handling system
- Implemented:
  - Level management and progression logic
  - Enemy and boss behavior systems
  - Collision detection and combat mechanics
  - A cutscene playback system to control narrative transitions
- Integrated all visual assets into the engin
- Implemented A* pathfinding AI for enemies

### Diego Eleazar
**Art, Level Design & Supporting Development**
- Created the majority of visual assets, including sprites, backgrounds, and animations
- Planned level layouts, enemy placement, and overall level flow
- Implemented select code components related to:
  - Title screen
  - Entity walking animations (animation strips)
- Collaborated closely throughout development, providing design feedback and implementation support

## Technical Details
- Language: Java
- Paradigm: Object-Oriented Programming
- Core Systems:
  - Custom game loop and rendering
  - Entity-based architecture (player, enemies, bosses)
  - Collision and damage handling
  - Level sequencing and state transitions
  - Scripted cutscene system for narrative delivery

## Visual Assets Note
Due to time constraints, not all background art assets were completed for every level.  
Some levels currently use placeholder layout backgrounds that clearly display level geometry such as walls and boundaries.

All gameplay systems, collision detection, progression logic, and story flow are fully implemented. The game is playable from start to finish, with placeholder layouts ensuring that all mechanics and level functionality remain clear and fully functional.

## Development Notes
Burn was developed under a short time constraint (~1 month) with a strong focus on completing a fully playable, story-driven experience rather than a purely technical demo or prototype.
