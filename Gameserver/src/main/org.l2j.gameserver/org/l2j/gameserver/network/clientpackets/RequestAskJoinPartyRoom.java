package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.L2World;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ExAskJoinPartyRoom;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

import java.nio.ByteBuffer;

/**
 * Format: (ch) S
 *
 * @author -Wooden-, Tryskell
 */
public class RequestAskJoinPartyRoom extends IClientIncomingPacket {
    private String _name;

    @Override
    public void readImpl(ByteBuffer packet) {
        _name = readString(packet);
    }

    @Override
    public void runImpl() {
        final L2PcInstance player = client.getActiveChar();
        if (player == null) {
            return;
        }

        // Send PartyRoom invite request (with activeChar) name to the target
        final L2PcInstance target = L2World.getInstance().getPlayer(_name);
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
