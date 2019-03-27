@echo off
title Login Server Console
:start
echo Starting L2J Auth Server.
echo.
java -Xmx256m -p ./lib -cp ./lib/* -m org.l2j.authserver/org.l2j.authserver.AuthServer --enable-preview
if ERRORLEVEL 2 goto restart
if ERRORLEVEL 1 goto error
goto end
:restart
echo.
echo Admin Restart ...
echo.
goto start
:error
echo.
echo Server terminated abnormaly
echo.
:end
echo.
echo server terminated
echo.
pause
