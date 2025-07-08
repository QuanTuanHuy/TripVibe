#!/bin/bash

# Create output directories
mkdir -p pkg/proto

# Generate Go code for memo service
protoc --go_out=. --go_opt=paths=source_relative \
    --go-grpc_out=. --go-grpc_opt=paths=source_relative \
    ui/proto/memo.proto

mv ui/proto/*.pb.go pkg/proto/

echo "Proto files generated successfully!"