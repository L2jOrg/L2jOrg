package org.l2j.gameserver.network.l2.s2c;

import java.util.Collection;

import org.l2j.gameserver.model.entity.boat.Shuttle;
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
    protected final void writeImpl()
    {
        writeInt(_shuttle.getBoatId()); // Shuttle ID (Arkan: 1,2; Cruma: 3)
        writeInt(_shuttle.getX()); // X
        writeInt(_shuttle.getY()); // Y
        writeInt(_shuttle.getZ()); // Z
        writeInt(0/*_shuttle.getHeading()*/); // Maybe H
        writeInt(_shuttle.getBoatId()); // unk??
        writeInt(_doors.size()); // doors_count
        for(ShuttleDoor door : _doors)
        {
            int doorId = door.getId();
            writeInt(doorId); // Door ID
            writeInt(door.unkParam[0]); // unk0
            writeInt(door.unkParam[1]); // unk1
            writeInt(door.unkParam[2]); // unk2
            writeInt(door.unkParam[3]); // unk3
            writeInt(door.unkParam[4]); // unk4
            writeInt(door.unkParam[5]); // unk5
            writeInt(door.unkParam[6]); // unk6
            writeInt(door.unkParam[7]); // unk7
            writeInt(door.unkParam[8]); // unk8
            boolean thisFloorDoor = _shuttle.getCurrentFloor().isThisFloorDoor(doorId);
            writeInt(thisFloorDoor && _shuttle.isDocked());
            writeInt(thisFloorDoor);
        }
    }
}