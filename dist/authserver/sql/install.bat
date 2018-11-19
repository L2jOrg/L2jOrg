@echo off

if exist mysql_settings.bat goto settings

echo Can't find mysql_settings.bat file!
goto end

:settings

call mysql_settings.bat
if errorlevel 1 goto end

for /r install %%f in (*.sql) do ( 
                echo Loading %%~nf ...
		mysql -h %DBHOST% -u %USER% --password=%PASS% -D %DBNAME% < %%f
	)
:end

pause
