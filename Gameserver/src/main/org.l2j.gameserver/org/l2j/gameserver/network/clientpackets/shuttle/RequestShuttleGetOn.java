package org.l2j.gameserver.network.clientpackets.shuttle;

import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.instance.Shuttle;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

import static java.util.Objects.isNull;

/**
 * @author UnAfraid
 */
public class RequestShuttleGetOn extends ClientPacket {

    private int x;
    private int y;
    private int z;

    @Override
    public void readImpl() {
        readInt(); // charId
        x = readInt();
        y = readInt();
        z = readInt();
    }

    @Override
    public void runImpl() {
        final Player player = client.getPlayer();
        if (isNull(player)) {
            return;
        }

        World.getInstance().forEachVisibleObjectInRange(player, Shuttle.class, 1000, shuttle -> {
            shuttle.addPassenger(player);
            player.getInVehiclePosition().setXYZ(x, y, z);
        });
    }
}
