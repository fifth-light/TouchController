package top.fifthlight.touchcontroller.transformer;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class GuiIngameTransformer extends TouchControllerClassVisitor {
    public GuiIngameTransformer(ClassVisitor classVisitor) {
        super(Opcodes.ASM5, classVisitor);
    }

    @Override
    public String getClassName() {
        return "net.minecraft.client.gui.GuiIngame";
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if ("renderHotbar".equals(name) || "func_180479_a".equals(mapSelfMethodName(name, desc))) {
            String renderHelperName = "net.minecraft.client.renderer.RenderHelper";
            String unmappedRenderHelperName = unmapClassName(renderHelperName);
            return new MethodVisitor(Opcodes.ASM5, super.visitMethod(access, name, desc, signature, exceptions)) {
                // Call GuiIngameHelper.renderHotbar before every RenderHelper.enableGUIStandardItemLighting();
                @Override
                public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                    if (opcode != Opcodes.INVOKESTATIC) {
                        super.visitMethodInsn(opcode, owner, name, desc, itf);
                        return;
                    }
                    if (!unmappedRenderHelperName.equals(owner)) {
                        super.visitMethodInsn(opcode, owner, name, desc, itf);
                        return;
                    }
                    String mappedMethodName = mapMethodName(renderHelperName, name, desc);
                    if (!"enableGUIStandardItemLighting".equals(name) && !"func_74520_c".equals(mappedMethodName)) {
                        super.visitMethodInsn(opcode, owner, name, desc, itf);
                        return;
                    }
                    visitVarInsn(Opcodes.ALOAD, 1);
                    super.visitMethodInsn(Opcodes.INVOKESTATIC, "top/fifthlight/touchcontroller/helper/GuiIngameHelper", "renderHotbar", "(Lnet/minecraft/client/gui/ScaledResolution;)V", false);
                    super.visitMethodInsn(opcode, owner, name, desc, itf);
                }
            };
        } else if ("renderAttackIndicator".equals(name) || "func_184045_a".equals(mapSelfMethodName(name, desc))) {
            String selfClassName = unmapClassName(getClassName());
            // Redirect all drawTexturedModalRectWrapper to GuiIngameHelper.drawTexturedModalRectWrapper
            return new MethodVisitor(Opcodes.ASM5, super.visitMethod(access, name, desc, signature, exceptions)) {
                private int times = 0;
                @Override
                public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                    if (opcode != Opcodes.INVOKEVIRTUAL) {
                        super.visitMethodInsn(opcode, owner, name, desc, itf);
                        return;
                    }
                    if (!selfClassName.equals(owner)) {
                        super.visitMethodInsn(opcode, owner, name, desc, itf);
                        return;
                    }
                    String mappedMethodName = mapSelfMethodName(name, desc);
                    if (!"drawTexturedModalRectWrapper".equals(name) && !"func_73729_b".equals(mappedMethodName)) {
                        super.visitMethodInsn(opcode, owner, name, desc, itf);
                        return;
                    }
                    switch (times) {
                        case 0:
                            visitInsn(Opcodes.ICONST_0);
                            break;
                        case 1:
                            visitInsn(Opcodes.ICONST_1);
                            break;
                        case 2:
                            visitInsn(Opcodes.ICONST_2);
                            break;
                        case 3:
                            visitInsn(Opcodes.ICONST_3);
                            break;
                        case 4:
                            visitInsn(Opcodes.ICONST_4);
                            break;
                        case 5:
                            visitInsn(Opcodes.ICONST_5);
                            break;
                        default:
                            visitLdcInsn(times);
                            break;
                    }
                    times++;
                    super.visitMethodInsn(Opcodes.INVOKESTATIC, "top/fifthlight/touchcontroller/helper/GuiIngameHelper", "drawTexturedModalRectWrapper", "(Lnet/minecraft/client/gui/GuiIngame;IIIIIII)V", false);
                }
            };
        } else {
            return super.visitMethod(access, name, desc, signature, exceptions);
        }
    }
}
