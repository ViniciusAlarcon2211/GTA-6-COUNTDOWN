@echo off
where gradle >nul 2>nul
if %ERRORLEVEL% EQU 0 (
  gradle %*
  exit /b %ERRORLEVEL%
)
echo Gradle nao esta instalado. Abra o projeto no Android Studio ou instale Gradle 8.11.1.
exit /b 1
