package org.l2j.gameserver.mobius.gameserver.model.actor.instance;

import org.l2j.gameserver.mobius.gameserver.enums.InstanceType;
import org.l2j.gameserver.mobius.gameserver.enums.Team;
import org.l2j.gameserver.mobius.gameserver.model.L2Party;
import org.l2j.gameserver.mobius.gameserver.model.actor.L2Character;
import org.l2j.gameserver.mobius.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.mobius.gameserver.model.actor.templates.L2NpcTemplate;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ActionFailed;

public class L2EffectPointInstance extends L2Npc
{
    private final L2PcInstance _owner;

    public L2EffectPointInstance(L2NpcTemplate template, L2Character owner)
    {
        super(template);
        setInstanceType(InstanceType.L2EffectPointInstance);
        setIsInvul(false);
        _owner = owner == null ? null : owner.getActingPlayer();
        if (owner != null)
        {
            setInstance(owner.getInstanceWorld());
        }
    }

    @Override
    public L2PcInstance getActingPlayer()
    {
        return _owner;
    }

    /**
     * this is called when a player interacts with this NPC
     * @param player
     */
    @Override
    public void onAction(L2PcInstance player, boolean interact)
    {
        // Send a Server->Client ActionFailed to the L2PcInstance in order to avoid that the client wait another packet
        player.sendPacket(ActionFailed.STATIC_PACKET);
    }

    @Override
    public void onActionShift(L2PcInstance player)
    {
        if (player == null)
        {
            return;
        }

        player.sendPacket(ActionFailed.STATIC_PACKET);
    }

    /**
     * Return the L2Party object of its L2PcInstance owner or null.
     */
    @Override
    public L2Party getParty()
    {
        if (_owner == null)
        {
            return null;
        }

        return _owner.getParty();
    }

    /**
     * Return True if the L2Character has a Party in progress.
     */
    @Override
    public boolean isInParty()
    {
        return (_owner != null) && _owner.isInParty();
    }

    @Override
    public int getClanId()
    {
        return (_owner != null) ? _owner.getClanId() : 0;
    }

    @Override
    public int getAllyId()
    {
        return (_owner != null) ? _owner.getAllyId() : 0;
    }

    @Override
    public final byte getPvpFlag()
    {
        return _owner != null ? _owner.getPvpFlag() : 0;
    }

    @Override
    public final Team getTeam()
    {
        return _owner != null ? _owner.getTeam() : Team.NONE;
    }
}