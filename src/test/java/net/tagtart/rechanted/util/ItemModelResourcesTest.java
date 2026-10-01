package net.tagtart.rechanted.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.tagtart.rechanted.item.ModItems;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipFile;

import static org.junit.jupiter.api.Assertions.*;

class ItemModelResourcesTest {
    @Test
    void everyRegisteredItemHasAPackagedModelAndReadableTextures() throws IOException {
        try (ZipFile jar = openJar()) {
            // Deferred item IDs are available without constructing items or a world.
            assertFalse(ModItems.ITEMS.getEntries().isEmpty());
            Set<String> checkedModels = new HashSet<>();
            for (var item : ModItems.ITEMS.getEntries()) {
                assertModelResources(jar, item.getId().getNamespace() + ":item/" + item.getId().getPath(), checkedModels);
            }
        }
    }

    @Test
    void allBookRaritiesHaveTheExpectedModelOverrides() throws IOException {
        try (ZipFile jar = openJar()) {
            JsonObject model = readModel(jar, "rechanted:item/rechanted_book");
            String[] rarities = {"dusty", "simple", "unique", "elite", "ultimate", "legendary"};
            var overrides = model.getAsJsonArray("overrides");
            assertEquals(rarities.length, overrides.size());
            for (int i = 0; i < rarities.length; ++i) {
                JsonObject override = overrides.get(i).getAsJsonObject();
                assertEquals(i, override.getAsJsonObject("predicate").get("rechanted:book_rarity").getAsInt());
                assertEquals("rechanted:item/" + rarities[i], override.get("model").getAsString());
            }
        }
    }

    @Test
    void testClassesAndTheTestProviderTagAreNotPackaged() throws IOException {
        try (ZipFile jar = openJar()) {
            assertNull(jar.getEntry("net/tagtart/rechanted/util/ItemModelResourcesTest.class"));
            assertNull(jar.getEntry("net/tagtart/rechanted/util/EnchantingPowerTest.class"));
            assertNull(jar.getEntry("data/minecraft/tags/block/enchantment_power_provider.json"));
        }
    }

    private static ZipFile openJar() throws IOException {
        return new ZipFile(System.getProperty("rechanted.testJar"));
    }

    private static JsonObject readModel(ZipFile jar, String modelId) throws IOException {
        String[] id = modelId.split(":", 2);
        String path = "assets/" + id[0] + "/models/" + id[1] + ".json";
        var entry = jar.getEntry(path);
        assertNotNull(entry, "Missing packaged model: " + path);
        try (var reader = new InputStreamReader(jar.getInputStream(entry), StandardCharsets.UTF_8)) {
            return JsonParser.parseReader(reader).getAsJsonObject();
        }
    }

    private static void assertModelResources(ZipFile jar, String modelId, Set<String> checkedModels) throws IOException {
        if (!checkedModels.add(modelId)) {
            return;
        }
        JsonObject model = readModel(jar, modelId);
        if (model.has("parent") && model.get("parent").getAsString().startsWith("rechanted:")) {
            assertModelResources(jar, model.get("parent").getAsString(), checkedModels);
        }
        if (model.has("textures")) {
            for (var texture : model.getAsJsonObject("textures").entrySet()) {
                String reference = texture.getValue().getAsString();
                if (!reference.startsWith("rechanted:")) {
                    continue; // Vanilla textures and #references are resolved by Minecraft.
                }
                String path = "assets/rechanted/textures/" + reference.substring("rechanted:".length()) + ".png";
                var entry = jar.getEntry(path);
                assertNotNull(entry, "Missing packaged texture: " + path);
                try (var stream = jar.getInputStream(entry)) {
                    assertNotNull(ImageIO.read(stream), "Unreadable texture: " + path);
                }
            }
        }
        if (model.has("overrides")) {
            for (var override : model.getAsJsonArray("overrides")) {
                assertModelResources(jar, override.getAsJsonObject().get("model").getAsString(), checkedModels);
            }
        }
    }
}
