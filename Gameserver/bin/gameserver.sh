#!/bin/bash

err=1
until [ $err == 0 ];
do
	java -Dfile.encoding=UTF-8 -Xms512m -Xmx2g -cp 'config:./lib/*' org.l2j.gameserver.GameServer
    err=$?
	sleep 10;
done
