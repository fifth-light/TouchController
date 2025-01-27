package top.fifthlight.touchcontroller.mixin;

import net.minecraft.util.math.vector.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.fifthlight.touchcontroller.helper.PerspectiveInvertible;

@Mixin(Matrix4f.class)
public class Matrix4fMixin implements PerspectiveInvertible {
    @Shadow
    protected float m00;
    @Shadow
    protected float m11;
    @Shadow
    protected float m23;
    @Shadow
    protected float m32;
    @Shadow
    protected float m22;
    @Shadow
    protected float m33;

    // The invert() in Minecraft doesn't work. So I copied JOML to here.
    public Matrix4f touchController$invertPerspective() {
        float a = 1.0f / (m00 * m11);
        float l = -1.0f / (m23 * m32);
        Matrix4f result = new Matrix4f();
        Matrix4fMixin destMixin = (Matrix4fMixin) (Object) result;
        destMixin.m00 = m11 * a;
        destMixin.m11 = m00 * a;
        destMixin.m23 = m23 * l;
        destMixin.m32 = -m32 * l;
        destMixin.m33 = m22 * l;
        return result;
    }
}
