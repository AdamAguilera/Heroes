
Author: Adam Aguilera

## Heroes #UNFINISHED
Minecraft plugin which adds heroes with different abilities.

# Version
1.0 #UNFINISHED

# Command Usage
/hero help
  description: provides a list of commands for the Heroes plugin with a brief explanation 
  return type: message directly to player
  
/hero list
  description: provides a list of the available heroes for the user to select from
               with a brief explanation of each hero
  return type: message directly to player
  
/hero set <heroname>
  description: sets the player's hero, will override current hero if already a hero
  return type: message directly to player
  
/hero get
  description: tells player which hero they currently are
  return type: message directly to player


## Hero List

# Bakugo
Passive Perks:
  Fire Resistance infinite duration.
  
Items:
  Flint:
     description: Upon moving, a trail of fire is left behind the player and grants speed I boost
     duration: 20s
     cooldown: 30s
     ability type: basic
     
  Gunpowder:
     description: Creates mini explosion where player is looking and propels him upwards
     restriction: Must be used below player
     consumption: 1 gunpowder per use
     cooldown: 0.1s
     ability type: basic
     
  Blaze Rod:
     description: Creates massive explosion in the line of sight of player for 50 blocks
     cooldown: 60
     ability type: special
     
# Todoroki
Passive Perks:
  Walking on any form of ice or snow grants speed I
  Walking on packed ice grants speed II
  Walking on blue ice grants speed III and strength I
  All enemies walking on packed ice gives slowness II and poison II
  All enemies walking on blue ice gives slowness IV and wither III
  
Items:
  Snowball:
     description: Shoots a line of blue ice in line of sight of player, if it makes contact with a living entity, it will 
                  cast imprison on them
                   (Imprison creates an ice chamber around the entity hit, and places ice below them)
                   (If ice exists below the player before being hit, it turns ground ice into blue ice)
     cooldown: 15s
     ability type: basic
     
  Prismarine Shard:
     description: Replaces all surface blocks in a 50x50x50 block radius into packed ice. 
                  If block was previously packed ice, it turns block into blue ice instead.
     cooldown: 60s
     ability type: super
     
# Pointer # INPROGRESS

# INPROGRESS

# Deku # INPROGRESS

# INPROGRESS

