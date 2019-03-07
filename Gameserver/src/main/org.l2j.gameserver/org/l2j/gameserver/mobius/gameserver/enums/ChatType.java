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
package org.l2j.gameserver.mobius.gameserver.enums;

/**
 * @author St3eT
 */
public enum ChatType
{
	GENERAL(0),
	SHOUT(1),
	WHISPER(2),
	PARTY(3),
	CLAN(4),
	GM(5),
	PETITION_PLAYER(6),
	PETITION_GM(7),
	TRADE(8),
	ALLIANCE(9),
	ANNOUNCEMENT(10),
	BOAT(11),
	FRIEND(12),
	MSNCHAT(13),
	PARTYMATCH_ROOM(14),
	PARTYROOM_COMMANDER(15),
	PARTYROOM_ALL(16),
	HERO_VOICE(17),
	CRITICAL_ANNOUNCE(18),
	SCREEN_ANNOUNCE(19),
	BATTLEFIELD(20),
	MPCC_ROOM(21),
	NPC_GENERAL(22),
	NPC_SHOUT(23),
	NPC_WHISPER(24),
	WORLD(25);
	
	private final int _clientId;
	
	private ChatType(int clientId)
	{
		_clientId = clientId;
	}
	
	/**
	 * @return the client id.
	 */
	public int getClientId()
	{
		return _clientId;
	}
	
	/**
	 * Finds the {@code ChatType} by its clientId
	 * @param clientId the clientId
	 * @return the {@code ChatType} if its found, {@code null} otherwise.
	 */
	public static ChatType findByClientId(int clientId)
	{
		for (ChatType ChatType : values())
		{
			if (ChatType.getClientId() == clientId)
			{
				return ChatType;
			}
		}
		return null;
	}
}