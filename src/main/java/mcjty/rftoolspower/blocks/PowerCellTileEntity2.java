package mcjty.rftoolspower.blocks;

import mcjty.rftoolspower.config.ConfigSetup;

public class PowerCellTileEntity2 extends PowerCellTileEntity {

    @Override
    Tier getTier() {
        return Tier.TIER2;
    }

    @Override
    long getLocalMaxEnergy() {
        return ConfigSetup.TIER2_MAXRF.get();
    }

    @Override
    long getRfPerTickPerSide() {
        return ConfigSetup.TIER2_RFPERTICK.get();
    }

}
