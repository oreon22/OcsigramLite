# Справочник по файлам OcsiGram

Этот файл описывает назначение файлов проекта и действия, которые нужно выполнить вручную.

## Корневые файлы

### `settings.gradle.kts`

Регистрирует ровно два модуля: `:app` и `:tdlib`. Репозитории зависимостей находятся только здесь. Не добавляйте другие модули и не переносите сюда зависимости приложения.

### `build.gradle.kts`

Подключает версии Gradle-плагинов проекта: Android, Compose, сериализация, KSP, Hilt и Firebase. Не меняйте плагин `org.jetbrains.kotlin.android`: в AGP 9 Kotlin встроен.

### `gradle.properties`

Содержит общие настройки Gradle, AndroidX и две переменные версии приложения:

- `appVersionName` — версия для `BuildConfig.VERSION_NAME`;
- `appVersionCode` — код версии приложения.

Меняйте эти значения перед релизом.

### `gradle/libs.versions.toml`

Единое место для версий и координат зависимостей. Не добавляйте сюда зависимости, которых нет в техническом задании. Применяемые библиотеки разделены на плагины и зависимости.

### `gradle/wrapper/`

Содержит файлы Gradle Wrapper. Для сборки используется Gradle 9.6.0.

Исполняемые файлы проекта:

- `gradlew` — Linux и macOS;
- `gradlew.bat` — Windows.

### `.gitignore`

Запрещает попадание в Git:

- реального `config/tdlib.properties`;
- реального `config/signing.properties`;
- файлов `*.jks` и `*.keystore`;
- реальных `app/src/prod/google-services.json`;
- реальных `app/src/beta/google-services.json`;
- локальной папки `.gradle`, локального SDK и IDE-файлов.

Не удаляйте эти правила.

### `.editorconfig`

Устанавливает единый UTF-8 формат исходников и отступы в четыре пробела.

### `.github/workflows/build.yml`

CI-сборка GitHub Actions.

Она запускает:

- `:app:assembleProdDebug`;
- `:app:assembleBetaDebug`;
- `:app:testProdDebugUnitTest`;
- `:app:lintProdDebug`.

Перед сборкой CI копирует примеры конфигурационных файлов, поэтому пока отсутствуют реальные TDLib-файлы сборка остановится на этом шаге.

## Конфигурационные файлы

### `config/tdlib.properties.example`

Пример файла с фиктивным Telegram API ID и API Hash:

```properties
api_id=12345
api_hash=0123456789abcdef0123456789abcdef
```

Файл остаётся в Git намеренно.

**Что выполнить:**

1. Откройте https://my.telegram.org.
2. Создайте приложение Telegram.
3. Скопируйте файл:

```bash
cp config/tdlib.properties.example config/tdlib.properties
```

4. В `config/tdlib.properties` замените:
   - `api_id` на свой числовой Telegram API ID;
   - `api_hash` на свой Telegram API Hash.

Реальный файл не добавляйте в Git. Gradle берёт реальный файл в первую очередь, а пример — только для сборки без настройки.

### `config/signing.properties.example`

Пример настроек подписи release-сборки.

**Что выполнить:**

1. Создайте keystore:

```bash
keytool -genkeypair -v \
  -keystore config/ocsigram-release.jks \
  -alias ocsigram \
  -keyalg RSA \
  -keysize 2048 \
  -validity 10000
```

2. Скопируйте пример:

```bash
cp config/signing.properties.example config/signing.properties
```

3. В `config/signing.properties` замените:
   - `storeFile` — путь до keystore;
   - `storePassword` — пароль keystore;
   - `keyAlias` — имя ключа, например `ocsigram`;
   - `keyPassword` — пароль ключа.

Можно не создавать файл, а задать переменные окружения для CI:

- `OCSIGRAM_STORE_FILE`;
- `OCSIGRAM_STORE_PASSWORD`;
- `OCSIGRAM_KEY_ALIAS`;
- `OCSIGRAM_KEY_PASSWORD`.

Если ни локальный файл, ни переменные не настроены, release подписывается debug-ключом с предупреждением в Gradle.

Не записывайте настоящие пароли в пример и не коммитьте keystore.

## Модуль `tdlib`

### `tdlib/build.gradle.kts`

Gradle-модуль библиотеки. Namespace: `ocsigram.lite.org.tdlib`.

Он хранит только Java-биндинги и native-библиотеки TDLib. Здесь не должно быть Maven-зависимости TDLib.

### `tdlib/consumer-rules.pro`

Правило R8, которое сохраняет классы `org.drinkless.tdlib.*`.

### `tdlib/README.md`

Инструкция по сборке TDLib из исходников через Docker и размещению полученных файлов.

**Что выполнить:**

1. Соберите TDLib из стабильного коммитa в своём форке:

```bash
docker build \
  --build-arg COMMIT_HASH=<hash коммита TDLib> \
  --build-arg ANDROID_NDK_VERSION=r28 \
  --output tdlib .
```

