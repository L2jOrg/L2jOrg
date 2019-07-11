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
package handlers.effecthandlers;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.data.xml.impl.CubicData;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.templates.L2CubicTemplate;
import org.l2j.gameserver.model.cubic.CubicInstance;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.model.stats.Stats;
import org.l2j.gameserver.network.serverpackets.ExUserInfoCubic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Summon Cubic effect implementation.
 * @author Zoey76
 */
public final class SummonCubic extends AbstractEffect
{
	private static final Logger LOGGER = LoggerFactory.getLogger(SummonCubic.class);
	
	private final int _cubicId;
	private final int _cubicLvl;
	
	public SummonCubic(StatsSet params)
	{
		_cubicId = params.getInt("cubicId", -1);
		_cubicLvl = params.getInt("cubicLvl", 0);
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(Creature effector, Creature effected, Skill skill, Item item)
	{
		if (!effected.isPlayer() || effected.isAlikeDead() || effected.getActingPlayer().inObserverMode())
		{
			return;
		}
		
		if (_cubicId < 0)
		{
			LOGGER.warn(SummonCubic.class.getSimpleName() + ": Invalid Cubic ID:" + _cubicId + " in skill ID: " + skill.getId());
			return;
		}
		
		final Player player = effected.getActingPlayer();
		if (player.inObserverMode() || player.isMounted())
		{
			return;
		}
		
		// If cubic is already present, it's replaced.
		final CubicInstance cubic = player.getCubicById(_cubicId);
		if (cubic != null)
		{
			if (cubic.getTemplate().getLevel() > _cubicLvl)
			{
				// What do we do in such case?
				return;
			}
			
			cubic.deactivate();
		}
		else
		{
			// If maximum amount is reached, random cubic is removed.
			// Players with no mastery can have only one cubic.
			final int allowedCubicCount = (int) effected.getActingPlayer().getStat().getValue(Stats.MAX_CUBIC, 1);
			final int currentCubicCount = player.getCubics().size();
			// Extra cubics are removed, one by one, randomly.
			for (int i = 0; i <= (currentCubicCount - allowedCubicCount); i++)
			{
				final int removedCubicId = (int) player.getCubics().keySet().toArray()[Rnd.get(currentCubicCount)];
				final CubicInstance removedCubic = player.getCubicById(removedCubicId);
				removedCubic.deactivate();
			}
		}
		
		final L2CubicTemplate template = CubicData.getInstance().getCubicTemplate(_cubicId, _cubicLvl);
		if (template == null)
		{
			LOGGER.warn("Attempting to summon cubic without existing template id: " + _cubicId + " level: " + _cubicLvl);
			return;
		}
		
		// Adding a new cubic.
		player.addCubic(new CubicInstance(effected.getActingPlayer(), effector.getActingPlayer(), template));
		player.sendPacket(new ExUserInfoCubic(player));
		player.broadcastCharInfo();
	}
}
