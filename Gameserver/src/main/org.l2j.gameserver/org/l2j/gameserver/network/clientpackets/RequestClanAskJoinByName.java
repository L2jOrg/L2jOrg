package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.World;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.AskJoinPledge;

/**
 * @author Mobius
 */
public class RequestClanAskJoinByName extends ClientPacket {
    private String _playerName;
    private int _pledgeType;

    @Override
    public void readImpl() {
        _playerName = readString();
        _pledgeType = readInt();
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();
        if ((activeChar == null) || (activeChar.getClan() == null)) {
            return;
        }

        final Player invitedPlayer = World.getInstance().findPlayer(_playerName);
        if (!activeChar.getClan().checkClanJoinCondition(activeChar, invitedPlayer, _pledgeType)) {
            return;
        }
        if (!activeChar.getRequest().setRequest(invitedPlayer, this)) {
            return;
        }

        invitedPlayer.sendPacket(new AskJoinPledge(activeChar, _pledgeType, activeChar.getClan().getName()));
    }

    public int getPledgeType()
    {
        return _pledgeType;
    }
}