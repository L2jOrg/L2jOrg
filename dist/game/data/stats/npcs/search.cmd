@echo off
title XML Search
cls
:search
set /p text="Enter search text: "
echo.
echo.search text "%text%" result:
findstr /I /N %text% *.xml
echo.
goto search