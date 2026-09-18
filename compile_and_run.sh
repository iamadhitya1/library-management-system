#!/bin/bash
echo "Compiling Library Management System..."
cd src
javac -cp ".:../lib/gson-2.10.1.jar" -d . *.java
if [ $? -ne 0 ]; then
    echo "Compilation failed."
    exit 1
fi
echo "Compilation successful. Starting application..."
echo ""
java -cp ".:../lib/gson-2.10.1.jar" Main
