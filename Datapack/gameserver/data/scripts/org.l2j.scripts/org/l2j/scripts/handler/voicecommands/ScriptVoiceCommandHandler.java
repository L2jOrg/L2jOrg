package org.l2j.scripts.handler.voicecommands;

import org.l2j.gameserver.handler.voicecommands.IVoicedCommandHandler;
import org.l2j.gameserver.handler.voicecommands.VoicedCommandHandler;
import org.l2j.gameserver.listener.script.OnInitScriptListener;

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