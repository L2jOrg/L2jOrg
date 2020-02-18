package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.network.SystemMessageId;

import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Give XP and SP effect implementation.
 * @author quangnguyen
 * @author JoeAlisson
 */
public final class GiveExpAndSp extends AbstractEffect {

    private final int xp;
    private final int sp;

    private GiveExpAndSp(StatsSet params) {
        xp = params.getInt("xp", 0);
        sp = params.getInt("sp", 0);
    }

    @Override
    public boolean isInstant()
    {
        return true;
    }

    @Override
    public void instant(Creature effector, Creature effected, Skill skill, Item item) {
        if (!isPlayer(effector) || !isPlayer(effected) || effected.isAlikeDead()) {
            return;
        }

        if (sp != 0 && xp != 0) {
            effector.getActingPlayer().getStats().addExp(xp);
            effector.getActingPlayer().getStats().addSp(sp);
            effector.sendPacket(getSystemMessage(SystemMessageId.YOU_HAVE_ACQUIRED_S1_XP_BONUS_S2_AND_S3_SP_BONUS_S4).addLong(xp).addLong(0).addLong(sp).addLong(0));
        }
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new GiveExpAndSp(data);
        }

        @Override
        public String effectName() {
            return "acquire-xp-sp";
        }
    }
}