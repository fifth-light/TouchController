package top.fifthlight.touchcontroller.transformer;

import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import org.objectweb.asm.ClassVisitor;

public abstract class TouchControllerClassVisitor extends ClassVisitor {
    public TouchControllerClassVisitor(int api, ClassVisitor visitor) {
        super(api, visitor);
    }

    public abstract String getClassName();

    public String unmapClassName(String typeName) {
        return FMLDeobfuscatingRemapper.INSTANCE.unmap(typeName.replace('.', '/')).replace('/', '.');
    }

    public String mapMethodName(String typeName, String methodName, String desc) {
        return FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(unmapClassName(typeName), methodName, desc);
    }

    public String mapSelfMethodName(String methodName, String desc) {
        return FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(unmapClassName(getClassName()), methodName, desc);
    }

    public String mapFieldName(String typeName, String fieldName, String desc) {
        return FMLDeobfuscatingRemapper.INSTANCE.mapFieldName(unmapClassName(typeName), fieldName, desc);
    }

    public String mapSelfFieldName(String fieldName, String desc) {
        return FMLDeobfuscatingRemapper.INSTANCE.mapFieldName(unmapClassName(getClassName()), fieldName, desc);
    }
}
