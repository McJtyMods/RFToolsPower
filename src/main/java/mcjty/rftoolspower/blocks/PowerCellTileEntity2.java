package mcjty.rftoolspower.blocks;

public class PowerCellTileEntity2 extends PowerCellTileEntity {

    @Override
    Tier getTier() {
        return Tier.TIER2;
    }

    @Override
    int getLocalMaxEnergy() {
        return 10000000; // @todo config
    }

    @Override
    int getRfPerTickPerSide() {
        return 5000;
    }

}
