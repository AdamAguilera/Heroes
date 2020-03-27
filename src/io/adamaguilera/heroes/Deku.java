package io.adamaguilera.heroes;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.HashSet;

public class Deku implements Listener {
    final boolean dropItems = true;
    HashMap<Player, DekuBuffs> players;


    public Deku() {
        players = new HashMap<>();

    }

    public boolean isDeku(Player p) {
        return players.containsKey(p);
    }

    public DekuBuffs getDekuBuffs (Player p) {
        return players.getOrDefault(p, null);
    }

    public void addPlayer(Player player) {
        player.sendMessage(ChatColor.GREEN + "You are now Deku");
        players.put(player, new DekuBuffs (player));
    }

    public void removePlayer(Player player) {
        player.sendMessage(ChatColor.GREEN + "You are no longer Deku");
        players.remove(player);
    }


    public boolean isLeftClick(Action action) {
        if (action.equals(Action.LEFT_CLICK_AIR) ||
                action.equals(Action.LEFT_CLICK_BLOCK)) {
            return true;
        }
        return false;
    }

    private boolean isRightClick(Action action) {
        if (action.equals(Action.RIGHT_CLICK_AIR) ||
                action.equals(Action.RIGHT_CLICK_BLOCK)) {
            return true;
        }
        return false;
    }

    private Material getHand(Player p) {
        return p.getInventory().getItemInMainHand().getType();
    }

    /*
    =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    EVENT HANDLERS:
    Contains the following:

    =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
     */

    @EventHandler
    public void onPlayerDropEvent(PlayerDropItemEvent e) {
        Player p = e.getPlayer();
        if (!isDeku(p)) {
            if (!dropItems) {
                e.setCancelled(true);
            }
            return;
        } else {
            // is deku
            DekuBuffs dekuBuffs = getDekuBuffs(p);
            Material item = e.getItemDrop().getItemStack().getType();
            // check if item is valid
            if (dekuBuffs.isBuff (item)) {
                // cancel drop items
                e.setCancelled(true);
                // send item
                dekuBuffs.activate (p.getInventory().getItemInMainHand());
            } else {
                // if drop items wants to be enabled or disabled
                if (!dropItems) {
                    e.setCancelled(true);
                }
            }
            return;
        }
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractEvent e) {
        if (isLeftClick(e.getAction())) {

        } else if (isRightClick(e.getAction())) {

        } else {
            return;
        }
    }
    /*


    END OF EVENT HANDLERS


     */
}
