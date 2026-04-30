@echo off

chcp 65001

mkdir out 2>nul

javac -encoding UTF-8 -cp "lib/*" -d out *.java
if %errorlevel% neq 0 (
    echo Compilation failed
    pause
    exit /b
)

java -Dfile.encoding=UTF-8 -cp "out;lib/*" Main