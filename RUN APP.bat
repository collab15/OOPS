@echo off

mkdir out 2>nul

javac -cp "lib/*" -d out *.java
if %errorlevel% neq 0 (
    echo Compilation failed
    pause
    exit /b
)

java -cp "out;lib/*" Main