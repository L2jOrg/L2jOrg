package l2s.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import l2s.gameserver.model.Playable;
import l2s.gameserver.model.actor.instances.creature.Abnormal;
import l2s.gameserver.utils.AbnormalsComparator;
import l2s.gameserver.utils.SkillUtils;

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
			l2s.gameserver.model.actor.instances.creature.Abnormal[] effects = activeChar.getAbnormalList().toArray();
			Arrays.sort(effects, AbnormalsComparator.getInstance());
			for(l2s.gameserver.model.actor.instances.creature.Abnormal effect : effects)
			{
				if(effect != null)
					effect.addPartySpelledIcon(this);
			}
		}
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_type);
		writeD(_objId);
		writeD(_effects.size());
		for(Abnormal temp : _effects)
		{
			writeD(temp._skillId);
			writeH(temp._level);
			writeD(0x00); // UNK Ertheia
			writeH(temp._duration);
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