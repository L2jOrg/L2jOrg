package l2s.commons.map.hash;

import gnu.trove.map.hash.TIntObjectHashMap;

/**
 * @author Bonux
**/
public final class TIntStringHashMap extends TIntObjectHashMap<String>
{
	public String getNotNull(int key)
	{
		String value = get(key);
		return value == null ? "" : value;
	}
}