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
package org.l2j.gameserver.mobius.gameserver.network.loginserverpackets.game;

import org.l2j.commons.network.BaseSendablePacket;

import java.util.List;

/**
 * @author mrTJO Thanks to mochitto
 */
public class ReplyCharacters extends BaseSendablePacket
{
	public ReplyCharacters(String account, int chars, List<Long> timeToDel)
	{
		writeC(0x08);
		writeS(account);
		writeC(chars);
		writeC(timeToDel.size());
		for (long time : timeToDel)
		{
			writeQ(time);
		}
	}
	
	@Override
	public byte[] getContent()
	{
		return getBytes();
	}
}
