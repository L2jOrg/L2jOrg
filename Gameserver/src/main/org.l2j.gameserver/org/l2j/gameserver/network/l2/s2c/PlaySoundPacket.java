package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.utils.Location;

public class PlaySoundPacket extends L2GameServerPacket
{
	public static final L2GameServerPacket SIEGE_VICTORY = new PlaySoundPacket("Siege_Victory");
	public static final L2GameServerPacket B04_S01 = new PlaySoundPacket("B04_S01");
	public static final L2GameServerPacket HB01 = new PlaySoundPacket(PlaySoundPacket.Type.MUSIC, "HB01", 0, 0, 0, 0, 0);
	public static final L2GameServerPacket BROKEN_KEY = new PlaySoundPacket("ItemSound2.broken_key");

	public enum Type
	{
		SOUND,
		MUSIC,
		VOICE
	}

	private Type _type;
	private String _soundFile;
	private int _hasCenterObject;
	private int _objectId;
	private int _x, _y, _z;

	public PlaySoundPacket(String soundFile)
	{
		this(Type.SOUND, soundFile, 0, 0, 0, 0, 0);
	}

	public PlaySoundPacket(Type type, String soundFile, int c, int objectId, Location loc)
	{
		this(type, soundFile, c, objectId, loc == null ? 0 : loc.x, loc == null ? 0 : loc.y, loc == null ? 0 : loc.z);
	}

	public PlaySoundPacket(Type type, String soundFile, int c, int objectId, int x, int y, int z)
	{
		_type = type;
		_soundFile = soundFile;
		_hasCenterObject = c;
		_objectId = objectId;
		_x = x;
		_y = y;
		_z = z;
	}

	@Override
	protected final void writeImpl()
	{
		//dSdddddd
		writeInt(_type.ordinal()); //0 for quest and ship, c4 toturial = 2
		writeString(_soundFile);
		writeInt(_hasCenterObject); //0 for quest; 1 for ship;
		writeInt(_objectId); //0 for quest; objectId of ship
		writeInt(_x); //x
		writeInt(_y); //y
		writeInt(_z); //z
	}
}