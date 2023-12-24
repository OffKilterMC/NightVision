package net.offkiltermc.nightvision.forge;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.logging.LogUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(NightVision.MODID)
public class NightVision {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "nightvision";

    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public NightVision() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    public static boolean valueIsOn;

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    public static final Lazy<KeyMapping> EXAMPLE_MAPPING = Lazy.of(() -> new KeyMapping("key.offkilter.nightvision", // Will be localized using this translation key
            InputConstants.Type.KEYSYM, // Default mapping is on the keyboard
            GLFW.GLFW_KEY_N, // Default key is N
            "key.categories.misc" // Mapping will be in the misc category
    ));

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        // Event is on the mod event bus only on the physical client
        @SubscribeEvent
        public static void registerBindings(RegisterKeyMappingsEvent event) {
            LOGGER.info("HELLO from registerBindings");
            event.register(EXAMPLE_MAPPING.get());
        }

        @SubscribeEvent
        public void onClientSetup(FMLClientSetupEvent event) {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ClientModEvents2 {

        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            if (event.phase == TickEvent.Phase.END) { // Only call code once as the tick event is called twice every tick
                while (EXAMPLE_MAPPING.get().consumeClick()) {
                    valueIsOn = !valueIsOn;

                    Component statusText;
                    if (valueIsOn) {
                        statusText = Component.translatable("offkilter.nightvision.on").withStyle(ChatFormatting.GREEN);
                    } else {
                        statusText = Component.translatable("offkilter.nightvision.off").withStyle(ChatFormatting.RED);
                    }

                    if (Minecraft.getInstance().player != null) {
                        Minecraft.getInstance().player.displayClientMessage(Component.translatable("offkilter.nightvision.message", statusText), true);
                    }
                }
            }
        }

    }
}
