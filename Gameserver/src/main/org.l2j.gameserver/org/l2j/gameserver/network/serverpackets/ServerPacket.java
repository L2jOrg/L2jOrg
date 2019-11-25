package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.WritablePacket;
import org.l2j.gameserver.GameServer;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.l2j.gameserver.enums.InventorySlot.*;

/**
 * @author KenM
 */
public abstract class ServerPacket extends WritablePacket<GameClient> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerPacket.class);

    private InventorySlot[] PAPERDOLL_ORDER = new InventorySlot[] {
        UNDERWEAR, RIGHT_EAR, LEFT_EAR, NECK, RIGHT_FINGER, LEFT_FINGER,
        HEAD, RIGHT_HAND, LEFT_HAND, GLOVES, CHEST, LEGS, FEET, BACK, RIGHT_HAND,
        HAIR, HAIR2, RIGHT_BRACELET, LEFT_BRACELET, AGATHION1, AGATHION2, AGATHION3,
        AGATHION4, AGATHION5, TALISMAN1, TALISMAN2, TALISMAN3, TALISMAN4, TALISMAN5, TALISMAN6,
        BELT, BROOCH, BROOCH_JEWEL1, BROOCH_JEWEL2, BROOCH_JEWEL3, BROOCH_JEWEL4, BROOCH_JEWEL5, BROOCH_JEWEL6,
        ARTIFACT_BOOK, ARTIFACT1, ARTIFACT2, ARTIFACT3, ARTIFACT4, ARTIFACT5, ARTIFACT6,
        ARTIFACT7, ARTIFACT8, ARTIFACT9, ARTIFACT10, ARTIFACT11, ARTIFACT12,  ARTIFACT13, ARTIFACT14,
        ARTIFACT15, ARTIFACT16, ARTIFACT17, ARTIFACT18, ARTIFACT19, ARTIFACT20, ARTIFACT21
    };

    private InventorySlot[] PAPERDOLL_ORDER_AUGMENT = new InventorySlot[] {
        RIGHT_HAND,
        LEFT_HAND,
        RIGHT_HAND
    };

    public InventorySlot[] getPaperdollOrder() {
        return PAPERDOLL_ORDER;
    }

    public InventorySlot[] getPaperdollOrderAugument() {
        return PAPERDOLL_ORDER_AUGMENT;
    }

    /**
     * Sends this packet to the target player, useful for lambda operations like <br>
     * {@code World.getInstance().getPlayers().forEach(packet::sendTo)}
     *
     * @param player to send the packet
     */
    public void sendTo(Player player) {
        player.sendPacket(this);
    }

    @Override
    protected boolean write(GameClient client) {
        try {
            writeImpl(client);
            return true;
        } catch (Exception e) {
            LOGGER.error("[{}] Error writing packet {} to client {}", GameServer.fullVersion, this, client);
            LOGGER.error(e.getLocalizedMessage(), e);
        }
        return false;
    }

    public void runImpl(Player player) {

    }

    public void writeOptionalD(int value) {
        if (value >= Short.MAX_VALUE) {
            writeShort(Short.MAX_VALUE);
            writeInt(value);
        } else {
            writeShort((short) value);
        }
    }

    protected void writeId(ServerPacketId packet) {
        writeByte(packet.getId());
        if(packet.getExtId() > -1) {
            writeShort(packet.getExtId());
        }
    }

    protected abstract void writeImpl(GameClient client) throws Exception;
}
