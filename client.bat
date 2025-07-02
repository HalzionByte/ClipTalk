@echo off
echo Compiling Client JavaFX App...

REM Compile all Java files inside src package
javac --module-path lib --add-modules javafx.controls -d out src\*.java

if %ERRORLEVEL% neq 0 (
    echo Compile failed. Check your code.
    pause
    exit /b
)

echo Starting JavaFX Client UI...
start "Azura UI" cmd /k "java --module-path lib --add-modules javafx.controls -cp out src.ClientUI"

pause
