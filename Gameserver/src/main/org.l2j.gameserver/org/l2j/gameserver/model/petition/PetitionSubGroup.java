package org.l2j.gameserver.model.petition;

import org.l2j.gameserver.handler.petition.IPetitionHandler;
import org.l2j.gameserver.scripts.Scripts;

/**
 * @author VISTALL
 * @date 7:32/25.07.2011
 */
public class PetitionSubGroup extends PetitionGroup
{
	private final IPetitionHandler _handler;

	public PetitionSubGroup(int id, String handler)
	{
		super(id);

		Class<?> clazz = Scripts.getInstance().getClasses().get("handler.petition." + handler);

		try
		{
			_handler = (IPetitionHandler) clazz.getDeclaredConstructor().newInstance();
		}
		catch(Exception e)
		{
			throw new Error(e);
		}
	}

	public IPetitionHandler getHandler()
	{
		return _handler;
	}
}