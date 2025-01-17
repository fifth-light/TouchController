package top.fifthlight.touchcontroller;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import top.fifthlight.touchcontroller.transformer.EntityPlayerSPTransformer;
import top.fifthlight.touchcontroller.transformer.EntityRendererTransformer;
import top.fifthlight.touchcontroller.transformer.KeyBindingTransformer;
import top.fifthlight.touchcontroller.transformer.MouseHelperTransformer;

import java.util.function.Function;

public class TouchControllerTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        Function<ClassVisitor, ClassVisitor> visitorFactory;
        switch (transformedName) {
            case "net.minecraft.util.MouseHelper":
                visitorFactory = MouseHelperTransformer::new;
                break;
            case "net.minecraft.client.settings.KeyBinding":
                visitorFactory = KeyBindingTransformer::new;
                break;
            case "net.minecraft.client.entity.EntityPlayerSP":
                visitorFactory = EntityPlayerSPTransformer::new;
                break;
            case "net.minecraft.client.renderer.EntityRenderer":
                visitorFactory = EntityRendererTransformer::new;
                break;
            default:
                return basicClass;
        }
        ClassReader classReader = new ClassReader(basicClass);
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        ClassVisitor visitor = visitorFactory.apply(classWriter);
        classReader.accept(visitor, 0);
        return classWriter.toByteArray();
    }
}