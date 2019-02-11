#!/bin/sh

if [ -f mysql_settings.sh ]; then
        . mysql_settings.sh
else
        echo "Can't find mysql_settings.sh file!"
        exit
fi

for sqlfile in install/*.sql
do
        echo Loading $sqlfile ...
        mysql -h $DBHOST -u $USER --password=$PASS -D $DBNAME < $sqlfile
done
