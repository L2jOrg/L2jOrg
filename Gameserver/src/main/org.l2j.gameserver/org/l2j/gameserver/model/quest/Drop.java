package org.l2j.gameserver.model.quest;

import java.util.Arrays;

import static org.l2j.commons.util.Util.INT_ARRAY_EMPTY;

public class Drop
{
	public final int condition;
	public final int maxcount;
	public final int chance;

	public int[] itemList = INT_ARRAY_EMPTY;

	public Drop(int condition, int maxcount, int chance)
	{
		this.condition = condition;
		this.maxcount = maxcount;
		this.chance = chance;
	}

	public Drop addItem(int item) {
		itemList = Arrays.copyOf(itemList, itemList.length + 1);
		itemList[itemList.length -1] = item;
		return this;
	}
}