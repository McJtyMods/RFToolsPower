package mcjty.rftoolspower.modules.powercell.data;

import mcjty.rftoolspower.modules.powercell.PowerCellConfig;
import net.minecraft.core.BlockPos;

import java.util.HashSet;
import java.util.Set;

public class PowerCellNetwork {

    private long energy;
    private long maxEnergy;
    private final int networkId;

    // For statistics, keep track of energy added/removed
    private long extracted;
    private long inserted;

    private static int lastNetworkId = 0;

    private final Set<Long> positions = new HashSet<>();

    public PowerCellNetwork() {
        networkId = lastNetworkId++;
    }

    public int getNetworkId() {
        return networkId;
    }

    public boolean isValid() {
        return positions.size() <= PowerCellConfig.NETWORK_MAX.get();
    }

    public boolean contains(BlockPos pos) {
        return positions.contains(pos.asLong());
    }

    public void add(BlockPos pos) {
        positions.add(pos.asLong());
    }

    public Set<Long> getPositions() {
        return positions;
    }

    public long getEnergy() {
        return energy;
    }

    public void setEnergy(long energy) {
        this.energy = energy;
    }

    public void addEnergy(long diff) {
        this.energy += diff;
        inserted += diff;
    }

    public void extractEnergy(long diff) {
        this.energy -= diff;
        extracted += diff;
    }

    public long getExtracted() {
        return extracted;
    }

    public void setExtracted(long extracted) {
        this.extracted = extracted;
    }

    public long getInserted() {
        return inserted;
    }

    public void setInserted(long inserted) {
        this.inserted = inserted;
    }

    public long getMaxEnergy() {
        return maxEnergy;
    }

    public void setMaxEnergy(long maxEnergy) {
        this.maxEnergy = maxEnergy;
    }
}
