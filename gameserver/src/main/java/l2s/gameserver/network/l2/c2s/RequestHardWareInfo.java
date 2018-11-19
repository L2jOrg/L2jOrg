package l2s.gameserver.network.l2.c2s;

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
		_mac = readS();
		_windowsPlatformId = readD();
		_windowsMajorVersion = readD();
		_windowsMinorVersion = readD();
		_windowsBuildNumber = readD();
		_DXVersion = readD();
		_DXRevision = readD();
		_cpu = readS();
		_cpuSpeed = readD();
		_cpuCoreCount = readD();
		_unk8 = readD();
		_unk9 = readD();
		_PhysMemory1 = readD();
		_PhysMemory2 = readD();
		_unk12 = readD();
		_videoMemory = readD();
		_unk14 = readD();
		_vgaVersion = readD();
		_vgaName = readS();
		_driverVersion = readS();
	}

	@Override
	protected void runImpl()
	{
	}
}