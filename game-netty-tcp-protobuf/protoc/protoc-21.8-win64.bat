@echo off
chcp 65001
for %%i in (../proto/*.proto) do (
    echo 编译协议：%%i
    protoc-21.8-win64.exe --proto_path=../proto --java_out=../src/main/java %%i
)
pause
