# AKREP JARVIS

AKREP JARVIS için Kotlin ve Jetpack Compose tabanlı Android uygulama temeli.

## İlk aşama

- Türkçe karanlık tema
- Animasyonlu giriş ekranı
- Ana HUD iskeleti
- Gerçek batarya, depolama, bellek ve ağ bilgileri
- Android 8.0+ hedefi
- Merkezi Gradle Version Catalog

## Derleme

Java 17 veya Java 21 ve Android SDK (API 35) gereklidir.

```bash
./gradlew :app:assembleDebug
```

## GitHub Actions APK çıktısı

`main` dalına yapılan gönderimler ve elle başlatılan workflow çalıştırmaları
`AKREP-JARVIS-debug-apk` adıyla derlenen APK dosyasını workflow Artifacts bölümünde
sunar.
