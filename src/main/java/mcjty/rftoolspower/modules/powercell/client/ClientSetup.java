package mcjty.rftoolspower.modules.powercell.client;

import com.google.common.collect.Lists;
import mcjty.rftoolspower.RFToolsPower;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.Map;

public class ClientSetup {

    public static List<Identifier> onTextureStitch() {
        return List.of(
            Identifier.fromNamespaceAndPath(RFToolsPower.MODID, "block/powercell/cellboth_t1"),
            Identifier.fromNamespaceAndPath(RFToolsPower.MODID, "block/powercell/cellhoriz_t1"),
            Identifier.fromNamespaceAndPath(RFToolsPower.MODID, "block/powercell/celllower_t1"),
            Identifier.fromNamespaceAndPath(RFToolsPower.MODID, "block/powercell/cellmiddle_t1"),
            Identifier.fromNamespaceAndPath(RFToolsPower.MODID, "block/powercell/cellupper_t1"),
            Identifier.fromNamespaceAndPath(RFToolsPower.MODID, "block/powercell/cellboth_t2"),
            Identifier.fromNamespaceAndPath(RFToolsPower.MODID, "block/powercell/celllower_t2"),
            Identifier.fromNamespaceAndPath(RFToolsPower.MODID, "block/powercell/cellmiddle_t2"),
            Identifier.fromNamespaceAndPath(RFToolsPower.MODID, "block/powercell/cellupper_t2"),
            Identifier.fromNamespaceAndPath(RFToolsPower.MODID, "block/powercell/cellboth_t3"),
            Identifier.fromNamespaceAndPath(RFToolsPower.MODID, "block/powercell/celllower_t3"),
            Identifier.fromNamespaceAndPath(RFToolsPower.MODID, "block/powercell/cellmiddle_t3"),
            Identifier.fromNamespaceAndPath(RFToolsPower.MODID, "block/powercell/cellupper_t3"),
            Identifier.fromNamespaceAndPath(RFToolsPower.MODID, "block/powercell/inputmask"),
            Identifier.fromNamespaceAndPath(RFToolsPower.MODID, "block/powercell/outputmask")
        );
    }

    public static void onModelBake(Map<BlockState, BlockStateModel> models) {
        PowerCellBakedModel model = new PowerCellBakedModel();
        models.replaceAll((state, original) -> state.getBlock() instanceof mcjty.rftoolspower.modules.powercell.blocks.PowerCellBlock ? model : original);
    }
}
