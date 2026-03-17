# Audio Master - Build Instructions

Since the current environment doesn't have the full Android SDK installed, you can compile this project using one of the following methods:

## Method 1: Using Android Studio (PC)
1. Open Android Studio.
2. Select **File > Open** and point to this directory: `/home/kyytech/Downloads/module/apk`.
3. Wait for Gradle sync to finish.
4. Click **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
5. The APK will be in `app/build/outputs/apk/debug/app-debug.apk`.

## Method 2: Using AIDE (Android App)
1. Copy this entire folder to your phone's storage.
2. Open **AIDE**.
3. Point AIDE to this folder.
4. AIDE will automatically detect it as an Android project.
5. Tap **Run** to compile and install.

## Method 3: Using Termux (Advanced)
If you have Termux installed on your phone:
1. Install OpenJDK 17: `pkg install openjdk-17`
2. Grant storage access: `termux-setup-storage`
3. Navigate to this folder.
4. Run: `./gradlew assembleDebug`

### IMPORTANT: Root Access Required
This app uses `su` commands to read `/proc/asound`. Make sure your phone is rooted with Magisk/KernelSU for accurate results.
