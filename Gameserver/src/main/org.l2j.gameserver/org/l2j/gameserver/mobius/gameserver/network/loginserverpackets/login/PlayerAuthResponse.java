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
package org.l2j.gameserver.mobius.gameserver.network.loginserverpackets.login;

import org.l2j.commons.network.BaseRecievePacket;

/**
 * @author -Wooden-
 */
public class PlayerAuthResponse extends BaseRecievePacket
{
	private final String _account;
	private final boolean _authed;
	
	/**
	 * @param decrypt
	 */
	public PlayerAuthResponse(byte[] decrypt)
	{
		super(decrypt);
		
		_account = readS();
		_authed = readC() != 0;
	}
	
	/**
	 * @return Returns the account.
	 */
	public String getAccount()
	{
		return _account;
	}
	
	/**
	 * @return Returns the authed state.
	 */
	public boolean isAuthed()
	{
		return _authed;
	}
}