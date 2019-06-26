package mcjty.rftoolspower.blocks;

import mcjty.rftoolspower.config.Config;

public class PowerCellTileEntity3 extends PowerCellTileEntity {

    public PowerCellTileEntity3() {
        super(ModBlocks.TYPE_CELL3);
    }

    @Override
    Tier getTier() {
        return Tier.TIER3;
    }

    @Override
    long getLocalMaxEnergy() {
        return Config.TIER3_MAXRF.get();
    }

    @Override
    long getRfPerTickPerSide() {
        return Config.TIER3_RFPERTICK.get();
    }

}
