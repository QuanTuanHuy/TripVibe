@echo off
REM Enhanced Memory System Testing Script for Windows
REM Run this after starting the application

set BASE_URL=http://localhost:8080/api/public/v1
set CONV_ID=test-enhanced-memory-%RANDOM%

echo === Enhanced Memory System Testing ===
echo.

echo Test 1: Starting new conversation...
curl -X POST "%BASE_URL%/chat/rag" ^
  -H "Content-Type: application/json" ^
  -d "{\"message\": \"Hello! My name is John and I work as a software engineer.\", \"conversationId\": \"%CONV_ID%\", \"maxResults\": 3}"

echo.
echo Test 2: Follow-up question to test memory...
curl -X POST "%BASE_URL%/chat/rag" ^
  -H "Content-Type: application/json" ^
  -d "{\"message\": \"What is my name and profession?\", \"conversationId\": \"%CONV_ID%\", \"maxResults\": 3}"

echo.
echo Test 3: Check memory statistics...
curl -X GET "%BASE_URL%/memory/stats/%CONV_ID%"

echo.
echo Test 4: Get memory variables...
curl -X GET "%BASE_URL%/memory/variables/%CONV_ID%"

echo.
echo Test 5: Get memory buffer...
curl -X GET "%BASE_URL%/memory/buffer/%CONV_ID%"

echo.
echo Test 6: System memory stats...
curl -X GET "%BASE_URL%/memory/system/stats"

echo.
echo Test 7: Clear memory...
curl -X DELETE "%BASE_URL%/memory/%CONV_ID%"

echo.
echo Test 8: Verify memory cleared...
curl -X GET "%BASE_URL%/memory/stats/%CONV_ID%"

echo.
echo === Testing completed ===
echo Conversation ID used: %CONV_ID%
pause
