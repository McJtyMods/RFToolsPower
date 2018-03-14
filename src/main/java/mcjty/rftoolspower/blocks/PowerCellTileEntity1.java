package mcjty.rftoolspower.blocks;

public class PowerCellTileEntity1 extends PowerCellTileEntity {

    @Override
    Tier getTier() {
        return Tier.TIER1;
    }

    @Override
    int getLocalMaxEnergy() {
        return 1000000; // @todo config
    }

    @Override
    int getRfPerTickPerSide() {
        return 500;
    }
}
