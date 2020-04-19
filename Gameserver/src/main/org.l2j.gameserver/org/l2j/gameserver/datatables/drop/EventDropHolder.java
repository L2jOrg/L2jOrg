package org.l2j.gameserver.datatables.drop;

import io.github.joealisson.primitive.IntCollection;
import org.l2j.gameserver.model.holders.DropHolder;

/**
 * @author Mobius
 * @author JoeAlisson
 */
public class EventDropHolder extends DropHolder {

	private final int minLevel;
	private final int maxLevel;
	private final IntCollection monsterIds;
	
	public EventDropHolder(int itemId, long min, long max, double chance, int minLevel, int maxLevel, IntCollection monsterIds) {
		super(null, itemId, min, max, chance);
		this.minLevel = minLevel;
		this.maxLevel = maxLevel;
		this.monsterIds = monsterIds;
	}
	
	public int getMinLevel()
	{
		return minLevel;
	}
	
	public int getMaxLevel()
	{
		return maxLevel;
	}

	public boolean hasMonster(int id) {
		return monsterIds.contains(id);
	}

	public boolean checkLevel(int level) {
		return minLevel <= level && maxLevel >= level;
	}
}
