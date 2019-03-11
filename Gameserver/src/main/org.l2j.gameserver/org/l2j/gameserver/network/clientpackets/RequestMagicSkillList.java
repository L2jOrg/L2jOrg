package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * @author UnAfraid
 */
@SuppressWarnings("unused")
public class RequestMagicSkillList extends IClientIncomingPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestMagicSkillList.class);
    private int _objectId;
    private int _charId;
    private int _unk;

    @Override
    public void readImpl(ByteBuffer packet) {
        _objectId = packet.getInt();
        _charId = packet.getInt();
        _unk = packet.getInt();
    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        if (activeChar.getObjectId() != _objectId) {
            LOGGER.warn("Player: " + activeChar + " requested " + getClass().getSimpleName() + " with different object id: " + _objectId);
            return;
        }

        activeChar.sendSkillList();
    }
}
