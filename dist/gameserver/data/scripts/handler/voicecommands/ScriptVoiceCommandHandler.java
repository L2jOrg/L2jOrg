package handler.voicecommands;

import l2s.gameserver.handler.voicecommands.IVoicedCommandHandler;
import l2s.gameserver.handler.voicecommands.VoicedCommandHandler;
import l2s.gameserver.listener.script.OnInitScriptListener;

/**
 * @author Bonux
**/
public abstract class ScriptVoiceCommandHandler implements IVoicedCommandHandler, OnInitScriptListener
{
	@Override
	public void onInit()
	{
		VoicedCommandHandler.getInstance().registerVoicedCommandHandler(this);
	}
}