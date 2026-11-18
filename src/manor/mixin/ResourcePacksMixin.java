package manor.mixin;

import java.util.ArrayList;
import java.util.List;
import manor.ManorPack;
import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(targets="net.minecraft.class_6861", remap=false)
abstract class ResourcePacksMixin {
    @ModifyVariable(method="<init>(Lnet/minecraft/class_3264;Ljava/util/List;)V", at=@At("HEAD"),
        argsOnly=true, ordinal=0, remap=false)
    private static List<class_3262> manorAddPack(List<class_3262> packs, class_3264 type, List<class_3262> original) {
        if (type != class_3264.field_14190) return packs;
        var marker = class_2960.method_60654("manor_compat:structure/entrance/empty.nbt");
        for (int i = 0; i < packs.size(); i++) {
            if (packs.get(i).method_14405(type, marker) != null) {
                var result = new ArrayList<>(packs);
                result.add(i + 1, ManorPack.open());
                return result;
            }
        }
        return packs;
    }
}
