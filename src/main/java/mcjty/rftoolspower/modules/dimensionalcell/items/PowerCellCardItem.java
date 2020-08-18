package mcjty.rftoolspower.modules.dimensionalcell.items;

import mcjty.lib.builder.TooltipBuilder;
import mcjty.lib.tooltips.ITooltipSettings;
import mcjty.rftoolspower.RFToolsPower;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import java.util.List;

import static mcjty.lib.builder.TooltipBuilder.header;
import static mcjty.lib.builder.TooltipBuilder.parameter;

public class PowerCellCardItem extends Item implements ITooltipSettings {

    private final TooltipBuilder tooltipBuilder = new TooltipBuilder()
            .info(header(),
                    parameter("info", stack -> {
                        int id = getId(stack);
                        if (id == -1) {
                            return "<unlinked>";
                        } else {
                            return Integer.toString(id);
                        }
                    }));


    public PowerCellCardItem() {
        super(new Properties().group(RFToolsPower.setup.getTab()));
        ItemModelsProperties.func_239418_a_(this, new ResourceLocation(RFToolsPower.MODID, "linked"), (stack, world, livingEntity) -> {
            int id = getId(stack);
            return id == -1 ? 0 : 1;
        });
    }

    @Override
    public void addInformation(ItemStack itemStack, World world, List<ITextComponent> list, ITooltipFlag flag) {
        super.addInformation(itemStack, world, list, flag);
        tooltipBuilder.makeTooltip(getRegistryName(), itemStack, list, flag);
    }

    public static int getId(ItemStack stack) {
        if (!stack.hasTag()) {
            return -1;
        }
        if (!stack.getTag().contains("id")) {
            return -1;
        }
        return stack.getTag().getInt("id");
    }

    public static void setId(ItemStack stack, int id) {
        stack.getOrCreateTag().putInt("id", id);
    }
}