2. Скопируйте сгенерированные файлы из `tdlib/java/` в:

```text
tdlib/src/main/java/org/drinkless/tdlib/
```

Нужны не изменённые вручную копии:

- `Client.java`;
- `TdApi.java`.

3. Скопируйте native-библиотеки:

- `tdlib/libs/arm64-v8a/libtdjni.so` → `tdlib/src/main/jniLibs/arm64-v8a/libtdjni.so`;
- `tdlib/libs/x86_64/libtdjni.so` → `tdlib/src/main/jniLibs/x86_64/libtdjni.so`.

4. Запишите реальный commit в поле `Собрано из коммита` в `tdlib/README.md`.

Используйте ABI только `arm64-v8a` и `x86_64`. Бинарник должен быть собран с NDK r28 или новее и выровнен для страниц памяти 16 КБ.

## Модуль `app`

### `app/build.gradle.kts`

Основная конфигурация Android-приложения.

Здесь настроены:

- namespace `ocsigram.lite.org`;
- `minSdk = 34`;
- `targetSdk = 35`;
- `compileSdk = 37`;
- applicationId для `prod` и суффикс `.beta` для беты;
- release-подпись;
- build types;
- ABI;
- Compose и зависимости;
- чтение TDLib API ID и API Hash;
- условное применение Firebase-плагинов.

Меняйте только разумные параметры релиза и версии. Не меняйте applicationId без согласования с Telegram/Firebase.

### `app/proguard-rules.pro`

Правила R8 для release-сборки. Содержит сохранение Java-биндингов TDLib. Не удаляйте keep-правила для `org.drinkless.tdlib`.

## Манифест и ресурсы приложения

### `app/src/main/AndroidManifest.xml`

Содержит:

- разрешения INTERNET, сетевого состояния и уведомлений;
- имя приложения и Application-класс;
- MainActivity;
- FCM-сервис с `exported=false`;
- FileProvider для обмена файлами в бете;
- запрет резервного копирования через настройки приложения.

FCM-сервис менять не требуется до проверки реальных сигнатур TDLib.

### `app/src/main/res/xml/data_extraction_rules.xml`

Исключает каталог TDLib из Android backup и device transfer.

### `app/src/main/res/xml/file_paths.xml`

Разрешает beta-экранам отдавать через FileProvider только папку `logs`.

### `app/src/main/res/values/`

Русские строки приложения по умолчанию.

Все видимые пользователю строки должны находиться здесь, а не быть прописаны в Kotlin-коде.

### `app/src/main/res/values-en/`

Английские строки приложения.

При добавлении строки в русскую папку добавьте её и в английскую.

### Иконки приложения

- `app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml` — базовая иконка;
- `app/src/prod/res/mipmap-anydpi-v26/ic_launcher.xml` — prod-иконка;
- `app/src/prod/res/drawable/ic_launcher_foreground.xml` — foreground prod-иконки;
- `app/src/beta/res/mipmap-anydpi-v26/ic_launcher.xml` — beta-иконка;
- `app/src/beta/res/drawable/ic_launcher_foreground.xml` — foreground beta-иконки.

Изображения в prod и beta отличаются по цвету.

## Kotlin-код приложения

### `app/src/main/kotlin/ocsigram/lite/org/OcsiGramApp.kt`

Application-класс Hilt. Создаёт notification channel и запускает flavor-specific настройку. В beta включает StrictMode.

### `app/src/main/kotlin/ocsigram/lite/org/MainActivity.kt`

Главная Activity. Запускает SplashScreen, edge-to-edge, Compose-тему и корневую навигацию.

### `core/tdlib/`

- `TdGateway` — интерфейс для TDLib и тестов;
- `TdClient` — singleton-клиент TDLib;
- `TdException` — ошибка с кодом и текстом TDLib;
- `TdLogConfigurator` — интерфейс настройки логирования.

**Что проверить после добавления TDLib:**

- сигнатуры `Client.create`;
- сигнатуру `Client.send`;
- сигнатуру `Client.execute`;
- названия классов состояний авторизации;
- названия и поля `TdlibParameters`;
- сигнатуры логирования;
- названия классов для DeviceToken;
- сигнатуры ProcessPushNotification.

Все несоответствия исправляйте по реальным `Client.java` и `TdApi.java`, а не по предположению.

### `core/analytics/`

Интерфейсы аналитики и Crashlytics и две реализации:

- Firebase;
- `NoOp`, когда Firebase не настроен.

Не передавайте в аналитику номера телефонов, имена, id пользователей/чатов или текст сообщений.

### `core/push/OcsiFirebaseMessagingService.kt`

Скелет FCM-сервиса.

Нужно проверить реальный TDLib API и завершить вызовы:

- регистрации FCM-токена через `RegisterDevice`;
- обработки push через `ProcessPushNotification`.

Разрешение уведомлений запрашивается после успешного входа, не при старте приложения.

### `core/flavor/`

Интерфейс различий prod и beta-сборок. Никакой beta-код не должен попадать в prod. Реализации находятся в:

