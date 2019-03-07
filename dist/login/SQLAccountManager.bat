@echo off
title SQL Account Manager
color 17
java -Djava.util.logging.config.file=console.cfg -cp ./../libs/* com.l2jmobius.tools.accountmanager.SQLAccountManager
if %errorlevel% == 0 (
echo.
echo Execution successful
echo.
) else (
echo.
echo An error has occurred while running the L2J Account Manager!
echo.
echo Possible reasons for this to happen:
echo.
echo - Missing .jar files or ../libs directory.
echo - MySQL server not running or incorrect MySQL settings:
echo    check ./config/loginserver.ini
echo - Wrong data types or values out of range were provided:
echo    specify correct values for each required field
echo.
)
pause