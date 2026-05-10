@echo off
echo Compiling Library Management System...
cd src
javac *.java
if %errorlevel% neq 0 (
    echo Compilation failed.
    pause
    exit /b 1
)
echo Compilation successful. Starting application...
echo.
java Main
pause
