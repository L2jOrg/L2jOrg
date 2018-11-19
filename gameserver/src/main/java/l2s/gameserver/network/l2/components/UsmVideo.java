package l2s.gameserver.network.l2.components;

import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.s2c.ExShowUsmPacket;
import l2s.gameserver.network.l2.s2c.L2GameServerPacket;

/**
 * @author Bonux
 */
public enum UsmVideo implements IBroadcastPacket
{
	Q001(1),
	GD10_PROLOGUE(2),
	Q002(3),
	Q003(4),
	Q004(5),
	Q005(6),
	Q006(7),
	Q007(8),
	Q009(9),
	Q010(10),
	Q011(11), //some sord of dancing happy spirits
	Q012(12), //for death guliatin mass attack
	Q013(13),
	Q014(14),
	Q015(15),
	AWEKE1(139),
	AWEKE2(140),
	AWEKE3(141),
	AWEKE4(142),
	AWEKE5(143),
	AWEKE6(144),
	AWEKE7(145),
	AWEKE8(146),
	ERTHEIA(147),
	HEROES(148);

	private final int _id;
	private final L2GameServerPacket _static;

	UsmVideo(int id)
	{
		_id = id;
		_static = new ExShowUsmPacket(id);
	}

	public int getId()
	{
		return _id;
	}

	@Override
	public L2GameServerPacket packet(Player player)
	{
		return _static;
	}
}
