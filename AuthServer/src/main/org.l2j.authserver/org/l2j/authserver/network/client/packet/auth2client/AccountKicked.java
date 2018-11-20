/* This program is free software; you can redistribute it and/or modify
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

/**
 * @author KenM
 */
public final class AccountKicked extends L2LoginServerPacket {

	
	private final AccountKickedReason _reason;
	
	public AccountKicked(AccountKickedReason reason) {
		_reason = reason;
	}
	
	@Override
	protected void write()
	{
		writeByte(0x02);
		writeInt(_reason.getCode());
	}

    @Override
    protected int packetSize() {
        return super.packetSize() + 5;
    }

    public enum AccountKickedReason  {
        REASON_FALSE_DATA_STEALER_REPORT(0),
        REASON_DATA_STEALER(1),
        REASON_SOUSPICION_DATA_STEALER(3),
        REASON_NON_PAYEMENT_CELL_PHONE(4),
        REASON_30_DAYS_SUSPENDED_CASH(8),
        REASON_PERMANENTLY_SUSPENDED_CASH(16),
        REASON_PERMANENTLY_BANNED(32),
        REASON_ACCOUNT_MUST_BE_VERIFIED(64);
        private final int _code;

        AccountKickedReason(int code)
        {
            _code = code;
        }

        public final int getCode()
        {
            return _code;
        }
    }

}
