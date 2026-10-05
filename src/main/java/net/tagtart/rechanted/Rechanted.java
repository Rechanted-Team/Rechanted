package net.tagtart.rechanted;

import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.tagtart.rechanted.attachments.ModAttachments;
import net.tagtart.rechanted.block.ModBlocks;
import net.tagtart.rechanted.block.entity.ModBlockEntities;
import net.tagtart.rechanted.block.renderer.RechantedTableRenderer;
import net.tagtart.rechanted.block.renderer.RechantedTrophyRenderer;
import net.tagtart.rechanted.component.ModDataComponents;
import net.tagtart.rechanted.command.ModCommands;
import net.tagtart.rechanted.config.RechantedCommonConfigs;
import net.tagtart.rechanted.effect.ModEffects;
import net.tagtart.rechanted.enchantment.ModEnchantmentEffects;
import net.tagtart.rechanted.entity.ModEntities;
import net.tagtart.rechanted.entity.ModEntityModelLayers;
import net.tagtart.rechanted.entity.renderer.LuckyGemEntityRenderer;
import net.tagtart.rechanted.entity.renderer.ReturnGemBeamEntityRenderer;
import net.tagtart.rechanted.item.ModCreativeModeTabs;
import net.tagtart.rechanted.item.ModItemProperties;
import net.tagtart.rechanted.item.ModItems;
import net.tagtart.rechanted.loot.ModLootModifiers;
import net.tagtart.rechanted.screen.InlineItemStackIconsComponent;
import net.tagtart.rechanted.screen.ModMenuTypes;
import net.tagtart.rechanted.screen.RechantedTablePoolDisplayScreen;
import net.tagtart.rechanted.screen.RechantedTableScreen;
import net.tagtart.rechanted.sound.ModSounds;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(Rechanted.MOD_ID)
public class Rechanted {
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "rechanted";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    // The constructor for the mod class is the first code that is run when your mod
    // is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and
    // pass them in automatically.
    public Rechanted(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in
        // this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.addListener(ModCommands::register);

        ModItems.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModEntities.register(modEventBus);
        ModAttachments.register(modEventBus);
        ModDataComponents.register(modEventBus);
        ModSounds.register(modEventBus);
        ModEffects.register(modEventBus);

        ModCreativeModeTabs.register(modEventBus);
        ModEnchantmentEffects.register(modEventBus);
        ModLootModifiers.register(modEventBus);
        // Register our mod's ModConfigSpec so that FML can create and load the config
        // file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, RechantedCommonConfigs.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {

    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    @EventBusSubscriber(modid = MOD_ID, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {

            ModItemProperties.addCustomItemProperties();
            ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () -> ConfigurationScreen::new);
        }

        @SubscribeEvent
        public static void registerScreens(RegisterMenuScreensEvent event) {
            event.register(ModMenuTypes.RECHANTED_TABLE_MENU.get(), RechantedTableScreen::new);
            event.register(ModMenuTypes.RECHANTED_TABLE_POOL_DISPLAY_MENU.get(), RechantedTablePoolDisplayScreen::new);
        }

        @SubscribeEvent
        public static void registerRenderer(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(ModEntities.RETURN_GEM_BEAM_ENTITY.get(), ReturnGemBeamEntityRenderer::new);
            event.registerEntityRenderer(ModEntities.THROWN_WARP_GEM_ENTITY.get(), ThrownItemRenderer::new);
            event.registerEntityRenderer(ModEntities.LUCKY_GEM_ENTITY.get(), LuckyGemEntityRenderer::new);

            event.registerBlockEntityRenderer(ModBlockEntities.RECHANTED_TABLE_BE.get(), RechantedTableRenderer::new);
            event.registerBlockEntityRenderer(ModBlockEntities.RECHANTED_TROPHY_BE.get(), RechantedTrophyRenderer::new);
        }

        @SubscribeEvent
        public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
            event.registerLayerDefinition(
                    ModEntityModelLayers.TROPHY_BOOK,
                    RechantedTrophyRenderer.TrophyBookModel::createBodyLayer
            );
        }

        @SubscribeEvent
        public static void onRegisterTooltips(RegisterClientTooltipComponentFactoriesEvent event) {
            event.register(InlineItemStackIconsComponent.IconRowTooltip.class, InlineItemStackIconsComponent::new);
        }
    }
}
