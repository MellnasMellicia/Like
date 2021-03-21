package amata1219.like.command;

import amata1219.like.Like;
import amata1219.like.Main;
import amata1219.like.bryionake.constant.CommandSenderCasters;
import amata1219.like.bryionake.constant.Parsers;
import amata1219.like.bryionake.dsl.BukkitCommandExecutor;
import amata1219.like.bryionake.dsl.context.CommandContext;
import amata1219.like.bryionake.dsl.parser.FailableParser;
import amata1219.like.bryionake.interval.Endpoint;
import amata1219.like.bryionake.interval.Interval;
import amata1219.like.config.MainConfig;
import amata1219.like.sound.SoundEffects;
import amata1219.like.ui.LikeRangeSearchingUI;
import com.google.common.base.Joiner;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class LikeSearchCommand implements BukkitCommandExecutor {

    private final CommandContext<CommandSender> executor;

    {
        MainConfig config = Main.plugin().config();

        FailableParser<Integer> radius = Parsers.define(
                Parsers.u32,
                new Interval<>(Endpoint.openEndpoint(0), Endpoint.closedEndpoint((Supplier<Integer>) config::rangeSearchRadiusLimit)),
                () -> ChatColor.RED + "検索範囲の半径Rは、0 < R ≦ " + config.rangeSearchRadiusLimit() + " の間で指定して下さい。"
        );

        CommandContext<Player> search = define(
                () -> ChatColor.GRAY + "指定範囲内のLike一覧を表示する: /likesearch [半径] (/likesc [半径])",
                (sender, unparsedArguments, parsedArguments) -> {
                    if (Main.plugin().controlLikeViewListener.viewersToRespawnPoints.containsKey(sender)) {
                        sender.sendMessage(ChatColor.RED + "視点移動中にこのコマンドを実行することはできません。");
                        SoundEffects.FAILED.play(sender);
                        return;
                    }

                    int scopeRadius = parsedArguments.poll();
                    Location origin = sender.getLocation();
                    List<Like> likesInRange = new ArrayList<>();
                    Main plugin = Main.plugin();
                    for (int x = origin.getBlockX() - scopeRadius; x <= origin.getBlockX() + scopeRadius; x = (x / 16 + 1) * 16) {
                        for (int z = origin.getBlockZ() - scopeRadius; z <= origin.getBlockZ() + scopeRadius; z = (z / 16 + 1) * 16) {
                            for (Like like : plugin.likeMap.get(x, z)) {
                                Location loc  = like.hologram.getLocation();
                                double distance2d = Math.sqrt(Math.pow(origin.getX() - loc.getX(), 2) + Math.pow(origin.getZ() - loc.getZ(), 2));
                                if (distance2d <= scopeRadius) likesInRange.add(like);
                            }
                        }
                    }

                    new LikeRangeSearchingUI(likesInRange).open(sender);
                },
                radius
        );

        executor = define(CommandSenderCasters.casterToPlayer, search);
    }

    @Override
    public CommandContext<CommandSender> executor() {
        return executor;
    }

}
