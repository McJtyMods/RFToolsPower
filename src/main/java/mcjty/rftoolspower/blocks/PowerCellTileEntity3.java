package mcjty.rftoolspower.blocks;

import mcjty.rftoolspower.config.ConfigSetup;

public class PowerCellTileEntity3 extends PowerCellTileEntity {

    @Override
    Tier getTier() {
        return Tier.TIER3;
    }

    @Override
    long getLocalMaxEnergy() {
        return ConfigSetup.TIER3_MAXRF;
    }

    @Override
    long getRfPerTickPerSide() {
        return ConfigSetup.TIER3_RFPERTICK;
    }

}
