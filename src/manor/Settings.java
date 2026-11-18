package manor;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import net.fabricmc.loader.api.FabricLoader;

public final class Settings {
    static final List<String> MELEE = List.of("vindicator", "basher", "inquisitor", "marauder");
    static final List<String> CASTER = List.of("evoker", "provoker", "archivist", "alchemist", "firecaller", "illusioner", "necromancer", "sorcerer");
    private static final String VERSION = "2";
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("manor-illagers.properties");
    private static final Properties values = new Properties();

    static {
        try {
            if (Files.exists(FILE)) {
                try (Reader reader = Files.newBufferedReader(FILE)) { values.load(reader); }
            }
            if (!VERSION.equals(values.getProperty("configVersion"))) {
                values.clear();
                save();
            }
        } catch (IOException ex) { throw new UncheckedIOException(ex); }
    }

    static synchronized int chance(String mob) {
        int fallback = switch (mob) {
            case "vindicator" -> 50;
            case "basher", "provoker", "archivist" -> 100;
            default -> 0;
        };
        try { return Math.clamp(Integer.parseInt(values.getProperty(mob, "").trim()), 0, 100); }
        catch (NumberFormatException ex) { return fallback; }
    }

    public static boolean isMelee(String mob) { return MELEE.contains(mob); }

    public static synchronized boolean guaranteed(String mob) {
        return Boolean.parseBoolean(values.getProperty(mob + ".guaranteed", "false").trim());
    }

    private static void save() throws IOException {
        Files.createDirectories(FILE.getParent());
        Path temp = Files.createTempFile(FILE.getParent(), "manor", ".tmp");
        try {
            try (BufferedWriter writer = Files.newBufferedWriter(temp)) {
                writer.write("# DO NOT TOUCH\nconfigVersion=" + VERSION + "\n");
                for (List<String> group : List.of(MELEE, CASTER)) {
                    writer.write(group == MELEE ? "\n# Melee mobs\n" : "\n# Caster mobs\n");
                    writer.write("# First entry: vanilla chance (%). Other entries: relative weights (0-100).\n");
                    for (String mob : group) {
                        writer.write(mob + "=" + chance(mob) + "\n");
                        writer.write(mob + ".guaranteed=" + guaranteed(mob) + "\n");
                    }
                }
            }
            try { Files.move(temp, FILE, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING); }
            catch (AtomicMoveNotSupportedException ex) { Files.move(temp, FILE, StandardCopyOption.REPLACE_EXISTING); }
        } finally { Files.deleteIfExists(temp); }
    }

    private static void update(String key, String value) {
        Object old = values.setProperty(key, value);
        try { save(); } catch (IOException ex) {
            if (old == null) values.remove(key); else values.put(key, old);
            throw new UncheckedIOException(ex);
        }
    }

    static synchronized void setChance(String mob, int value) {
        if (chance(mob) != value) update(mob, Integer.toString(Math.clamp(value, 0, 100)));
    }

    static synchronized void toggle(String mob) {
        update(mob + ".guaranteed", Boolean.toString(!guaranteed(mob)));
    }

    public static synchronized String select(String slot) {
        List<String> group = slot.startsWith("basher") ? MELEE : CASTER;
        String suffix = slot.endsWith("2") ? "2" : "";
        String vanilla = group.getFirst();
        var random = ThreadLocalRandom.current();
        if (random.nextInt(100) < chance(vanilla)) return "minecraft:illager_mansion/" + vanilla + suffix;
        List<String> candidates = group.subList(1, group.size());
        int total = candidates.stream().mapToInt(Settings::chance).sum();
        if (total == 0) return "manor_compat:empty/" + slot;
        int roll = random.nextInt(total);
        for (String mob : candidates) {
            roll -= chance(mob);
            if (roll < 0) return "manor_compat:configured/" + mob + suffix;
        }
        throw new AssertionError("Invalid encounter weight total");
    }
}
