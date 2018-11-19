package l2s.gameserver.model.entity.olympiad;

public class Stadia
{
	private boolean _freeToUse = true;

	public boolean isFreeToUse()
	{
		return _freeToUse;
	}

	public void setStadiaBusy()
	{
		_freeToUse = false;
	}

	public void setStadiaFree()
	{
		_freeToUse = true;
	}
}