/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package org.l2j.authserver.network;

import org.l2j.authserver.network.client.packet.auth2client.LoginOk;
import org.l2j.authserver.network.client.packet.auth2client.PlayOk;

import java.util.Objects;

/**
 * <p>
 * This class is used to represent session keys used by the client to authenticate in the gameserver
 * </p>
 * <p>
 * A SessionKey is made up of two 8 bytes keys. One is send in the {@link LoginOk} packet and the other is sent in {@link PlayOk}
 * </p>
 * @author -Wooden-
 */
public class SessionKey  {
	public int gameServerSessionId;
	public int gameserverAccountId;
	public int authAccountId;
	public int authKey;
	
	public SessionKey(int authAccountId, int authKey, int gameServerSession, int gameserverAccountId)
	{
		gameServerSessionId = gameServerSession;
		this.gameserverAccountId = gameserverAccountId;
		this.authAccountId = authAccountId;
		this.authKey = authKey;
	}
	
	@Override
	public String toString()
	{
		return "PlayOk: " + gameServerSessionId + " " + gameserverAccountId + " LoginOk:" + authAccountId + " " + authKey;
	}
	
	public boolean checkLoginPair(int accountId, int authKey) {
		return this.authAccountId == accountId && this.authKey == authKey;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SessionKey that = (SessionKey) o;
        return gameServerSessionId == that.gameServerSessionId &&
                gameserverAccountId == that.gameserverAccountId &&
                authAccountId == that.authAccountId &&
                authKey == that.authKey;
    }

    @Override
    public int hashCode() {
        return Objects.hash(gameServerSessionId, gameserverAccountId, authAccountId, authKey);
    }
}