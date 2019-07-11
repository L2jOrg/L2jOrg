package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.WritablePacket;
import org.l2j.gameserver.GameServer;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.itemcontainer.Inventory;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author KenM
 */
public abstract class ServerPacket extends WritablePacket<GameClient> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServerPacket.class);

    int[] PAPERDOLL_ORDER = new int[] {
        Inventory.PAPERDOLL_UNDER,
        Inventory.PAPERDOLL_REAR,
        Inventory.PAPERDOLL_LEAR,
        Inventory.PAPERDOLL_NECK,
        Inventory.PAPERDOLL_RFINGER,
        Inventory.PAPERDOLL_LFINGER,
        Inventory.PAPERDOLL_HEAD,
        Inventory.PAPERDOLL_RHAND,
        Inventory.PAPERDOLL_LHAND,
        Inventory.PAPERDOLL_GLOVES,
        Inventory.PAPERDOLL_CHEST,
        Inventory.PAPERDOLL_LEGS,
        Inventory.PAPERDOLL_FEET,
        Inventory.PAPERDOLL_CLOAK,
        Inventory.PAPERDOLL_RHAND,
        Inventory.PAPERDOLL_HAIR,
        Inventory.PAPERDOLL_HAIR2,
        Inventory.PAPERDOLL_RBRACELET,
        Inventory.PAPERDOLL_LBRACELET,
        Inventory.PAPERDOLL_AGATHION1,
        Inventory.PAPERDOLL_AGATHION2,
        Inventory.PAPERDOLL_AGATHION3,
        Inventory.PAPERDOLL_AGATHION4,
        Inventory.PAPERDOLL_AGATHION5,
        Inventory.TALISMAN1,
        Inventory.TALISMAN2,
        Inventory.TALISMAN3,
        Inventory.TALISMAN4,
        Inventory.TALISMAN5,
        Inventory.TALISMAN6,
        Inventory.PAPERDOLL_BELT,
        Inventory.PAPERDOLL_BROOCH,
        Inventory.PAPERDOLL_BROOCH_JEWEL1,
        Inventory.PAPERDOLL_BROOCH_JEWEL2,
        Inventory.PAPERDOLL_BROOCH_JEWEL3,
        Inventory.PAPERDOLL_BROOCH_JEWEL4,
        Inventory.PAPERDOLL_BROOCH_JEWEL5,
        Inventory.PAPERDOLL_BROOCH_JEWEL6,
        Inventory.PAPERDOLL_ARTIFACT_BOOK,
        Inventory.PAPERDOLL_ARTIFACT1,
        Inventory.PAPERDOLL_ARTIFACT2,
        Inventory.PAPERDOLL_ARTIFACT3,
        Inventory.PAPERDOLL_ARTIFACT4,
        Inventory.PAPERDOLL_ARTIFACT5,
        Inventory.PAPERDOLL_ARTIFACT6,
        Inventory.PAPERDOLL_ARTIFACT7,
        Inventory.PAPERDOLL_ARTIFACT8,
        Inventory.PAPERDOLL_ARTIFACT9,
        Inventory.PAPERDOLL_ARTIFACT10,
        Inventory.PAPERDOLL_ARTIFACT11,
        Inventory.PAPERDOLL_ARTIFACT12,
        Inventory.PAPERDOLL_ARTIFACT13,
        Inventory.PAPERDOLL_ARTIFACT14,
        Inventory.PAPERDOLL_ARTIFACT15,
        Inventory.PAPERDOLL_ARTIFACT16,
        Inventory.PAPERDOLL_ARTIFACT17,
        Inventory.PAPERDOLL_ARTIFACT18,
        Inventory.PAPERDOLL_ARTIFACT19,
        Inventory.PAPERDOLL_ARTIFACT20,
        Inventory.PAPERDOLL_ARTIFACT21,
    };

    int[] PAPERDOLL_ORDER_AUGMENT = new int[] {
        Inventory.PAPERDOLL_RHAND,
        Inventory.PAPERDOLL_LHAND,
        Inventory.PAPERDOLL_RHAND
    };

    public int[] getPaperdollOrder() {
        return PAPERDOLL_ORDER;
    }

    public int[] getPaperdollOrderAugument() {
        return PAPERDOLL_ORDER_AUGMENT;
    }

    /**
     * Sends this packet to the target player, useful for lambda operations like <br>
     * {@code L2World.getInstance().getPlayers().forEach(packet::sendTo)}
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
