package mcjty.rftoolspower.blocks;

import net.minecraft.util.math.BlockPos;

import java.util.HashSet;
import java.util.Set;

public class PowercellNetwork {

    private long energy;

    private Set<Long> positions = new HashSet<>();

    public boolean contains(BlockPos pos) {
        return positions.contains(pos.toLong());
    }

    public void add(BlockPos pos) {
        positions.add(pos.toLong());
    }
}
