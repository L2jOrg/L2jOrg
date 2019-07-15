#!/bin/bash

err=1
until [ $err == 0 ];
do
	java --enable-preview -Dfile.encoding=UTF-8 -Xmx256m -p ./lib -cp './lib/*' --add-exports java.base/jdk.internal.misc=io.github.joealisson.primitive -m org.l2j.authserver/org.l2j.authserver.AuthServer
	err=$?
	sleep 10;
done