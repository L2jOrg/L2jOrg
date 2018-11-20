package org.l2j.gameserver.templates.fakeplayer.actions;

import org.l2j.gameserver.ai.FakeAI;
import org.l2j.gameserver.handler.bbs.BbsHandlerHolder;
import org.l2j.gameserver.handler.bbs.IBbsHandler;

import org.dom4j.Element;

public class UseCommunityAction extends AbstractAction
{
	private final String _bypass;

	public UseCommunityAction(String bypass, double chance)
	{
		super(chance);
		_bypass = bypass;
	}

	@Override
	public boolean performAction(FakeAI ai)
	{
		IBbsHandler handler = BbsHandlerHolder.getInstance().getCommunityHandler(_bypass);
		if(handler != null)
		{
			handler.onBypassCommand(ai.getActor(), _bypass);
			return true;
		}
		return false;
	}

	public static UseCommunityAction parse(Element element)
	{
		String bypass = element.attributeValue("bypass");
		double chance = element.attributeValue("chance") == null ? 100 : Double.parseDouble(element.attributeValue("chance"));
		return new UseCommunityAction(bypass, chance);
	}
}