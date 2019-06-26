package mcjty.rftoolspower.blocks;


import net.minecraft.state.IProperty;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Optional;

public class PropertySideType implements IProperty<SideType> {

    private final String name;

    public PropertySideType(String name) {
        this.name = name;
    }

    @Override
    @Nonnull
    public String getName() {
        return name;
    }

    @Override
    @Nonnull
    public Collection<SideType> getAllowedValues() {
        return SideType.VALUES;
    }

    @Override
    @Nonnull
    public Class<SideType> getValueClass() {
        return SideType.class;
    }

    @Override
    @Nonnull
    public Optional<SideType> parseValue(String value) {
        return Optional.of(SideType.valueOf(value));
    }

    @Override
    @Nonnull
    public String getName(@Nonnull SideType value) {
        return value.getName();
    }
}
