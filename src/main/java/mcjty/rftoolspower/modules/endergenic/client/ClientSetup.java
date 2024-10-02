package mcjty.rftoolspower.modules.endergenic.client;

import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class ClientSetup {
    public static List<ResourceLocation> onTextureStitch() {
        return List.of(EndergenicRenderer.HALO,
                EndergenicRenderer.BLACKFLASH,
                EndergenicRenderer.WHITEFLASH,
                EndergenicRenderer.BLUEGLOW,
                EndergenicRenderer.REDGLOW);
    }
}
