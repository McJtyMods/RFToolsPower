package mcjty.rftoolspower.items;

import mcjty.lib.McJtyRegister;
import mcjty.rftoolspower.RFToolsPower;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class PowerCoreItem extends Item {

    public PowerCoreItem(String tier) {
        setUnlocalizedName("rftoolspower.power_core" + tier);
        setRegistryName("power_core" + tier);
        setCreativeTab(RFToolsPower.tabRfToolsPower);
        McJtyRegister.registerLater(this, RFToolsPower.instance);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add("This item is a crafting ingredient");
        tooltip.add("for powercells");
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

}
