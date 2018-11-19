package l2s.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.List;

import l2s.gameserver.model.Player;
import l2s.gameserver.model.Servitor;

public class ExEventMatchTeamInfo extends L2GameServerPacket
{
	@SuppressWarnings("unused")
	private int leader_id, loot;
	private List<EventMatchTeamInfo> members = new ArrayList<EventMatchTeamInfo>();

	public ExEventMatchTeamInfo(List<Player> party, Player exclude)
	{
		leader_id = party.get(0).getObjectId();
		loot = party.get(0).getParty().getLootDistribution();

		for(Player member : party)
			if(!member.equals(exclude))
				members.add(new EventMatchTeamInfo(member));
	}

	@Override
	protected void writeImpl()
	{
		// TODO dcd[dSdddddddddd]
	}

	public static class EventMatchTeamInfo
	{
		public MathMember member;
		public List<MathMember> m_servitors;

		public EventMatchTeamInfo(Player player)
		{
			member = new MathMember();
			member.name = player.getName();
			member.objId = player.getObjectId();
			member.curCp = (int) player.getCurrentCp();
			member.maxCp = player.getMaxCp();
			member.curHp = (int) player.getCurrentHp();
			member.maxHp = player.getMaxHp();
			member.curMp = (int) player.getCurrentMp();
			member.maxMp = player.getMaxMp();
			member.level = player.getLevel();
			member.classId = player.getClassId().getId();
			member.raceId = player.getRace().ordinal();

			m_servitors = new ArrayList<MathMember>();

			for(Servitor s : player.getServitors())
			{
				MathMember m_servitor = new MathMember();
				m_servitor.name = s.getName();
				m_servitor.objId = s.getObjectId();
				m_servitor.npcId = s.getNpcId() + 1000000;
				m_servitor.curHp = (int) s.getCurrentHp();
				m_servitor.maxHp = s.getMaxHp();
				m_servitor.curMp = (int) s.getCurrentMp();
				m_servitor.maxMp = s.getMaxMp();
				m_servitor.level = s.getLevel();
				m_servitors.add(m_servitor);
			}
		}
	}

	public static class MathMember
	{
		public String name;
		public int objId, npcId, curCp, maxCp, curHp, maxHp, curMp, maxMp, level, classId, raceId;
	}
}