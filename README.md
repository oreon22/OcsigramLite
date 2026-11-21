# OcsiGram

OcsiGram — основа Android-клиента Telegram на TDLib. В этой версии есть инфраструктура сборки, авторизация и минимальные экраны для чатов и настроек. Чаты, сообщения и медиа намеренно не реализованы.

## Требования

- Android Studio с SDK Platform 37.2 и Build Tools 36 или новее.
- JDK 17 (JDK 21 тоже подходит).
- Устройство или эмулятор Android 14 и новее.
- APK предназначен для ABI `arm64-v8a` и `x86_64`.
- Для сборки 16 КБ: NDK r28 или новее.

## Быстрый старт

1. Клонируйте репозиторий.
2. Скопируйте `config/tdlib.properties.example` в `config/tdlib.properties`.
3. Скопируйте `config/signing.properties.example` в `config/signing.properties`.
4. Откройте проект в Android Studio и дождитесь синхронизации Gradle.
5. Добавьте файлы TDLib в модуль `tdlib`.

До добавления Java-биндингов и `libtdjni.so` сборка преднамеренно не проходит: публичный API TDLib доступен только в реальных сгенерированных файлах.

## Что заменить самостоятельно

### Telegram api_id и api_hash

1. Откройте https://my.telegram.org.
2. Создайте приложение и получите `api_id` и `api_hash`.
3. Запишите значения в `config/tdlib.properties`.
4. Не используйте реальные значения в git.

Пример:

```properties
api_id=12345
api_hash=0123456789abcdef0123456789abcdef
```

### Release-подпись

Создайте ключ:

```bash
keytool -genkeypair -v -keystore config/ocsigram-release.jks \
  -alias ocsigram -keyalg RSA -keysize 2048 -validity 10000
```

Заполните `config/signing.properties` путём до keystore относительно корня проекта и паролями. Для CI используйте переменные окружения:

- `OCSIGRAM_STORE_FILE`
- `OCSIGRAM_STORE_PASSWORD`
- `OCSIGRAM_KEY_ALIAS`
- `OCSIGRAM_KEY_PASSWORD`

### Firebase

В консоли Firebase добавьте два Android-приложения:

- `ocsigram.lite.org`;
- `ocsigram.lite.org.beta`.

Скопируйте соответствующие файлы в:

- `app/src/prod/google-services.json`;
- `app/src/beta/google-services.json`.

Firebase отключается автоматически, если хотя бы одного файла нет.
Чтобы принудительно отключить Firebase, установите `firebaseEnabled=false` в `gradle.properties` или передайте `-PfirebaseEnabled=false`. При выключенном Firebase используются NoOp analytics/crash reporter, FCM service отключён, а Firebase Gradle-плагины не применяются. Firebase SDK остаётся зависимостью приложения, так как Firebase реализации компилируются вместе с ним.

### TDLib

Следуйте инструкциям в [tdlib/README.md](tdlib/README.md). Скачайте Java-файлы и `.so` только из своей сборки TDLib.

Для Android-релеев TDLib соберите как минимум `arm64-v8a` и `x86_64`. Экспортируйте `.so` из сборки с NDK r28 или новее для выравнивания 16 КБ.

### Данные FCM Telegram

В настройках приложения Telegram загрузите данные серверной аутентификации FCM на my.telegram.org в разделе Push Notifications/Configuring the application. Работоспособность push нужно проверять вручную на реальном устройстве с установленными Google Play services.

## Варианты сборки

Команды:

```bash
./gradlew :app:assembleProdDebug
./gradlew :app:assembleBetaDebug
./gradlew :app:assembleProdRelease
./gradlew :app:assembleBetaRelease
./gradlew :app:bundleProdRelease
./gradlew :app:testProdDebugUnitTest
./gradlew :app:lintProdDebug
```

Варианты:

- `prod`: приложение «OcsiGram», package `ocsigram.lite.org`, логи отключены.
- `beta`: приложение «OcsiGram Beta», package `ocsigram.lite.org.beta`, подробные логи и отладочные экраны.

Обе сборки могут стоять на одном телефоне одновременно.

## Структура проекта

- `:app` — Compose UI, MVVM, навигация, Hilt и слой данных.
- `:tdlib` — Java-биндинги и нативные библиотеки TDLib.
- В `app/src/prod` только prod-реализации.
- В `app/src/beta` только beta-реализации и файловые логи.

В проекте ровно два Gradle-модуля.

## Проверка 16 КБ

Для собранного APK из Android SDK Build-Tools 35 или новее:

```bash
zipalign -v -c -P 16 4 app/build/outputs/apk/prod/release/app-prod-release.apk
```

Дополнительно проверьте:

- APK Analyzer → раздел native libs: `.so` не упакованы со сжатием;
- Lint в Android Studio не должен выдавать предупреждения о несоответствии страниц памяти;
- приложение запускается на эмуляторе с образом Android 15+ с поддержкой страниц по 16 КБ.

AGP выравнивает несжатые native-библиотеки внутри APK при подходящем варианте сборки. Бинарники TDLib всё равно должны собираться с совместимым NDK.

## Дальше

- Реализовать список чатов, сообщения и медиа.
- Добавить baseline profile после стабилизации Compose-путей.
- Зашифровать базу TDLib ключом Android Keystore вместо пустого ключа.
- Проверить и обновить сигнатуры сгенерированных Java-классов TDLib после копирования реальных файлов.

Базовые ссылки:

- https://developer.android.com/guide/practices/page-sizes
- https://developer.android.com/topic/performance/baselineprofiles/overview
