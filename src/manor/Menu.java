package manor;
import com.terraformersmc.modmenu.api.*;
import net.minecraft.*;

public final class Menu implements ModMenuApi {
    public ConfigScreenFactory<?> getModConfigScreenFactory() { return Options::new; }

    private static final class Options extends class_437 {
        final boolean caster;
        int headerY;
        private class_2561 toggleLabel(String mob) {
            return class_2561.method_43470("Guaranteed: " + (Settings.guaranteed(mob) ? "On" : "Off"));
        }
        final class_437 parent;
        Options(class_437 parent) { this(parent, false); }
        Options(class_437 parent, boolean caster) { super(class_2561.method_43470("Manor encounters")); this.parent = parent; this.caster = caster; }
        @Override protected void method_25426() {
            int width = Math.min(145, (field_22789 - 14) / 2);
            int left = field_22789 / 2 - width - 2;
            var group = caster ? Settings.CASTER : Settings.MELEE;
            int rows = group.size();
            headerY = Math.max(2, (field_22790 - (rows + 2) * 22 - 12) / 2);
            int top = headerY + 12;
            for (int i = 0; i < rows; i++) {
                String mob = group.get(i);
                int y = top + i * 22;
                method_37063(new ChanceSlider(mob, left, y, width));
                method_37063(class_4185.method_46430(toggleLabel(mob), b -> {
                    Settings.toggle(mob);
                    b.method_25355(toggleLabel(mob));
                }).method_46434(left + width + 4, y, width, 20).method_46431());
            }
            int footer = top + rows * 22;
            method_37063(class_4185.method_46430(class_2561.method_43470(caster ? "< Melee" : "Caster >"),
                b -> field_22787.method_1507(new Options(parent, !caster)))
                .method_46434(left, footer, width, 20).method_46431());
            method_37063(class_4185.method_46430(class_2561.method_43470("Reset all to defaults"), b -> {
                Settings.reset();
                field_22787.method_1507(new Options(parent, caster));
            }).method_46434(left + width + 4, footer, width, 20).method_46431());
            method_37063(class_4185.method_46430(class_2561.method_43470("Done"), b -> method_25419())
                .method_46434(left, footer + 22, width * 2 + 4, 20).method_46431());
        }
        @Override public void method_25394(class_332 context, int x, int y, float delta) {
            super.method_25394(context, x, y, delta);
            context.method_25300(field_22793, caster ? "Caster (2/2)" : "Melee (1/2)", field_22789 / 2, headerY, 0xffffff);
        }
        @Override public void method_25419() { field_22787.method_1507(parent); }
    }

    private static final class ChanceSlider extends class_357 {
        private final String mob;

        ChanceSlider(String mob, int x, int y, int width) {
            super(x, y, width, 20, class_2561.method_43470(""), Settings.chance(mob) / 100.0);
            this.mob = mob;
            method_25346();
        }

        private int percentage() {
            return (int) Math.round(field_22753 * 100);
        }

        @Override protected void method_25346() {
            String name = Character.toUpperCase(mob.charAt(0)) + mob.substring(1);
            method_25355(class_2561.method_43470(name + "  " + percentage() + (mob.equals("vindicator") || mob.equals("evoker") ? "%" : " weight")));
        }

        @Override protected void method_25344() {
            Settings.setChance(mob, percentage());
        }
    }
}
