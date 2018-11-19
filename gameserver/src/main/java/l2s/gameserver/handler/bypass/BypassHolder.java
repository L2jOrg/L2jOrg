package l2s.gameserver.handler.bypass;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import l2s.commons.data.xml.AbstractHolder;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

/**
 * @author VISTALL
 * @date 15:51/12.07.2011
 */
public class BypassHolder extends AbstractHolder
{
	private static final BypassHolder _instance = new BypassHolder();
	private Map<String, Pair<Object, Method>> _bypasses = new HashMap<String, Pair<Object, Method>>();

	public static BypassHolder getInstance()
	{
		return _instance;
	}

	public void registerBypass(String bypass, Object o, Method method)
	{
		Pair<Object, Method> old = _bypasses.put(bypass, new ImmutablePair<Object, Method>(o, method));
		if(old != null)
			warn("Duplicate bypass: " + bypass + " old: (" + old.getKey().getClass().getName() + ":" + old.getRight().getName() + "), new: (" + o.getClass().getName() + ":" + method.getName() + ")");
	}

	public Pair<Object, Method> getBypass(String name)
	{
		return _bypasses.get(name);
	}

	@Override
	public int size()
	{
		return _bypasses.size();
	}

	@Override
	public void clear()
	{
		_bypasses.clear();
	}
}