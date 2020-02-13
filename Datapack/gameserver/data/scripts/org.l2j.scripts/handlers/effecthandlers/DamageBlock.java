package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectFlag;

/**
 * Effect that blocks damage and heals to HP/MP. <BR>
 * Regeneration or DOT shouldn't be blocked, Vampiric Rage and Balance Life as well.
 * @author Nik
 * @author JoeAlisson
 */
public final class DamageBlock extends AbstractEffect {
	public final boolean blockHp;
	public final boolean blockMp;
	
	public DamageBlock(StatsSet params) {
		blockHp = params.getBoolean("block-hp");
		blockMp = params.getBoolean("block-mp");
	}
	
	@Override
	public long getEffectFlags() {
		int mask =0;
		if(blockHp) {
			mask |= EffectFlag.HP_BLOCK.getMask();
		}
		if(blockMp) {
			mask |= EffectFlag.MP_BLOCK.getMask();
		}
		return mask;
	}
}
