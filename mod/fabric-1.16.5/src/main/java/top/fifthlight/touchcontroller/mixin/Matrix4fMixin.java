package top.fifthlight.touchcontroller.mixin;

import net.minecraft.util.math.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.fifthlight.touchcontroller.helper.PerspectiveInvertible;

@Mixin(Matrix4f.class)
public class Matrix4fMixin implements PerspectiveInvertible {
    @Shadow
    protected float a00;
    @Shadow
    protected float a11;
    @Shadow
    protected float a23;
    @Shadow
    protected float a32;
    @Shadow
    protected float a22;
    @Shadow
    protected float a33;

    // The invert() in Minecraft doesn't work. So I copied JOML to here.
    public Matrix4f touchController$invertPerspective() {
        float a = 1.0f / (a00 * a11);
        float l = -1.0f / (a23 * a32);
        Matrix4f result = new Matrix4f();
        Matrix4fMixin destMixin = (Matrix4fMixin) (Object) result;
        destMixin.a00 = a11 * a;
        destMixin.a11 = a00 * a;
        destMixin.a23 = a23 * l;
        destMixin.a32 = -a32 * l;
        destMixin.a33 = a22 * l;
        return result;
    }
}
