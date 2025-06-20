@echo off
setlocal

:: Generate a timestamp for the report
for /f %%i in ('wmic os get localdatetime ^| find "."') do set datetime=%%i
set timestamp=%datetime:~0,4%-%datetime:~4,2%-%datetime:~6,2%_%datetime:~8,2%-%datetime:~10,2%-%datetime:~12,2%

:: Output file
set outfile=test-results.txt

:: Write header
echo =============================== > %outfile%
echo       JUnit Test Report         >> %outfile%
echo       Run at: %timestamp%       >> %outfile%
echo =============================== >> %outfile%
echo. >> %outfile%

:: Run JUnit tests with no ANSI colors
java -jar lib\junit-platform-console-standalone-1.10.2.jar ^
--class-path bin ^
--scan-class-path ^
--details tree ^
--disable-ansi-colors >> %outfile%

:: Footer
echo. >> %outfile%
echo =============================== >> %outfile%
echo         End of Report           >> %outfile%
echo =============================== >> %outfile%

:: Show results in terminal
type %outfile%
pause
