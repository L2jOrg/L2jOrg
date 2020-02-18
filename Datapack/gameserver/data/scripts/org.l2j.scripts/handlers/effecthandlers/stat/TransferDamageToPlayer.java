package handlers.effecthandlers.stat;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.stats.Stat;

import static org.l2j.gameserver.util.GameUtils.isPlayable;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Transfer Damage effect implementation.
 *
 * @author UnAfraid
 * @author JoeAlisson
 */
public final class TransferDamageToPlayer extends AbstractStatAddEffect {

    private TransferDamageToPlayer(StatsSet params) {
        super(params, Stat.TRANSFER_DAMAGE_TO_PLAYER);
    }

    @Override
    public void onExit(Creature effector, Creature effected, Skill skill) {
        if (isPlayable(effected) && isPlayer(effector)) {
            ((Playable) effected).setTransferDamageTo(null);
        }
    }

    @Override
    public void onStart(Creature effector, Creature effected, Skill skill, Item item) {
        if (isPlayable(effected) && isPlayer(effector)) {
            ((Playable) effected).setTransferDamageTo(effector.getActingPlayer());
        }
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new TransferDamageToPlayer(data);
        }

        @Override
        public String effectName() {
            return "TransferDamageToPlayer";
        }
    }
}