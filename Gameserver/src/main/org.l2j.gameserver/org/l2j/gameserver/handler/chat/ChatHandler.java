package org.l2j.gameserver.handler.chat;

import org.l2j.commons.data.xml.AbstractHolder;
import org.l2j.gameserver.network.l2.components.ChatType;

/**
 * @author VISTALL
 * @date 18:17/12.03.2011
 */
public class ChatHandler extends AbstractHolder
{
	private static final ChatHandler _instance = new ChatHandler();

	private IChatHandler[] _handlers = new IChatHandler[ChatType.VALUES.length];

	public static ChatHandler getInstance()
	{
		return _instance;
	}

	private ChatHandler()
	{

	}

	public void register(IChatHandler chatHandler)
	{
		_handlers[chatHandler.getType().ordinal()] = chatHandler;
	}

	public IChatHandler getHandler(ChatType type)
	{
		return _handlers[type.ordinal()];
	}

	@Override
	public int size()
	{
		return _handlers.length;
	}

	@Override
	public void clear()
	{

	}
}