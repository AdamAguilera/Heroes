package io.adamaguilera.heroes;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;

// Class cooldowns contains a list of players and their respective cooldowns
// Each ability has a cooldown, and this class is used to determine
// whether that player's ability is on cooldown

public class Cooldown {
    // cd is the cooldown for the ability, in hundred millis
    int cd;
    // name is the name of the cooldown
    String name;
    // cd message is the message to pass when the player is on cooldown
    // NOTE: _ is used as an optional replacement for the remaining time left
    // on cooldown
    String cdMessage;
    // message to send player when ability is used
    String activeMessage;
    // cooldowns is a map to get a 0(1) lookup on the player's cooldown
    HashMap<Player, Long> cooldowns;

    public Cooldown (String name, int cd, String cdMessage, String activeMessage) {
        this.name = name;
        this.cd = cd;
        this.cdMessage = cdMessage;
        this.activeMessage = activeMessage;
        cooldowns = new HashMap<>();
    }

    public Cooldown (String name, int cd, String message, boolean isCDMessage) {
        this.name = name;
        this.cd = cd;
        if (isCDMessage) {
            this.cdMessage = message;
            this.activeMessage = null;
        } else {
            this.cdMessage = null;
            this.activeMessage = message;
        }
        cooldowns = new HashMap<>();
    }

    public Cooldown (String name, int cd) {
        this.name = name;
        this.cd = cd;
        this.cdMessage = null;
        this.activeMessage = null;
        cooldowns = new HashMap<>();
    }


    // returns name of cooldown
    public String getName () {
        return name;
    }

    private boolean hasCDMessage () {
        return (cdMessage != null);
    }
    // returns the cooldown message to be sent to player
    private String getCDMessage (long secondsLeft) {
        // replaces all occurences of _ with the seconds left
        return cdMessage.replaceAll("_", "" + secondsLeft);
    }

    // determines if the player is on cooldown for this ability
    // if on cooldown for longer than a second, it will pass a message
    // to the player notifying them how long they have left on their cooldown
    public boolean onCooldown (Player p, boolean putCooldown) {
        // get seconds left
        long secondsLeft = secondsLeft(p);
        // determine if ability was just used (so you dont send message)
        if (secondsLeft > (cd - 1)) {
            // don't send a cooldown message
            return true;
        } else if (secondsLeft > 0) {
            // send a cooldown message
            if (hasCDMessage ()) {
                p.sendMessage(getCDMessage(secondsLeft/10));
            }
            return true;
        }
        // otherwise not on cooldown
        // put them on cooldown
        if (putCooldown) {
            if (getActiveMessage() != null) {
                p.sendMessage(getActiveMessage());
            }
            addCooldown(p);
        }
        // if not put on cooldown still return false
        return false;
    }

    public String getActiveMessage () {
        return activeMessage;
    }
    // put's a player on cooldown
    public void addCooldown (Player p) {
        cooldowns.put(p, System.currentTimeMillis());
    }
    // get the number of seconds left on cooldown
    // in millis
    public long secondsLeft (Player p) {
        if (cooldowns.getOrDefault(p, 0L) == 0) {
            return 0;
        } else {
            return (((cooldowns.get(p)/100) + cd) - System.currentTimeMillis()/100);
        }
    }
}
