package org.l2j.gameserver.data.xml.holder;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.l2j.commons.data.xml.AbstractHolder;
import org.l2j.gameserver.templates.premiumaccount.PremiumAccountTemplate;

import java.util.Collection;

public class PremiumAccountHolder extends AbstractHolder
{
	private static final PremiumAccountHolder _instance = new PremiumAccountHolder();

	private static final PremiumAccountTemplate DEFAULT_ACCOUNT_TEMPLATE = new PremiumAccountTemplate(0);

	private final TIntObjectMap<PremiumAccountTemplate> _premiumAccounts = new TIntObjectHashMap<>();

	public static PremiumAccountHolder getInstance()
	{
		return _instance;
	}

	public void addPremiumAccount(PremiumAccountTemplate premiumAccount)
	{
		_premiumAccounts.put(premiumAccount.getType(), premiumAccount);
	}

	public PremiumAccountTemplate getPremiumAccount(int type)
	{
		if(type == 0 && !_premiumAccounts.containsKey(type))
			return DEFAULT_ACCOUNT_TEMPLATE;
		return _premiumAccounts.get(type);
	}

	public Collection<PremiumAccountTemplate> getPremiumAccounts()
	{
		return _premiumAccounts.valueCollection();
	}

	@Override
	public int size()
	{
		return _premiumAccounts.size();
	}

	@Override
	public void clear()
	{
		_premiumAccounts.clear();
	}
}