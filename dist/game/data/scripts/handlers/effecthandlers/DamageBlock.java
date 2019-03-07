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

import com.l2jmobius.gameserver.model.StatsSet;
import com.l2jmobius.gameserver.model.effects.AbstractEffect;
import com.l2jmobius.gameserver.model.effects.EffectFlag;

/**
 * Effect that blocks damage and heals to HP/MP. <BR>
 * Regeneration or DOT shouldn't be blocked, Vampiric Rage and Balance Life as well.
 * @author Nik
 */
public final class DamageBlock extends AbstractEffect
{
	private final boolean _blockHp;
	private final boolean _blockMp;
	
	public DamageBlock(StatsSet params)
	{
		final String type = params.getString("type", null);
		_blockHp = type.equalsIgnoreCase("BLOCK_HP");
		_blockMp = type.equalsIgnoreCase("BLOCK_MP");
	}
	
	@Override
	public long getEffectFlags()
	{
		return _blockHp ? EffectFlag.HP_BLOCK.getMask() : (_blockMp ? EffectFlag.MP_BLOCK.getMask() : EffectFlag.NONE.getMask());
	}
}
