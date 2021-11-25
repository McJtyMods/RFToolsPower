package mcjty.rftoolspower.modules.endergenic.data;

import mcjty.lib.varia.NamedEnum;

import java.util.HashMap;
import java.util.Map;

public enum EnderMonitorMode implements NamedEnum<EnderMonitorMode> {
    MODE_LOSTPEARL("Lost Pearl"),
    MODE_PEARLFIRED("Pearl Fired"),
    MODE_PEARLARRIVED("Pearl Arrived"),
    ;

    private static final Map<String,EnderMonitorMode> modeToMode = new HashMap<>();

    private final String description;

    EnderMonitorMode(String description) {
        this.description = description;
    }

    @Override
    public String getName() {
        return description;
    }

    @Override
    public String[] getDescription() {
        return new String[] { description };
    }


    public static EnderMonitorMode getMode(String mode) {
        return modeToMode.get(mode);
    }



    static {
        for (EnderMonitorMode mode : values()) {
            modeToMode.put(mode.description, mode);
        }
    }

}
