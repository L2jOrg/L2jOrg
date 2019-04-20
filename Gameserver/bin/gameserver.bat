@echo off
title L2 Classic: Seven Signs (Game Server)

:start
echo Starting GameServer.
echo.

java -Dfile.encoding=UTF-8 -Xms512m -Xmx2g -p ./lib -cp config;./lib/* -m org.l2j.gameserver/org.l2j.gameserver.GameServer --enable-preview

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
