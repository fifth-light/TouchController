package top.fifthlight.touchcontroller.transformer;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class MovementInputFromOptionsTransformer extends TouchControllerClassVisitor {
    public MovementInputFromOptionsTransformer(ClassVisitor classVisitor) {
        super(Opcodes.ASM5, classVisitor);
    }

    @Override
    public String getClassName() {
        return "net.minecraft.util.MovementInputFromOptions";
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if ("updatePlayerMoveState".equals(name) || "func_78898_a".equals(mapSelfMethodName(name, desc))) {
            String selfClassName = unmapClassName(getClassName());
            return new MethodVisitor(Opcodes.ASM5, super.visitMethod(access, name, desc, signature, exceptions)) {
                // Add code to updatePlayerMoveState after this.sneak = ...:
                // KeyboardInputEvents.onEndTick(this);
                @Override
                public void visitFieldInsn(int opcode, String owner, String name, String desc) {
                    if (opcode != Opcodes.PUTFIELD) {
                        super.visitFieldInsn(opcode, owner, name, desc);
                        return;
                    }
                    if (!selfClassName.equals(owner)) {
                        super.visitFieldInsn(opcode, owner, name, desc);
                        return;
                    }
                    if (!"sneak".equals(name) && !"field_78899_d".equals(mapSelfFieldName(name, desc))) {
                        super.visitFieldInsn(opcode, owner, name, desc);
                        return;
                    }
                    super.visitFieldInsn(opcode, owner, name, desc);
                    visitVarInsn(Opcodes.ALOAD, 0);
                    visitMethodInsn(Opcodes.INVOKESTATIC, "top/fifthlight/touchcontroller/helper/MovementInputFromOptionsHelper", "onEndTick", "(Lnet/minecraft/util/MovementInput;)V", false);
                }
            };
        } else {
            return super.visitMethod(access, name, desc, signature, exceptions);
        }
    }
}
