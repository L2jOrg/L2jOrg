package handler.admincommands;

import l2s.gameserver.handler.admincommands.AdminCommandHandler;
import l2s.gameserver.handler.admincommands.IAdminCommandHandler;
import l2s.gameserver.listener.script.OnInitScriptListener;

/**
 * @author VISTALL
 * @date 22:57/08.04.2011
 */
public abstract class ScriptAdminCommand implements IAdminCommandHandler, OnInitScriptListener
{
	@Override
	public void onInit()
	{
		AdminCommandHandler.getInstance().registerAdminCommandHandler(this);
	}
}
