@echo off
chcp 65001 >nul
title Компиляция SwiftKek v1.4

echo ========================================
echo        Сборка SwiftKek v1.4
echo ========================================

echo.
echo 📁 Файлы в папке:
dir /b *.java *.yml 2>nul

echo.
echo 🗑️ Очистка старых файлов...
if exist SwiftKek.class del SwiftKek.class
if exist SwiftKek.jar del SwiftKek.jar

echo.
echo 🔨 Компилируем Java код...
javac -cp "spigot-api-1.21.1.jar" SwiftKek.java

if errorlevel 1 (
    echo.
    echo ❌ ОШИБКА компиляции!
    echo.
    echo 🔧 Возможные решения:
    echo 1. Проверьте наличие spigot-api-1.21.1.jar
    echo 2. Убедитесь что установлена Java JDK 17+
    echo 3. Проверьте синтаксис кода в SwiftKek.java
    echo 4. Убедитесь что файл называется SwiftKek.java
    pause
    exit /b 1
)

echo.
echo 📦 Создаем JAR файл...
echo Мануальное создание JAR...
if not exist SwiftKek.class (
    echo ❌ Файл SwiftKek.class не найден!
    pause
    exit /b 1
)

:: Создаем временную папку для JAR
if exist _temp rmdir /s /q _temp
mkdir _temp

:: Копируем файлы
copy SwiftKek.class _temp\ >nul
copy plugin.yml _temp\ >nul

:: Создаем JAR вручную через 7-Zip или WinRAR
echo Создаем JAR архиватором...
"C:\Program Files\7-Zip\7z.exe" a -tzip SwiftKek.jar .\_temp\* >nul 2>&1

if not exist SwiftKek.jar (
    echo Пробуем альтернативный метод...
    powershell -Command "Compress-Archive -Path .\_temp\* -DestinationPath SwiftKek.jar -Force" >nul 2>&1
)

:: Убираем временную папку
if exist _temp rmdir /s /q _temp

if not exist SwiftKek.jar (
    echo.
    echo ❌ Не удалось создать JAR!
    echo Установите 7-Zip или используйте ручное создание
    echo.
    echo 🔧 Ручное создание:
    echo 1. Создайте ZIP архив с файлами:
    echo    - SwiftKek.class
    echo    - plugin.yml
    echo 2. Переименуйте .zip в .jar
    pause
    exit /b 1
)

echo.
echo ✅ УСПЕШНО скомпилировано!
echo 📁 Создан файл: SwiftKek.jar
echo 📏 Размер: %~z0 bytes
echo.
echo 🚀 Плагин готов к использованию!
echo.

pause