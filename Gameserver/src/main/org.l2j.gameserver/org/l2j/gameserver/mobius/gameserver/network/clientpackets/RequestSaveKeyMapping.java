package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.Config;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.ConnectionState;

import java.nio.ByteBuffer;

/**
 * Request Save Key Mapping client packet.
 *
 * @author Mobius
 */
public class RequestSaveKeyMapping extends IClientIncomingPacket {
    public static final String UI_KEY_MAPPING_VAR = "UI_KEY_MAPPING";
    public static final String SPLIT_VAR = "	";
    private byte[] _uiKeyMapping;

    @Override
    public void readImpl(ByteBuffer packet) {
        final int dataSize = packet.getInt();
        if (dataSize > 0) {
            _uiKeyMapping = new byte[dataSize];
            packet.get(_uiKeyMapping);
        }
    }

    @Override
    public void runImpl() {
        final L2PcInstance player = client.getActiveChar();
        if (!Config.STORE_UI_SETTINGS || //
                (player == null) || //
                (_uiKeyMapping == null) || //
                (client.getConnectionState() != ConnectionState.IN_GAME)) {
            return;
        }

        String uiKeyMapping = "";
        for (Byte b : _uiKeyMapping) {
            uiKeyMapping += b + SPLIT_VAR;
        }
        player.getVariables().set(UI_KEY_MAPPING_VAR, uiKeyMapping);
    }
}
