package org.l2j.gameserver.config.xml;

import org.l2j.gameserver.config.xml.parser.HostsConfigParser;

/**
 * @author Bonux
**/
public abstract class ConfigParsers
{
	public static void parseAll()
	{
		HostsConfigParser.getInstance().load();
	}
}