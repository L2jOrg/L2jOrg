#!/bin/sh
java -Djava.util.logging.config.file=console.cfg -cp ./../libs/*: com.l2jmobius.tools.accountmanager.SQLAccountManager
