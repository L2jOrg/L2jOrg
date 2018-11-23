package org.l2j.gameserver.network.l2.c2s;

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
	protected void readImpl()
	{
		_mac = readString();
		_windowsPlatformId = readInt();
		_windowsMajorVersion = readInt();
		_windowsMinorVersion = readInt();
		_windowsBuildNumber = readInt();
		_DXVersion = readInt();
		_DXRevision = readInt();
		_cpu = readString();
		_cpuSpeed = readInt();
		_cpuCoreCount = readInt();
		_unk8 = readInt();
		_unk9 = readInt();
		_PhysMemory1 = readInt();
		_PhysMemory2 = readInt();
		_unk12 = readInt();
		_videoMemory = readInt();
		_unk14 = readInt();
		_vgaVersion = readInt();
		_vgaName = readString();
		_driverVersion = readString();
	}

	@Override
	protected void runImpl()
	{
	}
}