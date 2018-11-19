package l2s.gameserver.model.base;

public enum Sex
{
	MALE,
	FEMALE;

	public static final Sex[] VALUES = values();

	public Sex revert()
	{
		switch(this)
		{
			case MALE:
				return FEMALE;
			case FEMALE:
				return MALE;
		}
		return this;
	}
}