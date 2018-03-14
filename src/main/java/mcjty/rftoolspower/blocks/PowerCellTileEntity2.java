package mcjty.rftoolspower.blocks;

import mcjty.rftoolspower.config.Config;

public class PowerCellTileEntity2 extends PowerCellTileEntity {

    @Override
    Tier getTier() {
        return Tier.TIER2;
    }

    @Override
    int getLocalMaxEnergy() {
        return Config.TIER2_MAXRF;
    }

    @Override
    int getRfPerTickPerSide() {
        return Config.TIER2_RFPERTICK;
    }

}
