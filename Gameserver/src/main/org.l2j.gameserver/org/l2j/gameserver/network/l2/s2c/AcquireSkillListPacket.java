package org.l2j.gameserver.network.l2.s2c;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.List;

import org.l2j.gameserver.data.xml.holder.SkillAcquireHolder;
import org.l2j.gameserver.data.xml.holder.SkillHolder;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.Skill;
import org.l2j.gameserver.model.SkillLearn;
import org.l2j.gameserver.model.base.AcquireType;
import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.templates.item.data.ItemData;

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
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putShort((short) _skills.size());
		for(SkillLearn sk : _skills)
		{
			Skill skill = SkillHolder.getInstance().getSkill(sk.getId(), sk.getLevel());
			if(skill == null)
				continue;

			buffer.putInt(sk.getId());
			buffer.putShort((short) sk.getLevel());
			buffer.putLong(sk.getCost());
			buffer.put((byte)sk.getMinLevel());
			buffer.put((byte)0x00);

			List<ItemData> requiredItems = sk.getRequiredItemsForLearn(AcquireType.NORMAL);
			buffer.put((byte)requiredItems.size());
			for(ItemData item : requiredItems)
			{
				buffer.putInt(item.getId());
				buffer.putLong(item.getCount());
			}

			buffer.put((byte)0x00);
		}
	}
}