- `app/src/prod/kotlin/.../core/flavor/FlavorFeatures.kt`;
- `app/src/beta/kotlin/.../core/flavor/FlavorFeatures.kt`.

### `core/di/`

Hilt-модули:

- общий модуль приложения;
- привязка TDLib-клиента;
- flavor-модуль, выбранный исходниками конкретной сборки.

### `data/auth/AuthState.kt`

Состояния авторизации, включая телефоном, код, пароль, регистрацию, ready и unsupported-состояния.

### `data/auth/AuthRepository.kt`

Преобразует действия UI в методы TDLib и делает понятные русские сообщения об ошибках.

Форматирование парсит коды вроде:

- `PHONE_NUMBER_INVALID`;
- `PHONE_CODE_INVALID`;
- `PHONE_CODE_EXPIRED`;
- `PASSWORD_HASH_INVALID`;
- `FLOOD_WAIT_X`.

### `data/settings/SettingsRepository.kt`

Хранит тему и включение динамических цветов в DataStore.

Без дополнительных действий настройки сохраняются автоматически.

### `feature/auth/`

Экран и ViewModel авторизации.

Поле ввода меняется в зависимости от `AuthState`. Кнопки блокируются во время запроса.

### `feature/home/`

Пустой экран-заглушка с TopAppBar и пустым состоянием.

Список чатов здесь намеренно не реализован.

### `feature/settings/`

Экран темы, динамических цветов, информации о приложении и beta-дополнений.

Beta-экран использует:

- `BetaLogsScreen.kt`;
- `BetaDebugScreen.kt`;
- FileLoggingTree.

### `navigation/AppNavigation.kt`

Единая навигация приложения. При смене AuthState меняется корневой экран:

- Loading → splash и авторизация;
- неготовая авторизация → Auth;
- Ready → Home.

Сейчас использована типобезопасная Navigation Compose, если стабильная Navigation 3 не была совместима с выбранным toolchain.

### `ui/theme/Theme.kt`

Material 3 тема. Поддерживает системную, светлую и тёмную темы и Material You, если включён Android 12+.

## Firebase-файлы

### `app/src/prod/google-services.json.example`

Пример Firebase-конфигурации для package `ocsigram.lite.org`.

### `app/src/beta/google-services.json.example`

Пример Firebase-конфигурации для package `ocsigram.lite.org.beta`.

**Что выполнить:**

1. В консоли Firebase создайте Android-приложение для `ocsigram.lite.org`.
2. Скачайте его `google-services.json`.
3. Положите файл в `app/src/prod/google-services.json`.
4. Создайте отдельное приложение Firebase для `ocsigram.lite.org.beta`.
5. Положите его JSON в `app/src/beta/google-services.json`.

Оба реальных JSON-файла игнорируются Git. Если хотя бы одного файла нет, Google Services и Crashlytics-плагины не применяются.

### Серверная аутентификация FCM

Для работающих push-уведомлений в настройках приложения Telegram на my.telegram.org загрузите данные серверной аутентификации Firebase Cloud Messaging согласно документации Telegram Push Updates.

Push нужно вручную проверить на реальном устройстве с Google Play services.

## Тесты

### `app/src/test/kotlin/ocsigram/lite/org/data/auth/AuthRepositoryTest.kt`

Проверяет состояние ожидания телефона и преобразование типовых ошибок TDLib.

Запуск:

```bash
./gradlew :app:testProdDebugUnitTest
```

После изменения AuthRepository добавляйте тесты в этот каталог.

## Команды сборки

Prod Debug:

```bash
./gradlew :app:assembleProdDebug
```

Beta Debug:

```bash
./gradlew :app:assembleBetaDebug
```

Prod Release:

```bash
./gradlew :app:assembleProdRelease
```

Beta Release:

```bash
./gradlew :app:assembleBetaRelease
```

Prod App Bundle:

```bash
./gradlew :app:bundleProdRelease
```

Unit-тесты:

```bash
./gradlew :app:testProdDebugUnitTest
```

Lint:

```bash
./gradlew :app:lintProdDebug
```

## Что заменить до первой полной сборки

Сводный чек-лист:

- [ ] Android SDK Platform 37 установлен;
- [ ] `config/tdlib.properties` содержит свой API ID и API Hash;
- [ ] `config/signing.properties` содержит параметры своего keystore;
- [ ] `app/src/prod/google-services.json` взят из Firebase;
- [ ] `app/src/beta/google-services.json` взят из Firebase;
- [ ] `Client.java` и `TdApi.java` скопированы в `tdlib/src/main/java/org/drinkless/tdlib/`;
- [ ] `libtdjni.so` скопирован для ABI `arm64-v8a`;
- [ ] `libtdjni.so` скопирован для ABI `x86_64`;
- [ ] бинарники собраны с NDK r28 или новее;
- [ ] commit TDLib записан в `tdlib/README.md`;
- [ ] серверная аутентификация FCM загружена в настройках Telegram;
- [ ] push-уведомления проверены вручную.
