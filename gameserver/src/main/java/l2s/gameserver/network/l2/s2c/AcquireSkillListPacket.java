package l2s.gameserver.network.l2.s2c;

import java.util.Collection;
import java.util.List;

import l2s.gameserver.data.xml.holder.SkillAcquireHolder;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.SkillLearn;
import l2s.gameserver.model.base.AcquireType;
import l2s.gameserver.templates.item.data.ItemData;

/**
 * @author VISTALL
 * @date 22:22/25.05.2011
 */
public class AcquireSkillListPacket extends L2GameServerPacket
{
	private Player _player;
	private Collection<SkillLearn> _skills;

	public AcquireSkillListPacket(Player player)
	{
		_player = player;
		_skills = SkillAcquireHolder.getInstance().getAcquirableSkillListByClass(player);
	}

	@Override
	protected final void writeImpl()
	{
		writeH(_skills.size());
		for(SkillLearn sk : _skills)
		{
			Skill skill = SkillHolder.getInstance().getSkill(sk.getId(), sk.getLevel());
			if(skill == null)
				continue;

			writeD(sk.getId());
			writeH(sk.getLevel());
			writeQ(sk.getCost());
			writeC(sk.getMinLevel());
			writeC(0x00);

			List<ItemData> requiredItems = sk.getRequiredItemsForLearn(AcquireType.NORMAL);
			writeC(requiredItems.size());
			for(ItemData item : requiredItems)
			{
				writeD(item.getId());
				writeQ(item.getCount());
			}

			writeC(0x00);
		}
	}
}