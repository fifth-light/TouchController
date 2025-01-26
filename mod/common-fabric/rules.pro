-dontobfuscate
-dontoptimize

-dontwarn android.annotation.*

-keep class top.fifthlight.touchcontroller.TouchController
-keep class top.fifthlight.touchcontroller.TouchControllerModMenuApiImpl
-keep class top.fifthlight.touchcontroller.mixin.* { *; }
-keep class top.fifthlight.combine.platform.CombineScreen { *; }
-keep class top.fifthlight.touchcontroller.platform.win32.Interface { *; }
-keep class top.fifthlight.touchcontroller.platform.android.Transport { *; }

-keeppackagenames top.fifthlight.touchcontroller.**
-keeppackagenames top.fifthlight.combine.**
-repackageclasses top.fifthlight.touchcontroller.relocated

-allowaccessmodification

-keepattributes Signature,Exceptions,*Annotation*,InnerClasses,PermittedSubclasses,EnclosingMethod,Deprecated,SourceFile,LineNumberTable
