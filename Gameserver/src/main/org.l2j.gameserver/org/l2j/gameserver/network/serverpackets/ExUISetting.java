package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author Mobius
 */
public class ExUISetting extends IClientOutgoingPacket {
    public static final String UI_KEY_MAPPING_VAR = "UI_KEY_MAPPING";
    public static final String SPLIT_VAR = "	";
    private final byte[] _uiKeyMapping;

    public ExUISetting(L2PcInstance player) {
        if (player.getVariables().hasVariable(UI_KEY_MAPPING_VAR)) {
            _uiKeyMapping = player.getVariables().getByteArray(UI_KEY_MAPPING_VAR, SPLIT_VAR);
        } else {
            _uiKeyMapping = null;
        }
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_UI_SETTING.writeId(packet);
        if (_uiKeyMapping != null) {
            packet.putInt(_uiKeyMapping.length);
            packet.put(_uiKeyMapping);
        } else {
            packet.putInt(0);
        }
    }
}
