package io.adamaguilera.heroes;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Snowball;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;

import java.util.*;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

// Todoroki is an ICE hero that has the following abilities
// Freeze, requires an ICESHARD, and freezes the surface around him in a radius of 20
//
public class Todoroki implements Listener {
    // players contains the set of players that are Todoroki
    HashSet<Player> players;
    // Radius and Height for the freeze ability
    final int freezeRadius = 20;
    final int freezeHeight = 10;
    final int prisonRadius = 2;

    //
    final int MAXPENETRATION = 2;
    final int MAXFREEZESHOOT = 50;
    // Cooldown for freeze ability
    Cooldown freezeCooldown;
    Cooldown prisonCooldown;

    public Todoroki() {
        players = new HashSet<Player>();
        freezeCooldown = new Cooldown ("freeze", 600,
                ChatColor.AQUA + "You cannot use freeze for _ more seconds!",
                ChatColor.AQUA + "You just froze the landscape!");
        prisonCooldown = new Cooldown ("prison", 20,
                ChatColor.AQUA + "You cannot use imprison for _ more seconds!", true);
    } // 200

    // returns if the player is todoroki
    public boolean isTodoroki (Player player) {
        return players.contains(player);
    }

    public boolean addPlayer (Player player) {
        player.sendMessage(ChatColor.AQUA + "You are now Todoroki");
        players.add(player);
        return true;
    }

    public boolean removePlayer (Player player) {
        player.sendMessage(ChatColor.AQUA + "You are no longer Todoroki");
        players.remove(player);
        return true;
    }

    // Determines if the item is a shard
    private boolean isShard (Material item) {
        if (item.equals(Material.PRISMARINE_SHARD)) {
            return true;
        }
        return false;
    }

    private boolean isSnowball (Material item) {
        if (item.equals(Material.SNOWBALL)) {
            return true;
        }
        return false;
    }

    // Determines if action was a right click
    private boolean rightClicked (Action act) {
        if (act.equals(Action.RIGHT_CLICK_AIR) || act.equals(Action.RIGHT_CLICK_BLOCK)) {
            return true;
        }
        return false;
    }

    private void setBlock (Location loc, Material block) {
        loc.getBlock().setType(block);
    }

    private boolean isAir (Location loc) {
        if (loc.getBlock().getType().isAir() || !loc.getBlock().getType().isSolid()) {
            return true;
        }
        return false;
    }

    private boolean isBlueIce (Material block) {
        if (block.equals(Material.BLUE_ICE)) {
            return true;
        }
        return false;
    }

    private boolean isPackedIce (Material block) {
        if (block.equals(Material.PACKED_ICE)) {
            return true;
        }
        return false;
    }

    private boolean isIce (Material block) {
        if (block.equals(Material.BLUE_ICE) || block.equals(Material.FROSTED_ICE) ||
                block.equals(Material.ICE) || block.equals(Material.PACKED_ICE) ||
                block.equals(Material.SNOW) || block.equals(Material.SNOW_BLOCK)) {
            return true;
        }
        return false;
    }

    private String getFreezeEffect (Material block) {
        if (isBlueIce (block)) {
            return "strong";
        } else if (isPackedIce (block)) {
            return "medium";
        } else if (isIce (block)) {
            return "weak";
        } else {
            return "none";
        }
    }

    private void setFloorBlock (Location loc, Material mat) {
        if (isPackedIce(mat)) {
            if (isPackedIce(loc.getBlock().getType())) {
                setBlock(loc, Material.BLUE_ICE);
            } else {
                setBlock(loc, Material.PACKED_ICE);
            }
        } else {
            setBlock (loc, mat);
        }
    }

