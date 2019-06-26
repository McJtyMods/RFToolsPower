package mcjty.rftoolspower.blocks;

import net.minecraft.state.IProperty;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Optional;

public class PropertySideTier implements IProperty<Tier> {

    private final String name;

    public PropertySideTier(String name) {
        this.name = name;
    }

    @Override
    @Nonnull
    public String getName() {
        return name;
    }

    @Override
    @Nonnull
    public Collection<Tier> getAllowedValues() {
        return Tier.VALUES;
    }

    @Override
    @Nonnull
    public Class<Tier> getValueClass() {
        return Tier.class;
    }

    @Override
    @Nonnull
    public Optional<Tier> parseValue(@Nonnull String value) {
        return Optional.of(Tier.valueOf(value));
    }

    @Override
    @Nonnull
    public String getName(@Nonnull Tier value) {
        return value.getName();
    }
}
