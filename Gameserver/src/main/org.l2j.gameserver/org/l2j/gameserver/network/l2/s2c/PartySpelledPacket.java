package org.l2j.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.l2j.gameserver.model.Playable;
import org.l2j.gameserver.model.actor.instances.creature.Abnormal;
import org.l2j.gameserver.utils.AbnormalsComparator;
import org.l2j.gameserver.utils.SkillUtils;

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
	protected final void writeImpl()
	{
		writeInt(_type);
		writeInt(_objId);
		writeInt(_effects.size());
		for(Abnormal temp : _effects)
		{
			writeInt(temp._skillId);
			writeShort(temp._level);
			writeInt(0x00); // UNK Ertheia
			writeShort(temp._duration);
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