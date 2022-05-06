# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class MID to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file MID.
#-renamesourcefileattribute SourceFile

-optimizationpasses 5

-dontusemixedcaseclassnames

-dontskipnonpubliclibraryclasses

-dontoptimize

-dontpreverify

-verbose

-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-keepattributes *Annotation*

-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.preference.Preference


-keep class com.google.android.material.* {*;}
-keep class androidx.* {*;}
-keep public class * extends androidx.*
-keep interface androidx.* {*;}
-dontwarn com.google.android.material.**
-dontnote com.google.android.material.**
-dontwarn androidx.**


-ignorewarnings

-dump class_files.txt
-printseeds seeds.txt
-printusage unused.txt
-printmapping mapping.txt


-dontwarn com.google.**
-keep class com.google.protobuf.* {*;}

-keep class com.star.theBigDipper.ui.activity.* {*;}
-keep class com.star.theBigDipper.model.* {*;}
-keep class com.star.theBigDipper.util.Utils {*;}
-keep class com.star.theBigDipper.util.AccountUtils {*;}
-keep class com.star.theBigDipper.service.* {*;}
-keep class com.star.theBigDipper.widget.navigator.* {*;}
-keep class com.star.theBigDipper.Constants {*;}




-keep public class * extends android.view.View {
      public <init>(android.content.Context);
      public <init>(android.content.Context, android.util.AttributeSet);
      public <init>(android.content.Context, android.util.AttributeSet, int);
      public void set*(...);
  }

-keepclasseswithmembernames class * {
      native <methods>;
 }

-keepclasseswithmembers class * {
      public <init>(android.content.Context, android.util.AttributeSet);
}

   -keepclassmembers class * extends android.app.Activity {
      public void *(android.view.View);
}

-keep class * implements android.os.Parcelable {
   public static final android.os.Parcelable$Creator *;
}

-keepnames class * implements java.io.Serializable

-keepclassmembers class * implements java.io.Serializable {
        static final long serialVersionUID;
        private static final java.io.ObjectStreamField[] serialPersistentFields;
        !static !transient <fields>;
        !private <fields>;
        !private <methods>;
        private void writeObject(java.io.ObjectOutputStream);
        private void readObject(java.io.ObjectInputStream);
        java.lang.Object writeReplace();
        java.lang.Object readResolve();
    }

-keepclassmembers enum * {
      public static **[] values();
      public static ** valueOf(java.lang.String);
    }

-keepclassmembers class * {
        public void *ButtonClicked(android.view.View);
    }

-keepclassmembers class **.R$* {
        public static <fields>;
    }

    -keep class com.google.zxing.* { *; }
    -keep class com.google.zxing.*


#EventBus
-keepattributes *Annotation*
-keepclassmembers class * {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }


-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.*{*;}

-keep class com.kongzue.dialog.* { *; }
-dontwarn com.kongzue.dialog.**

# 额外的，建议将 android.view 也列入 keep 范围：
-keep class android.view.* { *; }

# AndroidX版本请使用如下配置：
-dontwarn androidx.renderscript.**
-keep public class androidx.renderscript.* { *; }
# Gson
-keepattributes  *Annotation*
-keep class com.google.gson.stream.* { *;}