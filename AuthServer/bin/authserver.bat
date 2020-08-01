@echo off
title Auth Server
:start
echo Starting Auth Server.
echo.
java --enable-preview -Dfile.encoding=UTF-8 -Xmx256m -p ./lib -cp ./lib/* --add-exports java.base/jdk.internal.misc=io.github.joealisson.primitive -m org.l2j.authserver/org.l2j.authserver.AuthServer
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
