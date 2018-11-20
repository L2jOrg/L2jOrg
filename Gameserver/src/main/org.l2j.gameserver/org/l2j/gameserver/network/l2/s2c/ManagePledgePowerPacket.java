package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.pledge.RankPrivs;

public class ManagePledgePowerPacket extends L2GameServerPacket
{
	private int _action, _clanId, privs;

	public ManagePledgePowerPacket(Player player, int action, int rank)
	{
		_clanId = player.getClanId();
		_action = action;
		RankPrivs temp = player.getClan().getRankPrivs(rank);
		privs = temp == null ? 0 : temp.getPrivs();
		player.sendPacket(new PledgeReceiveUpdatePower(privs));
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_clanId);
		writeD(_action);
		writeD(privs);
	}
}