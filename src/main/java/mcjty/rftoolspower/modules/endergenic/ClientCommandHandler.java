package mcjty.rftoolspower.modules.endergenic;

import mcjty.lib.McJtyLib;
import mcjty.lib.typed.Key;
import mcjty.lib.typed.Type;
import mcjty.rftoolspower.RFToolsPower;
import mcjty.rftoolspower.modules.endergenic.blocks.EndergenicTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class ClientCommandHandler {

    public static final String CMD_FLASH_ENDERGENIC = "flashEndergenic";
    public static final Key<Integer> PARAM_GOODCOUNTER = new Key<>("goodcounter", Type.INTEGER);
    public static final Key<Integer> PARAM_BADCOUNTER = new Key<>("badcounter", Type.INTEGER);
    public static final Key<BlockPos> PARAM_POS = new Key<>("pos", Type.BLOCKPOS);

    public static void registerCommands() {
        McJtyLib.registerClientCommand(RFToolsPower.MODID, CMD_FLASH_ENDERGENIC, (player, arguments) -> {
            BlockPos pos = arguments.get(PARAM_POS);
            TileEntity te = McJtyLib.proxy.getClientWorld().getBlockEntity(pos);
            if (te instanceof EndergenicTileEntity) {
                EndergenicTileEntity tileEntity = (EndergenicTileEntity) te;
                tileEntity.syncCountersFromServer(arguments.get(PARAM_GOODCOUNTER), arguments.get(PARAM_BADCOUNTER));
            }
            return true;
        });
    }
}
