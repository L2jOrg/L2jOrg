#!/bin/bash

if [ ! -f mysql_settings.conf ]; then
    echo "Can't find mysql_settings.conf file!"
    exit
fi

script_count=$(ls -l install | grep \\.sql | wc -l)
count=0
for sqlfile in install/*.sql
do
    count=$(( $count + 1 ))
    echo -e "Installing sql: $sqlfile ... [$(( $count *100 / $script_count))%]\033[0K\r"
    mysql --defaults-extra-file=mysql_settings.conf < $sqlfile
done
