package io.adamaguilera.heroes;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.event.block.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

/*
=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
Hero - Bakugo:
Contains the following:
movement - detects movement of player, if they are not todoroki, checks to see if they are standing on blue ice
                                       if they are, grants potion buff
onEntityInteract - detects iteraction of player to see if they use their abilities
=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
 */
public class Bakugo implements Listener {
    HashSet<Player> players;
    HashSet<Player> activatedFire;
    Cooldown fireCooldown;
    Cooldown blastCooldown;
    Cooldown levitationCooldown;
    int time;
    int taskID;

    public Bakugo() {
        players = new HashSet<Player>();
        activatedFire = new HashSet<Player>();
        fireCooldown = new Cooldown ("fire", 300,
                ChatColor.RED + "You cannot activate fire for _ seconds!",
                ChatColor.RED + "You unleashed a trail of fire");
        levitationCooldown = new Cooldown ("levitation", 2);
        blastCooldown = new Cooldown("blast", 50,
                            ChatColor.RED +"You cannot blast for _ seconds!", true);
    }

    public boolean isBakugo (Player player) {
        return players.contains(player);
    }

    public boolean addPlayer (Player player) {
        player.sendMessage(ChatColor.RED + "You are now Bakugo");
        PotionEffect fire = new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0);
        player.addPotionEffect(fire);
        players.add(player);
        return true;
    }

    public boolean removePlayer (Player player) {
        player.sendMessage(ChatColor.RED + "You are no longer Bakugo");
        player.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
        players.remove(player);
        return true;
    }

    public boolean activateFire (Player player) {
        if (activatedFire != null) {
            PotionEffect speed = new PotionEffect(PotionEffectType.SPEED, 400, 0);
            player.addPotionEffect(speed);
            activatedFire.add(player);
            return true;
        }
        return false;
    }

    public boolean deactivateFire (Player player) {
        if (activatedFire != null) {
            activatedFire.remove(player);
            return true;
        }
        return false;
    }

    @EventHandler
    public void movement(PlayerMoveEvent e) {
        if (!isBakugo(e.getPlayer()))
            return;
        if (!activatedFire.contains(e.getPlayer()))
            return;

        int fromX = e.getFrom().getBlockX();
        int fromY = e.getFrom().getBlockY();
        int fromZ = e.getFrom().getBlockZ();

        // if the player is not moving don't do anything
        if (e.getTo().getBlockX() == fromX &&
                e.getTo().getBlockY() == fromY &&
                e.getTo().getBlockZ() == fromZ) {
            return; //The player hasn't moved
        }
        // if the player is jumping, don't set blocks to fire
        if (e.getTo().getY() > e.getFrom().getY()) {
            return;
        }

        Location loc = new Location (e.getFrom().getWorld(), fromX, fromY, fromZ);
        // make sure it is not a fence which breaks through prison ability
        //if (!loc.getBlock().getType().equals(Material.DARK_OAK_FENCE)) {
        loc.getBlock().setType(Material.FIRE);
        //}
        return;
    }

    public boolean isRightClick (Action act) {
        if (act.equals(Action.RIGHT_CLICK_AIR) || act.equals(Action.RIGHT_CLICK_BLOCK)) {
            return true;
        }
        return false;
    }

    private void levitate (Player p) {
        p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20, 3));
        p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 10, 2));
        p.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 5, 25));
    }

    private void minorExplosion (Location loc) {
        loc.getWorld().createExplosion(loc, 2.25F, true, true);
    }

    private boolean isMaterial (ItemStack item, Material mat) {
        return item.getType().equals(mat);
    }

    private void blast (Location base, Player p) {
        float size = 3.0F;
        for (int i = 2; i < 45; i ++) {
            Location loc = vectorToLocation(base, getEyeDirection(p).multiply(i));
            loc.getWorld().createExplosion(loc, size, true, true);
            size += 0.2F;
        }
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



    @EventHandler
    public void onEntityInteract (PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if (!isBakugo(e.getPlayer()))
            return;

        if (!isRightClick(e.getAction())) {
            return;
        }

        ItemStack item = player.getInventory().getItemInMainHand();
        if (isMaterial(item, Material.BLAZE_ROD)) {
           if (!blastCooldown.onCooldown(player, true)) {

               Vector direction = player.getEyeLocation().getDirection().normalize();
               /*
               printLocation (player, player.getLocation());
               printLocation (player, direction);
               Location firstLoc = vectorToLocation(player.getEyeLocation(), direction.multiply(5));
               printLocation (player, firstLoc);
               */
               blast (player.getEyeLocation(), player);
           }
        } else if (isMaterial(item, Material.GUNPOWDER)) {
            // check to see if interaction is on a block
            if (e.getClickedBlock() != null) {
                // make sure block is at same level or above
                if (e.getClickedBlock().getLocation().getBlockY() <= player.getLocation().getBlockY()) {
                    // no cooldown on levitation, but cooldown for 5 ticks to prevent double
                    // consumption of gunpowder
                    if (!levitationCooldown.onCooldown(player, true)) {
                        // if so then consume gunpowder
                        item.setAmount(item.getAmount() - 1);
                        // then grant levitation on bakugo
                        levitate(player);
                        // blow up block looked at
                        minorExplosion(e.getClickedBlock().getLocation());
                    }
                }
            }
        } else if (isMaterial(item, Material.FLINT)) {
            if (!fireCooldown.onCooldown(player,true)) {
                setTimer (20);
                activateFire(player);
                startFire (player);
            }
        }
    }

    public void setTimer (int amt) {
        time = amt;
    }

    public void startFire (Player p) {
        p.sendMessage(ChatColor.RED + "Activated fire trail.");
        final Player player = p;
        taskID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(JavaPlugin.getPlugin(Hero.class), new Runnable() {
            public void run() {
                if (time <= 0) {
                    player.sendMessage(ChatColor.RED + "Fire trail recharging.");
                    deactivateFire(player);
                    stopTimer();
                    return;
                }
                time = time - 1;
            }
        }, 0L, 20L);
    }

    public void stopTimer () {
        Bukkit.getScheduler().cancelTask(taskID);
    }
}