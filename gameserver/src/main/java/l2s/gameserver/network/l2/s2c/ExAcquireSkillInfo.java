package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Skill;
import l2s.gameserver.model.SkillLearn;
import l2s.gameserver.model.base.AcquireType;
import l2s.gameserver.templates.item.data.ItemData;

import java.util.List;

public class ExAcquireSkillInfo extends L2GameServerPacket
{
	private Skill _skill;
	private List<ItemData> _requiredItems;
	private SkillLearn _learn;

	public ExAcquireSkillInfo(Player player, AcquireType type, SkillLearn learn)
	{
		_learn = learn;
		_requiredItems = _learn.getRequiredItemsForLearn(type);
		_skill = SkillHolder.getInstance().getSkill(_learn.getId(), _learn.getLevel());
	}

	@Override
	public void writeImpl()
	{
		writeD(_learn.getId());
		writeD(_learn.getLevel());
		writeQ(_learn.getCost());
		writeH(_learn.getMinLevel());
		writeH(0x00); // Dual-class min level.

		writeD(_requiredItems.size());
		for(ItemData item : _requiredItems)
		{
			writeD(item.getId());
			writeQ(item.getCount());
		}

		writeD(0x00);
	}
}