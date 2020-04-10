package handlers.effecthandlers;

import org.l2j.gameserver.api.costume.CostumeAPI;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author JoeAlisson
 */
public class AcquireCostume extends AbstractEffect {

    private final int id;

    private AcquireCostume(StatsSet data) {
        id = data.getInt("id");
    }

    @Override
    public boolean isInstant() {
        return true;
    }

    @Override
    public void instant(Creature effector, Creature effected, Skill skill, Item item) {
        if(isPlayer(effected)) {
            var player = (Player) effected;
            if(player.destroyItem("Consume", item, 1, null, true)) {
                CostumeAPI.imprintCostumeOnPlayer(player, id);
            }
        }
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new AcquireCostume(data);
        }

        @Override
        public String effectName() {
            return "acquire-costume";
        }
    }
}
