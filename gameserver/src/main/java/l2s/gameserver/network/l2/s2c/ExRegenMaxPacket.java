package l2s.gameserver.network.l2.s2c;

public class ExRegenMaxPacket extends L2GameServerPacket
{
	private double _max;
	private int _count;
	private int _time;

	public ExRegenMaxPacket(double max, int count, int time)
	{
		_max = max * .66;
		_count = count;
		_time = time;
	}

	public static final int POTION_HEALING_GREATER = 16457;
	public static final int POTION_HEALING_MEDIUM = 16440;
	public static final int POTION_HEALING_LESSER = 16416;

	/**
	 * Пример пакета - Пришло после использования Healing Potion (инфа для Interlude, в Kamael пакет не изменился)
	 *
	 * FE 01 00 01 00 00 00 0F 00 00 00 03 00 00 00 00 00 00 00 00 00 38 40 // Healing Potion
	 * FE 01 00 01 00 00 00 0F 00 00 00 03 00 00 00 00 00 00 00 00 00 49 40 // Greater Healing Potion
	 * FE 01 00 01 00 00 00 0F 00 00 00 03 00 00 00 00 00 00 00 00 00 20 40 // Lesser Healing Potion
	 *
	 * FE - тип
	 * 01 00 - субтип
	 * 01 00 00 00 - хз что
	 * 0F 00 00 00 - count?
	 * 03 00 00 00 - время?
	 * 00 00 00 00 00 00 38 40 - максимум?
	 */
	@Override
	protected void writeImpl()
	{
		writeD(1);
		writeD(_count);
		writeD(_time);
		writeF(_max);
	}
}