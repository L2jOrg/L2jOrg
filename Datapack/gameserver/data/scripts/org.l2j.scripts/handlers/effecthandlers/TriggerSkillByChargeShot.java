package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.enums.ShotType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayeableChargeShots;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2j.gameserver.model.holders.SkillHolder;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.SkillCaster;

import java.util.function.Consumer;

import static org.l2j.gameserver.util.GameUtils.isSummon;

/**
 * @author JoeAlisson
 */
public final class TriggerSkillByChargeShot extends AbstractEffect {
    private final SkillHolder skill;
    private final ShotType type;
    private final boolean forBeast;
    private final boolean blessed;

    private TriggerSkillByChargeShot(StatsSet data) {
        skill = new SkillHolder(data.getInt("skill"), data.getInt("power", 1));
        type = data.getEnum("type", ShotType.class);
        forBeast = data.getBoolean("for-beast");
        blessed = data.getBoolean("blessed");
    }

    private void onChargeShotEvent(OnPlayeableChargeShots event) {
        if(event.getShotType() != type || forBeast != isSummon(event.getPlayable()) || blessed != event.isBlessed()) {
            return;
        }
        var triggerSkill = skill.getSkill();
        var playable = event.getPlayable();
        var target = triggerSkill.getTarget(playable, false, false, false);
        SkillCaster.triggerCast(playable, (Creature) target, triggerSkill);
    }

    @Override
    public void onExit(Creature effector, Creature effected, Skill skill)
    {
        effected.removeListenerIf(EventType.ON_PLAYER_CHARGE_SHOTS, listener -> listener.getOwner() == this);
    }

    @Override
    public void onStart(Creature effector, Creature effected, Skill skill, Item item) {
        effected.addListener(new ConsumerEventListener(effected, EventType.ON_PLAYER_CHARGE_SHOTS, (Consumer<OnPlayeableChargeShots>) this::onChargeShotEvent, this));
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new TriggerSkillByChargeShot(data);
        }

        @Override
        public String effectName() {
            return "trigger-skill-by-charge-shot";
        }
    }
}
