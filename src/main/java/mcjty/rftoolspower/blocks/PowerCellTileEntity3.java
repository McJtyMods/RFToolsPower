package mcjty.rftoolspower.blocks;

import mcjty.rftoolspower.config.Config;

public class PowerCellTileEntity3 extends PowerCellTileEntity {

    @Override
    Tier getTier() {
        return Tier.TIER3;
    }

    @Override
    int getLocalMaxEnergy() {
        return Config.TIER3_MAXRF;
    }

    @Override
    int getRfPerTickPerSide() {
        return Config.TIER3_RFPERTICK;
    }

}
