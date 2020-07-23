package mcjty.rftoolspower.modules.blazing.blocks;

import mcjty.rftoolspower.modules.blazing.items.BlazingRod;
import mcjty.rftoolspower.modules.blazing.items.IBlazingRod;

import java.util.function.Function;

public class BlazingAgitatorAlgorithm {

    private final Function<Integer, IBlazingRod> items;

    public BlazingAgitatorAlgorithm(Function<Integer, IBlazingRod> items) {
        this.items = items;
    }

    /// Calculate the quality factor for this rod alone without looking at neighbours
    private float calculateQualityFactor(int i) {
        IBlazingRod stack = items.apply(i);
        if (stack.isValid()) {
            float duration = stack.getPowerDuration() * BlazingRod.START_DURATION / 30000.0f;
            float quality = stack.getPowerQuality() * BlazingRod.START_QUALITY / 150000000.0f;
            return (duration + quality) / 2.0f;
        }
        return 0;
    }

    /// Calculate the difference between the main quality factor and the quality factor for this rod alone and account for empty stack penalty
    private float calculateQualityFactorDiff(float fThis, int i, int x, int y) {
        if (x < 0 || y < 0 || x > 2 || y > 2) {
            return -.03f;      // Penalty for empty slot
        }
        IBlazingRod stack = items.apply(i);
        if (!stack.isValid()) {
            return -.03f;      // Penalty for empty slot
        }
        return Math.max(-.03f, calculateQualityFactor(i) - fThis);
    }

    /// Calculate the quality factor for this slot given the adjacent slots
    private float calculateAdjacencyFactor(int i) {
        float fThis = calculateQualityFactor(i);
        int x = i % 3;
        int y = i / 3;
        float factor = 1.0f;
        factor += calculateQualityFactorDiff(fThis, i - 1, x-1, y);    // Index left
        factor += calculateQualityFactorDiff(fThis, i + 1, x+1, y);    // Index right
        factor += calculateQualityFactorDiff(fThis, i - 3, x, y-1);    // Index top
        factor += calculateQualityFactorDiff(fThis, i + 3, x, y+1);    // Index bottom
        if (factor < 0.01f) {
            factor = 0.01f;
        }
        return factor / 4;
    }

    public void tickBlazingRod(int i, IBlazingRod stack, float timeLeft, float infuseFactor) {
        float adjacencyFactor = calculateAdjacencyFactor(i);
        timeLeft -= adjacencyFactor / 5;
        stack.setAgitationTimeLeft(timeLeft);

        float powerQuality = stack.getPowerQuality();
        powerQuality += adjacencyFactor * adjacencyFactor * 10 * (1+infuseFactor/10.0f);
        stack.setPowerQuality(powerQuality);

        float powerDuration = stack.getPowerDuration();
        powerDuration += adjacencyFactor * adjacencyFactor * (1+infuseFactor/10.0f);
        stack.setPowerDuration(powerDuration);
    }
}
