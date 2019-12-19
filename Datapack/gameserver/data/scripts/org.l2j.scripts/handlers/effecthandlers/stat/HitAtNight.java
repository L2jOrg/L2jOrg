package handlers.effecthandlers.stat;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.world.WorldTimeController;

/**
 * @author Mobius
 */
public class HitAtNight extends AbstractStatEffect {

    public HitAtNight(StatsSet params) {
        super(params, Stat.HIT_AT_NIGHT);
    }

    @Override
    public void onStart(Creature effector, Creature effected, Skill skill, Item item) {
        WorldTimeController.getInstance().addShadowSenseCharacter(effected);
    }

    @Override
    public void onExit(Creature effector, Creature effected, Skill skill) {
        WorldTimeController.getInstance().removeShadowSenseCharacter(effected);
    }
}
