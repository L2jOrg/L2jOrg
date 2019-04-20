#!/bin/bash

err=1
until [ $err == 0 ];
do
	java --enable-preview -Dfile.encoding=UTF-8 -Xmx256m -p ./lib -cp './lib/*' -m org.l2j.authserver/org.l2j.authserver.AuthServer
	err=$?
	sleep 10;
done