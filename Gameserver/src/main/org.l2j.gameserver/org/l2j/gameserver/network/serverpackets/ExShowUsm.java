package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author Sdw
 */
public class ExShowUsm extends ServerPacket {
    public static final ExShowUsm GOD_INTRO = new ExShowUsm(2);
    public static final ExShowUsm SECOND_TRANSFER_QUEST = new ExShowUsm(4);
    public static final ExShowUsm OCTAVIS_INSTANCE_END = new ExShowUsm(6);
    public static final ExShowUsm AWAKENING_END = new ExShowUsm(10);
    public static final ExShowUsm ERTHEIA_FIRST_QUEST = new ExShowUsm(14);
    public static final ExShowUsm USM_Q015_E = new ExShowUsm(15); // Chamber of Prophecies instance
    public static final ExShowUsm ERTHEIA_INTRO_FOR_ERTHEIA = new ExShowUsm(147);
    public static final ExShowUsm ERTHEIA_INTRO_FOR_OTHERS = new ExShowUsm(148);

    private final int _videoId;

    private ExShowUsm(int videoId) {
        _videoId = videoId;
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_SHOW_USM);

        writeInt(_videoId);
    }

}
