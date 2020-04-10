package handlers.effecthandlers;

import org.l2j.gameserver.api.costume.CostumeAPI;
import org.l2j.gameserver.api.costume.CostumeGrade;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;

import java.util.EnumSet;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author JoeAlisson
 */
public class AcquireRandomCostume extends AbstractEffect {

    private final EnumSet<CostumeGrade> grades;

    private AcquireRandomCostume(StatsSet data) {
        grades = data.getStringAsEnumSet("grades", CostumeGrade.class);
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
                CostumeAPI.imprintRandomCostumeOnPlayer(player, grades);
            }
        }
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new AcquireRandomCostume(data);
        }

        @Override
        public String effectName() {
            return "acquire-random-costume";
        }
    }
}
