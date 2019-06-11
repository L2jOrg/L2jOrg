package org.l2j.authserver.network.client.packet.auth2client;

import org.l2j.authserver.network.client.AuthClient;
import org.l2j.authserver.network.client.packet.L2LoginServerPacket;

public final class PlayFail extends L2LoginServerPacket {

	private final PlayFailReason _reason;
	
	public PlayFail(PlayFailReason reason)
	{
		_reason = reason;
	}
	
	@Override
	protected void writeImpl(AuthClient client) {
		writeByte((byte)0x06);
		writeByte((byte)_reason.getCode());
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
