## Mannequin overhaul
* Distant Friends has been updated to use the new Mannequin entity added in 1.21.9
* The mod can now function server-side only
* A command has been added to force spawn a distant friend mannequin: `/distantfriends spawnFriend [pos]`, this will spawn a mannequin at the given position (or at the player if no position is given)
* Mannequins will now hold a range of random items in their hands that they will switch through when looked at
* Mannequins no longer punch the air since Mannequins can't attack
* The spawning of Distant Friends has been reworked, they should be rarer than before
* A config option `showNames` has been added to toggle the nametag visibility
* A config option `spawnDimensions` has been added to control which dimensions Distant Friends can spawn in (By default, they only spawn in the Overworld)
* The syntax of the `friends` config option has changed, it's now `<username>,<texture_location/body_type>,<body_type>`
* The location of the skin texture inside the loaded resource packs. 
* The body type is either `slim` or `wide`.
* The second (and third) argument of the `friends` config option are optional, if no texture location is given the mod will use the default skin location for that username. If no body type is given, it will default to `wide`.