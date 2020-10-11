package lol.hyper.playertracker;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

public class CommandPlayer implements CommandExecutor {
    final String pattern = "MM/dd/yyyy HH:mm:ss";
    final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

    String joinDateString;
    Date joinDate;
    String lastPlayedString;
    Date lastPlayed;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) {
            UUID uuid = Bukkit.getOfflinePlayer(args[0]).getUniqueId();
            try {
                if (MYSQLController.lookUpFirstJoin(uuid) == null) {
                    sender.sendMessage(ChatColor.RED + "Player was not found. Maybe they changed their username?");
                } else {
                    lastPlayed = Date.from(Instant.ofEpochMilli(Long.parseLong(MYSQLController.lookUpLastLogin(uuid))));
                    lastPlayedString = simpleDateFormat.format(lastPlayed);
                    joinDate = Date.from(Instant.ofEpochMilli(Long.parseLong(MYSQLController.lookUpFirstJoin(uuid))));
                    joinDateString = simpleDateFormat.format(joinDate);
                    sender.sendMessage(ChatColor.GOLD + "--------------------------------------------");
                    sender.sendMessage(ChatColor.DARK_AQUA + args[0] + " was first seen on " + joinDateString + " EST.");
                    if (Bukkit.getServer().getPlayerExact(args[0]) != null) {
                        sender.sendMessage(ChatColor.DARK_AQUA + args[0] + " is currently online.\n");
                    } else {
                        sender.sendMessage(ChatColor.DARK_AQUA + args[0] + " was last seen on " + lastPlayedString + " EST.");
                    }
                    sender.sendMessage(ChatColor.GOLD + "--------------------------------------------");
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Invalid syntax. Do /player <player> instead.");
        }
        return true;
    }
}