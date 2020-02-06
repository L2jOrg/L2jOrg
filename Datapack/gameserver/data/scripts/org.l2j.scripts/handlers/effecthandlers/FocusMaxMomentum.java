package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.EtcStatusUpdate;

import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Focus Max Energy effect implementation.
 * @author Adry_85
 */
public final class FocusMaxMomentum extends AbstractEffect {
    public FocusMaxMomentum(StatsSet params) {
    }

    @Override
    public boolean isInstant()
    {
        return true;
    }

    @Override
    public void instant(Creature effector, Creature effected, Skill skill, Item item) {
        if (isPlayer(effected)) {
            final Player player = effected.getActingPlayer();

            final int count = (int) effected.getStats().getValue(Stat.MAX_MOMENTUM, 0);
            player.setCharges(count);
            player.sendPacket(getSystemMessage(SystemMessageId.YOUR_FORCE_HAS_INCREASED_TO_LEVEL_S1).addInt(count));
            player.sendPacket(new EtcStatusUpdate(player));
        }
    }
}