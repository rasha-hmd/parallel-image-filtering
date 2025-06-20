@echo off
echo 🔄 Rebuilding project...

:: Step 1: Clean
call clean.bat

:: Step 2: Compile all source files
javac -cp "lib/*" -d bin ^
src\parallel\*.java ^
src\processor\*.java ^
src\filters\*.java ^
src\benchmark\*.java ^
src\test\ImageProcessorTest.java

echo Rebuild complete.
pause
