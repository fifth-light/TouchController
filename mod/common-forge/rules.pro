-dontobfuscate
-dontoptimize

-dontwarn android.annotation.*

-keep class top.fifthlight.touchcontroller.mixin.* { *; }
-keep class top.fifthlight.touchcontroller.ForgeGuiFactoryImpl
-keep class top.fifthlight.touchcontroller.TouchControllerCorePlugin
-keep class top.fifthlight.touchcontroller.TouchControllerTransformer { *; }
-keep class top.fifthlight.combine.platform.CombineScreen { *; }
-keep class top.fifthlight.touchcontroller.helper.* { *; }
-keep @net.minecraftforge.fml.common.Mod class *
-keepclassmembers class * {
    @net.minecraftforge.fml.common.Mod$EventHandler *;
    @net.minecraftforge.eventbus.api.SubscribeEvent *;
    @net.minecraftforge.fml.common.eventhandler.SubscribeEvent *;
}
-keep class top.fifthlight.touchcontroller.platform.win32.Interface { *; }
-keep class top.fifthlight.touchcontroller.platform.android.Transport { *; }

-keeppackagenames top.fifthlight.touchcontroller.*
-keeppackagenames top.fifthlight.combine.*
-repackageclasses top.fifthlight.touchcontroller.relocated

-allowaccessmodification

-keepattributes Signature,Exceptions,*Annotation*,InnerClasses,PermittedSubclasses,EnclosingMethod,Deprecated,SourceFile,LineNumberTable
