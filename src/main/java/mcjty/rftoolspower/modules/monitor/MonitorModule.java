package mcjty.rftoolspower.modules.monitor;

import mcjty.lib.blocks.LogicSlabBlock;
import mcjty.lib.container.GenericContainer;
import mcjty.lib.modules.IModule;
import mcjty.rftoolspower.modules.monitor.blocks.PowerLevelTileEntity;
import mcjty.rftoolspower.modules.monitor.blocks.PowerMonitorTileEntity;
import mcjty.rftoolspower.modules.monitor.client.GuiPowerMonitor;
import mcjty.rftoolspower.modules.monitor.client.PowerLevelRenderer;
import mcjty.rftoolspower.setup.Registration;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import static mcjty.rftoolspower.setup.Registration.*;

public class MonitorModule implements IModule {

    public static final RegistryObject<LogicSlabBlock> POWER_MONITOR = BLOCKS.register("power_monitor", PowerMonitorTileEntity::createBlock);
    public static final RegistryObject<Item> POWER_MONITOR_ITEM = ITEMS.register("power_monitor", () -> new BlockItem(POWER_MONITOR.get(), Registration.createStandardProperties()));
    public static final RegistryObject<TileEntityType<?>> TYPE_POWER_MONITOR = TILES.register("power_monitor", () -> TileEntityType.Builder.of(PowerMonitorTileEntity::new, POWER_MONITOR.get()).build(null));
    public static final RegistryObject<ContainerType<GenericContainer>> CONTAINER_POWER_MONITOR = CONTAINERS.register("power_monitor", GenericContainer::createContainerType);

    public static final RegistryObject<LogicSlabBlock> POWER_LEVEL = BLOCKS.register("power_level", PowerLevelTileEntity::createBlock);
    public static final RegistryObject<Item> POWER_LEVEL_ITEM = ITEMS.register("power_level", () -> new BlockItem(POWER_LEVEL.get(), Registration.createStandardProperties()));
    public static final RegistryObject<TileEntityType<PowerLevelTileEntity>> TYPE_POWER_LEVEL = TILES.register("power_level", () -> TileEntityType.Builder.of(PowerLevelTileEntity::new, POWER_LEVEL.get()).build(null));

    public MonitorModule() {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(MonitorModule::onTextureStitch);
        });
    }

    @Override
    public void init(FMLCommonSetupEvent event) {

    }

    @Override
    public void initClient(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            GuiPowerMonitor.register();
        });

        PowerLevelRenderer.register();
    }

    @Override
    public void initConfig() {

    }

    public static void onTextureStitch(TextureStitchEvent.Pre event) {
        if (!event.getMap().location().equals(AtlasTexture.LOCATION_BLOCKS)) {
            return;
        }

        for (ResourceLocation digit : PowerLevelRenderer.DIGITS) {
            event.addSprite(digit);
        }
    }

}
