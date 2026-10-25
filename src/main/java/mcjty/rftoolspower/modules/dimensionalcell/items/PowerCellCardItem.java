package mcjty.rftoolspower.modules.dimensionalcell.items;

import mcjty.lib.builder.TooltipBuilder;
import mcjty.lib.tooltips.ITooltipSettings;
import mcjty.lib.varia.Tools;
import mcjty.rftoolspower.RFToolsPower;
import mcjty.rftoolspower.modules.dimensionalcell.DimensionalCellModule;
import mcjty.rftoolspower.modules.dimensionalcell.data.PowerCardData;
import mcjty.lib.gui.ManualEntry;
import mcjty.rftoolsbase.tools.ManualHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import mcjty.lib.varia.Lazy;

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


    public PowerCellCardItem(net.minecraft.world.item.Item.Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack itemStack, TooltipContext context, @Nonnull net.minecraft.world.item.component.TooltipDisplay display, java.util.function.Consumer<Component> consumer, @Nonnull TooltipFlag flag) {
        List<Component> list = new java.util.ArrayList<>();
        super.appendHoverText(itemStack, context, display, consumer, flag);
        tooltipBuilder.get().makeTooltip(Tools.getId(this), itemStack, list, flag);

        list.forEach(consumer);
    }

    public static int getId(ItemStack stack) {
        return stack.getOrDefault(DimensionalCellModule.ITEM_POWERCARD_DATA.get(), PowerCardData.EMPTY).id();
    }

    public static void setId(ItemStack stack, int id) {
        stack.set(DimensionalCellModule.ITEM_POWERCARD_DATA.get(), new PowerCardData(id));
    }

    @Override
    public ManualEntry getManualEntry() {
        return ManualHelper.create("rftoolspower:powerstorage/powercell_card");
    }

}
