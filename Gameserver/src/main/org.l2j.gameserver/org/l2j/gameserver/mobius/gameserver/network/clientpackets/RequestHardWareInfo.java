package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.Config;
import org.l2j.gameserver.mobius.gameserver.model.L2World;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.holders.ClientHardwareInfoHolder;
import org.l2j.gameserver.mobius.gameserver.network.Disconnection;

import java.nio.ByteBuffer;

/**
 * @author Mobius
 */
public final class RequestHardWareInfo extends IClientIncomingPacket
{
    private String _macAddress;
    private int _windowsPlatformId;
    private int _windowsMajorVersion;
    private int _windowsMinorVersion;
    private int _windowsBuildNumber;
    private int _directxVersion;
    private int _directxRevision;
    private String _cpuName;
    private int _cpuSpeed;
    private int _cpuCoreCount;
    private int _vgaCount;
    private int _vgaPcxSpeed;
    private int _physMemorySlot1;
    private int _physMemorySlot2;
    private int _physMemorySlot3;
    private int _videoMemory;
    private int _vgaVersion;
    private String _vgaName;
    private String _vgaDriverVersion;

    @Override
    public void readImpl(ByteBuffer packet)
    {
        _macAddress = readString(packet);
        _windowsPlatformId = packet.getInt();
        _windowsMajorVersion = packet.getInt();
        _windowsMinorVersion = packet.getInt();
        _windowsBuildNumber = packet.getInt();
        _directxVersion = packet.getInt();
        _directxRevision = packet.getInt();
        packet.get(new byte[16]);
        _cpuName = readString(packet);
        _cpuSpeed = packet.getInt();
        _cpuCoreCount = packet.get();
        packet.getInt();
        _vgaCount = packet.getInt();
        _vgaPcxSpeed = packet.getInt();
        _physMemorySlot1 = packet.getInt();
        _physMemorySlot2 = packet.getInt();
        _physMemorySlot3 = packet.getInt();
        packet.get();
        _videoMemory = packet.getInt();
        packet.getInt();
        _vgaVersion = packet.getShort();
        _vgaName = readString(packet);
        _vgaDriverVersion = readString(packet);
    }

    @Override
    public void runImpl()
    {
        client.setHardwareInfo(new ClientHardwareInfoHolder(_macAddress, _windowsPlatformId, _windowsMajorVersion, _windowsMinorVersion, _windowsBuildNumber, _directxVersion, _directxRevision, _cpuName, _cpuSpeed, _cpuCoreCount, _vgaCount, _vgaPcxSpeed, _physMemorySlot1, _physMemorySlot2, _physMemorySlot3, _videoMemory, _vgaVersion, _vgaName, _vgaDriverVersion));
        if (Config.HARDWARE_INFO_ENABLED && (Config.MAX_PLAYERS_PER_HWID > 0))
        {
            int count = 0;
            for (L2PcInstance player : L2World.getInstance().getPlayers())
            {
                if ((player.isOnlineInt() == 1) && (player.getClient().getHardwareInfo().equals(client.getHardwareInfo())))
                {
                    count++;
                }
            }
            if (count >= Config.MAX_PLAYERS_PER_HWID)
            {
                Disconnection.of(client).defaultSequence(false);
                return;
            }
        }
    }
}
