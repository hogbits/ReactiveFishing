# Reactive Fishing Plugin Instructions

## Project Info
Plugin Name: ReactiveFishing
Main Package: com.hogbits.reactivefishing
Namespace: com.hogbits
Build Tool: Maven
Java Version: 21
API: Paper 1.21.11

## Architecture
Suggested classes:
- ReactiveFishingPlugin (main class)
- FishingListener
- MinigameManager
- MinigameSession
- GUIBuilder
- ConfigManager
- CommandHandler

# Required Event Listeners
- PlayerFishEvent
- PlayerInteractEvent
- InventoryClickEvent
- InventoryCloseEvent
- PlayerQuitEvent

## Core Gameplay

Implement a reaction-based fishing minigame:

### Flow
- Detect when a fish bites using PlayerFishEvent (State.BITE or State.CAUGHT_FISH)
- Based on config trigger_percent, decide whether to trigger the minigame
- If triggered:
- - Cancel the normal fishing result
- - Store fishing context (player, hook, loot)
- - After fish bite is detected, the player must right-click with the fishing rod to start the minigame GUI
- - This should be handled via PlayerInteractEvent
- On right-click:
- - Open minigame GUI
- Player must click at correct time:
- - ✅ Success → give configured fish item
- - ❌ Fail → cancel catch
- Timeout → fail automatically

## Minigame Logic

### GUI
- Size: 27 slots
- Title: configurable (Catch the Fish! default)

### Layout

#### Decorative Rows
- Slots 0–8: alternating CYAN / LIGHT_BLUE stained glass panes
- Slots 9–17: alternating LIGHT_BLUE / CYAN (background layer)
- Slots 18, 20, 21, 23, 24, 26: SEAGRASS
- Slots 19, 22, 25: KELP

#### Active Gameplay Zone
- Slots 9–17
- A moving fish indicator:
- - Use a distinct item (e.g., PUFFERFISH or COD)
- A catch zone:
- - 3 consecutive LIME stained glass panes
- - Random start index between 9–15

#### Fish Movement
- Fish moves 1 slot at a time
- Direction:
- - Starts moving right
- - At slot 17 → reverse left
- - At slot 9 → reverse right
- - Movement repeats ("bouncing")

#### Timing
- Movement interval = config difficulty (ticks per step)
- Default: 10 ticks

## Player Interaction

### Clicking Behavior
- Clicking any slot in GUI triggers result check

### Win Condition
- Fish is inside catch zone when clicked

### Lose Conditions
- Fish outside catch zone when clicked
- Timeout exceeded

## Timeout
- Configurable (time_limit)
- Default: 5 seconds
- Use Bukkit scheduler
- On timeout:
- Close GUI
- Fail minigame

## Configuration (config.yml)
```
fish_indicator: TROPICAL_FISH   # Material used as fishing indicator
time_limit: 10                  # Time limit in seconds before auto-fail
difficulty: 5                   # Ticks per fish movement (Lower = faster)
gui_title: "Catch the Fish!"    # Title shown in minigame GUI
trigger_percent: 100            # Chance (1–100) to trigger minigame
show_chat: false                # Sends player chat messages during fishing minigame 
```

## Commands
/reactivefishing reload
/rf reload
- Reload config safely
- Send confirmation message to sender

## Important Rules
- Prevent item movement in GUI
- Cancel all clicks except detection
- Only one active minigame per player
- Handle edge cases:
- - Player logs out mid-game. fails minigame
- - Player closes GUI early. Fails minigame
- - Prevent multiple minigames running per player
- - Cancel any existing session before starting a new one

## Visual Feedback
Play sounds on:
- Start: entity.experience_orb.pickup
- Success: entity.player.levelup
- Fail: block.note_block.bass

## Output Requirements
Generate:
- Full Maven project structure
- config.yml
- All Java classes
- Clean, readable, documented code
- No placeholders or TODOs

## Constraints
- Must compile and run without modification
- Use only Paper API (no NMS)
- Follow best practices for performance and thread safety