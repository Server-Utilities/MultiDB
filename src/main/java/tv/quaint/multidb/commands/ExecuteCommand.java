package tv.quaint.multidb.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tv.quaint.multidb.MultiDB;
import tv.quaint.multidb.utils.DataSource;
import tv.quaint.multidb.utils.PluginUtils;
import tv.quaint.multidb.utils.StringUtils;
import tv.quaint.multidb.utils.objects.CustomSQLInfo;

import java.util.ArrayList;
import java.util.List;

public class ExecuteCommand implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        MultiDB.configHandler.reloadExecutions();

        if (args.length <= 0) {
            sender.sendMessage(StringUtils.codedString("&cYou haven't supplied an execution (and an optional player)!"));
            return false;
        }

//        if (args.length >= 3) {
//            sender.sendMessage(StringUtils.codedString("&cYou must only supply an execution (and an optional player), nothing else!"));
//            return false;
//        }

        if (! MultiDB.configHandler.isValidExecution(args[0])) {
            sender.sendMessage(StringUtils.codedString("&cYou haven't supplied a valid execution!"));
            return false;
        }

        if (args.length == 1) {
            if (sender instanceof Player player) {
                CustomSQLInfo sqlInfo = PluginUtils.getExecutionByIdentifier(args[0]);
                if (sqlInfo == null) {
                    sender.sendMessage(StringUtils.codedString("&cThe specified execution is either not loaded or does not exist!"));
                    return false;
                }

                DataSource.execute(sqlInfo, player);
            } else {
                sender.sendMessage(StringUtils.codedString("&cMust be a player or supply a player!"));
            }
        } else {
            if (sender instanceof Player player) {
                String[] extra = StringUtils.argsMinus(args, 0);
                if (args[1].startsWith("-p:")) {
                    player = MultiDB.instance.getServer().getPlayer(args[1].substring("-p:".length()));
                    if (player == null) {
                        sender.sendMessage(StringUtils.codedString("&cThat player is either not online or does not exist!"));
                        return false;
                    }
                    extra = StringUtils.argsMinus(args, 0, 1);
                }

                CustomSQLInfo sqlInfo = PluginUtils.getExecutionByIdentifier(args[0]);
                if (sqlInfo == null) {
                    sender.sendMessage(StringUtils.codedString("&cThe specified execution is either not loaded or does not exist!"));
                    return false;
                }

                DataSource.execute(sqlInfo, player, extra);
            } else {
                sender.sendMessage(StringUtils.codedString("&cMust be a player or supply a player!"));
            }
        }

        sender.sendMessage(StringUtils.codedString("&eExecution &7(&c%set%&7) &efinished&8!"
                .replace("%set%", args[0])
        ));

        return true;
    }


    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return new ArrayList<>(PluginUtils.getExecutionsAsStrings());
        } else if (args.length == 2) {
            if (! args[1].startsWith("-p:")) return new ArrayList<>();

            List<String> adjustedPlayers = new ArrayList<>();

            for (String s : PluginUtils.getOnlinePlayerNames()) {
                adjustedPlayers.add("-p:" + s);
            }

            return adjustedPlayers;
        } else {
            return new ArrayList<>();
        }
    }
}
