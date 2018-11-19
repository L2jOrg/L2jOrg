package l2s.gameserver.model.items;

import java.util.Calendar;

import l2s.gameserver.dao.CharacterDAO;

/**
 * @author Bonux
 */
public class CommissionItem extends ItemInfo
{
	private int _ownerId;
	private String _ownerName;
	private int _commissionId;
	private long _commissionPrice;
	private int _registerDate;
	private int _periodDays;

	public CommissionItem(int ownerId)
	{
		setOwnerId(ownerId);
		setOwnerName(CharacterDAO.getInstance().getNameByObjectId(ownerId));
	}

	public CommissionItem(ItemInstance item, int ownerId, String ownerName, int commissionId, long commissionPrice, int periodDays)
	{
		super(item);
		setOwnerId(ownerId);
		setOwnerName(ownerName);
		setCommissionId(commissionId);
		setCommissionPrice(commissionPrice);

		Calendar registerDate = Calendar.getInstance();
		registerDate.set(Calendar.SECOND, 0);
		registerDate.set(Calendar.MILLISECOND, 0);
		setRegisterDate((int) (registerDate.getTimeInMillis() / 1000L));

		setPeriodDays(periodDays);
	}

	public String getOwnerName()
	{
		return _ownerName;
	}

	public void setOwnerName(String val)
	{
		_ownerName = val;
	}

	public int getCommissionId()
	{
		return _commissionId;
	}

	public void setCommissionId(int val)
	{
		_commissionId = val;
	}

	public long getCommissionPrice()
	{
		return _commissionPrice;
	}

	public void setCommissionPrice(long val)
	{
		_commissionPrice = val;
	}

	public int getEndPeriodDate()
	{
		return (_periodDays * 24 * 60 * 60) + _registerDate;
	}

	public int getRegisterDate()
	{
		return _registerDate;
	}

	public void setRegisterDate(int val)
	{
		_registerDate = val;
	}

	public int getPeriodDays()
	{
		return _periodDays;
	}

	public void setPeriodDays(int val)
	{
		_periodDays = val;
	}
}
