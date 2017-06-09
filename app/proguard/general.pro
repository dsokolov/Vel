#-dontpreverify
#-verbose
#-dontobfuscate

#-keepclassmembers class * implements JsonParseable {
#    public ** create;
#}
#-keepclassmembers class * extends SQLiteOpenHelper {
#    *;
#}
#
## Preserve static fields of inner classes of R classes that might be accessed
## through introspection.
#-keepclassmembers class **.R$* {
#  public static <fields>;
#}
#
## Preserve the special static methods that are required in all enumeration classes.
#-keepclassmembers enum * {
#    public static **[] values();
#    public static ** valueOf(java.lang.String);
#}
#
#-keep public class * {
#    public protected *;
#}
#
#-keep class * implements android.os.Parcelable {
#  public static final android.os.Parcelable$Creator *;
#}


## Explicitly preserve all serialization members. The Serializable interface
## is only a marker interface, so it wouldn't save them.
#-keepclassmembers class * implements java.io.Serializable {
#    static final long serialVersionUID;
#    private static final java.io.ObjectStreamField[] serialPersistentFields;
#    private void writeObject(java.io.ObjectOutputStream);
#    private void readObject(java.io.ObjectInputStream);
#    java.lang.Object writeReplace();
#    java.lang.Object readResolve();
#}

#
##To remove debug logs:

-assumenosideeffects class android.util.Log {
    public static *** i(...);
    public static *** d(...);
    public static *** v(...);
}

-keepclasseswithmembernames,includedescriptorclasses class * {
    native <methods>;
}

-repackageclasses

-allowaccessmodification

-optimizations !code/allocation/variable