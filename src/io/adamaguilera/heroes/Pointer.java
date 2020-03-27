package io.adamaguilera.heroes;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;


import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class Pointer implements Listener {
    HashSet<Player> players;

    final double MARKRADIUS = 50.0;
    final int TARGETRADIUS = 100;
    HashMap<Player, Player> targeted;

    Cooldown markCooldown;
    Cooldown targetCooldown;

    public Pointer() {
        players = new HashSet<Player>();
        markCooldown = new Cooldown ("mark", 10,
                ChatColor.GRAY + "You cannot mark for _ more seconds!", true);
    } // 200

    // returns if the player is todoroki
    public boolean isPointer (Player player) {
        return players.contains(player);
    }

    public boolean addPlayer (Player player) {
        player.sendMessage(ChatColor.GRAY + "You are now Pointer");
        players.add(player);
        return true;
    }

    public boolean removePlayer (Player player) {
        player.sendMessage(ChatColor.GRAY + "You are no longer Pointer");
        players.remove(player);
        return true;
    }

    // Determines if action was a right click
    private boolean rightClicked (Action act) {
        if (act.equals(Action.RIGHT_CLICK_AIR) || act.equals(Action.RIGHT_CLICK_BLOCK)) {
            return true;
        }
        return false;
    }

    private boolean isItem (Material hand, Material compare) {
        if (compare.equals(hand))
            return true;

        return false;
    }

    private void mark (Player p) {
        PotionEffect selfBlind = new PotionEffect (PotionEffectType.BLINDNESS, 400, 0);

        p.addPotionEffect(selfBlind);

        int numPlayers = 0;
        for (Entity e : getLivingEntities(p.getLocation(), MARKRADIUS)) {
            if (e instanceof LivingEntity &&
                !e.equals(p)) {
                if (e instanceof Player) {
                    numPlayers ++;
                }
                LivingEntity currEntity = (LivingEntity) e;
                currEntity.addPotionEffect(new PotionEffect (PotionEffectType.GLOWING, 300, 0));
            }
        }

        if (numPlayers > 1) {
            p.sendMessage(ChatColor.GRAY + "A single player is nearby.");
        } else if (numPlayers == 1) {
            p.sendMessage(ChatColor.GRAY + "Multiple players detected nearby.");
        } else {
            p.sendMessage(ChatColor.GRAY + "No players detected.");
        }
    }

    private void target (Player p) {
        for (int curr = 0; curr < 100; curr ++) {

        }
    }

    private LivingEntity hitTarget (Location loc, Player user) {
        if (loc == null) return null;
        Collection<Entity> lst = loc.getWorld().getNearbyEntities(loc, 1, 2, 1);
        if (lst == null) return null;

        for (Entity entity : lst) {
            if (entity instanceof LivingEntity && !entity.equals(user))
                return (LivingEntity) entity;
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

    private Collection<Entity> getLivingEntities (Location loc, double rad) {
        return loc.getWorld().getNearbyEntities(loc, rad, rad, rad);
    }


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
    public void onEntityInteract (PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (!isPointer(p)) {
            return;
        }
        // check that right shard was clicked and player is holding a shard
        if (rightClicked(e.getAction()) &&
                (isItem(p.getInventory().getItemInMainHand().getType(), Material.NETHER_STAR) ||
                 isItem(p.getInventory().getItemInOffHand().getType(), Material.NETHER_STAR))) {
            // check cooldown
            if (!markCooldown.onCooldown(p, true)) {
                mark(p);
            }
            // otherwise they were on cooldown
        }
    }
    /*


    END OF EVENT HANDLERS


     */
}
