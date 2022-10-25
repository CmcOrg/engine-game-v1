@echo off
for %%i in (../proto/*.proto) do (
    protoc-21.8-win64.exe --proto_path=../proto --java_out=../src/main/java %%i
    echo From %%i To %%~ni.java Successfully!
)
pause
