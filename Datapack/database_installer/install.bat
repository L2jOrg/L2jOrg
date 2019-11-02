@echo off
echo Make sure mysql is on your bin path!
if exist mysql_settings.conf goto settings

echo Can't find mysql_settings.conf file!
goto end

:settings

for /r install %%f in (*.sql) do ( 
                echo Loading %%~nf ...
		mysql --defaults-extra-file=mysql_settings.conf < %%f
	)
:end

pause
