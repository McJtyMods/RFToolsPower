package mcjty.rftoolspower.modules.dimensionalcell.items;

import mcjty.lib.builder.TooltipBuilder;
import mcjty.lib.tooltips.ITooltipSettings;
import mcjty.rftoolspower.RFToolsPower;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.Lazy;

import java.util.List;

import static mcjty.lib.builder.TooltipBuilder.header;
import static mcjty.lib.builder.TooltipBuilder.parameter;

import net.minecraft.world.item.Item.Properties;

import javax.annotation.Nonnull;

public class PowerCellCardItem extends Item implements ITooltipSettings {

    private final Lazy<TooltipBuilder> tooltipBuilder = () -> new TooltipBuilder()
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
        super(new Properties().tab(RFToolsPower.setup.getTab()));
    }

    public static void initOverrides(PowerCellCardItem item) {
        ItemProperties.register(item, new ResourceLocation(RFToolsPower.MODID, "linked"), (stack, world, livingEntity, seed) -> {
            int id = getId(stack);
            return id == -1 ? 0 : 1;
        });
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack itemStack, Level world, @Nonnull List<Component> list, @Nonnull TooltipFlag flag) {
        super.appendHoverText(itemStack, world, list, flag);
        tooltipBuilder.get().makeTooltip(getRegistryName(), itemStack, list, flag);
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
