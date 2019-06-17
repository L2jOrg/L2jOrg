package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author Mobius
 */
public class ExUISetting extends ServerPacket {
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
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_UI_SETTING);
        if (_uiKeyMapping != null) {
            writeInt(_uiKeyMapping.length);
            writeBytes(_uiKeyMapping);
        } else {
            writeInt(0);
        }
    }

}
