package mcjty.rftoolspower.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class FabricDataGen implements DataGeneratorEntrypoint {
    @Override public void onInitializeDataGenerator(FabricDataGenerator generator) {
        var pack = generator.createPack();
        pack.addProvider((output, lookup) -> {
            var data = new mcjty.lib.datagen.DataGen(output, lookup);
            mcjty.rftoolspower.RFToolsPower.instance.modules.datagen(data, lookup);
            data.generate();
            return data;
        });
        pack.addProvider((output, lookup) -> new mcjty.rftoolsbase.tools.ClientItemDefinitions(output, "rftoolspower", itemDefinitions()));
        pack.addProvider((output, lookup) -> new mcjty.rftoolspower.modules.dimensionalcell.DataGenHelper.Provider(output));
    }

    private static java.util.Map<String, com.google.gson.JsonObject> itemDefinitions() {
        var card = new com.google.gson.JsonObject();
        var model = new com.google.gson.JsonObject();
        model.addProperty("type", "minecraft:condition");
        model.addProperty("property", "rftoolspower:linked");
        var linked = new com.google.gson.JsonObject();
        linked.addProperty("type", "minecraft:model");
        linked.addProperty("model", "rftoolspower:item/powercell_card_linked");
        var unlinked = new com.google.gson.JsonObject();
        unlinked.addProperty("type", "minecraft:model");
        unlinked.addProperty("model", "rftoolspower:item/powercell_card_unlinked");
        model.add("on_true", linked);
        model.add("on_false", unlinked);
        card.add("model", model);
        return java.util.Map.of("powercell_card", card);
    }
}
