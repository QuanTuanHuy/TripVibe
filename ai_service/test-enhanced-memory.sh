#!/bin/bash

# Enhanced Memory System Testing Script
# Run this after starting the application

BASE_URL="http://localhost:8080/api/public/v1"

echo "=== Enhanced Memory System Testing ==="
echo ""

# Test 1: Basic conversation with memory
echo "Test 1: Starting new conversation..."
CONV_ID="test-enhanced-memory-$(date +%s)"

curl -X POST "$BASE_URL/chat/rag" \
  -H "Content-Type: application/json" \
  -d "{
    \"message\": \"Hello! My name is John and I work as a software engineer.\",
    \"conversationId\": \"$CONV_ID\",
    \"maxResults\": 3
  }" | jq -r '.data.response'

echo ""
echo "Test 2: Follow-up question to test memory..."

curl -X POST "$BASE_URL/chat/rag" \
  -H "Content-Type: application/json" \
  -d "{
    \"message\": \"What is my name and profession?\",
    \"conversationId\": \"$CONV_ID\",
    \"maxResults\": 3
  }" | jq -r '.data.response'

echo ""
echo "Test 3: Check memory statistics..."

curl -X GET "$BASE_URL/memory/stats/$CONV_ID" | jq '.data'

echo ""
echo "Test 4: Get memory variables..."

curl -X GET "$BASE_URL/memory/variables/$CONV_ID" | jq '.data'

echo ""
echo "Test 5: Get memory buffer (raw format)..."

curl -X GET "$BASE_URL/memory/buffer/$CONV_ID" | jq -r '.data'

echo ""
echo "Test 6: System memory stats..."

curl -X GET "$BASE_URL/memory/system/stats" | jq '.data'

echo ""
echo "Test 7: Long conversation to test token limits..."

for i in {1..5}; do
  echo "Sending message $i/5..."
  curl -X POST "$BASE_URL/chat/rag" \
    -H "Content-Type: application/json" \
    -d "{
      \"message\": \"Tell me more about artificial intelligence and machine learning. This is message number $i. Please provide a detailed explanation.\",
      \"conversationId\": \"$CONV_ID\",
      \"maxResults\": 2
    }" | jq -r '.data.response' | head -3
  echo "..."
  echo ""
done

echo "Final memory stats after long conversation:"
curl -X GET "$BASE_URL/memory/stats/$CONV_ID" | jq '.data'

echo ""
echo "Test 8: Clear memory..."

curl -X DELETE "$BASE_URL/memory/$CONV_ID" | jq -r '.data'

echo ""
echo "Test 9: Verify memory cleared..."

curl -X GET "$BASE_URL/memory/stats/$CONV_ID" | jq '.data'

echo ""
echo "=== Testing completed ==="
echo "Conversation ID used: $CONV_ID"
