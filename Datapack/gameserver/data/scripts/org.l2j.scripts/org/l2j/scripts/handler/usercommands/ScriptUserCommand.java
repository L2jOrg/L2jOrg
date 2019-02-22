package org.l2j.scripts.handler.usercommands;

import org.l2j.gameserver.handler.usercommands.IUserCommandHandler;
import org.l2j.gameserver.handler.usercommands.UserCommandHandler;
import org.l2j.gameserver.listener.script.OnInitScriptListener;

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