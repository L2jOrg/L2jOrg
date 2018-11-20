@echo off
title L2-Scripts Classic: Saviors (Zaken) (Game Server)

:start
echo Starting GameServer.
echo.

java -server -Dfile.encoding=UTF-8 -XX:+UseConcMarkSweepGC -Xms2g -Xmx5g -cp config;./lib/* org.l2j.gameserver.GameServer

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
