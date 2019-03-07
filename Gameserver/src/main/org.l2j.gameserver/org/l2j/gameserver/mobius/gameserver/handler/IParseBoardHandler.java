/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.mobius.gameserver.handler;

import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;

import java.util.logging.Logger;

/**
 * Community Board interface.
 * @author Zoey76
 */
public interface IParseBoardHandler
{
	Logger LOG = Logger.getLogger(IParseBoardHandler.class.getName());
	
	/**
	 * Parses a community board command.
	 * @param command the command
	 * @param player the player
	 * @return
	 */
	boolean parseCommunityBoardCommand(String command, L2PcInstance player);
	
	/**
	 * Gets the community board commands.
	 * @return the community board commands
	 */
	String[] getCommunityBoardCommands();
}
