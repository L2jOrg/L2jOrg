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
package org.l2j.authserver.network.client.packet.auth2client;

import org.l2j.authserver.network.client.packet.L2LoginServerPacket;

public final class PlayFail extends L2LoginServerPacket {

	private final PlayFailReason _reason;
	
	public PlayFail(PlayFailReason reason)
	{
		_reason = reason;
	}
	
	@Override
	protected void write() {
		writeByte(0x06);
		writeByte(_reason.getCode());
	}

    @Override
    protected int packetSize() {
        return super.packetSize() + 2;
    }

    public enum PlayFailReason
	{
		REASON_SYSTEM_ERROR(0x01),
		REASON_USER_OR_PASS_WRONG(0x02),
		REASON3(0x03),
		REASON4(0x04),
		REASON_TOO_MANY_PLAYERS(0x0f);

		private final int _code;

		PlayFailReason(int code)
		{
			_code = code;
		}

		public final int getCode()
		{
			return _code;
		}
	}
}
