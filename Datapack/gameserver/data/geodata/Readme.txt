##############################################
GEODATA COMPENDIUM
##############################################

Comprehensive guide for geodata, by Tryskell and Hasha.

I	- How to configure it
		a - Prerequisites
		b - Make it work
		c - L2D format
II	- Addendum

##############################################
I - How to configure it
##############################################

----------------------------------------------
a - Prerequisites
----------------------------------------------

* A 64bits Windows/Java JDK is a must-have to run server with geodata. Linux servers don't have the issue.
* The server can start (hardly) with -Xmx3000m. -Xmx4g is recommended.

----------------------------------------------
b - Make it work
----------------------------------------------

To make geodata working:
* unpack your geodata files into "/data/geodata" folder
* open "/config/GeoEngine.ini" with your favorite text editor and then edit following config:
  - CoordSynchronize = 2
* If you do not use any geodata files, the server will automatically change this setting to -1.

----------------------------------------------
c - L2D format
----------------------------------------------

* L2D is a new geodata file format. It holds diagonal movement informations, in addition to regular NSWE flags.
* Heavier file weight (+30%), but the pathfinding algorithms are processed way faster (-35% calculation times).
* L2D files can be converted from L2OFF/L2J formats without losing any information. Converter is part of the gameserver.
* Keep in mind to convert new geodata files, once you update your L2OFF/L2J ones.
