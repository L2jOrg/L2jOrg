package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Servitor;
import org.l2j.gameserver.utils.Location;

public class PetStatusUpdatePacket extends L2GameServerPacket
{
	private int type, obj_id, level;
	private int maxFed, curFed, maxHp, curHp, maxMp, curMp;
	private long exp, exp_this_lvl, exp_next_lvl;
	private Location _loc;
	private String title;

	public PetStatusUpdatePacket(final Servitor summon)
	{
		type = summon.getServitorType();
		obj_id = summon.getObjectId();
		_loc = summon.getLoc();

		title = summon.getTitle();
		if(title.equals(Servitor.TITLE_BY_OWNER_NAME))
			title = summon.getPlayer().getVisibleName(summon.getPlayer());

		curHp = (int) summon.getCurrentHp();
		maxHp = summon.getMaxHp();
		curMp = (int) summon.getCurrentMp();
		maxMp = summon.getMaxMp();
		curFed = summon.getCurrentFed();
		maxFed = summon.getMaxFed();
		level = summon.getLevel();
		exp = summon.getExp();
		exp_this_lvl = summon.getExpForThisLevel();
		exp_next_lvl = summon.getExpForNextLevel();
	}

	@Override
	protected final void writeImpl()
	{
		writeInt(type);
		writeInt(obj_id);
		writeInt(_loc.x);
		writeInt(_loc.y);
		writeInt(_loc.z);
		writeString(title);
		writeInt(curFed);
		writeInt(maxFed);
		writeInt(curHp);
		writeInt(maxHp);
		writeInt(curMp);
		writeInt(maxMp);
		writeInt(level);
		writeLong(exp);
		writeLong(exp_this_lvl);// 0% absolute value
		writeLong(exp_next_lvl);// 100% absolute value
		writeInt(0); // ???
	}
}