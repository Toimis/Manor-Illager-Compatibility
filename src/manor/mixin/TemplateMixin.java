package manor.mixin;
import manor.Settings;
import net.minecraft.class_2960;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(targets="net.minecraft.class_3485", remap=false)
abstract class TemplateMixin {
    @ModifyVariable(method="method_15094(Lnet/minecraft/class_2960;)Ljava/util/Optional;", at=@At("HEAD"), argsOnly=true, ordinal=0, remap=false)
    private class_2960 manorCompatSelect(class_2960 id) {
        String key = id.toString();
        if (!key.startsWith("manor_compat:")) return id;
        String slot = key.substring("manor_compat:".length());
        if (slot.startsWith("guarantee/")) {
            String mob = slot.substring("guarantee/".length());
            return class_2960.method_60654("manor_compat:entrance/" + (Settings.guaranteed(mob) ? mob : "empty"));
        }
        return switch (slot) {
            case "basher","basher2","archivist","archivist2","provoker","provoker2" -> class_2960.method_60654(Settings.select(slot));
            default -> id;
        };
    }
}
