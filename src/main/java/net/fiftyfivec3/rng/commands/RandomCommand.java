package net.fiftyfivec3.rng.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fiftyfivec3.rng.Config;
import net.fiftyfivec3.rng.utils.RandomLoader;
import net.fiftyfivec3.rng.utils.RandomTracker;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static net.minecraft.server.command.CommandManager.literal;

public class RandomCommand {
    private static final Text successText = new LiteralText("Successfully compiled").formatted(Formatting.GREEN);
    private static final Text failedText = new LiteralText("Failed to compile").formatted(Formatting.RED);
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder<ServerCommandSource> display = literal("display")
                .then(literal("on").executes(ctx -> {
                    ctx.getSource().sendFeedback(new LiteralText("Display On"), false);
                    Config.displayWorld = true;
                    return 1;
                }))
                .then(literal("off").executes(ctx -> {
                    ctx.getSource().sendFeedback(new LiteralText("Display Off"), false);
                    Config.displayWorld = false;
                    return 1;
                }));

        LiteralArgumentBuilder<ServerCommandSource> reset = literal("default")
                .executes(ctx -> {
                    ctx.getSource().sendFeedback(new LiteralText("RNG set to default"), false);
                    RandomTracker.function = null;
                    return 1;
                });

        LiteralArgumentBuilder<ServerCommandSource> compile = literal("compile")
                .executes(ctx -> {
                    ServerPlayerEntity player = ctx.getSource().getPlayer();
                    player.getItemsHand().forEach(
                            item -> {
                                CompoundTag compoundTag = item.getTag();
                                if (compoundTag != null) {
                                    ListTag listTag = compoundTag.getList("pages", 8);
                                    StringBuilder codeBuilder = new StringBuilder();
                                    for (int i = 0; i < listTag.size(); ++i) {
                                        String line = listTag.getString(i).replaceAll("\\\\n", "\n");
                                        codeBuilder.append(line);
                                    }
                                    String code = codeBuilder.toString();
                                    System.out.println(code);
                                    if (!code.equals("")) {
                                        boolean success = RandomLoader.compile(code, player);
                                        player.sendMessage(success ? successText : failedText, false);
                                    }
                                }
                            }
                    );
                    return 1;
                });

        dispatcher.register(literal("rng").then(display).then(compile).then(reset));
    }
}
