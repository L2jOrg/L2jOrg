package l2s.gameserver.templates;

import l2s.commons.collections.MultiValueSet;

public class StatsSet extends MultiValueSet<String>
{
	private static final long serialVersionUID = -2209589233655930756L;
	@SuppressWarnings("serial")
	public static final StatsSet EMPTY = new StatsSet()
	{
		@Override
		public Object put(String a, Object a2)
		{
			throw new UnsupportedOperationException();
		}
	};

	public StatsSet()
	{
		super();
	}

	public StatsSet(StatsSet set)
	{
		super(set);
	}

	@Override
	public StatsSet clone()
	{
		return new StatsSet(this);
	}
}