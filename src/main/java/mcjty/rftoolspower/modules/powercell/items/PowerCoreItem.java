package mcjty.rftoolspower.modules.powercell.items;

import mcjty.lib.builder.TooltipBuilder;
import mcjty.lib.tooltips.ITooltipSettings;
import mcjty.lib.varia.Tools;
import mcjty.rftoolspower.RFToolsPower;
import mcjty.lib.gui.ManualEntry;
import mcjty.rftoolsbase.tools.ManualHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.util.Lazy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static mcjty.lib.builder.TooltipBuilder.header;

public class PowerCoreItem extends Item implements ITooltipSettings {

    private final Lazy<TooltipBuilder> tooltipBuilder = Lazy.of(() -> new TooltipBuilder()
            .info(header()));

    public PowerCoreItem(net.minecraft.world.item.Item.Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(@Nonnull ItemStack stack, TooltipContext worldIn, @Nonnull net.minecraft.world.item.component.TooltipDisplay display, java.util.function.Consumer<Component> consumer, @Nonnull TooltipFlag flagIn) {
        List<Component> tooltip = new java.util.ArrayList<>();
        super.appendHoverText(stack, worldIn, display, consumer, flagIn);
        tooltipBuilder.get().makeTooltip(Tools.getId(this), stack, tooltip, flagIn);

        tooltip.forEach(consumer);
    }

    @Override
    public ManualEntry getManualEntry() {
        return ManualHelper.create("rftoolspower:powerstorage/powercell");
    }

}
