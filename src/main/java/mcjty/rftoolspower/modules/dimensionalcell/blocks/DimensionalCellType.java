package mcjty.rftoolspower.modules.dimensionalcell.blocks;

public enum DimensionalCellType {
    UNKNOWN(false, false, false),
    NORMAL(false, false, false),
    SIMPLE(true, false, false),
    ADVANCED(false, true, false),
    CREATIVE(false, true, true);    // Creative is also advanced

    private final boolean simple;
    private final boolean advanced;
    private final boolean creative;

    DimensionalCellType(boolean simple, boolean advanced, boolean creative) {
        this.simple = simple;
        this.advanced = advanced;
        this.creative = creative;
    }

    public boolean isSimple() {
        return simple;
    }

    public boolean isAdvanced() {
        return advanced;
    }

    public boolean isCreative() {
        return creative;
    }
}
