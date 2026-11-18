package manor.mixin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import manor.worldgen.ManorRooms;
import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(targets="net.minecraft.class_6626", remap=false)
abstract class ManorPiecesMixin {
    @ModifyArg(method="method_38714()Lnet/minecraft/class_6624;", at=@At(value="INVOKE",
        target="Lnet/minecraft/class_6624;<init>(Ljava/util/List;)V"), index=0)
    private List<class_3443> manorCompatAssignGuarantees(List<class_3443> pieces) {
        return ManorRooms.assign(pieces);
    }
}
