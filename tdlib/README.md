# Модуль TDLib

Модуль хранит Java-биндинги и нативные библиотеки TDLib. Maven-зависимость специально не используется.

## Сборка из исходников

В форке [TDLib](https://github.com/tdlib/td) соберите Android-библиотеку через Docker:

```bash
docker build --build-arg COMMIT_HASH=<hash> --build-arg ANDROID_NDK_VERSION=<r28 или новее> --output tdlib .
```

OpenSSL оставьте подключённым по умолчанию для стандартного бинарника TDLib.

Результат будет в:
- `tdlib/libs/` — `.so`;
- `tdlib/java/` — Java-файлы `Client.java` и `TdApi.java`.

Скопируйте:
- файлы из `tdlib/java/` в `tdlib/src/main/java/org/drinkless/tdlib/`;
- `arm64-v8a/libtdjni.so` в `tdlib/src/main/jniLibs/arm64-v8a/`;
- `x86_64/libtdjni.so` в `tdlib/src/main/jniLibs/x86_64/`.

Поле для фиксации версии сборки:

**Собрано из коммита:** `не установлено`
