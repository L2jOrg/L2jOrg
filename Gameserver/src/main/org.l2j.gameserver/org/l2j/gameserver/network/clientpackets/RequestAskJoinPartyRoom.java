package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.L2World;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ExAskJoinPartyRoom;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

/**
 * Format: (ch) S
 *
 * @author -Wooden-, Tryskell
 */
public class RequestAskJoinPartyRoom extends ClientPacket {
    private String _name;

    @Override
    public void readImpl() {
        _name = readString();
    }

    @Override
    public void runImpl() {
        final Player player = client.getActiveChar();
        if (player == null) {
            return;
        }

        // Send PartyRoom invite request (with activeChar) name to the target
        final Player target = L2World.getInstance().getPlayer(_name);
        if (target != null) {
            if (!target.isProcessingRequest()) {
                player.onTransactionRequest(target);
                target.sendPacket(new ExAskJoinPartyRoom(player));
            } else {
                player.sendPacket(SystemMessage.getSystemMessage(SystemMessageId.C1_IS_ON_ANOTHER_TASK_PLEASE_TRY_AGAIN_LATER).addPcName(target));
            }
        } else {
            player.sendPacket(SystemMessageId.THAT_PLAYER_IS_NOT_ONLINE);
        }
    }
}
