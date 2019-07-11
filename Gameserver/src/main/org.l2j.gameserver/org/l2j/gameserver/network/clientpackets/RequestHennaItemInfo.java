package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.data.xml.impl.HennaData;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.items.L2Henna;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.l2j.gameserver.network.serverpackets.HennaItemDrawInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Zoey76
 */
public final class RequestHennaItemInfo extends ClientPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestHennaItemInfo.class);
    private int _symbolId;

    @Override
    public void readImpl() {
        _symbolId = readInt();
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        final L2Henna henna = HennaData.getInstance().getHenna(_symbolId);
        if (henna == null) {
            if (_symbolId != 0) {
                LOGGER.warn(getClass().getSimpleName() + ": Invalid Henna Id: " + _symbolId + " from player " + activeChar);
            }
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }
        client.sendPacket(new HennaItemDrawInfo(henna, activeChar));
    }
}
