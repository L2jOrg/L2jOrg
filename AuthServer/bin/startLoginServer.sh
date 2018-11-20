#!/bin/bash

err=1
until [ $err == 0 ];
do
	java -Xms256m -Xmx512m  -cp './lib/*' org.l2j.authserver.AuthServer
	err=$?
	sleep 10;
done