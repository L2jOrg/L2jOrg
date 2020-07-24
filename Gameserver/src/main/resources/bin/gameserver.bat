@echo off
title Gameserver - L2jOrg Kamael Update Dawn Of Heroes

:start
echo Starting GameServer.
echo.

java --enable-preview -Dfile.encoding=UTF-8 -Xms512m -Xmx2g -p ./lib -cp config;./lib/* --add-exports java.base/jdk.internal.misc=io.github.joealisson.primitive -m org.l2j.gameserver/org.l2j.gameserver.GameServer

if ERRORLEVEL 2 goto restart
if ERRORLEVEL 1 goto error
goto end
:restart
echo.
echo Server restarted ...
echo.
goto start
:error
echo.
echo Server terminated abnormaly ...
echo.
:end
echo.
echo Server terminated ...
echo.

pause
