package handlers.effecthandlers;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.handler.ITargetTypeHandler;
import org.l2j.gameserver.handler.TargetHandler;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.character.OnCreatureAttackAvoid;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.skills.SkillCaster;
import org.l2j.gameserver.model.skills.targets.TargetType;

import java.util.function.Consumer;

import static java.util.Objects.isNull;
import static org.l2j.gameserver.util.GameUtils.isCreature;

/**
 * Trigger Skill By Avoid effect implementation.
 * @author Zealar
 * @author JoeAlisson
 */
public final class TriggerSkillByAvoid extends AbstractEffect {
    private final int chance;
    private final SkillHolder skill;
    private final TargetType targetType;

    private TriggerSkillByAvoid(StatsSet params) {
        chance = params.getInt("chance", 100);
        skill = new SkillHolder(params.getInt("skill", 0), params.getInt("power", 0));
        targetType = params.getEnum("target", TargetType.class, TargetType.TARGET);
    }

    private void onAvoidEvent(OnCreatureAttackAvoid event) {
        if (event.isDamageOverTime() || (chance == 0) || ((skill.getSkillId() == 0) || (skill.getLevel() == 0))) {
            return;
        }

        final ITargetTypeHandler targetHandler = TargetHandler.getInstance().getHandler(targetType);
        if (isNull(targetHandler)) {
            LOGGER.warn("Handler for target type: {} does not exist.", targetType);
            return;
        }

        if ((chance < 100) && (Rnd.get(100) > chance)) {
            return;
        }

        final Skill triggerSkill = skill.getSkill();
        WorldObject target = null;
        try {
            target = TargetHandler.getInstance().getHandler(targetType).getTarget(event.getTarget(), event.getAttacker(), triggerSkill, false, false, false);
        } catch (Exception e) {
            LOGGER.warn("Exception in ITargetTypeHandler.getTarget(): " + e.getMessage(), e);
        }

        if (isCreature(target)) {
            SkillCaster.triggerCast(event.getAttacker(), (Creature) target, triggerSkill);
        }
    }

    @Override
    public void onExit(Creature effector, Creature effected, Skill skill)
    {
        effected.removeListenerIf(EventType.ON_CREATURE_ATTACK_AVOID, listener -> listener.getOwner() == this);
    }

    @Override
    public void onStart(Creature effector, Creature effected, Skill skill, Item item) {
        effected.addListener(new ConsumerEventListener(effected, EventType.ON_CREATURE_ATTACK_AVOID, (Consumer<OnCreatureAttackAvoid>) this::onAvoidEvent, this));
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new TriggerSkillByAvoid(data);
        }

        @Override
        public String effectName() {
            return "trigger-skill-by-avoid";
        }
    }
}
