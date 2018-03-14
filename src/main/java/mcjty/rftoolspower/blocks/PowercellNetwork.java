package mcjty.rftoolspower.blocks;

import net.minecraft.util.math.BlockPos;

import java.util.HashSet;
import java.util.Set;

public class PowercellNetwork {

    private long energy;
    private long maxEnergy;

    private Set<Long> positions = new HashSet<>();

    public boolean contains(BlockPos pos) {
        return positions.contains(pos.toLong());
    }

    public void add(BlockPos pos) {
        positions.add(pos.toLong());
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

    public long getMaxEnergy() {
        return maxEnergy;
    }

    public void setMaxEnergy(long maxEnergy) {
        this.maxEnergy = maxEnergy;
    }
}
