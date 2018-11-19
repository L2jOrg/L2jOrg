package l2s.gameserver.network.l2.s2c;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * sample
 *
 * 0000: 85 02 00 10 04 00 00 01 00 4b 02 00 00 2c 04 00    .........K...,..
 * 0010: 00 01 00 58 02 00 00                               ...X...
 *
 *
 * format   h (dhd)
 *
 * @version $Revision: 1.3.2.1.2.6 $ $Date: 2005/04/05 19:41:08 $
 */
public class AbnormalStatusUpdatePacket extends L2GameServerPacket
{
	public static final int INFINITIVE_EFFECT = -1;
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

	public AbnormalStatusUpdatePacket()
	{
		_effects = new ArrayList<Abnormal>();
	}

	public void addEffect(int skillId, int dat, int duration)
	{
		_effects.add(new Abnormal(skillId, dat, duration));
	}

	@Override
	protected final void writeImpl()
	{
		writeH(_effects.size());

		for(Abnormal temp : _effects)
		{
			writeD(temp.skillId);
			writeH(temp.dat);
			writeD(0x00); // UNK Ertheia
			writeH(temp.duration);
		}
	}
}