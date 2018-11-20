#!/bin/bash

while :;
do
	java -server -Dfile.encoding=UTF-8 -XX:+UseConcMarkSweepGC -Xms2g -Xmx5g -cp config:./lib/* org.l2j.gameserver.GameServer > log/stdout.log 2>&1

	[ $? -ne 2 ] && break
	sleep 30;
done

