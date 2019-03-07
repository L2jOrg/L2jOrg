#! /bin/sh

java -Xmx512m -cp ../libs/*: com.l2jmobius.tools.geodataconverter.GeoDataConverter > log/stdout.log 2>&1

