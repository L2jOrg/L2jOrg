package l2s.gameserver.model.petition;

import l2s.gameserver.handler.petition.IPetitionHandler;
import l2s.gameserver.scripts.Scripts;

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
			_handler = (IPetitionHandler) clazz.newInstance();
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