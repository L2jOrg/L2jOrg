#!/bin/bash

err=1
until [ $err == 0 ];
do
	java --enable-preview -Dfile.encoding=UTF-8 -Xms512m -Xmx2g -p ./lib -cp 'config:./lib/*' --add-exports java.base/jdk.internal.misc=io.github.joealisson.primitive -m org.l2j.gameserver/org.l2j.gameserver.GameServer
  err=$?
	sleep 10;
done
