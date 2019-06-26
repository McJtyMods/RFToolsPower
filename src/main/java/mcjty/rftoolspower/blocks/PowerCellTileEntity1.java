package mcjty.rftoolspower.blocks;

import mcjty.rftoolspower.config.Config;

public class PowerCellTileEntity1 extends PowerCellTileEntity {

    public PowerCellTileEntity1() {
        super(ModBlocks.TYPE_CELL1);
    }

    @Override
    Tier getTier() {
        return Tier.TIER1;
    }

    @Override
    long getLocalMaxEnergy() {
        return Config.TIER1_MAXRF.get();
    }

    @Override
    long getRfPerTickPerSide() {
        return Config.TIER1_RFPERTICK.get();
    }
}
