package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

import static java.util.Objects.nonNull;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Take Castle Start effect implementation.
 * @author St3eT
 * @author JoeAlisson
 */
public final class TakeCastleStart extends AbstractEffect {
    private TakeCastleStart() {
    }

    @Override
    public boolean isInstant()
    {
        return true;
    }

    @Override
    public void instant(Creature effector, Creature effected, Skill skill, Item item) {
        if (!isPlayer(effector)) {
            return;
        }

        final Castle castle = CastleManager.getInstance().getCastle(effected);
        if (nonNull(castle) && castle.getSiege().isInProgress()) {
            castle.getSiege().announceToPlayer(SystemMessage.getSystemMessage(SystemMessageId.THE_OPPOSING_CLAN_HAS_STARTED_S1).addSkillName(skill.getId()), false);
        }
    }

    public static class Factory implements SkillEffectFactory {

        private static final TakeCastleStart INSTANCE = new TakeCastleStart();

        @Override
        public AbstractEffect create(StatsSet data) {
            return INSTANCE;
        }

        @Override
        public String effectName() {
            return "TakeCastleStart";
        }
    }
}