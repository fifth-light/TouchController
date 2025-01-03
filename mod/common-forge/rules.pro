-dontobfuscate

-keep class top.fifthlight.touchcontroller.mixin.* { *; }
-keep @net.minecraftforge.fml.common.Mod class *
-keepclassmembers class * {
    @net.minecraftforge.eventbus.api.SubscribeEvent *;
}
-keep class top.fifthlight.touchcontroller.platform.win32.Interface { *; }
-keep class top.fifthlight.touchcontroller.platform.android.Transport { *; }

-keeppackagenames top.fifthlight.touchcontroller.*
-keeppackagenames top.fifthlight.combine.*
-repackageclasses top.fifthlight.touchcontroller.relocated

-allowaccessmodification

-keepattributes Signature,Exceptions,*Annotation*,InnerClasses,PermittedSubclasses,EnclosingMethod,Deprecated,SourceFile,LineNumberTable
