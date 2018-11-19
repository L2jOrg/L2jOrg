package l2s.gameserver.data.xml.holder;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.Collection;

import l2s.commons.data.xml.AbstractHolder;
import l2s.gameserver.templates.BotPunishment;

public final class BotReportPropertiesHolder extends AbstractHolder
{
	private static final BotReportPropertiesHolder _instance = new BotReportPropertiesHolder();

	private final TIntObjectMap<BotPunishment> _punishments = new TIntObjectHashMap<BotPunishment>();

	public static BotReportPropertiesHolder getInstance()
	{
		return _instance;
	}

	public void addBotPunishment(BotPunishment punishment)
	{
		_punishments.put(punishment.getNeedReportPoints(), punishment);
	}

	public BotPunishment getBotPunishment(int needReportPoints)
	{
		return _punishments.get(needReportPoints);
	}

	public Collection<BotPunishment> getBotPunishments()
	{
		return _punishments.valueCollection();
	}

	@Override
	public int size()
	{
		return _punishments.size();
	}

	@Override
	public void clear()
	{
		_punishments.clear();
	}
}