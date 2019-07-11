package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.model.skills.AbnormalVisualEffect;
import org.l2j.gameserver.model.skills.Skill;

import java.util.Arrays;
import java.util.Objects;

public class VisualEffect extends AbstractEffect {

    private final AbnormalVisualEffect[] effects;
    private final boolean start;

    public VisualEffect(StatsSet params) {
        var effectTypes = params.getString("type", "NONE").split(";");
        effects = Arrays.stream(effectTypes).map(AbnormalVisualEffect::findByName).filter(Objects::nonNull).toArray(AbnormalVisualEffect[]::new);
        start = params.getString("mode", "START").equalsIgnoreCase("START");
    }


    @Override
    public boolean isInstant() {
        return true;
    }

    @Override
    public void instant(Creature effector, Creature effected, Skill skill, L2ItemInstance item) {
            if(start) {
            effected.getEffectList().startAbnormalVisualEffect(effects);
        } else {
            effected.getEffectList().stopAbnormalVisualEffect(effects);
        }
    }
}
