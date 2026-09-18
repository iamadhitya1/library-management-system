@echo off
echo Compiling Library Management System...
cd src
javac -cp ".;../lib/gson-2.10.1.jar" -d . *.java
if %errorlevel% neq 0 (
    echo Compilation failed.
    pause
    exit /b 1
)
echo Compilation successful. Starting application...
echo.
java -cp ".;../lib/gson-2.10.1.jar" Main
pause
