package l2s.gameserver.network.l2.s2c;

public class SocialActionPacket extends L2GameServerPacket
{
	private int _playerId;
	private int _actionId;

	public static final int GREETING = 2;
	public static final int VICTORY = 3;
	public static final int ADVANCE = 4;
	public static final int NO = 5;
	public static final int YES = 6;
	public static final int BOW = 7;
	public static final int UNAWARE = 8;
	public static final int WAITING = 9;
	public static final int LAUGH = 10;
	public static final int APPLAUD = 11;
	public static final int DANCE = 12;
	public static final int SORROW = 13;
	public static final int CHARM = 14;
	public static final int SHYNESS = 15;
	public static final int COUPLE_BOW = 16;
	public static final int COUPLE_HIGH_FIVE = 17;
	public static final int COUPLE_DANCE = 18;
	public static final int AWAKENING = 20;
	public static final int REAWAKENING = 21; // - Привзлетает и вспыхивает красным туманом.
	public static final int PROPOSE = 28;
	public static final int PROVOKE = 29;
	public static final int BOASTING = 30;
	/*
		22 - Подпрыгивает, вспышка и персонаж исчезает.
		31 - Блестки летают вокруг и кружится персонаж на месте. (Используется автоматически после использования салона красоты)
		32 - Разминается с битой а потом бьет ею.
		33 - Танцует как грузин.
		34 - Играет на муз. инструменте.
		35 - Дает ляпас.
		36 - Кидает бейсбольный мятч.
	*/
	public static final int LEVEL_UP = 2122;
	public static final int GIVE_HERO = 20016;

	public SocialActionPacket(int playerId, int actionId)
	{
		_playerId = playerId;
		_actionId = actionId;
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_playerId);
		writeD(_actionId);
		writeD(0); // ??? 0
	}
}