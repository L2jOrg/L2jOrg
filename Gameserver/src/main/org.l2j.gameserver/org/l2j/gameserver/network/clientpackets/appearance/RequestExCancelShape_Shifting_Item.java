package org.l2j.gameserver.network.clientpackets.appearance;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.actor.request.ShapeShiftingItemRequest;
import org.l2j.gameserver.network.clientpackets.IClientIncomingPacket;
import org.l2j.gameserver.network.serverpackets.appearance.ExShapeShiftingResult;

import java.nio.ByteBuffer;

/**
 * @author UnAfraid
 */
public class RequestExCancelShape_Shifting_Item extends IClientIncomingPacket {
    @Override
    public void readImpl(ByteBuffer packet) {

    }

    @Override
    public void runImpl() {
        final L2PcInstance player = client.getActiveChar();
        if (player == null) {
            return;
        }

        player.removeRequest(ShapeShiftingItemRequest.class);
        client.sendPacket(ExShapeShiftingResult.FAILED);
    }
}
