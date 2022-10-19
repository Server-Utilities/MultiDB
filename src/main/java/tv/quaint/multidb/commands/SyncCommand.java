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
import tv.quaint.multidb.utils.objects.Syncable;

import java.util.ArrayList;
import java.util.List;

public class SyncCommand implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        MultiDB.configHandler.reloadHosts();
        MultiDB.configHandler.reloadSyncables();

        if (args.length <= 0) {
            sender.sendMessage(StringUtils.codedString("&cYou haven't supplied a player (and an optional syncable)!"));
            return false;
        }

        if (args.length >= 3) {
            sender.sendMessage(StringUtils.codedString("&cYou must only supply a player (and an optional syncable), nothing else!"));
            return false;
        }

        if (args.length == 1) {
            Player player = MultiDB.instance.getServer().getPlayer(args[1].substring("-p:".length()));
            if (player == null) {
                sender.sendMessage(StringUtils.codedString("&cThat player is either not online or does not exist!"));
                return false;
            }

            for (Syncable syncable : MultiDB.configHandler.loadedSyncables) {
                String queryAnswer = DataSource.sync(syncable, player);

                sender.sendMessage(StringUtils.codedString("&eSync came back as &7(&c%set%&7)&8: &r%return%"
                        .replace("%set%", args[0])
                        .replace("%return%", queryAnswer)
                ));
            }
        } else {
            Player player = MultiDB.instance.getServer().getPlayer(args[1].substring("-p:".length()));
            if (player == null) {
                sender.sendMessage(StringUtils.codedString("&cThat player is either not online or does not exist!"));
                return false;
            }

            if (! MultiDB.configHandler.isValidSyncable(args[1])) {
                sender.sendMessage(StringUtils.codedString("&cYou haven't supplied a valid syncable!"));
                return false;
            }

            Syncable syncable = PluginUtils.getSyncableByIdentifier(args[1]);
            if (syncable == null) {
                sender.sendMessage(StringUtils.codedString("&cThe specified syncable is either not loaded or does not exist!"));
                return false;
            }

            String queryAnswer = DataSource.sync(syncable, player);

            sender.sendMessage(StringUtils.codedString("&eSync came back as &7(&c%set%&7)&8: &r%return%"
                    .replace("%set%", args[0])
                    .replace("%return%", queryAnswer)
            ));
        }

        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return new ArrayList<>(PluginUtils.getOnlinePlayerNames());
        } else if (args.length == 2) {
            return new ArrayList<>(PluginUtils.getSyncablesAsStrings());
        } else {
            return new ArrayList<>();
        }
    }
}
