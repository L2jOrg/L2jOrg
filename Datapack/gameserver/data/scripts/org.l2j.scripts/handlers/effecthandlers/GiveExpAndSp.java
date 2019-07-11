package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

/**
 * Give XP and SP effect implementation.
 * @author quangnguyen
 */
public final class GiveExpAndSp extends AbstractEffect
{
    private final int _xp;
    private final int _sp;

    public GiveExpAndSp(StatsSet params)
    {
        _xp = params.getInt("xp", 0);
        _sp = params.getInt("sp", 0);
    }

    @Override
    public boolean isInstant()
    {
        return true;
    }

    @Override
    public void instant(Creature effector, Creature effected, Skill skill, L2ItemInstance item)
    {
        if (!effector.isPlayer() || !effected.isPlayer() || effected.isAlikeDead())
        {
            return;
        }

        if ((_sp != 0) && (_xp != 0))
        {
            effector.getActingPlayer().getStat().addExp(_xp);
            effector.getActingPlayer().getStat().addSp(_sp);

            SystemMessage sm = null;
            sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_ACQUIRED_S1_XP_BONUS_S2_AND_S3_SP_BONUS_S4);
            sm.addLong(_xp);
            sm.addLong(0);
            sm.addLong(_sp);
            sm.addLong(0);
            effector.sendPacket(sm);
        }
    }
}