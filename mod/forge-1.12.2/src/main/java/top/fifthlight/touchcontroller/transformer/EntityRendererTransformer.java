package top.fifthlight.touchcontroller.transformer;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class EntityRendererTransformer extends TouchControllerClassVisitor {
    public EntityRendererTransformer(ClassVisitor classVisitor) {
        super(Opcodes.ASM5, classVisitor);
    }

    @Override
    public String getClassName() {
        return "net.minecraft.client.renderer.EntityRenderer";
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if ("updateCameraAndRender".equals(name) || "func_181560_a".equals(mapSelfMethodName(name, desc))) {
            String entityPlayerSPName = "net.minecraft.client.entity.EntityPlayerSP";
            String unmappedEntityPlayerSPName = unmapClassName(entityPlayerSPName);
            return new MethodVisitor(Opcodes.ASM5, super.visitMethod(access, name, desc, signature, exceptions)) {
                // method: updateCameraAndRender
                // insert in every EntityPlayerSP#turn(FF)V:
                // if (!EntityRendererHelper.doDisableMouseDirection()) {
                //     this.mc.player.turn(float, float);
                // }
                @Override
                public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                    if (opcode != Opcodes.INVOKEVIRTUAL) {
                        super.visitMethodInsn(opcode, owner, name, desc, itf);
                        return;
                    }
                    if (!unmappedEntityPlayerSPName.equals(owner)) {
                        super.visitMethodInsn(opcode, owner, name, desc, itf);
                        return;
                    }
                    String mappedMethodName = mapMethodName(entityPlayerSPName, name, desc);
                    if (!"turn".equals(name) && !"func_70082_c".equals(mappedMethodName)) {
                        super.visitMethodInsn(opcode, owner, name, desc, itf);
                        return;
                    }
                    super.visitMethodInsn(Opcodes.INVOKESTATIC, "top/fifthlight/touchcontroller/helper/EntityRendererHelper", "doDisableMouseDirection", "()Z", false);
                    Label turn = new Label();
                    Label skip = new Label();
                    visitJumpInsn(Opcodes.IFEQ, turn);
                    visitInsn(Opcodes.POP);
                    visitInsn(Opcodes.POP);
                    visitInsn(Opcodes.POP);
                    visitJumpInsn(Opcodes.GOTO, skip);
                    visitLabel(turn);
                    super.visitMethodInsn(opcode, owner, name, desc, itf);
                    visitLabel(skip);
                }
            };
        } else if ("getMouseOver".equals(name) || "func_78473_a".equals(mapSelfMethodName(name, desc))) {
            String entityName = "net.minecraft.entity.Entity";
            String unmappedEntityName = unmapClassName(entityName);
            return new MethodVisitor(Opcodes.ASM5, super.visitMethod(access, name, desc, signature, exceptions)) {
                // method: getMouseOver
                // replace entity.getLook(1.0F) to EntityRendererHelper.getLook()
                // replace entity.rayTrace(DF) to EntityRendererHelper.rayTrace()
                @Override
                public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                    if (opcode != Opcodes.INVOKEVIRTUAL) {
                        super.visitMethodInsn(opcode, owner, name, desc, itf);
                        return;
                    }
                    if (!unmappedEntityName.equals(owner)) {
                        super.visitMethodInsn(opcode, owner, name, desc, itf);
                        return;
                    }
                    String mappedMethodName = mapMethodName(unmappedEntityName, name, desc);
                    if ("getLook".equals(name) || "func_70676_i".equals(mappedMethodName)) {
                        visitInsn(Opcodes.POP);
                        visitVarInsn(Opcodes.ALOAD, 0);
                        visitVarInsn(Opcodes.FLOAD, 1);
                        super.visitMethodInsn(Opcodes.INVOKESTATIC, "top/fifthlight/touchcontroller/helper/EntityRendererHelper", "getLook", "(Lnet/minecraft/entity/Entity;Lnet/minecraft/client/renderer/EntityRenderer;F)Lnet/minecraft/util/math/Vec3d;", false);
                        return;
                    } else if ("rayTrace".equals(name) || "func_174822_a".equals(mappedMethodName)) {
                        visitVarInsn(Opcodes.ALOAD, 0);
                        super.visitMethodInsn(Opcodes.INVOKESTATIC, "top/fifthlight/touchcontroller/helper/EntityRendererHelper", "rayTrace", "(Lnet/minecraft/entity/Entity;DFLnet/minecraft/client/renderer/EntityRenderer;)Lnet/minecraft/util/math/RayTraceResult;", false);
                        return;
                    }
                    super.visitMethodInsn(opcode, owner, name, desc, itf);
                }
            };
        } else if ("isDrawBlockOutline".equals(name) || "func_175070_n".equals(mapSelfMethodName(name, desc))) {
            return new MethodVisitor(Opcodes.ASM5, super.visitMethod(access, name, desc, signature, exceptions)) {
                // Add code to isDrawBlockOutline:
                // if (EntityRendererHelper.doDisableBlockOutline()) {
                //     return;
                // }
                @Override
                public void visitCode() {
                    visitMethodInsn(Opcodes.INVOKESTATIC, "top/fifthlight/touchcontroller/helper/EntityRendererHelper", "doDisableBlockOutline", "()Z", false);
                    Label skip = new Label();
                    visitJumpInsn(Opcodes.IFEQ, skip);
                    visitInsn(Opcodes.ICONST_0);
                    visitInsn(Opcodes.IRETURN);
                    visitLabel(skip);
                    super.visitCode();
                }
            };
        } else {
            return super.visitMethod(access, name, desc, signature, exceptions);
        }
    }
}