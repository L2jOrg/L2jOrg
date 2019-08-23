package org.l2j.gameserver.network.serverpackets;


import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.SystemMessageId;

/**
 * @author Forsaiken, UnAfraid
 */
public final class SystemMessage extends AbstractMessagePacket<SystemMessage> {
    private SystemMessage(SystemMessageId smId) {
        super(smId);
    }

    public static SystemMessage sendString(String text) {
        if (text == null) {
            throw new NullPointerException();
        }

        final SystemMessage sm = getSystemMessage(SystemMessageId.S1_3);
        sm.addString(text);
        return sm;
    }

    public static SystemMessage getSystemMessage(SystemMessageId smId) {
        SystemMessage sm = smId.getStaticSystemMessage();
        if (sm != null) {
            return sm;
        }

        sm = new SystemMessage(smId);
        if (smId.getParamCount() == 0) {
            smId.setStaticSystemMessage(sm);
        }

        return sm;
    }

    /**
     * Use {@link #getSystemMessage(SystemMessageId)} where possible instead
     *
     * @param id
     * @return the system message associated to the given Id.
     */
    public static SystemMessage getSystemMessage(int id) {
        return getSystemMessage(SystemMessageId.getSystemMessageId(id));
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.SYSTEM_MESSAGE);

        writeShort((short) getId());
        writeMe();
    }

}
