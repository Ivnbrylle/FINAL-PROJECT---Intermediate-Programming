@echo off
REM Launch BioByte.
setlocal
set FX=lib\javafx-sdk-24.0.1\lib
set CP=build;lib\sqlite-jdbc-3.46.0.0.jar;lib\slf4j-api-2.0.13.jar;lib\slf4j-nop-2.0.13.jar
set MODS=javafx.controls,javafx.graphics

java --module-path "%FX%" --add-modules %MODS% --enable-native-access=javafx.graphics,ALL-UNNAMED -cp "%CP%" lifetrack.Main
endlocal
