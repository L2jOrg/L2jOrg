@echo off
title L2D geodata converter

java -Xmx512m -cp ./../libs/* com.l2jmobius.tools.geodataconverter.GeoDataConverter

pause
