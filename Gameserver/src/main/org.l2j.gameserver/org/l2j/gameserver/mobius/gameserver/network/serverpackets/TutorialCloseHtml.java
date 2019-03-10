package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * TutorialCloseHtml server packet implementation.
 *
 * @author HorridoJoho
 */
@StaticPacket
public class TutorialCloseHtml extends IClientOutgoingPacket {
    public static final TutorialCloseHtml STATIC_PACKET = new TutorialCloseHtml();

    private TutorialCloseHtml() {
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.TUTORIAL_CLOSE_HTML.writeId(packet);
    }
}
