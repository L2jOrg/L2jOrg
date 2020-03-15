package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.enums.PlayerAction;
import org.l2j.gameserver.handler.AdminCommandHandler;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerDlgAnswer;
import org.l2j.gameserver.model.events.returns.TerminateReturn;
import org.l2j.gameserver.model.holders.DoorRequestHolder;
import org.l2j.gameserver.model.holders.SummonRequestHolder;
import org.l2j.gameserver.network.SystemMessageId;

/**
 * @author Dezmond_snz
 * @author JoeAlisson
 */
public final class DlgAnswer extends ClientPacket {
    public static final int ANY_STRING = 1983;
    private int messageId;
    private int answer;
    private int requesterId;

    @Override
    public void readImpl() {
        messageId = readInt();
        answer = readInt();
        requesterId = readInt();
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        }

        final TerminateReturn term = EventDispatcher.getInstance().notifyEvent(new OnPlayerDlgAnswer(activeChar, messageId, answer, requesterId), activeChar, TerminateReturn.class);
        if ((term != null) && term.terminate()) {
            return;
        }

        if (messageId == ANY_STRING) {
            if (activeChar.removeAction(PlayerAction.ADMIN_COMMAND)) {
                final String cmd = activeChar.getAdminConfirmCmd();
                activeChar.setAdminConfirmCmd(null);
                if (answer == 0) {
                    return;
                }

                // The 'useConfirm' must be disabled here, as we don't want to repeat that process.
                AdminCommandHandler.getInstance().useAdminCommand(activeChar, cmd, false);
            }
        } else if ((messageId == SystemMessageId.C1_IS_ATTEMPTING_TO_DO_A_RESURRECTION_THAT_RESTORES_S2_S3_XP_ACCEPT.getId()) || (messageId == SystemMessageId.YOUR_CHARM_OF_COURAGE_IS_TRYING_TO_RESURRECT_YOU_WOULD_YOU_LIKE_TO_RESURRECT_NOW.getId())) {
            activeChar.reviveAnswer(answer);
        } else if (messageId == SystemMessageId.C1_WISHES_TO_SUMMON_YOU_FROM_S2_DO_YOU_ACCEPT.getId()) {
            final SummonRequestHolder holder = activeChar.removeScript(SummonRequestHolder.class);
            if ((answer == 1) && (holder != null) && (holder.getTarget().getObjectId() == requesterId)) {
                activeChar.teleToLocation(holder.getTarget().getLocation(), true);
            }
        } else if (messageId == SystemMessageId.WOULD_YOU_LIKE_TO_OPEN_THE_GATE.getId()) {
            final DoorRequestHolder holder = activeChar.removeScript(DoorRequestHolder.class);
            if ((holder != null) && (holder.getDoor() == activeChar.getTarget()) && (answer == 1)) {
                holder.getDoor().openMe();
            }
        } else if (messageId == SystemMessageId.WOULD_YOU_LIKE_TO_CLOSE_THE_GATE.getId()) {
            final DoorRequestHolder holder = activeChar.removeScript(DoorRequestHolder.class);
            if ((holder != null) && (holder.getDoor() == activeChar.getTarget()) && (answer == 1)) {
                holder.getDoor().closeMe();
            }
        }
    }
}
