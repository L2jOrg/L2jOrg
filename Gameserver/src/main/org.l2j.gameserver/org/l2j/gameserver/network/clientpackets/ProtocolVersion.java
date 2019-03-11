package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.network.serverpackets.KeyPacket;

import java.nio.ByteBuffer;
import java.util.logging.Logger;

/**
 * This class ...
 *
 * @version $Revision: 1.5.2.8.2.8 $ $Date: 2005/04/02 10:43:04 $
 */
public final class ProtocolVersion extends IClientIncomingPacket {
    private static final Logger LOGGER_ACCOUNTING = Logger.getLogger("accounting");

    private int _version;

    @Override
    public void readImpl(ByteBuffer packet) {
        _version = packet.getInt();
    }

    @Override
    public void runImpl() {
        // this packet is never encrypted
        if (_version == -2) {
            // this is just a ping attempt from the new C2 client
            client.closeNow();
        } else if (!Config.PROTOCOL_LIST.contains(_version)) {
            LOGGER_ACCOUNTING.warning("Wrong protocol version " + _version + ", " + client);
            client.setProtocolOk(false);
            client.close(new KeyPacket(client.enableCrypt(), 0));
        } else {
            client.sendPacket(new KeyPacket(client.enableCrypt(), 1));
            client.setProtocolOk(true);
        }
    }
}
