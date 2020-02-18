package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;

import static org.l2j.gameserver.util.GameUtils.isPlayer;
import static org.l2j.gameserver.util.GameUtils.isSummon;

/**
 * Immobile Pet Buff effect implementation.
 * @author demonia
 * @author JoeAlisson
 */
public final class ImmobilePetBuff extends AbstractEffect {

    private ImmobilePetBuff() {
    }

    @Override
    public void onExit(Creature effector, Creature effected, Skill skill)
    {
        effected.setIsImmobilized(false);
    }

    @Override
    public void onStart(Creature effector, Creature effected, Skill skill, Item item) {
        if (isSummon(effected) && isPlayer(effector) && (((Summon) effected).getOwner() == effector)) {
            effected.setIsImmobilized(true);
        }
    }

    public static class Factory implements SkillEffectFactory {
        private static final ImmobilePetBuff INSTANCE = new ImmobilePetBuff();

        @Override
        public AbstractEffect create(StatsSet data) {
            return INSTANCE;
        }

        @Override
        public String effectName() {
            return "ImmobilePetBuff";
        }
    }
}