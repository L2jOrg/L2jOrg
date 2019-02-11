package org.l2j.gameserver.network.l2.s2c;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.l2j.gameserver.model.Playable;
import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.utils.AbnormalsComparator;

public class PartySpelledPacket extends L2GameServerPacket
{
	private final int _type;
	private final int _objId;
	private final List<Abnormal> _effects;

	public PartySpelledPacket(Playable activeChar, boolean full)
	{
		_objId = activeChar.getObjectId();
		_type = activeChar.isPet() ? 1 : activeChar.isSummon() ? 2 : 0;
		// 0 - L2Player // 1 - петы // 2 - саммоны
		_effects = new ArrayList<Abnormal>();
		if(full)
		{
			org.l2j.gameserver.model.actor.instances.creature.Abnormal[] effects = activeChar.getAbnormalList().toArray();
			Arrays.sort(effects, AbnormalsComparator.getInstance());
			for(org.l2j.gameserver.model.actor.instances.creature.Abnormal effect : effects)
			{
				if(effect != null)
					effect.addPartySpelledIcon(this);
			}
		}
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_type);
		buffer.putInt(_objId);
		buffer.putInt(_effects.size());
		for(Abnormal temp : _effects)
		{
			buffer.putInt(temp._skillId);
			buffer.putShort((short) temp._level);
			buffer.putInt(0x00); // UNK Ertheia
			buffer.putShort((short) temp._duration);
		}
	}

	public void addPartySpelledEffect(int skillId, int level, int duration)
	{
		_effects.add(new Abnormal(skillId, level, duration));
	}

	static class Abnormal
	{
		final int _skillId;
		final int _level;
		final int _duration;

		public Abnormal(int skillId, int level, int duration)
		{
			_skillId = skillId;
			_level = level;
			_duration = duration;
		}
	}
}