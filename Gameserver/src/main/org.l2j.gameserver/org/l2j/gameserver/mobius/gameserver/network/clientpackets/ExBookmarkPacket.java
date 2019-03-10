package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import java.nio.ByteBuffer;

/**
 * @author Sdw
 */
public class ExBookmarkPacket extends IClientIncomingPacket {
    private IClientIncomingPacket _exBookmarkPacket;

    @Override
    public void readImpl(ByteBuffer packet) {
        final int subId = packet.getInt();

        switch (subId) {
            case 0: {
                _exBookmarkPacket = new RequestBookMarkSlotInfo(client);
                break;
            }
            case 1: {
                _exBookmarkPacket = new RequestSaveBookMarkSlot(client);
                break;
            }
            case 2: {
                _exBookmarkPacket = new RequestModifyBookMarkSlot(client);
                break;
            }
            case 3: {
                _exBookmarkPacket = new RequestDeleteBookMarkSlot(client);
                break;
            }
            case 4: {
                _exBookmarkPacket = new RequestTeleportBookMark(client);
                break;
            }
            case 5: {
                _exBookmarkPacket = new RequestChangeBookMarkSlot(client);
                break;
            }
        }
        if (_exBookmarkPacket != null) {
            _exBookmarkPacket.read(packet);
        }

    }

    @Override
    public void runImpl() throws Exception {
        if (_exBookmarkPacket != null) {
            _exBookmarkPacket.run();
        }
    }
}
