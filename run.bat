@echo off
echo Compiling JavaFX App...

REM Compile all Java files inside src package
javac --module-path lib --add-modules javafx.controls -d out src\*.java

if %ERRORLEVEL% neq 0 (
    echo Compile failed. Check your code.
    pause
    exit /b
)

echo Starting Server...
start "Server" cmd /k "java -cp out src.Server"

echo Starting Client...
start "Client" cmd /k "java -cp out src.Client"

echo Starting JavaFX UI...
start "Azura UI" cmd /k "java --module-path lib --add-modules javafx.controls -cp out src.ClientUI"

echo All components launched.
pause
