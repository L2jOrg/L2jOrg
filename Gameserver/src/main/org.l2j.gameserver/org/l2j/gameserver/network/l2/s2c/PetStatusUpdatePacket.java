package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.model.Servitor;
import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.utils.Location;

import java.nio.ByteBuffer;

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
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(type);
		buffer.putInt(obj_id);
		buffer.putInt(_loc.x);
		buffer.putInt(_loc.y);
		buffer.putInt(_loc.z);
		writeString(title, buffer);
		buffer.putInt(curFed);
		buffer.putInt(maxFed);
		buffer.putInt(curHp);
		buffer.putInt(maxHp);
		buffer.putInt(curMp);
		buffer.putInt(maxMp);
		buffer.putInt(level);
		buffer.putLong(exp);
		buffer.putLong(exp_this_lvl);// 0% absolute value
		buffer.putLong(exp_next_lvl);// 100% absolute value
		buffer.putInt(0); // ???
	}
}