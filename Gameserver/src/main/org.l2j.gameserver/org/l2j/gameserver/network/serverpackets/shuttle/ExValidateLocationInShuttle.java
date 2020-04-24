package org.l2j.gameserver.network.serverpackets.shuttle;

import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author UnAfraid
 */
public class ExValidateLocationInShuttle extends ServerPacket {
    private final Player _activeChar;
    private final int _shipId;
    private final int _heading;
    private final Location _loc;

    public ExValidateLocationInShuttle(Player player) {
        _activeChar = player;
        _shipId = _activeChar.getShuttle().getObjectId();
        _loc = player.getInVehiclePosition();
        _heading = player.getHeading();
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_VALIDATE_LOCATION_IN_SHUTTLE);

        writeInt(_activeChar.getObjectId());
        writeInt(_shipId);
        writeInt(_loc.getX());
        writeInt(_loc.getY());
        writeInt(_loc.getZ());
        writeInt(_heading);
    }

}
