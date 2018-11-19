package l2s.gameserver.model.actor.instances.player;

import l2s.gameserver.templates.HennaTemplate;

/**
 * @author Bonux
 */
public class Henna implements Comparable<Henna>
{
	private final HennaTemplate _template;
	private final int _drawTime;
	private final boolean _isPremium;

	public Henna(HennaTemplate template, int drawTime, boolean isPremium)
	{
		_template = template;
		_drawTime = drawTime;
		_isPremium = isPremium;
	}

	public HennaTemplate getTemplate()
	{
		return _template;
	}

	public int getDrawTime()
	{
		return _drawTime;
	}

	public boolean isPremium()
	{
		return _isPremium;
	}

	public int getLeftTime()
	{
		return _template.getPeriod() > 0 ? (int) ((getDrawTime() + (_template.getPeriod() * 60 * 60)) - (System.currentTimeMillis() / 1000)) : Integer.MAX_VALUE;
	}

	public static String toString(int symbolId, int drawTime, boolean premium)
	{
		return "Henna[symbolId=" + symbolId + ", drawTime=" + drawTime + ", isPremium=" + premium + "]";
	}

	@Override
	public String toString()
	{
		return toString(_template.getSymbolId(), _drawTime, _isPremium);
	}

	@Override
	public int compareTo(Henna o)
	{
		return getDrawTime() - o.getDrawTime();
	}
}