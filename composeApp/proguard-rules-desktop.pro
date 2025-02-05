#
# optimize
#
-dontoptimize

-keepattributes InnerClasses

#
# don't use this: KEEP ALL THE NAMES
#
#-keepnames class ** { *; }
#-keepnames class pt.demanda.quiz.** { *; }

#
# Classes that use @Serialize
#
-keepnames class **$$serializer { *; }

#
# ktor
#
-keepnames class io.ktor.** { *; }
-keepnames class okhttp3.** { *; }

#
# Room
#
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**
-keepnames class androidx.sqlite.** { *; }


#
# debug proguard
#
#-dump build/compose/binaries/class_files.txt
#-printseeds build/compose/binaries/seeds.txt
#-printusage build/compose/binaries/unused.txt
#-printmapping build/compose/binaries/mapping.txt
#
## ./compose/binaries/main-release/app/Quiz.app/Contents/MacOS/Quiz 2>> stack.txt
## ~/Library/Android/sdk/tools/proguard/bin/retrace.sh ./compose/binaries/mapping.txt stack.txt