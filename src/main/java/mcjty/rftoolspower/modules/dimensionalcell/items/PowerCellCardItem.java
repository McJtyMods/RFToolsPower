package mcjty.rftoolspower.modules.dimensionalcell.items;

import mcjty.rftoolspower.RFToolsPower;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.util.List;

public class PowerCellCardItem extends Item {

    public PowerCellCardItem() {
        super(new Properties().group(RFToolsPower.setup.getTab()));
        addPropertyOverride(new ResourceLocation(RFToolsPower.MODID, "linked"), (stack, world, livingEntity) -> {
            int id = getId(stack);
            return id == -1 ? 0 : 1;
        });
    }

    @Override
    public void addInformation(ItemStack itemStack, World world, List<ITextComponent> list, ITooltipFlag flag) {
        super.addInformation(itemStack, world, list, flag);
        list.add(new StringTextComponent(TextFormatting.WHITE + "Use to connect a powercell multiblock"));
        int id = getId(itemStack);
        if (id == -1) {
            list.add(new StringTextComponent(TextFormatting.YELLOW + "[UNLINKED]"));
        } else {
            list.add(new StringTextComponent(TextFormatting.GREEN + "Link id: " + id));
        }
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
