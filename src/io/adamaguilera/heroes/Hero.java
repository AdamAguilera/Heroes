package io.adamaguilera.heroes;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class Hero extends JavaPlugin {
    Bakugo bakugoListener;
    Todoroki todorokiListener;
    Pointer pointerListener;

    HashMap<Player, String> heroes = new HashMap<>();


    @Override
    public void onEnable() {
        getLogger().info("Heros plugin has been enabled");
        bakugoListener = new Bakugo();
        todorokiListener = new Todoroki();
        pointerListener = new Pointer();
        this.getServer().getPluginManager().registerEvents(bakugoListener, this);
        this.getServer().getPluginManager().registerEvents(todorokiListener, this);
        this.getServer().getPluginManager().registerEvents(pointerListener, this);
    }

    @Override
    public void onDisable() {
        getLogger().info("Heros plugin has been disabled");
    }
    /*
    =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    COMMANDS
    Format:
    /hero <set/get/remove/list/help> <heroName>
    where:
        /hero get - get current hero
        /hero set <heroName> - set player to hero (
        /hero remove - removes the hero from his current hero
        /hero list - sends a list of current heros to player
        /hero help - help command
    =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    */
    public boolean onCommand (CommandSender sender, Command cmd, String label, String[] args) {
        if (isDisabled())
            return false;

        if (cmd.getName().equalsIgnoreCase("hero") && sender instanceof Player) {
            Player player = (Player) sender;
            // set /hero to be same as /hero help
            if (args.length == 0) {
                args = new String[]{"help"};
            }

            switch (args[0].toLowerCase()) {
                // get's the player's current hero
                case "get":
                    // check args parameters
                    if (args.length != 1) {
                        return false;
                    }
                    // get hero
                    String hero = getHero (player);
                    // if it is null, not a hero
                    if (hero == null) {
                        player.sendMessage(ChatColor.RED + "You are not a hero");
                    } else {
                        // otherwise they are a hero
                        player.sendMessage(ChatColor.RED + "You are " + hero);
                    }
                    return true;
                case "set":
                    // set the current hero, change hero if already another hero
                    // check args params
                    if (args.length != 2) {
                        return false;
                    }
                    // now go through hero names
                    switch (args[1].toLowerCase()) {
                        case "bakugo":
                            onCommand(sender, cmd, label, new String[] {"remove"});
                            heroes.put(player, "bakugo");
                            bakugoListener.addPlayer(player);
                            return true;
                        case "todoroki":
                            heroes.put(player, "todoroki");
                            todorokiListener.addPlayer(player);
                            return true;
                        case "pointer":
                            heroes.put(player, "pointer");
                            pointerListener.addPlayer(player);
                            return true;
                        default:
                            player.sendMessage (ChatColor.RED +
                                    "Invalid hero name. Please use /hero list for a list of all heroes");
                            return true;
                    }
                case "remove":
                    switch (heroes.getOrDefault(player, "none")) {
                        case "bakugo":
                            bakugoListener.removePlayer (player);
                            heroes.remove(player);
                            return true;
                        case "todoroki":
                            todorokiListener.removePlayer (player);
                            heroes.remove(player);
                            return true;
                        default:
                            player.sendMessage (ChatColor.RED +
                                    "You are not a hero");
                    }
                    return true;
                case "list":
                    player.sendMessage(ChatColor.RED + "Available heros include:"
                            + "\nBakugo - creates trail of fire and shoots fireballs"
                            + "\nTodoroki - shoots iceshards");
                    return true;
                case "help":
                    player.sendMessage (ChatColor.RED +
                            "=-=-=-=-=-=-=-=-=-=-= Hero Commands =-=-=-=-=-=-=-=-=-=-=" +
                            "\n  /hero get - gets current hero" +
                            "\n  /hero set <heroName> - sets your current hero" +
                            "\n  /hero remove - removes your current hero" +
                            "\n  /hero list - provides a list of all heroes");
                    return true;
                default:
                    player.sendMessage (ChatColor.RED +
                            "This is an invalid command, please use /hero help for a list of commands.");
                    return true;
            }
        }
        return false;
    }

    private boolean isDisabled () {
        if (bakugoListener == null ||
                todorokiListener == null ||
                heroes == null ) {
            return true;
        }
        return false;
    }

    // isHero determines whether the player is currently a hero
    private boolean isHero (Player p) {
        if (heroes.containsKey (p) ) {
            return true;
        }
        return false;
    }

    private String getHero (Player p) {
        return heroes.getOrDefault(p, null);
    }

    private void setHero (Player p, String heroName) {
        if (!isHero(p)) {
            heroes.put(p, heroName);
        }
    }
}