package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.ThreadPoolManager;
import org.l2j.gameserver.mobius.gameserver.data.xml.impl.FakePlayerData;
import org.l2j.gameserver.mobius.gameserver.model.L2Clan;
import org.l2j.gameserver.mobius.gameserver.model.L2World;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.SystemMessageId;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.AskJoinPledge;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.SystemMessage;

import java.nio.ByteBuffer;

/**
 * This class ...
 *
 * @version $Revision: 1.3.4.4 $ $Date: 2005/03/27 15:29:30 $
 */
public final class RequestJoinPledge extends IClientIncomingPacket {
    private int _target;
    private int _pledgeType;

    @Override
    public void readImpl(ByteBuffer packet) {
        _target = packet.getInt();
        _pledgeType = packet.getInt();
    }

    private void scheduleDeny(L2PcInstance player, String name) {
        if (player != null) {
            SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_DID_NOT_RESPOND_INVITATION_TO_THE_CLAN_HAS_BEEN_CANCELLED);
            sm.addString(name);
            player.sendPacket(sm);
            player.onTransactionResponse();
        }
    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        final L2Clan clan = activeChar.getClan();
        if (clan == null) {
            return;
        }

        if ((activeChar.getTarget() != null) && (FakePlayerData.getInstance().isTalkable(activeChar.getTarget().getName()))) {
            if (FakePlayerData.getInstance().getInfo(activeChar.getTarget().getId()).getClanId() > 0) {
                activeChar.sendPacket(SystemMessageId.THAT_PLAYER_ALREADY_BELONGS_TO_ANOTHER_CLAN);
            } else {
                if (!activeChar.isProcessingRequest()) {
                    ThreadPoolManager.getInstance().schedule(() -> scheduleDeny(activeChar, activeChar.getTarget().getName()), 10000);
                    activeChar.blockRequest();
                } else {
                    final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_ON_ANOTHER_TASK_PLEASE_TRY_AGAIN_LATER);
                    msg.addString(activeChar.getTarget().getName());
                    activeChar.sendPacket(msg);
                }
            }
            return;
        }

        final L2PcInstance target = L2World.getInstance().getPlayer(_target);
        if (target == null) {
            activeChar.sendPacket(SystemMessageId.YOU_HAVE_INVITED_THE_WRONG_TARGET);
            return;
        }

        if (!clan.checkClanJoinCondition(activeChar, target, _pledgeType)) {
            return;
        }

        if (!activeChar.getRequest().setRequest(target, this)) {
            return;
        }

        final String pledgeName = activeChar.getClan().getName();
        target.sendPacket(new AskJoinPledge(activeChar, _pledgeType, pledgeName));
    }

    public int getPledgeType() {
        return _pledgeType;
    }
}
