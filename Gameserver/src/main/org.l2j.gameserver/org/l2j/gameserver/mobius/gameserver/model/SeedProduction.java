/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.mobius.gameserver.model;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author xban1x
 */
public class SeedProduction
{
	private final int _seedId;
	private final long _price;
	private final long _startAmount;
	private final AtomicLong _amount;
	
	public SeedProduction(int id, long amount, long price, long startAmount)
	{
		_seedId = id;
		_amount = new AtomicLong(amount);
		_price = price;
		_startAmount = startAmount;
	}
	
	public final int getId()
	{
		return _seedId;
	}
	
	public final long getAmount()
	{
		return _amount.get();
	}
	
	public final long getPrice()
	{
		return _price;
	}
	
	public final long getStartAmount()
	{
		return _startAmount;
	}
	
	public final void setAmount(long amount)
	{
		_amount.set(amount);
	}
	
	public final boolean decreaseAmount(long val)
	{
		long current;
		long next;
		do
		{
			current = _amount.get();
			next = current - val;
			if (next < 0)
			{
				return false;
			}
		}
		while (!_amount.compareAndSet(current, next));
		return true;
	}
}