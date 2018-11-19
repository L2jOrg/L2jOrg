package l2s.gameserver.data.htm;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import l2s.gameserver.utils.Language;
import org.napile.pair.primitive.IntObjectPair;
import org.napile.primitive.Containers;
import org.napile.primitive.maps.impl.HashIntObjectMap;
import org.napile.primitive.sets.IntSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HtmTemplates extends HashIntObjectMap<String>
{
	private static class EmptyHtmTemplates extends HtmTemplates
	{
		public EmptyHtmTemplates()
		{
			super(null, null);
		}

		@Override
		public int size()
		{
			return 0;
		}

		@Override
		public boolean isEmpty()
		{
			return true;
		}

		@Override
		public boolean containsKey(int key)
		{
			return false;
		}

		@Override
		public boolean containsValue(Object value)
		{
			return false;
		}

		@Override
		public String get(int key)
		{
			return null;
		}

		@Override
		public IntSet keySet()
		{
			return Containers.EMPTY_INT_SET;
		}

		@Override
		public Collection<String> values()
		{
			return Collections.emptySet();
		}

		@Override
		public Set<IntObjectPair<String>> entrySet()
		{
			return Collections.emptySet();
		}

		@Override
		public boolean equals(Object o)
		{
			return (o instanceof HtmTemplates) && ((HtmTemplates) o).size() == 0;
		}

		@Override
		public int hashCode()
		{
			return 0;
		}

		private Object readResolve()
		{
			return EMPTY_TEMPLATES;
		}
	}

	private static final Logger _log = LoggerFactory.getLogger(HtmTemplates.class);

	public static HtmTemplates EMPTY_TEMPLATES = new EmptyHtmTemplates();

	private final String _fileName;
	private final Language _lang;

	public HtmTemplates(String fileName, Language lang)
	{
		_fileName = fileName;
		_lang = lang;
	}

	@Override
	public String get(int key)
	{
		String value = super.get(key);
		if(value == null)
		{
			_log.warn("Dialog: data/html/" + _lang.getShortName() + "/" + _fileName + " not found template ID[" + key + "].");
			return "";
		}
		return value;
	}
}