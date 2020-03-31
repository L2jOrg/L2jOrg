package handlers.effecthandlers;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.character.OnCreatureAttack;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.BuffInfo;
import org.l2j.gameserver.model.skills.SkillCaster;

import java.util.function.Consumer;

import static java.util.Objects.isNull;
import static org.l2j.gameserver.util.GameUtils.isCreature;

/**
 * @author JoeAlisson
 */
public final class TriggerSkillByAttacking extends AbstractEffect {

    private final int chance;
    private final SkillHolder skill;

    private TriggerSkillByAttacking(StatsSet data) {
        chance = data.getInt("chance", 100);
        skill = new SkillHolder(data.getInt("skill"), data.getInt("power", 1));
    }

    private void onAttackEvent(OnCreatureAttack event) {
        if (chance == 0 || skill.getSkillId() == 0 || skill.getLevel() == 0) {
            return;
        }

        if (event.getAttacker() == event.getTarget()) {
            return;
        }

        if (!Rnd.chance(chance)) {
            return;
        }

        final Skill triggerSkill = skill.getSkill();
        var target = triggerSkill.getTarget(event.getAttacker(), event.getTarget(), false, false, false);
        if(isCreature(target)) {
            final BuffInfo info = ((Creature) target).getEffectList().getBuffInfoBySkillId(triggerSkill.getId());
            if (isNull(info) || (info.getSkill().getLevel() < triggerSkill.getLevel())) {
                SkillCaster.triggerCast(event.getAttacker(), (Creature) target, triggerSkill);
            }
        }
    }

    @Override
    public void onExit(Creature effector, Creature effected, Skill skill) {
        effected.removeListenerIf(EventType.ON_CREATURE_ATTACK, listener -> listener.getOwner() == this);
    }

    @Override
    public void onStart(Creature effector, Creature effected, Skill skill, Item item) {
        effected.addListener(new ConsumerEventListener(effected, EventType.ON_CREATURE_ATTACK, (Consumer<OnCreatureAttack>) this::onAttackEvent, this));
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new TriggerSkillByAttacking(data);
        }

        @Override
        public String effectName() {
            return "trigger-skill-by-attacking";
        }
    }
}
