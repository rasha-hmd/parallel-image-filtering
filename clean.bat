@echo off
echo Cleaning build directory...

:: Delete the bin folder and all its contents (quietly)
rmdir /s /q bin

:: Recreate an empty bin folder
mkdir bin

echo Clean complete.
pause
