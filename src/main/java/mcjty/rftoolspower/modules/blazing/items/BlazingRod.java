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

    private final TooltipBuilder tooltipBuilder = new TooltipBuilder()
            .info(key("message.rftoolsbase.shiftmessage"))
            .infoShift(header(),
                    parameter("time", stack -> Float.toString(getAgitationTimeLeft(stack))),
                    parameter("quality", stack -> Float.toString(getPowerQuality(stack))),
                    parameter("duration", stack -> Float.toString(getPowerDuration(stack))));


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
        return NBTTools.getFloat(stack, "time", 20.0f * 120.0f);
    }

    public static void setAgitationTimeLeft(ItemStack stack, float time) {
        stack.getOrCreateTag().putFloat("time", time);
    }

    // Quality is expressed in RF per 1000 ticks
    public static float getPowerQuality(ItemStack stack) {
        return NBTTools.getFloat(stack, "quality", 5000f);
    }

    public static void setPowerQuality(ItemStack stack, float quality) {
        stack.getOrCreateTag().putFloat("quality", quality);
    }

    // Duration is expressed in ticks
    public static float getPowerDuration(ItemStack stack) {
        return NBTTools.getFloat(stack, "duration", 10f);
    }

    public static void setPowerDuration(ItemStack stack, float duration) {
        stack.getOrCreateTag().putFloat("duration", duration);
    }
}
