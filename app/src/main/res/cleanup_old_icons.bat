@echo off
echo ============================================
echo Removing old DLAssistant icons...
echo ============================================

cd /d "%~dp0"

REM Remove old dlassistant icons from all mipmap folders
for %%d in (mipmap-hdpi mipmap-mdpi mipmap-xhdpi mipmap-xxhdpi mipmap-xxxhdpi) do (
    del /Q "%%d\ic_dlassistant.png" 2>nul
    del /Q "%%d\ic_main_dlassistant.webp" 2>nul
    del /Q "%%d\ic_main_dlassistant_foreground.webp" 2>nul
    del /Q "%%d\ic_main_dlassistant_round.webp" 2>nul
    del /Q "%%d\ic_send.png" 2>nul
    del /Q "%%d\ic_stop.png" 2>nul
)

REM Remove from mipmap-anydpi
del /Q "mipmap-anydpi\ic_dlassistant.xml" 2>nul
del /Q "mipmap-anydpi\ic_main_dlassistant.xml" 2>nul
del /Q "mipmap-anydpi\ic_main_dlassistant_round.xml" 2>nul

REM Remove from mipmap-anydpi-v26
del /Q "mipmap-anydpi-v26\ic_dlassistant.xml" 2>nul
del /Q "mipmap-anydpi-v26\ic_main_dlassistant.xml" 2>nul
del /Q "mipmap-anydpi-v26\ic_main_dlassistant_round.xml" 2>nul

echo.
echo Done! Old icons removed.
echo.
del /Q "%~f0"
