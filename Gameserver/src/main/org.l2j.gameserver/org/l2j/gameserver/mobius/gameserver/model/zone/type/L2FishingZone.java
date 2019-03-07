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
package org.l2j.gameserver.mobius.gameserver.model.zone.type;

import com.l2jmobius.Config;
import com.l2jmobius.commons.concurrent.ThreadPool;
import com.l2jmobius.gameserver.model.Fishing;
import com.l2jmobius.gameserver.model.PcCondOverride;
import com.l2jmobius.gameserver.model.actor.L2Character;
import com.l2jmobius.gameserver.model.actor.instance.L2PcInstance;
import com.l2jmobius.gameserver.model.zone.L2ZoneType;
import com.l2jmobius.gameserver.model.zone.ZoneId;
import com.l2jmobius.gameserver.network.serverpackets.fishing.ExAutoFishAvailable;

import java.lang.ref.WeakReference;

/**
 * A fishing zone
 * @author durgus
 */
public class L2FishingZone extends L2ZoneType
{
	public L2FishingZone(int id)
	{
		super(id);
	}
	
	@Override
	protected void onEnter(L2Character character)
	{
		if (character.isPlayer())
		{
			if ((Config.ALLOW_FISHING || character.canOverrideCond(PcCondOverride.ZONE_CONDITIONS)) && !character.isInsideZone(ZoneId.FISHING))
			{
				final WeakReference<L2PcInstance> weakPlayer = new WeakReference<>(character.getActingPlayer());
				ThreadPool.execute(new Runnable()
				{
					@Override
					public void run()
					{
						final L2PcInstance player = weakPlayer.get();
						if (player != null)
						{
							final Fishing fishing = player.getFishing();
							if (player.isInsideZone(ZoneId.FISHING))
							{
								if (fishing.canFish() && !fishing.isFishing())
								{
									if (fishing.isAtValidLocation())
									{
										player.sendPacket(ExAutoFishAvailable.YES);
									}
									else
									{
										player.sendPacket(ExAutoFishAvailable.NO);
									}
								}
								ThreadPool.schedule(this, 1500);
							}
							else
							{
								player.sendPacket(ExAutoFishAvailable.NO);
							}
						}
					}
				});
			}
			character.setInsideZone(ZoneId.FISHING, true);
		}
	}
	
	@Override
	protected void onExit(L2Character character)
	{
		if (character.isPlayer())
		{
			character.setInsideZone(ZoneId.FISHING, false);
			character.sendPacket(ExAutoFishAvailable.NO);
		}
	}
	
	/*
	 * getWaterZ() this added function returns the Z value for the water surface. In effect this simply returns the upper Z value of the zone. This required some modification of L2ZoneForm, and zone form extensions.
	 */
	public int getWaterZ()
	{
		return getZone().getHighZ();
	}
}
