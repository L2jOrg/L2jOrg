package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.data.xml.impl.HennaData;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.items.L2Henna;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.HennaItemRemoveInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * @author Zoey76
 */
public final class RequestHennaItemRemoveInfo extends IClientIncomingPacket
{
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestHennaItemRemoveInfo.class);
    private int _symbolId;

    @Override
    public void readImpl(ByteBuffer packet)
    {
        _symbolId = packet.getInt();
    }

    @Override
    public void runImpl()
    {
        final L2PcInstance activeChar = client.getActiveChar();
        if ((activeChar == null) || (_symbolId == 0))
        {
            return;
        }

        final L2Henna henna = HennaData.getInstance().getHenna(_symbolId);
        if (henna == null)
        {
            LOGGER.warn("Invalid Henna Id: " + _symbolId + " from player " + activeChar);
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        activeChar.sendPacket(new HennaItemRemoveInfo(henna, activeChar));
    }
}
