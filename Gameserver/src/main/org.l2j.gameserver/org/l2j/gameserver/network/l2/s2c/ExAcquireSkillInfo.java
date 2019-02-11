package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.data.xml.holder.SkillHolder;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.Skill;
import org.l2j.gameserver.model.SkillLearn;
import org.l2j.gameserver.model.base.AcquireType;
import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.templates.item.data.ItemData;

import java.nio.ByteBuffer;
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
	public void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_learn.getId());
		buffer.putInt(_learn.getLevel());
		buffer.putLong(_learn.getCost());
		buffer.putShort((short) _learn.getMinLevel());
		buffer.putShort((short) 0x00); // Dual-class min level.

		buffer.putInt(_requiredItems.size());
		for(ItemData item : _requiredItems)
		{
			buffer.putInt(item.getId());
			buffer.putLong(item.getCount());
		}

		buffer.putInt(0x00);
	}
}