package org.l2j.gameserver.network.clientpackets.adenadistribution;

import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.request.AdenaDistributionRequest;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.adenadistribution.ExDivideAdenaCancel;

import java.util.Objects;

/**
 * @author Sdw
 */
public class RequestDivideAdenaCancel extends ClientPacket {
    private boolean _cancel;

    @Override
    public void readImpl() {
        _cancel = readByte() == 0;
    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();
        if (player == null) {
            return;
        }

        if (_cancel) {
            final AdenaDistributionRequest request = player.getRequest(AdenaDistributionRequest.class);
            request.getPlayers().stream().filter(Objects::nonNull).forEach(p ->
            {
                p.sendPacket(SystemMessageId.ADENA_DISTRIBUTION_HAS_BEEN_CANCELLED);
                p.sendPacket(ExDivideAdenaCancel.STATIC_PACKET);
                p.removeRequest(AdenaDistributionRequest.class);
            });
        }
    }
}
