package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.RecipeItemMakeInfo;

import java.nio.ByteBuffer;

public final class RequestRecipeItemMakeInfo extends IClientIncomingPacket {
    private int _id;

    @Override
    public void readImpl(ByteBuffer packet) {
        _id = packet.getInt();
    }

    @Override
    public void runImpl() {
        final L2PcInstance player = client.getActiveChar();
        if (player == null) {
            return;
        }

        client.sendPacket(new RecipeItemMakeInfo(_id, player));
    }
}
