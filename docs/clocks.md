# Skybox clocks on Minecraft 1.21.1

The optional `properties.clock` field controls the time used by a skybox's fade and rotation. If omitted, it follows
the current level's day-time counter, preserving existing resource-pack behavior.

| Value | Behavior |
|---|---|
| `"default"` | Follow the level's day-time counter. |
| `"game_time"` | Follow gameplay time, ignoring `/time set` changes. |
| `{ "type": "fixed", "time": 6000 }` | Freeze the fade and rotation at tick `6000`. |

Minecraft 1.21.1 predates the named world-clock registry used by Minecraft 26.x. Named values such as
`"minecraft:overworld"` and `{ "type": "clock", "id": "example:clock" }` are rejected with a configuration error;
Nuit does not silently substitute another clock.

Each fade and rotation keeps its own `duration`. The selected clock is wrapped to that duration, and rotations use
partial-tick interpolation. With `skyboxRotation: false`, rotations follow Minecraft's celestial angle as before;
`speed: 0` disables time-based rotation in either mode.

An active sun decoration with `skyboxRotation: true`, a non-empty axis, and nonzero speed can also rotate the
overworld sunrise and fog direction. Multiple such decorations must use identical clock and rotation settings;
otherwise Nuit leaves those global effects dimension-driven and logs one conflict warning. Moon-only and stars-only
decorations do not control global celestial effects.
