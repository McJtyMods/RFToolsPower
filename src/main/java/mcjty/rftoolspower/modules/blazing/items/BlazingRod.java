package mcjty.rftoolspower.modules.blazing.items;

import mcjty.lib.builder.TooltipBuilder;
import mcjty.lib.varia.NBTTools;
import mcjty.rftoolspower.setup.Registration;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import java.util.List;

import static mcjty.lib.builder.TooltipBuilder.*;

public class BlazingRod extends Item {

    public static final float MAXTIME = 20.0f * 30.0f;
    public static final float START_QUALITY = 40000f;
    public static final float START_DURATION = 20f;

    private final TooltipBuilder tooltipBuilder = new TooltipBuilder()
            .info(key("message.rftoolsbase.shiftmessage"))
            .infoShift(header(),
                    parameter("time", stack -> getAgitationTimePercentage(stack) + "%"),
                    parameter("power", stack -> getRfPerTick(stack) + " RF/t"),
                    parameter("duration", stack -> getTotalTicks(stack) + " ticks"));


    public BlazingRod() {
        super(Registration.createStandardProperties());
    }

    @Override
    public void addInformation(ItemStack itemStack, World world, List<ITextComponent> list, ITooltipFlag flags) {
        super.addInformation(itemStack, world, list, flags);
        tooltipBuilder.makeTooltip(getRegistryName(), itemStack, list, flags);
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
