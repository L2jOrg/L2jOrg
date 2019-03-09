package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.model.L2World;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.AskJoinPledge;

import java.nio.ByteBuffer;

/**
 * @author Mobius
 */
public class RequestClanAskJoinByName extends IClientIncomingPacket
{
    private String _playerName;
    private int _pledgeType;

    @Override
    public void readImpl(ByteBuffer packet)
    {
        _playerName = readString(packet);
        _pledgeType = packet.getInt();
    }

    @Override
    public void runImpl()
    {
        final L2PcInstance activeChar = client.getActiveChar();
        if ((activeChar == null) || (activeChar.getClan() == null))
        {
            return;
        }

        final L2PcInstance invitedPlayer = L2World.getInstance().getPlayer(_playerName);
        if (!activeChar.getClan().checkClanJoinCondition(activeChar, invitedPlayer, _pledgeType))
        {
            return;
        }
        if (!activeChar.getRequest().setRequest(invitedPlayer, this))
        {
            return;
        }

        invitedPlayer.sendPacket(new AskJoinPledge(activeChar, _pledgeType, activeChar.getClan().getName()));
    }
}