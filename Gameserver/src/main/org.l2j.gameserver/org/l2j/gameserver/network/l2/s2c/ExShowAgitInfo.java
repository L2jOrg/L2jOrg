package org.l2j.gameserver.network.l2.s2c;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.l2j.gameserver.data.xml.holder.ResidenceHolder;
import org.l2j.gameserver.model.entity.residence.clanhall.NormalClanHall;
import org.l2j.gameserver.model.pledge.Clan;
import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.tables.ClanTable;

import static org.l2j.commons.util.Util.STRING_EMPTY;

public class ExShowAgitInfo extends L2GameServerPacket
{
	private final List<AgitInfo> _infos;

	public ExShowAgitInfo()
	{
		List<NormalClanHall> clanHalls = ResidenceHolder.getInstance().getResidenceList(NormalClanHall.class);
		_infos = new ArrayList<AgitInfo>(clanHalls.size());

		clanHalls.forEach(clanHall ->
		{
			int ch_id = clanHall.getId();
			int getType = clanHall.getClanHallType().ordinal();

			Clan clan = ClanTable.getInstance().getClan(clanHall.getOwnerId());
			String clan_name = clanHall.getOwnerId() == 0 || clan == null ? STRING_EMPTY : clan.getName();
			String leader_name = clanHall.getOwnerId() == 0 || clan == null ? STRING_EMPTY : clan.getLeaderName();
			_infos.add(new AgitInfo(clan_name, leader_name, ch_id, getType));
		});
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_infos.size());
		_infos.forEach(info ->
		{
			buffer.putInt(info.ch_id);
			writeString(info.clan_name, buffer);
			writeString(info.leader_name, buffer);
			buffer.putInt(info.getType);
		});
	}

	static class AgitInfo
	{
		public String clan_name, leader_name;
		public int ch_id, getType;

		public AgitInfo(String clan_name, String leader_name, int ch_id, int lease)
		{
			this.clan_name = clan_name;
			this.leader_name = leader_name;
			this.ch_id = ch_id;
			this.getType = lease;
		}
	}
}