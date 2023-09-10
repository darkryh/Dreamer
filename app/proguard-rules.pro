# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview** {
#  *;
#}

-keep class com.ead.project.dreamer.data.utils.media.CastOptionsProvider
-keep class com.ead.project.dreamer.app.** { *; }
-keep class com.ead.project.dreamer.data.utils.** { *; }
-keep class com.ead.project.dreamer.app.model.** { *; }
-keep class com.ead.project.dreamer.data.models.** { *; }
-keep class com.ead.project.dreamer.data.database.model.** { *; }
-keep class com.ead.project.dreamer.data.models.discord.** { *; }
-keep class com.ead.project.dreamer.data.retrofit.** { *; }

-keep class retrofit2.** { *; }
-keepattributes *Annotation*
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# When I remove this, java.security.cert.CertificateException: X.509 not found is thrown
-keep class java.security.cert.CertificateException.** { *;}

# I got the missing classes from missing_rules.txt and added the package names that created the problem here:
-keep class io.netty.** { *;}

-dontwarn com.aayushatharva.**
-dontwarn com.github.luben.**
-dontwarn com.google.protobuf.**
-dontwarn com.jcraft.jzlib.**
-dontwarn org.conscrypt.**
-dontwarn org.bouncycastle.**
-dontwarn org.openjsse.**
-dontwarn com.ning.compress.**
-dontwarn com.oracle.svm.core.annotate.**
-dontwarn io.netty.internal.tcnative.**
-dontwarn java.lang.management.**
-dontwarn lzma.sdk.**
-dontwarn net.jpountz.lz4.**
-dontwarn net.jpountz.xxhash.**
-dontwarn org.apache.log4j.**
-dontwarn org.apache.logging.log4j.**
-dontwarn org.eclipse.jetty.npn.**
-dontwarn org.jboss.marshalling.**
-dontwarn org.slf4j.impl.**
-dontwarn reactor.blockhound.**
-dontwarn sun.security.x509.**


# Gson uses generic type information stored in a class file when working with
# fields. Proguard removes such information by default, keep it.
-keepattributes Signature

# This is also needed for R8 in compat mode since multiple
# optimizations will remove the generic signature such as class
# merging and argument removal. See:
# https://r8.googlesource.com/r8/+/refs/heads/main/compatibility-faq.md#troubleshooting-gson-gson
-keep class com.google.gson.reflect.TypeToken { *; }
-keep class * extends com.google.gson.reflect.TypeToken

# Optional. For using GSON @Expose annotation
-keepattributes AnnotationDefault,RuntimeVisibleAnnotations

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# This is generated automatically by the Android Gradle plugin.