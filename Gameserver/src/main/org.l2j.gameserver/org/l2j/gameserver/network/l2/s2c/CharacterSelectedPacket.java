package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.GameTimeController;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.utils.Location;

import java.nio.ByteBuffer;

public class CharacterSelectedPacket extends L2GameServerPacket
{
	//   SdSddddddddddffddddddddddddddddddddddddddddddddddddddddd d
	private int _sessionId, char_id, clan_id, sex, race, class_id;
	private String _name, _title;
	private Location _loc;
	private double curHp, curMp;
	private int level, karma, _pk;
	private long _exp, _sp;

	public CharacterSelectedPacket(final Player cha, final int sessionId)
	{
		_sessionId = sessionId;

		_name = cha.getName();
		char_id = cha.getObjectId(); //FIXME 0x00030b7a ??
		_title = cha.getTitle();
		clan_id = cha.getClanId();
		sex = cha.getSex().ordinal();
		race = cha.getRace().ordinal();
		class_id = cha.getClassId().getId();
		_loc = cha.getLoc();
		curHp = cha.getCurrentHp();
		curMp = cha.getCurrentMp();
		_sp = cha.getSp();
		_exp = cha.getExp();
		level = cha.getLevel();
		karma = cha.getKarma();
		_pk = cha.getPkKills();
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		writeString(_name, buffer);
		buffer.putInt(char_id);
		writeString(_title, buffer);
		buffer.putInt(_sessionId);
		buffer.putInt(clan_id);
		buffer.putInt(0x00); //??
		buffer.putInt(sex);
		buffer.putInt(race);
		buffer.putInt(class_id);
		buffer.putInt(0x01); // active ??
		buffer.putInt(_loc.x);
		buffer.putInt(_loc.y);
		buffer.putInt(_loc.z);

		buffer.putDouble(curHp);
		buffer.putDouble(curMp);
		buffer.putLong(_sp);
		buffer.putLong(_exp);
		buffer.putInt(level);
		buffer.putInt(karma); //?
		buffer.putInt(_pk);
		// extra info
		buffer.putInt(GameTimeController.getInstance().getGameTime()); // in-game time
		buffer.putInt(0x00); //
		buffer.putInt(0x00); // Default classId

		buffer.putInt(0);
		buffer.putInt(0);
		buffer.putInt(0);
		buffer.putInt(0);

		buffer.put(new byte[64]);
		buffer.putInt(0);
	}
}