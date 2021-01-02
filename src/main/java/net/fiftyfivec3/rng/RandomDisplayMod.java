package net.fiftyfivec3.rng;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fiftyfivec3.rng.commands.RandomCommand;
import net.fiftyfivec3.rng.utils.RandomTracker;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.server.world.ServerWorld;

public class RandomDisplayMod implements ModInitializer {
//	public static boolean worldrng = false;

	public static long worldseed = 0;
	public static int worldcalls = 0;

	@Override
	public void onInitialize() {
		ClientTickEvents.END_WORLD_TICK.register((t) -> {
			if (Config.displayWorld) {
				MinecraftClient mc = MinecraftClient.getInstance();
				String dim = mc.player.getEntityWorld().getRegistryKey().getValue().getPath();
				for (ServerWorld world : mc.getServer().getWorlds()) {
					if (world.getRegistryKey().getValue().getPath().equals(dim)) {
						RandomTracker tracker = (RandomTracker) world.getRandom();
						worldcalls = tracker.calls;
						worldseed = tracker.getSeed();
						break;
					}
				}
			}
		});

		HudRenderCallback.EVENT.register((matrixStack, v) -> {
			if (Config.displayWorld) {
				TextRenderer tr = MinecraftClient.getInstance().textRenderer;
				String data = String.format("World rng: %20d (calls %d)", worldseed, worldcalls);
				tr.draw(matrixStack, data, 5, 5, 0xbcbcbc);
			}
		});

		CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> RandomCommand.register(dispatcher));
	}
}
