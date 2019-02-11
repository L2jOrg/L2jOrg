package org.l2j.gameserver.network.l2.c2s;

import java.nio.ByteBuffer;

public class RequestHardWareInfo extends L2GameClientPacket
{
	private String _mac;
	private String _cpu;
	private String _vgaName;
	private String _driverVersion;
	private int _windowsPlatformId;
	private int _windowsMajorVersion;
	private int _windowsMinorVersion;
	private int _windowsBuildNumber;
	private int _DXVersion;
	private int _DXRevision;
	private int _cpuSpeed;
	private int _cpuCoreCount;
	private int _unk8;
	private int _unk9;
	private int _PhysMemory1;
	private int _PhysMemory2;
	private int _unk12;
	private int _videoMemory;
	private int _unk14;
	private int _vgaVersion;

	@Override
	protected void readImpl(ByteBuffer buffer)
	{
		_mac = readString(buffer);
		_windowsPlatformId = buffer.getInt();
		_windowsMajorVersion = buffer.getInt();
		_windowsMinorVersion = buffer.getInt();
		_windowsBuildNumber = buffer.getInt();
		_DXVersion = buffer.getInt();
		_DXRevision = buffer.getInt();
		_cpu = readString(buffer);
		_cpuSpeed = buffer.getInt();
		_cpuCoreCount = buffer.getInt();
		_unk8 = buffer.getInt();
		_unk9 = buffer.getInt();
		_PhysMemory1 = buffer.getInt();
		_PhysMemory2 = buffer.getInt();
		_unk12 = buffer.getInt();
		_videoMemory = buffer.getInt();
		_unk14 = buffer.getInt();
		_vgaVersion = buffer.getInt();
		_vgaName = readString(buffer);
		_driverVersion = readString(buffer);
	}

	@Override
	protected void runImpl()
	{
	}
}