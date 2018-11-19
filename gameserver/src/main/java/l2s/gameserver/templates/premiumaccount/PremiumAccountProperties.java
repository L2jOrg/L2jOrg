package l2s.gameserver.templates.premiumaccount;

public class PremiumAccountProperties
{
	private final int _nameColor;
	private final int _titleColor;

	public PremiumAccountProperties(int nameColor, int titleColor)
	{
		_nameColor = nameColor;
		_titleColor = titleColor;
	}

	public int getNameColor()
	{
		return _nameColor;
	}

	public int getTitleColor()
	{
		return _titleColor;
	}
}