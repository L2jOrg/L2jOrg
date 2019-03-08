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
import org.l2j.gameserver.mobius.gameserver.data.xml.impl.SecondaryAuthData;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.Ex2ndPasswordAck;
import org.l2j.gameserver.mobius.gameserver.security.SecondaryPasswordAuth;

/**
 * (ch)cS{S} c: change pass? S: current password S: new password
 * @author mrTJO
 */
public class RequestEx2ndPasswordReq extends IClientIncomingPacket
{
	private int _changePass;
	private String _password;
	private String _newPassword;
	
	@Override
	public void readImpl(ByteBuffer packet)
	{
		_changePass = packet.get();
		_password = readString(packet);
		if (_changePass == 2)
		{
			_newPassword = readString(packet);
		}
		return true;
	}
	
	@Override
	public void runImpl()
	{
		if (!SecondaryAuthData.getInstance().isEnabled())
		{
			return;
		}
		
		final SecondaryPasswordAuth secondAuth = client.getSecondaryAuth();
		boolean success = false;
		
		if ((_changePass == 0) && !secondAuth.passwordExist())
		{
			success = secondAuth.savePassword(_password);
		}
		else if ((_changePass == 2) && secondAuth.passwordExist())
		{
			success = secondAuth.changePassword(_password, _newPassword);
		}
		
		if (success)
		{
			client.sendPacket(new Ex2ndPasswordAck(_changePass, Ex2ndPasswordAck.SUCCESS));
		}
	}
}
