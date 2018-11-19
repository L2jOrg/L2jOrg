package l2s.gameserver.handler.chat;

import l2s.gameserver.network.l2.components.ChatType;

/**
 * @author VISTALL
 * @date 18:16/12.03.2011
 */
public interface IChatHandler
{
	void say();

	ChatType getType();
}