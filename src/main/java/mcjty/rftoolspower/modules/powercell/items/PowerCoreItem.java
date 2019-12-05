package mcjty.rftoolspower.modules.powercell.items;

import mcjty.rftoolspower.RFToolsPower;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class PowerCoreItem extends Item {

    public PowerCoreItem() {
        super(new Properties().group(RFToolsPower.setup.getTab()));
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(new StringTextComponent("This item is a crafting ingredient"));
        tooltip.add(new StringTextComponent("for powercells"));
    }
}
