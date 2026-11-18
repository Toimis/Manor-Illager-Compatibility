package manor.worldgen;

import java.util.*;
import manor.Settings;
import net.minecraft.*;

public final class ManorRooms {
    private static final String MARKER = "manor_compat:guarantee/";

    private static String location(class_3784 element) {
        if (element instanceof class_3781 single) {
            return single.field_24015.left().map(Object::toString).orElse("");
        }
        return "";
    }

    public static List<class_3443> assign(List<class_3443> pieces) {
        for (class_3443 piece : pieces) {
            if (piece instanceof class_3790 entrance && entrance.method_16644() instanceof class_3782 list
                    && list.field_16676.stream().anyMatch(e -> location(e).startsWith(MARKER))) {
                assignRooms(pieces, entrance, list);
                break;
            }
        }
        return pieces;
    }

    private static void assignRooms(List<class_3443> pieces, class_3790 entrance, class_3782 original) {
        List<class_3790> melee = new ArrayList<>();
        List<class_3790> caster = new ArrayList<>();
        class_3790 mainHall = null;
        for (class_3443 piece : pieces) {
            if (!(piece instanceof class_3790 room)) continue;
            switch (location(room.method_16644())) {
                case "minecraft:illager_mansion/manor_fixed/mansion_main_hall1",
                     "minecraft:illager_mansion/manor_fixed/mansion_main_hall2",
                     "minecraft:illager_mansion/manor_fixed/mansion_main_hall3",
                     "minecraft:illager_mansion/manor_fixed/mansion_main_hall4" -> mainHall = room;
                case "manor_compat:basher", "manor_compat:basher2",
                     "minecraft:illager_mansion/vindicator", "minecraft:illager_mansion/vindicator2" -> melee.add(room);
                case "manor_compat:provoker", "manor_compat:provoker2", "manor_compat:archivist", "manor_compat:archivist2",
                     "minecraft:illager_mansion/evoker", "minecraft:illager_mansion/evoker2" -> caster.add(room);
            }
        }
        Collections.shuffle(melee);
        Collections.shuffle(caster);
        List<class_3784> children = new ArrayList<>();
        List<String> enabled = new ArrayList<>();
        for (class_3784 element : original.field_16676) {
            String key = location(element);
            if (key.startsWith(MARKER)) {
                String mob = key.substring(MARKER.length());
                if (Settings.guaranteed(mob)) enabled.add(mob);
            } else children.add(element);
        }
        Collections.shuffle(enabled);
        for (String mob : enabled) {
            if (mob.equals("necromancer") && mainHall != null) {
                var hall = mainHall.method_16644();
                var projection = hall.method_16624();
                var landing = class_3784.method_30434("manor_compat:necromancer_landing").apply(projection);
                mainHall.field_16693 = new class_3782(List.of(hall, landing), projection);
                continue;
            }
            List<class_3790> slots = Settings.isMelee(mob) ? melee : caster;
            if (slots.isEmpty()) {
                children.add(class_3784.method_30434("manor_compat:entrance/" + mob).apply(original.method_16624()));
            } else {
                class_3790 slot = slots.removeLast();
                class_3781 old = (class_3781) slot.method_16644();
                String prefix = mob.equals("vindicator") || mob.equals("evoker")
                    ? "minecraft:illager_mansion/" : "manor_compat:configured/";
                String suffix = location(old).endsWith("2") ? "2" : "";
                slot.field_16693 = class_3784.method_30435(prefix + mob + suffix, old.field_16674).apply(old.method_16624());
            }
        }
        // Replace per-piece elements so the shared pool stays intact and the save records the choice.
        entrance.field_16693 = new class_3782(children, original.method_16624());
    }
}
