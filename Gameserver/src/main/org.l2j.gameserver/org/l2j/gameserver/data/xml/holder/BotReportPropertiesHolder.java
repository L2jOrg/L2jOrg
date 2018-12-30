package org.l2j.gameserver.data.xml.holder;

import java.util.Collection;

import org.l2j.commons.data.xml.AbstractHolder;
import org.l2j.gameserver.templates.BotPunishment;
import io.github.joealisson.primitive.maps.IntObjectMap;
import io.github.joealisson.primitive.maps.impl.HashIntObjectMap;

public final class BotReportPropertiesHolder extends AbstractHolder
{
	private static final BotReportPropertiesHolder _instance = new BotReportPropertiesHolder();

	private final IntObjectMap<BotPunishment> _punishments = new HashIntObjectMap<BotPunishment>();

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
		return _punishments.values();
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