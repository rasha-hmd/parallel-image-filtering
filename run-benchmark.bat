@echo off
echo Running benchmark on benchmark_input.png...

:: Set classpath with all jars
set CLASSPATH=bin;lib\junit-jupiter-api-5.10.2.jar;lib\junit-jupiter-engine-5.10.2.jar;lib\junit-platform-engine-1.10.2.jar;lib\junit-platform-commons-1.10.2.jar;lib\opentest4j-1.3.0.jar;lib\apiguardian-api-1.1.2.jar;lib\flatlaf-3.2.jar

:: Run the benchmark class
java -cp %CLASSPATH% benchmark.BenchmarkRunner

echo.
echo ✅ Done. Output saved to benchmark.csv
pause
