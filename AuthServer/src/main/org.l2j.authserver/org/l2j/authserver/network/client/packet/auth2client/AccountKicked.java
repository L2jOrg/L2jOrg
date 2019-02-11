package org.l2j.authserver.network.client.packet.auth2client;

import org.l2j.authserver.network.client.AuthClient;
import org.l2j.authserver.network.client.packet.L2LoginServerPacket;

import java.nio.ByteBuffer;

/**
 * @author KenM
 */
public final class AccountKicked extends L2LoginServerPacket {

	
	private final AccountKickedReason _reason;
	
	public AccountKicked(AccountKickedReason reason) {
		_reason = reason;
	}
	
	@Override
	protected void writeImpl(AuthClient client, ByteBuffer buffer)
	{
		buffer.put((byte)0x02);
		buffer.putInt(_reason.getCode());
	}

    @Override
    protected int size(AuthClient client) {
        return super.size(client) + 5;
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
