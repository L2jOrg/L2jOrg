package org.l2j.gameserver.mobius.gameserver.network.serverpackets;


import org.l2j.gameserver.mobius.gameserver.model.L2Clan;
import org.l2j.gameserver.mobius.gameserver.model.skills.Skill;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

/**
 * @author -Wooden-
 */
public class PledgeSkillList implements IClientOutgoingPacket
{
	private final Skill[] _skills;
	private final SubPledgeSkill[] _subSkills;
	
	public static class SubPledgeSkill
	{
		int _subType;
		int _skillId;
		int _skillLvl;
		
		public SubPledgeSkill(int subType, int skillId, int skillLvl)
		{
			_subType = subType;
			_skillId = skillId;
			_skillLvl = skillLvl;
		}
	}
	
	public PledgeSkillList(L2Clan clan)
	{
		_skills = clan.getAllSkills();
		_subSkills = clan.getAllSubSkills();
	}
	
	@Override
	public boolean write(PacketWriter packet)
	{
		OutgoingPackets.PLEDGE_SKILL_LIST.writeId(packet);
		
		packet.writeD(_skills.length);
		packet.writeD(_subSkills.length); // Squad skill length
		for (Skill sk : _skills)
		{
			packet.writeD(sk.getDisplayId());
			packet.writeH(sk.getDisplayLevel());
			packet.writeH(0x00); // Sub level
		}
		for (SubPledgeSkill sk : _subSkills)
		{
			packet.writeD(sk._subType); // Clan Sub-unit types
			packet.writeD(sk._skillId);
			packet.writeH(sk._skillLvl);
			packet.writeH(0x00); // Sub level
		}
		return true;
	}
}
