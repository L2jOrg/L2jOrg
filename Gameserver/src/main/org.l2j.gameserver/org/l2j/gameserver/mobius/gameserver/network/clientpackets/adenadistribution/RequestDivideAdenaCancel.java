package org.l2j.gameserver.mobius.gameserver.network.clientpackets.adenadistribution;

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.actor.request.AdenaDistributionRequest;
import org.l2j.gameserver.mobius.gameserver.network.SystemMessageId;
import org.l2j.gameserver.mobius.gameserver.network.clientpackets.IClientIncomingPacket;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.adenadistribution.ExDivideAdenaCancel;

import java.nio.ByteBuffer;
import java.util.Objects;

/**
 * @author Sdw
 */
public class RequestDivideAdenaCancel extends IClientIncomingPacket
{
    private boolean _cancel;

    @Override
    public void readImpl(ByteBuffer packet)
    {
        _cancel = packet.get() == 0;
    }

    @Override
    public void runImpl()
    {
        final L2PcInstance player = client.getActiveChar();
        if (player == null)
        {
            return;
        }

        if (_cancel)
        {
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
