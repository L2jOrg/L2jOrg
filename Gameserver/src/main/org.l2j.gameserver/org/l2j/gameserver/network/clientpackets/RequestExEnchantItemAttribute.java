package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.request.EnchantItemAttributeRequest;
import org.l2j.gameserver.network.SystemMessageId;

public class RequestExEnchantItemAttribute extends ClientPacket {

    @Override
    public void readImpl() {
        var _objectId = readInt();
        var _count = readLong();
    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();
        if (player == null) {
            return;
        }

        final EnchantItemAttributeRequest request = player.getRequest(EnchantItemAttributeRequest.class);
        if (request == null) {
            return;
        }

        client.sendPacket(SystemMessageId.ELEMENTAL_POWER_ENHANCER_USAGE_REQUIREMENT_IS_NOT_SUFFICIENT);
        player.removeRequest(request.getClass());
    }
}
