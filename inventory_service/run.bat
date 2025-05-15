@echo off
echo Building Inventory Service...
call mvn clean package -DskipTests

if %ERRORLEVEL% NEQ 0 (
    echo Build failed with error code %ERRORLEVEL%
    exit /b %ERRORLEVEL%
)

echo.
echo Starting Inventory Service...
call mvn spring-boot:run
