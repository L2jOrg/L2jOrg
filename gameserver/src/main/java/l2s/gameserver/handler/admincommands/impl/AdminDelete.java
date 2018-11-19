package l2s.gameserver.handler.admincommands.impl;

import l2s.gameserver.instancemanager.RaidBossSpawnManager;
import org.apache.commons.lang3.math.NumberUtils;
import l2s.gameserver.dao.SpawnsDAO;
import l2s.gameserver.handler.admincommands.IAdminCommandHandler;
import l2s.gameserver.model.GameObject;
import l2s.gameserver.model.GameObjectsStorage;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Spawner;
import l2s.gameserver.model.instances.NpcInstance;
import l2s.gameserver.network.l2.components.SystemMsg;

public class AdminDelete implements IAdminCommandHandler
{
	private static enum Commands
	{
		admin_delete
	}

	@Override
	public boolean useAdminCommand(Enum<?> comm, String[] wordList, String fullString, Player activeChar)
	{
		Commands command = (Commands) comm;

		if(!activeChar.getPlayerAccess().CanEditNPC)
			return false;

		switch(command)
		{
			case admin_delete:
				GameObject obj = wordList.length == 1 ? activeChar.getTarget() : GameObjectsStorage.getNpc(NumberUtils.toInt(wordList[1]));
				if(obj != null && obj.isNpc())
				{
					NpcInstance target = (NpcInstance) obj;
					target.deleteMe();

					Spawner spawn = target.getSpawn();
					if(spawn != null)
						spawn.stopRespawn();

					SpawnsDAO.getInstance().delete(target);
					RaidBossSpawnManager.getInstance().deleteSpawn(target.getNpcId());
				}
				else
					activeChar.sendPacket(SystemMsg.INVALID_TARGET);
				break;
		}

		return true;
	}

	@Override
	public Enum<?>[] getAdminCommandEnum()
	{
		return Commands.values();
	}
}