package top.fifthlight.touchcontroller.transformer;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

@SuppressWarnings("unused")
public class MouseHelperTransformer extends TouchControllerClassVisitor {
    public MouseHelperTransformer(ClassVisitor classVisitor) {
        super(Opcodes.ASM5, classVisitor);
    }

    @Override
    public String getClassName() {
        return "net.minecraft.util.MouseHelper";
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if ("grabMouseCursor".equals(name) || "func_74372_a".equals(mapSelfMethodName(name, desc))) {
            return new MethodVisitor(Opcodes.ASM5, super.visitMethod(access, name, desc, signature, exceptions)) {
                // Add code to grabMouseCursor:
                // if (MouseHelperHelper.doDisableMouseGrab()) {
                //     return;
                // }
                @Override
                public void visitCode() {
                    visitMethodInsn(Opcodes.INVOKESTATIC, "top/fifthlight/touchcontroller/helper/MouseHelperHelper", "doDisableMouseGrab", "()Z", false);
                    Label skip = new Label();
                    visitJumpInsn(Opcodes.IFEQ, skip);
                    visitInsn(Opcodes.RETURN);
                    visitLabel(skip);
                    super.visitCode();
                }
            };
        } else {
            return super.visitMethod(access, name, desc, signature, exceptions);
        }
    }
}