    private boolean setFreezeEffect (Player p, String effect) {
        boolean isTodoroki = isTodoroki(p);
        PotionEffect slowness;
        PotionEffect wither;
        PotionEffect poison;
        PotionEffect speed;
        PotionEffect strength;

        switch (effect) {
            case "strong":
                if (isTodoroki) {
                    speed = new PotionEffect(PotionEffectType.SPEED, 20, 2);
                    strength = new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20, 0);
                    p.addPotionEffect(speed);
                    p.addPotionEffect(strength);
                } else {
                    slowness = new PotionEffect(PotionEffectType.SLOW, 20, 3);
                    if (!p.hasPotionEffect(PotionEffectType.WITHER)) {
                        wither = new PotionEffect(PotionEffectType.WITHER, 36, 1);
                        p.addPotionEffect(wither);
                    }
                    p.addPotionEffect(slowness);
                }
                return true;
            case "medium":
                if (isTodoroki) {
                    speed = new PotionEffect(PotionEffectType.SPEED, 20, 1);
                    p.addPotionEffect(speed);
                } else {
                    slowness = new PotionEffect(PotionEffectType.SLOW, 20, 1);
                    if (!p.hasPotionEffect(PotionEffectType.POISON)) {
                        poison = new PotionEffect(PotionEffectType.POISON, 13, 1);
                        p.addPotionEffect(poison);
                    }
                    p.addPotionEffect(slowness);

                }
                return true;
            case "weak":
                if (isTodoroki) {
                    speed = new PotionEffect(PotionEffectType.SPEED, 40, 0);
                    p.addPotionEffect(speed);
                    return true;
                }
            default:
                return false;
        }
    }


    /*
    =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=


    FREEZE SURFACE ABILITY FUNCTIONS


    =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     */

    private void freeze (Location loc) {
        // freeze across z
        World world = loc.getWorld();
        int currX = loc.getBlockX();
        int currY = loc.getBlockY();
        int currZ = loc.getBlockZ();

        for (int rad = (-1)*freezeRadius; rad < freezeRadius; rad ++) {
            freezeRow (new Location (world, currX, currY, currZ + rad));
        }
    }

    private void freezeRow (Location loc) {
        World world = loc.getWorld();
        int yCoord = loc.getBlockY();
        int zCoord = loc.getBlockZ();
        int xCoord = loc.getBlockX();

        // freeze across x
        for (int rad = (-1*freezeRadius); rad < freezeRadius; rad ++) {
            // get the surface block
            fillSurfaces(new Location(world, xCoord + rad, yCoord, zCoord));
        }
    }

    private void fillSurfaces (Location base) {
        int fromX = base.getBlockX();
        int fromY = base.getBlockY();
        int fromZ = base.getBlockZ();
        World world = base.getWorld();
        // check Y coords
        boolean wasLastBlock = false;

        for (int height = (-1*freezeHeight) ; height < freezeHeight; height ++) {
            // get current location at bottom of radius
            Location curr = new Location (world, fromX, fromY + height, fromZ);
            // check if it is air
            if (isAir(curr)) {
                // if it is air, check if the prior y coord was a block
                if (wasLastBlock) {
                    // if it was a block, set prior loc to ice
                    setFloorBlock (new Location(world, fromX, fromY + height - 1, fromZ), Material.PACKED_ICE);
                    // and current is air set set wasLastBlock false
                    wasLastBlock = false;
                }
                // otherwise last block was air and wait for it to not be air
            } else {
                // okay, it is a block, set wasLastBlock to true and continue
                wasLastBlock = true;
            }
        }
        // done!
    }

    /*
    END OF FREEZE SURFACE ABILITY

     */

    /*
    =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=


    FREEZE PRISON ABILITY FUNCTIONS


    =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     */

    private void shootFreeze (Player p) {
        int currPenetration = 0;
        Location base = p.getEyeLocation();
        for (int curr = 3; curr <= MAXFREEZESHOOT; curr ++ ) {
            Location loc = vectorToLocation(base, getEyeDirection(p).multiply(curr));
            LivingEntity hit = hitPlayer(loc, p);
            if (hit != null) {
                hit.damage((hit.getHealth()/4.0), p);
                freezePrison(hit.getLocation());
                return;
            } else if (!loc.getBlock().isPassable()) {
                currPenetration ++;
                if (currPenetration >= MAXPENETRATION) {
                    return;
                }
            } else {
                // it is air, turn into ice
                setBlock(loc, Material.BLUE_ICE);
                currPenetration = 0;
            }
        }
    }

    private LivingEntity hitPlayer (Location loc, Player user) {
        if (loc == null) return null;
        Collection<Entity> lst = loc.getWorld().getNearbyEntities(loc, 2, 2, 2);
        if (lst == null) return null;
        for (Entity entity : lst) {
            //if (entity instanceof Player) {
            if (entity instanceof LivingEntity && !entity.equals(user))
                return (LivingEntity) entity;
            //}
        }
        return null;
    }

    private Vector getEyeDirection (Player p) {
        return p.getEyeLocation().getDirection().normalize();
    }

    private Location vectorToLocation(Location base, Vector v) {
        double blockX = base.getX() + v.getX();
        double blockY = base.getY() + v.getY();
        double blockZ = base.getZ() + v.getZ();
        return new Location(base.getWorld(), blockX, blockY, blockZ);
    }

    private void freezePrison (Location base) {
        World world = base.getWorld();
        int baseX = base.getBlockX();
        int baseY = base.getBlockY();
        int baseZ = base.getBlockZ();

        // do freezeFloor
        int floorY = baseY - 1;
        for (int rad = (-1*prisonRadius); rad <= prisonRadius; rad ++) {
            freezePrisonSquare (new Location (world, baseX, floorY, baseZ+rad));
        }
        // do freezeWall
        freezeWall (new Location (world, baseX, baseY + 2, baseZ), Material.AIR);
        //freezeWall (base, Material.AIR);
        freezeWall (new Location (world, baseX, baseY, baseZ), Material.BLUE_ICE);

        // air out center
        emptyCenter (base, 1, 3);
        // do freezeCeiling
        floorY = baseY + 3;
        for (int rad = (-1*prisonRadius); rad <= prisonRadius; rad ++) {
            freezePrisonSquare(new Location (world, baseX, floorY, baseZ+rad));
        }
    }

    private void emptyCenter (Location base, int rad, int height) {
        World world = base.getWorld();
        int baseX = base.getBlockX();
        int baseY = base.getBlockY();
        int baseZ = base.getBlockZ();

        for (int x = -1*rad ; x <= rad; x ++) {
            for (int z = -1*rad; z <= rad; z++) {
                for (int y = 0; y < height; y++) {
                    setBlock (new Location (world, baseX + x, baseY + y, baseZ + z), Material.AIR);
                }
            }
        }
    }

    private void freezeWall (Location base, Material mat) {
        World world = base.getWorld();
        int baseX = base.getBlockX();
        int baseY = base.getBlockY();
        int baseZ = base.getBlockZ();

        int wallX = baseX;
        int wallY = baseY + 1;
        int wallZ = baseZ - prisonRadius;

        for (int rad = (-1*prisonRadius); rad <= prisonRadius; rad ++) {
            setBlock(new Location (world, wallX + rad, wallY, wallZ), mat);
        }
        wallZ = baseZ + prisonRadius;
        for (int rad = (-1*prisonRadius); rad <= prisonRadius; rad ++) {
            setBlock(new Location (world, wallX+rad, wallY, wallZ), mat);
        }
        wallZ = baseZ;
        wallX = baseX - prisonRadius;
        for (int rad = (-1*prisonRadius); rad <= prisonRadius; rad ++) {
            setBlock(new Location (world, wallX, wallY, wallZ + rad), mat);
        }
        wallX = baseX + prisonRadius;
        for (int rad = (-1*prisonRadius); rad <= prisonRadius; rad ++) {
            setBlock(new Location (world, wallX, wallY, wallZ + rad), mat);
        }
    }

    private void freezePrisonSquare (Location base) {
        World world = base.getWorld();
        int baseX = base.getBlockX();
        int baseY = base.getBlockY();
        int baseZ = base.getBlockZ();
        for (int rad = (-1*prisonRadius); rad <= prisonRadius; rad ++) {
            setFloorBlock (new Location (world, baseX+rad, baseY, baseZ), Material.PACKED_ICE);
        }
    }
    /*

    END OF FREEZE PRISON ABILITY

     */


    /*
    =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    EVENT HANDLERS:
    Contains the following:
    movement - detects movement of player, if they are not todoroki, checks to see if they are standing on blue ice
                                           if they are, grants potion buff
    onEntityInteract - detects iteraction of player to see if they use their abilities
    =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     */
    @EventHandler
    public void movement(PlayerMoveEvent e) {
        Player p = e.getPlayer();

        int fromX = e.getFrom().getBlockX();
        int fromY = e.getFrom().getBlockY();
        int fromZ = e.getFrom().getBlockZ();

        Material groundBlock = new Location (e.getFrom().getWorld(), fromX, fromY - 1, fromZ).getBlock().getType();

        Material legBlock = new Location (e.getFrom().getWorld(), fromX, fromY, fromZ).getBlock().getType();

        if (setFreezeEffect(p, getFreezeEffect(groundBlock))){
            return;
        }
        setFreezeEffect(p, getFreezeEffect(legBlock));
        return;
    }

    @EventHandler
    public void onEntityInteract (PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if (!isTodoroki(e.getPlayer())) {
            if (rightClicked(e.getAction()) &&
                    isSnowball(player.getInventory().getItemInMainHand().getType())) {
                e.setCancelled(true);
            }
            return;
        }


        // check that right shard was clicked and player is holding a shard
        if (rightClicked(e.getAction()) &&
                isShard(player.getInventory().getItemInMainHand().getType())) {
            // check cooldown
            if (!freezeCooldown.onCooldown(player, true)) {
                Location loc = player.getLocation();
                freeze (player.getLocation());
            }
            // otherwise they were on cooldown
        } else if (rightClicked(e.getAction()) &&
                isSnowball(player.getInventory().getItemInMainHand().getType())) {
            if (!prisonCooldown.onCooldown(player, true)) {
                // start shooting
                shootFreeze (player);
                e.setCancelled(true);
            }
        }
    }
    /*


    END OF EVENT HANDLERS


     */
}