package mcjty.rftoolspower.modules.dimensionalcell.client;

import com.mojang.serialization.MapCodec;
import mcjty.rftoolspower.modules.dimensionalcell.items.PowerCellCardItem;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.conditional.ConditionalItemModelProperty;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.RegisterConditionalItemModelPropertyEvent;

public record LinkedPowerCardProperty() implements ConditionalItemModelProperty {
    public static final MapCodec<LinkedPowerCardProperty> CODEC = MapCodec.unit(new LinkedPowerCardProperty());
    public static void register(RegisterConditionalItemModelPropertyEvent event) {
        event.register(Identifier.fromNamespaceAndPath("rftoolspower", "linked"), CODEC);
    }
    @Override public boolean get(ItemStack stack, ClientLevel level, LivingEntity entity, int seed, ItemDisplayContext context) {
        return PowerCellCardItem.getId(stack) != -1;
    }
    @Override public MapCodec<LinkedPowerCardProperty> type() { return CODEC; }
}
