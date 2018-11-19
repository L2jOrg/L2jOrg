package l2s.gameserver.model.reference;

import l2s.commons.lang.reference.AbstractHardReference;

public class L2Reference<T> extends AbstractHardReference<T>
{
	public L2Reference(T reference)
	{
		super(reference);
	}
}