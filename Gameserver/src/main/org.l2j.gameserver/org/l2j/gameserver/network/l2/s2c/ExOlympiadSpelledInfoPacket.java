package org.l2j.gameserver.network.l2.s2c;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.GameClient;

public class ExOlympiadSpelledInfoPacket extends L2GameServerPacket
{
	// chdd(dhd)
	private int char_obj_id = 0;
	private List<Abnormal> _effects;

	class Abnormal
	{
		int skillId;
		int dat;
		int duration;

		public Abnormal(int skillId, int dat, int duration)
		{
			this.skillId = skillId;
			this.dat = dat;
			this.duration = duration;
		}
	}

	public ExOlympiadSpelledInfoPacket()
	{
		_effects = new ArrayList<Abnormal>();
	}

	public void addEffect(int skillId, int dat, int duration)
	{
		_effects.add(new Abnormal(skillId, dat, duration));
	}

	public void addSpellRecivedPlayer(Player cha)
	{
		if(cha != null)
			char_obj_id = cha.getObjectId();
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(char_obj_id);
		buffer.putInt(_effects.size());
		for(Abnormal temp : _effects)
		{
			buffer.putInt(temp.skillId);
			buffer.putShort((short) temp.dat); // @Rivelia. Skill level by mask.
			buffer.putShort((short) 0); // @Rivelia. Sub skill level by mask.
			buffer.putInt(0);	//ERTHEIA
			buffer.putShort((short) temp.duration);
		}
	}
}