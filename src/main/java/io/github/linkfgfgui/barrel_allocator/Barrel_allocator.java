package io.github.linkfgfgui.barrel_allocator;

import com.mojang.logging.LogUtils;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
// import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

@Mod(Barrel_allocator.MODID)
public class Barrel_allocator {
    public static final String MODID = "barrel_allocator";
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    public static final DeferredBlock<Block> ALLOCATOR_BLOCK = BLOCKS.register("allocator", Allocator::new);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    public static final DeferredItem<BlockItem> ALLOCATOR_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("allocator", ALLOCATOR_BLOCK);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MODID);
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AllocatorBlockEntity>> ALLOCATOR_BE_TYPE =
            BLOCK_ENTITIES.register("allocator",
                    () -> BlockEntityType.Builder.of(
                            AllocatorBlockEntity::new,
                            ALLOCATOR_BLOCK.get()
                    ).build(null)
            );

    protected static final Logger LOGGER = LogUtils.getLogger();

    public Barrel_allocator(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        BLOCK_ENTITIES.register(modEventBus);

        NeoForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::registerCapabilities);
        modEventBus.addListener(this::addCreative);

        // modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

     // on the mod event bus
    public void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(Capabilities.ItemHandler.BLOCK, ALLOCATOR_BE_TYPE.get(), AllocatorBlockEntity::getCap);
        event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, ALLOCATOR_BE_TYPE.get(), AllocatorBlockEntity::getCapF);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.REDSTONE_BLOCKS) event.accept(ALLOCATOR_BLOCK.get());
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
    }

    @EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Some client setup code
        }
    }




}

