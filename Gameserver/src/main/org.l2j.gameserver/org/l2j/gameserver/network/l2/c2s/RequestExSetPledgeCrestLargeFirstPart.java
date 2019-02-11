package org.l2j.gameserver.network.l2.c2s;

import org.l2j.gameserver.cache.CrestCache;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.pledge.Clan;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.network.l2.s2c.ExSetPledgeEmblemAck;

import java.nio.ByteBuffer;

/**
 * @author Bonux
**/
public class RequestExSetPledgeCrestLargeFirstPart extends L2GameClientPacket
{
	private int _crestPart, _crestLeght, _length;
	private byte[] _data;

	/**
	 * format: chd(b)
     * @param buffer
     */
	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_crestPart = buffer.getInt();
		_crestLeght = buffer.getInt();
		_length = buffer.getInt();
		if(_length <= CrestCache.LARGE_CREST_PART_SIZE && _length == buffer.remaining())
		{
			_data = new byte[_length];
			buffer.get(_data);
		}
	}

	@Override
	protected void runImpl()
	{
		Player activeChar = client.getActiveChar();
		if(activeChar == null)
			return;

		Clan clan = activeChar.getClan();
		if(clan == null)
			return;

		if((activeChar.getClanPrivileges() & Clan.CP_CL_EDIT_CREST) == Clan.CP_CL_EDIT_CREST)
		{
			if(clan.isPlacedForDisband())
			{
				activeChar.sendPacket(SystemMsg.AS_YOU_ARE_CURRENTLY_SCHEDULE_FOR_CLAN_DISSOLUTION_YOU_CANNOT_REGISTER_OR_DELETE_A_CLAN_CREST);
				return;
			}

			int crestId = 0;
			if(_data != null)
			{
				crestId = CrestCache.getInstance().savePledgeCrestLarge(clan.getClanId(), _crestPart, _crestLeght, _data);
				if(crestId > 0)
				{
					activeChar.sendPacket(SystemMsg.THE_CLAN_CREST_WAS_SUCCESSFULLY_REGISTERED);
					clan.setCrestLargeId(crestId);
					clan.broadcastClanStatus(false, true, false);
				}
				activeChar.sendPacket(new ExSetPledgeEmblemAck(_crestPart));
			}
			else if(clan.hasCrestLarge())
			{
				CrestCache.getInstance().removePledgeCrestLarge(clan.getClanId());
				clan.setCrestLargeId(crestId);
				clan.broadcastClanStatus(false, true, false);
			}
		}
	}
}