package l2s.gameserver.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

public class XMLUtil
{
	@SuppressWarnings("unused")
	private static final Logger _log = LoggerFactory.getLogger(XMLUtil.class);

	public static String getAttributeValue(Node n, String item)
	{
		final Node d = n.getAttributes().getNamedItem(item);
		if(d == null)
			return StringUtils.EMPTY;
		final String val = d.getNodeValue();
		if(val == null)
			return StringUtils.EMPTY;
		return val;
	}

	public static boolean getAttributeBooleanValue(Node n, String item, boolean dflt)
	{
		final Node d = n.getAttributes().getNamedItem(item);
		if(d == null)
			return dflt;
		final String val = d.getNodeValue();
		if(val == null)
			return dflt;
		return Boolean.parseBoolean(val);
	}

	public static int getAttributeIntValue(Node n, String item, int dflt)
	{
		final Node d = n.getAttributes().getNamedItem(item);
		if(d == null)
			return dflt;
		final String val = d.getNodeValue();
		if(val == null)
			return dflt;
		return Integer.parseInt(val);
	}

	public static long getAttributeLongValue(Node n, String item, long dflt)
	{
		final Node d = n.getAttributes().getNamedItem(item);
		if(d == null)
			return dflt;
		final String val = d.getNodeValue();
		if(val == null)
			return dflt;
		return Long.parseLong(val);
	}
}