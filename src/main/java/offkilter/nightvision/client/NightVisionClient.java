package offkilter.nightvision.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class NightVisionClient implements ClientModInitializer {
    public static boolean valueIsOn;

    @Override
    public void onInitializeClient() {
        KeyMapping hotKey = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.offkilter.nightvision", InputConstants.KEY_N, "key.categories.misc"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (hotKey.consumeClick()) {
                valueIsOn = !valueIsOn;

                Component statusText;
                if (valueIsOn) {
                    statusText = Component.translatable("offkilter.nightvision.on").withStyle(ChatFormatting.GREEN);
                } else {
                    statusText = Component.translatable("offkilter.nightvision.off").withStyle(ChatFormatting.RED);
                }

                if (client.player != null) {
                    client.player.displayClientMessage(Component.translatable("offkilter.nightvision.message", statusText), true);
                }
            }
        });

        ServerWorldEvents.UNLOAD.register((server, world) -> valueIsOn = false);
    }
}
