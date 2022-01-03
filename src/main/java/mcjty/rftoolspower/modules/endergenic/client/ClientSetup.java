package mcjty.rftoolspower.modules.endergenic.client;

import mcjty.rftoolspower.modules.endergenic.EndergenicModule;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraftforge.client.event.TextureStitchEvent;

public class ClientSetup {
    public static void initClient() {
        ItemBlockRenderTypes.setRenderLayer(EndergenicModule.ENDERGENIC.get(), RenderType.translucent());
    }

    public static void onTextureStitch(TextureStitchEvent.Pre event) {
        if (!event.getAtlas().location().equals(TextureAtlas.LOCATION_BLOCKS)) {
            return;
        }

        event.addSprite(EndergenicRenderer.HALO);
        event.addSprite(EndergenicRenderer.BLACKFLASH);
        event.addSprite(EndergenicRenderer.WHITEFLASH);
        event.addSprite(EndergenicRenderer.BLUEGLOW);
        event.addSprite(EndergenicRenderer.REDGLOW);
    }
}
