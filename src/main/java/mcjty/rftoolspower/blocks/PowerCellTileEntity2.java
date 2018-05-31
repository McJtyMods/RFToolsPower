package mcjty.rftoolspower.blocks;

import mcjty.rftoolspower.config.Config;

public class PowerCellTileEntity2 extends PowerCellTileEntity {

    @Override
    Tier getTier() {
        return Tier.TIER2;
    }

    @Override
    long getLocalMaxEnergy() {
        return Config.TIER2_MAXRF;
    }

    @Override
    long getRfPerTickPerSide() {
        return Config.TIER2_RFPERTICK;
    }

}
