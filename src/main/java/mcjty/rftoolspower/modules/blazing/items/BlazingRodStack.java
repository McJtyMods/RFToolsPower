package mcjty.rftoolspower.modules.blazing.items;

import mcjty.rftoolspower.modules.blazing.BlazingSetup;
import net.minecraft.item.ItemStack;

public class BlazingRodStack implements IBlazingRod {

    private final ItemStack stack;

    @Override
    public boolean isValid() {
        return stack.getItem() == BlazingSetup.BLAZING_ROD.get();
    }

    public BlazingRodStack(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public int getInfusionStepsLeft() {
        return BlazingRod.getInfusionStepsLeft(stack);
    }

    @Override
    public void setInfusionStepsLeft(int steps) {
        BlazingRod.setInfusionStepsLeft(stack, steps);
    }

    @Override
    public float getAgitationTimeLeft() {
        return BlazingRod.getAgitationTimeLeft(stack);
    }

    @Override
    public void setAgitationTimeLeft(float time) {
        BlazingRod.setAgitationTimeLeft(stack, time);
    }

    @Override
    public float getPowerQuality() {
        return BlazingRod.getPowerQuality(stack);
    }

    @Override
    public void setPowerQuality(float quality) {
        BlazingRod.setPowerQuality(stack, quality);
    }

    @Override
    public float getPowerDuration() {
        return BlazingRod.getPowerDuration(stack);
    }

    @Override
    public void setPowerDuration(float duration) {
        BlazingRod.setPowerDuration(stack, duration);
    }
}
