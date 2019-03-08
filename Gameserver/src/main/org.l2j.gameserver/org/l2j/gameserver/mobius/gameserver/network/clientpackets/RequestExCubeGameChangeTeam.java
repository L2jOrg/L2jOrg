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
package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.commons.network.PacketReader;
import org.l2j.gameserver.mobius.gameserver.instancemanager.HandysBlockCheckerManager;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;

/**
 * Format: chdd d: Arena d: Team
 * @author mrTJO
 */
public final class RequestExCubeGameChangeTeam extends IClientIncomingPacket
{
	private int _arena;
	private int _team;
	
	@Override
	public void readImpl(ByteBuffer packet)
	{
		// client sends -1,0,1,2 for arena parameter
		_arena = packet.getInt() + 1;
		_team = packet.getInt();
		return true;
	}
	
	@Override
	public void runImpl()
	{
		// do not remove players after start
		if (HandysBlockCheckerManager.getInstance().arenaIsBeingUsed(_arena))
		{
			return;
		}
		final L2PcInstance player = client.getActiveChar();
		
		switch (_team)
		{
			case 0:
			case 1:
			{
				// Change Player Team
				HandysBlockCheckerManager.getInstance().changePlayerToTeam(player, _arena, _team);
				break;
			}
			case -1:
			{
				// Remove Player (me)
			}
			{
				final int team = HandysBlockCheckerManager.getInstance().getHolder(_arena).getPlayerTeam(player);
				// client sends two times this packet if click on exit
				// client did not send this packet on restart
				if (team > -1)
				{
					HandysBlockCheckerManager.getInstance().removePlayer(player, _arena, team);
				}
				break;
			}
			default:
			{
				LOGGER.warning("Wrong Cube Game Team ID: " + _team);
				break;
			}
		}
	}
}
