package handler.usercommands;

import l2s.gameserver.handler.usercommands.IUserCommandHandler;
import l2s.gameserver.handler.usercommands.UserCommandHandler;
import l2s.gameserver.listener.script.OnInitScriptListener;

/**
 * @author VISTALL
 * @date 16:53/24.06.2011
 */
public abstract class ScriptUserCommand implements IUserCommandHandler, OnInitScriptListener
{
	@Override
	public void onInit()
	{
		UserCommandHandler.getInstance().registerUserCommandHandler(this);
	}
}