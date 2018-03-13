package mcjty.rftoolspower.blocks;

import net.minecraftforge.common.property.IUnlistedProperty;

public class UnlistedPropertySideTier implements IUnlistedProperty<Tier> {

    private final String name;

    public UnlistedPropertySideTier(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isValid(Tier value) {
        return true;
    }

    @Override
    public Class<Tier> getType() {
        return Tier.class;
    }

    @Override
    public String valueToString(Tier value) {
        return value.getName();
    }
}
