@echo off
REM Compile all .java files into the build directory.
setlocal
set FX=lib\javafx-sdk-24.0.1\lib
set CP=lib\sqlite-jdbc-3.46.0.0.jar;lib\slf4j-api-2.0.13.jar;lib\slf4j-nop-2.0.13.jar
set MODS=javafx.controls,javafx.graphics

if not exist build mkdir build

REM Collect every .java path under src\ into sources.txt
if exist sources.txt del sources.txt
for /r src %%f in (*.java) do echo %%f>>sources.txt

REM Copy CSS resources into build\ so the running app can locate them via the classpath.
if not exist "build\lifetrack\ui" mkdir "build\lifetrack\ui"
copy /Y "src\lifetrack\ui\styles.css" "build\lifetrack\ui\styles.css" >nul

javac --module-path "%FX%" --add-modules %MODS% -d build -cp "%CP%" @sources.txt
set EC=%ERRORLEVEL%
del sources.txt

if %EC% NEQ 0 (
    echo.
    echo Compilation FAILED with exit code %EC%.
    exit /b %EC%
)
echo.
echo Compilation succeeded. Run run.bat to launch.
endlocal
