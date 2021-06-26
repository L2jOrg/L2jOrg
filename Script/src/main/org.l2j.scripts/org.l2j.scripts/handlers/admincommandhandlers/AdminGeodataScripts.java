package org.l2j.scripts.handlers.admincommandhandlers;

import java.io.File;
import java.util.StringTokenizer;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.engine.geoscripts.GeoEngine;
import org.l2j.gameserver.engine.geoscripts.utils.GeodataUtils;
import org.l2j.gameserver.handler.IAdminCommandHandler;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.util.BuilderUtil;

public class AdminGeodataScripts implements IAdminCommandHandler
{
	private static final String[] ADMIN_COMMANDS =
			{
		"admin_geo_z",
		"admin_geo_type",
		"admin_geo_nswe",
		"admin_geo_los",
		"admin_geo_load",
		"admin_geo_dump",
		"admin_geo_trace",
		"admin_geo_map",
		"admin_geo_grid"
	};

	@Override
	public boolean useAdminCommand(String command, Player activeChar)
	{
		final StringTokenizer st = new StringTokenizer(command, " ");
		final String actualCommand = st.nextToken();
		switch (actualCommand.toLowerCase())
		{
			case "admin_geo_z":
				activeChar.sendMessage("GeoEngine: Geo_Lower_Z = " + GeoEngine.getLowerHeight(activeChar.getLocation(), activeChar.getGeoIndex()) + " Geo_Upper_Z = " + GeoEngine.getUpperHeight(activeChar.getLocation(), activeChar.getGeoIndex()) + " Loc_Z = " + activeChar.getZ() + " Corrected_Z = " + activeChar.getGeoZ(activeChar.getLocation()));
				break;
			case "admin_geo_type":
				int type = GeoEngine.getType(activeChar.getX(), activeChar.getY(), activeChar.getGeoIndex());
				activeChar.sendMessage("GeoEngine: Geo_Type = " + type);
				break;
			case "admin_geo_nswe":
				String result = "";
				byte nswe = GeoEngine.getLowerNSWE(activeChar.getX(), activeChar.getY(), activeChar.getZ(), activeChar.getGeoIndex());
				if((nswe & 8) == 0)
					result += " N";
				if((nswe & 4) == 0)
					result += " S";
				if((nswe & 2) == 0)
					result += " W";
				if((nswe & 1) == 0)
					result += " E";
				activeChar.sendMessage("GeoEngine: Geo_NSWE -> " + nswe + "->" + result);
				break;
			case "admin_geo_los":
				if(activeChar.getTarget() != null)
					if(GeoEngine.canSeeTarget(activeChar, activeChar.getTarget()))
						activeChar.sendMessage("GeoEngine: Can See Target");
					else
						activeChar.sendMessage("GeoEngine: Can't See Target");
				else
					activeChar.sendMessage("None Target!");
				break;
			case "admin_geo_map":
				int x = (activeChar.getX() - World.MAP_MIN_X >> 15) + Config.GEO_X_FIRST;
				int y = (activeChar.getY() - World.MAP_MIN_Y >> 15) + Config.GEO_Y_FIRST;

				activeChar.sendMessage("GeoMap: " + x + "_" + y);
				break;
			case "admin_geo_grid":
				try
				{
					GeodataUtils.debugGrid(activeChar);
				}
				catch(Exception e)
				{
					GeodataUtils.debugGrid(activeChar);
				}
				break;
		}

		return true;
	}

	@Override
	public String[] getAdminCommandList()
	{
		return ADMIN_COMMANDS;
	}
}