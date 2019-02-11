package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.network.l2.GameClient;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class ExAbnormalStatusUpdateFromTargetPacket extends L2GameServerPacket
{
	public static final int INFINITIVE_EFFECT = -1;
	private List<Abnormal> _effects;
	private int _objectId;

	class Abnormal
	{
		int skillId;
		int dat;
		int duration;
		int effectorObjectId;
		int comboId;

		public Abnormal(int effectorObjectId, int skillId, int dat, int duration, int comboId)
		{
			this.skillId = skillId;
			this.dat = dat;
			this.duration = duration;
			this.effectorObjectId = effectorObjectId;
			this.comboId = comboId;
		}
	}

	public ExAbnormalStatusUpdateFromTargetPacket(int objId)
	{
		_objectId = objId;
		_effects = new ArrayList<Abnormal>();
	}

	public void addEffect(int effectorObjectId, int skillId, int dat, int duration, int comboId)
	{
		_effects.add(new Abnormal(effectorObjectId, skillId, dat, duration, comboId));
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_objectId);
		buffer.putShort((short) _effects.size());
		for(Abnormal temp : _effects)
		{
			buffer.putInt(temp.skillId);
			buffer.putShort((short) temp.dat);
			buffer.putShort((short) temp.comboId); // combo type ???
			buffer.putShort((short) temp.duration);
			buffer.putInt(temp.effectorObjectId); // Buffer OID
		}
	}
}