package manor;

import com.google.gson.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import net.fabricmc.loader.api.*;
import net.minecraft.*;

public final class ManorPack {
    private static final String POOLS = "data/minecraft/worldgen/template_pool/illager_mansion/";
    private static final String ROOMS = "data/manor_compat/structure/";
    private static Path directory;

    public static synchronized class_3262 open() {
        if (directory == null) {
            var upstream = FabricLoader.getInstance().getModContainer("mr_dungeons_andtavernswoodlandmansionreplacement").orElseThrow();
            try {
                Path root = Files.createTempDirectory("manor-illager-");
                root.toFile().deleteOnExit();
                generate(root, upstream);
                directory = root;
            } catch (IOException ex) {
                throw new UncheckedIOException("Could not generate manor encounters", ex);
            }
        }
        return new class_3259(new class_9224("manor_compat_generated", class_2561.method_43470("Manor encounters"),
            class_5352.field_25348, Optional.empty()), directory);
    }

    private static Path output(Path root, String name) throws IOException {
        Path file = root.resolve(name);
        if (!Files.exists(file.getParent())) {
            for (Path path = root; !path.equals(file.getParent()); ) {
                path = path.resolve(path.relativize(file).getName(0));
                if (Files.notExists(path)) {
                    Files.createDirectory(path);
                    path.toFile().deleteOnExit();
                }
            }
        }
        file.toFile().deleteOnExit();
        return file;
    }

    private static JsonObject readPool(ModContainer upstream, String name) throws IOException {
        try (var reader = Files.newBufferedReader(upstream.findPath(POOLS + name).orElseThrow())) {
            return JsonParser.parseReader(reader).getAsJsonObject();
        }
    }

    private static void generate(Path root, ModContainer upstream) throws IOException {
        String roomsName = "illager_mob.json";
        JsonObject rooms = readPool(upstream, roomsName);
        JsonArray entries = rooms.getAsJsonArray("elements");
        for (JsonElement entry : entries) {
            var object = entry.getAsJsonObject();
            object.addProperty("weight", object.get("weight").getAsInt() * 4);
        }
        var prototype = entries.get(0).getAsJsonObject();
        for (String suffix : List.of("", "2")) {
            for (String slot : List.of("basher", "provoker", "archivist")) {
                var added = prototype.deepCopy();
                added.addProperty("weight", slot.equals("basher") ? 4 : 2);
                added.getAsJsonObject("element").addProperty("location", "manor_compat:" + slot + suffix);
                entries.add(added);
            }
            for (var group : List.of(Settings.MELEE, Settings.CASTER)) {
                String vanilla = group.getFirst();
                for (String mob : group.subList(1, group.size())) {
                    var source = upstream.findPath("data/minecraft/structure/illager_mansion/" + vanilla + suffix + ".nbt").orElseThrow();
                    var template = class_2507.method_30613(source, class_2505.method_53898());
                    var entities = template.method_10554("entities", 10);
                    var data = new class_2487();
                    data.method_10582("id", (mob.equals("illusioner") ? "minecraft:" : "illagerinvasion:") + mob);
                    data.method_10567("PersistenceRequired", (byte)1);
                    entities.method_10602(0).method_10566("nbt", data);
                    String name = mob + suffix + ".nbt";
                    class_2507.method_30614(template, output(root, ROOMS + "configured/" + name));
                    if (List.of("basher", "provoker", "archivist").contains(mob)) {
                        class_2507.method_30614(template, output(root, ROOMS + name));
                        template.method_10566("entities", new class_2499());
                        class_2507.method_30614(template, output(root, ROOMS + "empty/" + name));
                    }
                }
            }
        }
        Files.writeString(output(root, POOLS + roomsName), rooms.toString());
        String entranceName = "illager_mansion_entry.json";
        var entrance = readPool(upstream, entranceName);
        var entry = entrance.getAsJsonArray("elements").get(0).getAsJsonObject();
        var base = entry.getAsJsonObject("element");
        var children = new JsonArray();
        children.add(base);
        var overlays = new ArrayList<String>();
        overlays.add("invoker_entry");
        for (var group : List.of(Settings.MELEE, Settings.CASTER))
            for (String mob : group) overlays.add("guarantee/" + mob);
        for (String overlay : overlays) {
            var element = base.deepCopy();
            element.addProperty("location", "manor_compat:" + overlay);
            element.addProperty("processors", "minecraft:empty");
            children.add(element);
        }
        var list = new JsonObject();
        list.addProperty("element_type", "minecraft:list_pool_element");
        list.add("projection", base.get("projection"));
        list.add("elements", children);
        entry.add("element", list);
        Files.writeString(output(root, POOLS + entranceName), entrance.toString());
    }
}
