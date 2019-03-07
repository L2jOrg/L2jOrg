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

import com.l2jmobius.commons.network.BaseSendablePacket;

/**
 * @author mrTJO
 */
public class TempBan extends BaseSendablePacket
{
	public TempBan(String accountName, String ip, long time)
	{
		writeC(0x0A);
		writeS(accountName);
		writeS(ip);
		writeQ(System.currentTimeMillis() + (time * 60000));
		// if (reason != null)
		// {
		// writeC(0x01);
		// writeS(reason);
		// }
		// else
		// {
		writeC(0x00);
		// }
	}
	
	@Override
	public byte[] getContent()
	{
		return getBytes();
	}
}
