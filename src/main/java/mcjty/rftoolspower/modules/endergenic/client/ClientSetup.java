package mcjty.rftoolspower.modules.endergenic.client;

import net.minecraft.resources.Identifier;

import java.util.List;

public class ClientSetup {
    public static List<Identifier> onTextureStitch() {
        return List.of(EndergenicRenderer.HALO,
                EndergenicRenderer.BLACKFLASH,
                EndergenicRenderer.WHITEFLASH,
                EndergenicRenderer.BLUEGLOW,
                EndergenicRenderer.REDGLOW);
    }
}
