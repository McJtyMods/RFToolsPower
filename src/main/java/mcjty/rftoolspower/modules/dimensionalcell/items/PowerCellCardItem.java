package mcjty.rftoolspower.modules.dimensionalcell.items;

import mcjty.lib.builder.TooltipBuilder;
import mcjty.lib.tooltips.ITooltipSettings;
import mcjty.lib.varia.Tools;
import mcjty.rftoolspower.RFToolsPower;
import mcjty.rftoolspower.modules.dimensionalcell.DimensionalCellModule;
import mcjty.rftoolspower.modules.dimensionalcell.data.PowerCardData;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.common.util.Lazy;

import javax.annotation.Nonnull;
import java.util.List;

import static mcjty.lib.builder.TooltipBuilder.header;
import static mcjty.lib.builder.TooltipBuilder.parameter;

public class PowerCellCardItem extends Item implements ITooltipSettings {

    private final Lazy<TooltipBuilder> tooltipBuilder = Lazy.of(() -> new TooltipBuilder()
            .info(header(),
                    parameter("info", stack -> {
                        int id = getId(stack);
                        if (id == -1) {
                            return "<unlinked>";
                        } else {
                            return Integer.toString(id);
                        }
                    })));


    public PowerCellCardItem() {
        super(RFToolsPower.setup.defaultProperties());
    }

    public static void initOverrides(PowerCellCardItem item) {
        ItemProperties.register(item, ResourceLocation.fromNamespaceAndPath(RFToolsPower.MODID, "linked"), (stack, world, livingEntity, seed) -> {
            int id = getId(stack);
            return id == -1 ? 0 : 1;
        });
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack itemStack, TooltipContext context, @Nonnull List<Component> list, @Nonnull TooltipFlag flag) {
        super.appendHoverText(itemStack, context, list, flag);
        tooltipBuilder.get().makeTooltip(Tools.getId(this), itemStack, list, flag);
    }

    public static int getId(ItemStack stack) {
        return stack.getOrDefault(DimensionalCellModule.ITEM_POWERCARD_DATA, PowerCardData.EMPTY).id();
    }

    public static void setId(ItemStack stack, int id) {
        stack.set(DimensionalCellModule.ITEM_POWERCARD_DATA, new PowerCardData(id));
    }
}
