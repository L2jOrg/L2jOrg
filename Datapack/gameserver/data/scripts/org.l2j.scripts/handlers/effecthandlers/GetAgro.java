package handlers.effecthandlers;

import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Attackable;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.world.World;

import java.util.Set;

import static java.util.Objects.nonNull;
import static org.l2j.gameserver.util.GameUtils.isAttackable;

/**
 * Get Agro effect implementation.
 * @author Adry_85, Mobius
 * @author JoeAlisson
 */
public final class GetAgro extends AbstractEffect {
    private GetAgro() {
    }

    @Override
    public EffectType getEffectType()
    {
        return EffectType.AGGRESSION;
    }

    @Override
    public boolean isInstant()
    {
        return true;
    }

    @Override
    public void instant(Creature effector, Creature effected, Skill skill, Item item) {
        if (isAttackable(effected)) {
            effected.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, effector);

            // Monsters from the same clan should assist.
            final NpcTemplate template = ((Attackable) effected).getTemplate();
            final Set<Integer> clans = template.getClans();
            if (nonNull(clans)) {
                World.getInstance().forEachVisibleObjectInRange(effected, Attackable.class, template.getClanHelpRange(), attackable -> receiveHate(attackable, effector), nearby -> canReceiveHate(nearby, clans));
            }
        }
    }

    private boolean canReceiveHate(Attackable nearby, Set<Integer> clans) {
        return !nearby.isMovementDisabled() && nearby.getTemplate().isClan(clans);
    }

    private void receiveHate(Attackable attackable, Creature effector) {
        attackable.addDamageHate(effector, 1, 200);
        attackable.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, effector);
    }

    public static class Factory implements SkillEffectFactory {
        private static final GetAgro INSTANCE = new GetAgro();
        @Override
        public AbstractEffect create(StatsSet data) {
            return INSTANCE;
        }

        @Override
        public String effectName() {
            return "GetAgro";
        }
    }
}
