package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.network.l2.s2c.updatetype.IUpdateTypeComponent;

/**
 * @author UnAfraid
 * @param <T>
 */
public abstract class AbstractMaskPacket<T extends IUpdateTypeComponent> extends L2GameServerPacket
{
	protected static final byte[] DEFAULT_FLAG_ARRAY =
	{
		(byte) 0x80,
		0x40,
		0x20,
		0x10,
		0x08,
		0x04,
		0x02,
		0x01
	};
	
	protected abstract byte[] getMasks();
	
	protected abstract void onNewMaskAdded(T component);
	
	@SuppressWarnings("unchecked")
	public void addComponentType(T... updateComponents)
	{
		for (T component : updateComponents)
		{
			if (!containsMask(component))
			{
				getMasks()[component.getMask() >> 3] |= DEFAULT_FLAG_ARRAY[component.getMask() & 7];
				onNewMaskAdded(component);
			}
		}
	}
	
	public boolean containsMask(T component)
	{
		return containsMask(component.getMask());
	}
	
	public boolean containsMask(int mask)
	{
		return (getMasks()[mask >> 3] & DEFAULT_FLAG_ARRAY[mask & 7]) != 0;
	}
}
