package mcjty.rftoolspower.blocks;

public class PowerCellTileEntity3 extends PowerCellTileEntity {

    @Override
    Tier getTier() {
        return Tier.TIER3;
    }

    @Override
    int getLocalMaxEnergy() {
        return 100000000; // @todo config
    }

    @Override
    int getRfPerTickPerSide() {
        return 50000;
    }

}
