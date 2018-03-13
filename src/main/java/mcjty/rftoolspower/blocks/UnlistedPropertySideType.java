package mcjty.rftoolspower.blocks;

import net.minecraftforge.common.property.IUnlistedProperty;

public class UnlistedPropertySideType implements IUnlistedProperty<SideType> {

    private final String name;

    public UnlistedPropertySideType(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isValid(SideType value) {
        return true;
    }

    @Override
    public Class<SideType> getType() {
        return SideType.class;
    }

    @Override
    public String valueToString(SideType value) {
        return value.getName();
    }
}
