
# PayTrack (Android, Kotlin + Jetpack Compose + Room)

Features:
- Enter vendor, amount, **debit/credit**, date, transaction ID, reason, and screenshot (Android Photo Picker).
- Search by vendor, min/max amount, and date presets (**last month**, **last year**, **custom**).
- Reports: debit/credit totals per **month**, per **year**.
- **Export to PDF** for any filtered list and share it.

## Build steps (Android Studio Hedgehog/Koala, AGP 8.5+)
1. Open Android Studio → **Open** → select this folder.
2. Let Gradle sync.
3. (Optional) Set your **applicationId** in `app/build.gradle.kts`.
4. **Build > Build Bundle(s)/APK(s) > Build APK(s)**. The debug APK will be in `app/build/outputs/apk/debug/`.
5. Install the APK on your device or run on an emulator.

Notes:
- Min SDK 26, Target 34.
- Uses AndroidX Photo Picker (no storage permission required).
- PDF files are written to app cache and shared using `FileProvider`.
