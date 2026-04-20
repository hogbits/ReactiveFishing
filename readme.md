# Reactive Fishing
Adds a reaction style fishing minigame when a player hooks a fish. The mechanics are quire simple. The reactive fishing minigame is initiated when a player hooks a fish. Players must click again inside the interface when the fish is within the "catch zone" indicated by the green glass panes. The catch zone location will appear randomly making it a little more unpredictable.

## Mechanics
The minigame is successful if the player catches a fish in the catch zone.
The minigame fails if the player clicks when the fish is outside the catch zone, or if the catch timer is exceeded.

## Requirements
Minecraft Paper 1.21.1 - 1.21.11

## Dependencies
- None
- Supports EMF if included

## Inspiration
This project was inspired by a few factors. I was planning a fishing tournament on my kids summer minecraft server. When one child said. "I'm going to build an AFK fish farm so I can win!" To which I thought to myself... "We'll see about that." 

In my research for a fishing plugin, I found [EvenMoreFish](https://modrinth.com/plugin/evenmorefish) on Modrinth and really liked the GUI screens and I liked how easy it was to customize fish options. One feature EMF lacks is a minigame or some way to prevent AFK fishing. Feeling inspired by the GUI screens and the desire to out-think my children, the idea for ReactiveFishing was formed.

At first, I built a solution using Skript (one of my favorite plugins) but the solution was a bit janky and while fine for single player was not well optimized for multiplayer. Since I know very little about Spigot/Bucket plugin development, I enlisted the help of Codex. It's something I wanted to try anyway. I was happy with the end result even if it took way more prompting than I expected. Maybe now, I will get inspired to actually learn Java. 