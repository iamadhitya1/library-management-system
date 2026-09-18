@echo off
echo Compiling source + tests...
if not exist out mkdir out
javac -cp "lib/gson-2.10.1.jar;lib/junit-platform-console-standalone-1.10.1.jar" -d out src\*.java test\*.java
if %errorlevel% neq 0 (
    echo Compilation failed.
    pause
    exit /b 1
)
echo Running tests...
java -cp "out;lib/gson-2.10.1.jar" -jar lib/junit-platform-console-standalone-1.10.1.jar ^
  execute --class-path "out;lib/gson-2.10.1.jar" --scan-class-path
pause
