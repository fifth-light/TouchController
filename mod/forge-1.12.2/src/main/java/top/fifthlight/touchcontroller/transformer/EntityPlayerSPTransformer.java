package top.fifthlight.touchcontroller.transformer;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class EntityPlayerSPTransformer extends TouchControllerClassVisitor {
    public EntityPlayerSPTransformer(ClassVisitor classVisitor) {
        super(Opcodes.ASM5, classVisitor);
    }

    @Override
    public String getClassName() {
        return "net.minecraft.client.entity.EntityPlayerSP";
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if ("onLivingUpdate".equals(name) || "func_70636_d".equals(mapSelfMethodName(name, desc))) {
            String selfClassName = unmapClassName(getClassName());
            return new MethodVisitor(Opcodes.ASM5, super.visitMethod(access, name, desc, signature, exceptions)) {
                private boolean isFirst = true;

                // replace this.onGround to EntityPlayerSPHelper.shouldReturnOnGround(this.onGround) when first access
                @Override
                public void visitFieldInsn(int opcode, String owner, String name, String desc) {
                    if (opcode != Opcodes.GETFIELD) {
                        super.visitFieldInsn(opcode, owner, name, desc);
                        return;
                    }
                    if (!selfClassName.equals(owner)) {
                        super.visitFieldInsn(opcode, owner, name, desc);
                        return;
                    }
                    if (!"onGround".equals(name) && !"field_70122_E".equals(mapSelfFieldName(name, desc))) {
                        super.visitFieldInsn(opcode, owner, name, desc);
                        return;
                    }
                    if (isFirst) {
                        isFirst = false;
                        super.visitFieldInsn(opcode, owner, name, desc);
                        visitMethodInsn(Opcodes.INVOKESTATIC, "top/fifthlight/touchcontroller/helper/EntityPlayerSPHelper", "shouldReturnOnGround", "(Z)Z", false);
                        return;
                    }
                    super.visitFieldInsn(opcode, owner, name, desc);
                }
            };
        } else {
            return super.visitMethod(access, name, desc, signature, exceptions);
        }
    }
}