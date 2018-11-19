package l2s.gameserver.handler.admincommands;

import l2s.gameserver.model.Player;

public interface IAdminCommandHandler
{
	/**
	 * this is the worker method that is called when someone uses an admin command.
	 * @param comm
	 * @param wordList
	 * @param fullString
	 * @param activeChar
	 * @return command success
	 */
	public boolean useAdminCommand(Enum<?> comm, String[] wordList, String fullString, Player activeChar);

	/**
	 * this method is called at initialization to register all the item ids automatically
	 * @return all known itemIds
	 */
	public Enum<?>[] getAdminCommandEnum();
}