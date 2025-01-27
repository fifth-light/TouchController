package top.fifthlight.touchcontroller.mixin;

import net.minecraft.util.math.vector.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Matrix4f.class)
public interface Matrix4fAccessor {
    @Accessor("m03")
    float getA03();

    @Accessor("m13")
    float getA13();
}
