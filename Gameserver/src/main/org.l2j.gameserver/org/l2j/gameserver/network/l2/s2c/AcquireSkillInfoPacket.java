package org.l2j.gameserver.network.l2.s2c;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.l2j.gameserver.model.SkillLearn;
import org.l2j.gameserver.model.base.AcquireType;
import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.templates.item.data.ItemData;

/**
 * Reworked: VISTALL
 */
public class AcquireSkillInfoPacket extends L2GameServerPacket
{
	private SkillLearn _learn;
	private AcquireType _type;
	private List<Require> _reqs = Collections.emptyList();

	public AcquireSkillInfoPacket(AcquireType type, SkillLearn learn)
	{
		_type = type;
		_learn = learn;
		_reqs = new ArrayList<Require>();
		for(ItemData item : _learn.getRequiredItemsForLearn(type))
			_reqs.add(new Require(99, item.getId(), item.getCount(), 50));
	}

	@Override
	public void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_learn.getId());
		buffer.putInt(_learn.getLevel());
		buffer.putLong(_learn.getCost()); // sp/rep
		buffer.putInt(_type.getId());

		buffer.putInt(_reqs.size()); //requires size

		for(Require temp : _reqs)
		{
			buffer.putInt(temp.type);
			buffer.putInt(temp.itemId);
			buffer.putLong(temp.count);
			buffer.putInt(temp.unk);
		}
	}

	private static class Require
	{
		public int itemId;
		public long count;
		public int type;
		public int unk;

		public Require(int pType, int pItemId, long pCount, int pUnk)
		{
			itemId = pItemId;
			type = pType;
			count = pCount;
			unk = pUnk;
		}
	}
}