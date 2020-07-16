package mcjty.rftoolspower.modules.endergenic;

import mcjty.lib.blocks.BaseBlock;
import mcjty.lib.blocks.LogicSlabBlock;
import mcjty.lib.container.GenericContainer;
import mcjty.rftoolspower.modules.endergenic.blocks.EnderMonitorTileEntity;
import mcjty.rftoolspower.modules.endergenic.blocks.EndergenicTileEntity;
import mcjty.rftoolspower.modules.endergenic.blocks.PearlInjectorTileEntity;
import mcjty.rftoolspower.setup.Registration;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;

import static mcjty.rftoolspower.setup.Registration.*;

public class EndergenicSetup {

    public static void register() {
        // Needed to force class loading
    }

    public static final RegistryObject<LogicSlabBlock> ENDER_MONITOR = BLOCKS.register("ender_monitor", EnderMonitorTileEntity::createBlock);
    public static final RegistryObject<Item> ENDER_MONITOR_ITEM = ITEMS.register("ender_monitor", () -> new BlockItem(ENDER_MONITOR.get(), Registration.createStandardProperties()));
    public static final RegistryObject<TileEntityType<?>> TYPE_ENDER_MONITOR = TILES.register("ender_monitor", () -> TileEntityType.Builder.create(EnderMonitorTileEntity::new, ENDER_MONITOR.get()).build(null));
    public static final RegistryObject<ContainerType<GenericContainer>> CONTAINER_ENDER_MONITOR = CONTAINERS.register("ender_monitor", GenericContainer::createContainerType);

    public static final RegistryObject<BaseBlock> PEARL_INJECTOR = BLOCKS.register("pearl_injector", PearlInjectorTileEntity::createBlock);
    public static final RegistryObject<Item> PEARL_INJECTOR_ITEM = ITEMS.register("pearl_injector", () -> new BlockItem(PEARL_INJECTOR.get(), Registration.createStandardProperties()));
    public static final RegistryObject<TileEntityType<?>> TYPE_PEARL_INJECTOR = TILES.register("pearl_injector", () -> TileEntityType.Builder.create(PearlInjectorTileEntity::new, PEARL_INJECTOR.get()).build(null));
    public static final RegistryObject<ContainerType<GenericContainer>> CONTAINER_PEARL_INJECTOR = CONTAINERS.register("pearl_injector", GenericContainer::createContainerType);

    public static final RegistryObject<BaseBlock> ENDERGENIC = BLOCKS.register("endergenic", EndergenicTileEntity::createBlock);
    public static final RegistryObject<Item> ENDERGENIC_ITEM = ITEMS.register("endergenic", () -> new BlockItem(ENDERGENIC.get(), Registration.createStandardProperties()));
    public static final RegistryObject<TileEntityType<EndergenicTileEntity>> TYPE_ENDERGENIC = TILES.register("endergenic", () -> TileEntityType.Builder.create(EndergenicTileEntity::new, ENDERGENIC.get()).build(null));
    public static final RegistryObject<ContainerType<GenericContainer>> CONTAINER_ENDERGENIC = CONTAINERS.register("endergenic", GenericContainer::createContainerType);

    //    public static GenericBlock<EndergenicTileEntity, GenericContainer> endergenicBlock;
//    public static GenericBlock<PearlInjectorTileEntity, GenericContainer> pearlInjectorBlock;
//    public static EnderMonitorBlock enderMonitorBlock;
//
//    public static void init() {
//        endergenicBlock = ModBlocks.builderFactory.<EndergenicTileEntity> builder("endergenic")
//                .tileEntityClass(EndergenicTileEntity.class)
//                .emptyContainer()
//                .flags(BlockFlags.REDSTONE_CHECK, BlockFlags.NON_OPAQUE, BlockFlags.RENDER_TRANSLUCENT)
//                .rotationType(BaseBlock.RotationType.NONE)
//                .infusable()
//                .guiId(GuiProxy.GUI_ENDERGENIC)
//                .info("message.rftools.shiftmessage")
//                .infoExtended("message.rftools.endergenic")
//                .build();
//
//        pearlInjectorBlock = ModBlocks.builderFactory.<PearlInjectorTileEntity> builder("pearl_injector")
//                .tileEntityClass(PearlInjectorTileEntity.class)
//                .container(PearlInjectorTileEntity.CONTAINER_FACTORY)
//                .flags(BlockFlags.REDSTONE_CHECK)
//                .guiId(GuiProxy.GUI_PEARL_INJECTOR)
//                .info("message.rftools.shiftmessage")
//                .infoExtended("message.rftools.pearl_injector")
//                .infoExtendedParameter(stack -> {
//                    int count = mapTag(stack, compound -> (int) ItemStackTools.getListStream(compound, "Items").filter(nbt -> !new ItemStack((CompoundNBT)nbt).isEmpty()).count(), 0);
//                    return Integer.toString(count);
//                })
//                .build();
//
//        enderMonitorBlock = new EnderMonitorBlock();
//    }
//
//    @SideOnly(Side.CLIENT)
//    public static void initClient() {
//        endergenicBlock.initModel();
//        endergenicBlock.setGuiFactory(GuiEndergenic::new);
//        EndergenicRenderer.register();
//
//        pearlInjectorBlock.initModel();
//        pearlInjectorBlock.setGuiFactory(GuiPearlInjector::new);
//
//        enderMonitorBlock.initModel();
//    }
}
