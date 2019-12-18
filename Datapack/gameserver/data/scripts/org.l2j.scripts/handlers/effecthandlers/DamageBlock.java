package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectFlag;

/**
 * Effect that blocks damage and heals to HP/MP. <BR>
 * Regeneration or DOT shouldn't be blocked, Vampiric Rage and Balance Life as well.
 * @author Nik
 */
public final class DamageBlock extends AbstractEffect {
	private final boolean blockHp;
	private final boolean blockMp;
	
	public DamageBlock(StatsSet params) {
		final String type = params.getString("type", null);
		blockHp = type.equalsIgnoreCase("BLOCK_HP");
		blockMp = type.equalsIgnoreCase("BLOCK_MP");
	}
	
	@Override
	public long getEffectFlags()
	{
		return blockHp ? EffectFlag.HP_BLOCK.getMask() : (blockMp ? EffectFlag.MP_BLOCK.getMask() : EffectFlag.NONE.getMask());
	}
}
