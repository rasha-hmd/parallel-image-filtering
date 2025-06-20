@echo off
echo Compiling all source files...

javac -cp "lib/*" -d bin ^
src\parallel\*.java ^
src\processor\*.java ^
src\filters\*.java ^
src\benchmark\*.java 

echo ✅ Compilation complete.
pause
