package l2s.gameserver.model.entity.events.objects;

import l2s.gameserver.model.pledge.Clan;

/**
 * @author VISTALL
 * @date 2:23/16.06.2011
 */
public class AuctionSiegeClanObject extends SiegeClanObject
{
	private static final long serialVersionUID = 1L;

	private long _bid;

	public AuctionSiegeClanObject(String type, Clan clan, long param)
	{
		this(type, clan, param, System.currentTimeMillis());
	}

	public AuctionSiegeClanObject(String type, Clan clan, long param, long date)
	{
		super(type, clan, param, date);
		_bid = param;
	}

	@Override
	public long getParam()
	{
		return _bid;
	}

	public void setParam(long param)
	{
		_bid = param;
	}
}