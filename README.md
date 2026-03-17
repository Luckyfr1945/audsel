# AudSel (Audio Selector Pro)

![App Version](https://img.shields.io/badge/App_Version-v2.0-gold.svg)
![Build](https://img.shields.io/badge/Build-Gradle_Successful-green.svg)
![License](https://img.shields.io/badge/License-MIT-blue.svg)

**AudSel** adalah aplikasi pendamping (companion app) resmi untuk modul Magisk **AudSel Pro / KYY Audio**. Aplikasi ini memungkinkan pengguna untuk melakukan kontrol real-time terhadap engine audio pada perangkat Android yang sudah di-root (khususnya Redmi 10).

## 📸 Tampilan Utama
Aplikasi menggunakan desain **Premium Dark Glassmorphism** dengan indikator real-time yang memantau status hardware audio Anda secara langsung.

## ✨ Fitur Unggulan
- **Real-time Monitoring**: Memantau Sample Rate (kHz) dan Audio Buffer langsung dari kernel.
- **Mode Switching**: Berpindah antar profil audio (Normal, Bass, Gaming, Hi-Fi, Cinema) dalam satu ketukan.
- **Dynamic Feedback**: UI yang berubah warna sesuai dengan mode yang aktif.
- **Auto-Detection**: Mendeteksi secara akurat jenis perangkat yang terhubung (Speaker, IEM/Headset, TWS, USB DAC).
- **Direct Update**: Jika modul belum terpasang, aplikasi menyediakan link unduhan langsung ke repositori resmi.

## 🛠️ Persyaratan
- Perangkat Android (Minimal SDK 24 / Android 7.0+).
- **Akses Root** (Magisk atau KernelSU).
- Terpasang [AudSel Magisk Module](https://github.com/Luckyfr1945/module-audio).

## 🚀 Cara Build (Developer)
Jika Anda ingin melakukan build sendiri:
1. Clone repositori ini.
2. Buka di Android Studio.
3. Pastikan `libsu` terintegrasi dengan benar.
4. Jalankan `./gradlew assembleDebug`.

## 📂 Struktur Project
- `MainActivity.kt`: Logika utama komunikasi dengan backend shell (`action.sh`).
- `activity_main.xml`: Layout modern dengan sistem card-based UI.
- `audio_tweaks.sh`: (Melalui modul) Berisi logic hardware overrides.

---
**Maintained by**: Luckyfr1945  
**Powered by**: Antigravity x KYY Tech
