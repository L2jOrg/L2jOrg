#!/bin/bash

err=1
until [ $err == 0 ];
do
	java -Xmx256m -cp './lib/*' org.l2j.authserver.AuthServer
	err=$?
	sleep 10;
done