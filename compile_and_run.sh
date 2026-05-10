#!/bin/bash
echo "Compiling Library Management System..."
cd src
javac *.java
if [ $? -ne 0 ]; then
    echo "Compilation failed."
    exit 1
fi
echo "Compilation successful. Starting application..."
echo ""
java Main
