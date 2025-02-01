package top.fifthlight.touchcontroller.transformer;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class KeyBindingTransformer extends TouchControllerClassVisitor {
    public KeyBindingTransformer(ClassVisitor classVisitor) {
        super(Opcodes.ASM5, classVisitor);
    }

    @Override
    public String getClassName() {
        return "net.minecraft.client.settings.KeyBinding";
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if ("isKeyDown".equals(name) || "func_151470_d".equals(mapSelfMethodName(name, desc))) {
            return new MethodVisitor(Opcodes.ASM5, super.visitMethod(access, name, desc, signature, exceptions)) {
                private boolean isFirst = true;

                // Insert code before the return:
                // <return value> = KeyBindingHelper.isPressed(this, <return value>);
                @Override
                public void visitInsn(int opcode) {
                    if (opcode == Opcodes.IRETURN && isFirst) {
                        isFirst = false;
                        visitVarInsn(Opcodes.ALOAD, 0);
                        visitMethodInsn(Opcodes.INVOKESTATIC, "top/fifthlight/touchcontroller/helper/KeyBindingHelper", "isKeyDown", "(ZLnet/minecraft/client/settings/KeyBinding;)Z", false);
                    }
                    super.visitInsn(opcode);
                }
            };
        } else if ("setKeyBindState".equals(name) || "func_74510_a".equals(mapSelfMethodName(name, desc))) {
            String selfClassName = unmapClassName(getClassName());
            return new MethodVisitor(Opcodes.ASM5, super.visitMethod(access, name, desc, signature, exceptions)) {
                // add code around keybinding.pressed = pressed:
                // if (KeyBindingHelper.shouldSendKey(keybinding, pressed) {
                //     keybinding.pressed = pressed; // Original
                // }
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
                    if (!"pressed".equals(name) && !"field_74513_e".equals(mapSelfFieldName(name, desc))) {
                        super.visitFieldInsn(opcode, owner, name, desc);
                        return;
                    }
                    visitInsn(Opcodes.POP);
                    super.visitMethodInsn(Opcodes.INVOKESTATIC, "top/fifthlight/touchcontroller/helper/KeyBindingHelper", "doDisableKey", "(Lnet/minecraft/client/settings/KeyBinding;)Z", false);
                    Label skip = new Label();
                    visitJumpInsn(Opcodes.IFNE, skip);
                    visitVarInsn(Opcodes.ALOAD, 3);
                    visitVarInsn(Opcodes.ILOAD, 1);
                    super.visitFieldInsn(opcode, owner, name, desc);
                    visitLabel(skip);
                }
            };
        } else if ("onTick".equals(name) || "func_74507_a".equals(mapSelfMethodName(name, desc))) {
            String selfClassName = unmapClassName(getClassName());
            return new MethodVisitor(Opcodes.ASM5, super.visitMethod(access, name, desc, signature, exceptions)) {
                // change code around ++keybinding.pressTime:
                //
                // int times = keybinding.pressTime + 1; // Original
                // if (KeyBindingHelper.doDisableKey(keybinding) {
                //     keybinding.pressTime = times;
                // }
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
                    if (!"pressTime".equals(name) && !"field_151474_i".equals(mapSelfFieldName(name, desc))) {
                        super.visitFieldInsn(opcode, owner, name, desc);
                        return;
                    }
                    visitVarInsn(Opcodes.ALOAD, 1);
                    super.visitMethodInsn(Opcodes.INVOKESTATIC, "top/fifthlight/touchcontroller/helper/KeyBindingHelper", "doDisableKey", "(Lnet/minecraft/client/settings/KeyBinding;)Z", false);
                    Label end = new Label();
                    Label clean = new Label();
                    visitJumpInsn(Opcodes.IFNE, clean);
                    super.visitFieldInsn(opcode, owner, name, desc);
                    visitJumpInsn(Opcodes.GOTO, end);
                    visitLabel(clean);
                    visitInsn(Opcodes.POP);
                    visitInsn(Opcodes.POP);
                    visitLabel(end);
                }
            };
        } else {
            return super.visitMethod(access, name, desc, signature, exceptions);
        }
    }
}