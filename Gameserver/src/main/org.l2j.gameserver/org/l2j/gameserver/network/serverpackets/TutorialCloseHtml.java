package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * TutorialCloseHtml server packet implementation.
 *
 * @author HorridoJoho
 */
@StaticPacket
public class TutorialCloseHtml extends ServerPacket {
    public static final TutorialCloseHtml STATIC_PACKET = new TutorialCloseHtml();

    private TutorialCloseHtml() {
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.TUTORIAL_CLOSE_HTML);
    }

}
