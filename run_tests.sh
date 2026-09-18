#!/bin/bash
echo "Compiling source + tests..."
mkdir -p out
javac -cp "lib/gson-2.10.1.jar;lib/junit-platform-console-standalone-1.10.1.jar" -d out src/*.java test/*.java
if [ $? -ne 0 ]; then
    echo "Compilation failed."
    exit 1
fi
echo "Running tests..."
java -cp "out;lib/gson-2.10.1.jar" -jar lib/junit-platform-console-standalone-1.10.1.jar \
  execute --class-path "out;lib/gson-2.10.1.jar" --scan-class-path
