package mcjty.rftoolspower.modules.blazing.items;

import mcjty.lib.builder.TooltipBuilder;
import mcjty.lib.varia.NBTTools;
import mcjty.rftoolspower.setup.Registration;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Lazy;

import javax.annotation.Nonnull;
import java.util.List;

import static mcjty.lib.builder.TooltipBuilder.*;

public class BlazingRod extends Item {

    public static final float MAXTIME = 20.0f * 30.0f;
    public static final float START_QUALITY = 60000f;
    public static final float START_DURATION = 20f;
    public static final int MAX_INFUSION_STEPS = 64;

    private final Lazy<TooltipBuilder> tooltipBuilder = () -> new TooltipBuilder()
            .info(key("message.rftoolsbase.shiftmessage"))
            .infoShift(header(),
                    parameter("time", BlazingRod::isCharging, stack -> getAgitationTimePercentage(stack) + "%"),
                    parameter("infused", BlazingRod::isInfused, stack -> getInfusionPercentage(stack) + "%"),
                    parameter("power", stack -> getRfPerTick(stack) + " RF/t"),
                    parameter("duration", stack -> getTotalTicks(stack) + " ticks"));


    public BlazingRod() {
        super(Registration.createStandardProperties());
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack itemStack, World world, @Nonnull List<ITextComponent> list, @Nonnull ITooltipFlag flags) {
        super.appendHoverText(itemStack, world, list, flags);
        tooltipBuilder.get().makeTooltip(getRegistryName(), itemStack, list, flags);
    }

    public static boolean isCharging(ItemStack stack) {
        float left = getAgitationTimeLeft(stack);
        return left > 0;
    }

    public static boolean isInfused(ItemStack stack) {
        int left = getInfusionStepsLeft(stack);
        return left < MAX_INFUSION_STEPS;
    }

    public static int getInfusionPercentage(ItemStack stack) {
        int left = getInfusionStepsLeft(stack);
        return (MAX_INFUSION_STEPS - left) * 100 / MAX_INFUSION_STEPS;
    }

    // Get the number of infusing steps that are still possible
    public static int getInfusionStepsLeft(ItemStack stack) {
        return NBTTools.getInt(stack, "infSteps", MAX_INFUSION_STEPS);
    }

    public static void setInfusionStepsLeft(ItemStack stack, int steps) {
        stack.getOrCreateTag().putInt("infSteps", steps);
    }

    // Get time left (in ticks) before ready
    public static float getAgitationTimeLeft(ItemStack stack) {
        return NBTTools.getFloat(stack, "time", MAXTIME);
    }

    public static void setAgitationTimeLeft(ItemStack stack, float time) {
        stack.getOrCreateTag().putFloat("time", time);
    }

    public static int getAgitationTimePercentage(ItemStack stack) {
        float left = getAgitationTimeLeft(stack);
        return (int) ((MAXTIME - left) * 100 / MAXTIME);
    }

    // Quality is expressed in RF per 1000 ticks
    public static float getPowerQuality(ItemStack stack) {
        return NBTTools.getFloat(stack, "quality", START_QUALITY);
    }

    public static void setPowerQuality(ItemStack stack, float quality) {
        stack.getOrCreateTag().putFloat("quality", quality);
    }

    public static int getRfPerTick(ItemStack stack) {
        return (int) (getPowerQuality(stack) / 1000);
    }

    // Duration is expressed in ticks
    public static float getPowerDuration(ItemStack stack) {
        return NBTTools.getFloat(stack, "duration", START_DURATION);
    }

    public static void setPowerDuration(ItemStack stack, float duration) {
        stack.getOrCreateTag().putFloat("duration", duration);
    }

    public static int getTotalTicks(ItemStack stack) {
        return (int) getPowerDuration(stack);
    }
}
