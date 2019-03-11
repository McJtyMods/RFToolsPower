package mcjty.rftoolspower.blocks;

import mcjty.rftoolspower.config.ConfigSetup;

public class PowerCellTileEntity1 extends PowerCellTileEntity {

    @Override
    Tier getTier() {
        return Tier.TIER1;
    }

    @Override
    long getLocalMaxEnergy() {
        return ConfigSetup.TIER1_MAXRF;
    }

    @Override
    long getRfPerTickPerSide() {
        return ConfigSetup.TIER1_RFPERTICK;
    }
}
