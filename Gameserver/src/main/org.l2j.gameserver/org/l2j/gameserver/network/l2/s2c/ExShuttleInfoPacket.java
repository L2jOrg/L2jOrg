package org.l2j.gameserver.network.l2.s2c;

import java.nio.ByteBuffer;
import java.util.Collection;

import org.l2j.gameserver.model.entity.boat.Shuttle;
import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.templates.ShuttleTemplate.ShuttleDoor;

/**
 * @author Bonux
 **/
public class ExShuttleInfoPacket extends L2GameServerPacket
{
    private final Shuttle _shuttle;
    private final Collection<ShuttleDoor> _doors;

    public ExShuttleInfoPacket(Shuttle shuttle)
    {
        _shuttle = shuttle;
        _doors = shuttle.getTemplate().getDoors();
    }

    @Override
    protected final void writeImpl(GameClient client, ByteBuffer buffer)
    {
        buffer.putInt(_shuttle.getBoatId()); // Shuttle ID (Arkan: 1,2; Cruma: 3)
        buffer.putInt(_shuttle.getX()); // X
        buffer.putInt(_shuttle.getY()); // Y
        buffer.putInt(_shuttle.getZ()); // Z
        buffer.putInt(0/*_shuttle.getHeading()*/); // Maybe H
        buffer.putInt(_shuttle.getBoatId()); // unk??
        buffer.putInt(_doors.size()); // doors_count
        for(ShuttleDoor door : _doors)
        {
            int doorId = door.getId();
            buffer.putInt(doorId); // Door ID
            buffer.putInt(door.unkParam[0]); // unk0
            buffer.putInt(door.unkParam[1]); // unk1
            buffer.putInt(door.unkParam[2]); // unk2
            buffer.putInt(door.unkParam[3]); // unk3
            buffer.putInt(door.unkParam[4]); // unk4
            buffer.putInt(door.unkParam[5]); // unk5
            buffer.putInt(door.unkParam[6]); // unk6
            buffer.putInt(door.unkParam[7]); // unk7
            buffer.putInt(door.unkParam[8]); // unk8
            boolean thisFloorDoor = _shuttle.getCurrentFloor().isThisFloorDoor(doorId);
            buffer.putInt(thisFloorDoor && _shuttle.isDocked() ? 0x01 : 0x00);
            buffer.putInt(thisFloorDoor ? 0x01 : 0x00);
        }
    }
}