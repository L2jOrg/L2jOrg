package l2s.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.List;

import l2s.gameserver.data.xml.holder.ResidenceHolder;
import l2s.gameserver.model.entity.residence.clanhall.NormalClanHall;
import l2s.gameserver.model.pledge.Clan;
import l2s.gameserver.tables.ClanTable;

public class ExShowAgitSiegeInfo extends L2GameServerPacket
{
	private final List<AgitInfo> _infos;

	public ExShowAgitSiegeInfo()
	{
		List<NormalClanHall> clanHalls = ResidenceHolder.getInstance().getResidenceList(NormalClanHall.class);
		_infos = new ArrayList<AgitInfo>(clanHalls.size());
		clanHalls.forEach(clanHall ->
		{
			int ch_id = clanHall.getId();
			int getType = clanHall.getClanHallType().ordinal();
			Clan clan = ClanTable.getInstance().getClan(clanHall.getOwnerId());
			String clan_name = clanHall.getOwnerId() == 0 || clan == null ? "" : clan.getName();
			String leader_name = clanHall.getOwnerId() == 0 || clan == null ? "" : clan.getLeaderName();
			int siegeDate = (int) (clanHall.getSiegeDate().getTimeInMillis() / 1000);
			_infos.add(new AgitInfo(clan_name, leader_name, ch_id, getType, siegeDate));
		});
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_infos.size());
		_infos.forEach(info ->
		{
			writeD(info.ch_id);
			writeD(info.siegeDate);
			writeString(info.clan_name);
			writeString(info.leader_name);
			writeH(info.getType);
		});
	}

	static class AgitInfo
	{
		public String clan_name;
		public String leader_name;
		public int ch_id;
		public int getType;
		public int siegeDate;

		public AgitInfo(String clan_name, String leader_name, int ch_id, int lease, int siegeDate)
		{
			this.clan_name = clan_name;
			this.leader_name = leader_name;
			this.ch_id = ch_id;
			this.getType = lease;
			this.siegeDate = siegeDate;
		}
	}

}

