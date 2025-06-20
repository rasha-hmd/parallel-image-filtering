@echo off
echo 🐳 Building Docker image...
docker build -t image-filter .

echo.
echo 🚀 Running container...
docker run --rm image-filter

echo.
echo ✅ Docker execution complete.
pause
