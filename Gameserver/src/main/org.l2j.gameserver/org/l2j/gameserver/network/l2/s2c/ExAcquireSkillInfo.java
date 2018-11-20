package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.data.xml.holder.SkillHolder;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.Skill;
import org.l2j.gameserver.model.SkillLearn;
import org.l2j.gameserver.model.base.AcquireType;
import org.l2j.gameserver.templates.item.data.ItemData;

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
		writeInt(_learn.getId());
		writeInt(_learn.getLevel());
		writeLong(_learn.getCost());
		writeShort(_learn.getMinLevel());
		writeShort(0x00); // Dual-class min level.

		writeInt(_requiredItems.size());
		for(ItemData item : _requiredItems)
		{
			writeInt(item.getId());
			writeLong(item.getCount());
		}

		writeInt(0x00);
	}
}