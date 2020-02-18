package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Change Hair Style effect implementation.
 * @author Zoey76
 * @author JoeAlisson
 */
public final class ChangeHairStyle extends AbstractEffect {
    private final int value;

    private ChangeHairStyle(StatsSet params)
    {
        value = params.getInt("power", 0);
    }

    @Override
    public boolean isInstant()
    {
        return true;
    }

    @Override
    public void instant(Creature effector, Creature effected, Skill skill, Item item) {
        if (!isPlayer(effected)) {
            return;
        }

        final Player player = effected.getActingPlayer();
        player.getAppearance().setHairStyle(value);
        player.broadcastUserInfo();
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new ChangeHairStyle(data);
        }

        @Override
        public String effectName() {
            return "ChangeHairStyle";
        }
    }
}
