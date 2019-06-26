package mcjty.rftoolspower.blocks;

import mcjty.rftoolspower.config.Config;

public class PowerCellTileEntity2 extends PowerCellTileEntity {

    public PowerCellTileEntity2() {
        super(ModBlocks.TYPE_CELL2);
    }

    @Override
    Tier getTier() {
        return Tier.TIER2;
    }

    @Override
    long getLocalMaxEnergy() {
        return Config.TIER2_MAXRF.get();
    }

    @Override
    long getRfPerTickPerSide() {
        return Config.TIER2_RFPERTICK.get();
    }

}
