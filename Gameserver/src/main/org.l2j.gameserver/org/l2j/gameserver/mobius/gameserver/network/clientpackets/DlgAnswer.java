package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.enums.PlayerAction;
import org.l2j.gameserver.mobius.gameserver.handler.AdminCommandHandler;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.mobius.gameserver.model.events.impl.character.player.OnPlayerDlgAnswer;
import org.l2j.gameserver.mobius.gameserver.model.events.returns.TerminateReturn;
import org.l2j.gameserver.mobius.gameserver.model.holders.DoorRequestHolder;
import org.l2j.gameserver.mobius.gameserver.model.holders.SummonRequestHolder;
import org.l2j.gameserver.mobius.gameserver.network.SystemMessageId;

import java.nio.ByteBuffer;

/**
 * @author Dezmond_snz
 */
public final class DlgAnswer extends IClientIncomingPacket {
    private int _messageId;
    private int _answer;
    private int _requesterId;

    @Override
    public void readImpl(ByteBuffer packet) {
        _messageId = packet.getInt();
        _answer = packet.getInt();
        _requesterId = packet.getInt();
    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        final TerminateReturn term = EventDispatcher.getInstance().notifyEvent(new OnPlayerDlgAnswer(activeChar, _messageId, _answer, _requesterId), activeChar, TerminateReturn.class);
        if ((term != null) && term.terminate()) {
            return;
        }

        if (_messageId == SystemMessageId.S1_3.getId()) {
            if (activeChar.removeAction(PlayerAction.ADMIN_COMMAND)) {
                final String cmd = activeChar.getAdminConfirmCmd();
                activeChar.setAdminConfirmCmd(null);
                if (_answer == 0) {
                    return;
                }

                // The 'useConfirm' must be disabled here, as we don't want to repeat that process.
                AdminCommandHandler.getInstance().useAdminCommand(activeChar, cmd, false);
            }
        } else if ((_messageId == SystemMessageId.C1_IS_ATTEMPTING_TO_DO_A_RESURRECTION_THAT_RESTORES_S2_S3_XP_ACCEPT.getId()) || (_messageId == SystemMessageId.YOUR_CHARM_OF_COURAGE_IS_TRYING_TO_RESURRECT_YOU_WOULD_YOU_LIKE_TO_RESURRECT_NOW.getId())) {
            activeChar.reviveAnswer(_answer);
        } else if (_messageId == SystemMessageId.C1_WISHES_TO_SUMMON_YOU_FROM_S2_DO_YOU_ACCEPT.getId()) {
            final SummonRequestHolder holder = activeChar.removeScript(SummonRequestHolder.class);
            if ((_answer == 1) && (holder != null) && (holder.getTarget().getObjectId() == _requesterId)) {
                activeChar.teleToLocation(holder.getTarget().getLocation(), true);
            }
        } else if (_messageId == SystemMessageId.WOULD_YOU_LIKE_TO_OPEN_THE_GATE.getId()) {
            final DoorRequestHolder holder = activeChar.removeScript(DoorRequestHolder.class);
            if ((holder != null) && (holder.getDoor() == activeChar.getTarget()) && (_answer == 1)) {
                holder.getDoor().openMe();
            }
        } else if (_messageId == SystemMessageId.WOULD_YOU_LIKE_TO_CLOSE_THE_GATE.getId()) {
            final DoorRequestHolder holder = activeChar.removeScript(DoorRequestHolder.class);
            if ((holder != null) && (holder.getDoor() == activeChar.getTarget()) && (_answer == 1)) {
                holder.getDoor().closeMe();
            }
        }
    }
}
