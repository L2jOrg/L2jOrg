package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.GameTimeController;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.utils.Location;

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
	protected final void writeImpl()
	{
		writeString(_name);
		writeInt(char_id);
		writeString(_title);
		writeInt(_sessionId);
		writeInt(clan_id);
		writeInt(0x00); //??
		writeInt(sex);
		writeInt(race);
		writeInt(class_id);
		writeInt(0x01); // active ??
		writeInt(_loc.x);
		writeInt(_loc.y);
		writeInt(_loc.z);

		writeF(curHp);
		writeF(curMp);
		writeLong(_sp);
		writeLong(_exp);
		writeInt(level);
		writeInt(karma); //?
		writeInt(_pk);
		// extra info
		writeInt(GameTimeController.getInstance().getGameTime()); // in-game time
		writeInt(0x00); //
		writeInt(0x00); // Default classId

		writeInt(0);
		writeInt(0);
		writeInt(0);
		writeInt(0);

		writeB(new byte[64]);
		writeInt(0);
	}
}