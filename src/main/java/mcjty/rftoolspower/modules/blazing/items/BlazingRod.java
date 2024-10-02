package mcjty.rftoolspower.modules.blazing.items;

import mcjty.lib.builder.TooltipBuilder;
import mcjty.lib.varia.NBTTools;
import mcjty.lib.varia.Tools;
import mcjty.rftoolspower.setup.Registration;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.Lazy;

import javax.annotation.Nonnull;
import java.util.List;

import static mcjty.lib.builder.TooltipBuilder.*;

public class BlazingRod extends Item {

    public static final float MAXTIME = 20.0f * 30.0f;
    public static final float START_QUALITY = 60000f;
    public static final float START_DURATION = 20f;
    public static final int MAX_INFUSION_STEPS = 64;

    private final Lazy<TooltipBuilder> tooltipBuilder = Lazy.of(() -> new TooltipBuilder()
            .info(key("message.rftoolsbase.shiftmessage"))
            .infoShift(header(),
                    parameter("time", BlazingRod::isCharging, stack -> getAgitationTimePercentage(stack) + "%"),
                    parameter("infused", BlazingRod::isInfused, stack -> getInfusionPercentage(stack) + "%"),
                    parameter("power", stack -> getRfPerTick(stack) + " RF/t"),
                    parameter("duration", stack -> getTotalTicks(stack) + " ticks")));


    public BlazingRod() {
        super(Registration.createStandardProperties());
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack itemStack, TooltipContext context, @Nonnull List<Component> list, @Nonnull TooltipFlag flags) {
        super.appendHoverText(itemStack, context, list, flags);
        tooltipBuilder.get().makeTooltip(Tools.getId(this), itemStack, list, flags);
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
        // @todo 1.21 data
//        stack.getOrCreateTag().putInt("infSteps", steps);
    }

    // Get time left (in ticks) before ready
    public static float getAgitationTimeLeft(ItemStack stack) {
        return NBTTools.getFloat(stack, "time", MAXTIME);
    }

    public static void setAgitationTimeLeft(ItemStack stack, float time) {
        // @todo 1.21 data
//        stack.getOrCreateTag().putFloat("time", time);
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
        // @todo 1.21 data
//        stack.getOrCreateTag().putFloat("quality", quality);
    }

    public static int getRfPerTick(ItemStack stack) {
        return (int) (getPowerQuality(stack) / 1000);
    }

    // Duration is expressed in ticks
    public static float getPowerDuration(ItemStack stack) {
        // @todo 1.21 data
        return NBTTools.getFloat(stack, "duration", START_DURATION);
    }

    public static void setPowerDuration(ItemStack stack, float duration) {
        // @todo 1.21 data
//        stack.getOrCreateTag().putFloat("duration", duration);
    }

    public static int getTotalTicks(ItemStack stack) {
        return (int) getPowerDuration(stack);
    }
}
