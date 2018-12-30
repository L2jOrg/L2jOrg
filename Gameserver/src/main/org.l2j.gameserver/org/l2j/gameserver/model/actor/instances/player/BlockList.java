package org.l2j.gameserver.model.actor.instances.player;

import java.util.Collection;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.dao.CharacterBlockListDAO;
import org.l2j.gameserver.dao.CharacterDAO;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.World;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.network.l2.s2c.ExBlockAddResult;
import org.l2j.gameserver.network.l2.s2c.ExBlockDefailInfo;
import org.l2j.gameserver.network.l2.s2c.ExBlockRemoveResult;
import org.l2j.gameserver.network.l2.s2c.SystemMessagePacket;
import io.github.joealisson.primitive.maps.IntObjectMap;
import io.github.joealisson.primitive.maps.impl.HashIntObjectMap;

import static org.l2j.commons.util.Util.isNullOrEmpty;

/**
 * @author Bonux
**/
public class BlockList
{
	public static final int MAX_BLOCK_LIST_SIZE = 128;

	private IntObjectMap<Block> _blockList = new HashIntObjectMap<Block>(0);
	private final Player _owner;

	public BlockList(Player owner)
	{
		_owner = owner;
	}

	public void restore()
	{
		_blockList = CharacterBlockListDAO.getInstance().select(_owner);
	}

	public Block get(int objectId)
	{
		return _blockList.get(objectId);
	}

	public Block get(String name)
	{
		if(isNullOrEmpty(name))
			return null;

		for(Block b : values())
		{
			if(name.equalsIgnoreCase(b.getName()))
				return b;
		}
		return null;
	}

	public boolean contains(int objectId)
	{
		return _blockList.containsKey(objectId);
	}

	public boolean contains(Player player)
	{
		if(player == null)
			return false;
		return contains(player.getObjectId());
	}

	public boolean contains(String name)
	{
		return get(name) != null;
	}

	public int size()
	{
		return _blockList.size();
	}

	public Block[] values()
	{
		return _blockList.values().toArray(new Block[_blockList.size()]);
	}

	public Collection<Block> valueCollection()
	{
		return _blockList.values();
	}

	public boolean isEmpty()
	{
		return _blockList.isEmpty();
	}

	public void add(String name)
	{
		if(isNullOrEmpty(name) || name.equalsIgnoreCase(_owner.getName()) || contains(name))
		{
			_owner.sendPacket(SystemMsg.YOU_HAVE_FAILED_TO_REGISTER_THE_USER_TO_YOUR_IGNORE_LIST);
			return;
		}

		int blockedObjId;
		Player blockedPlayer = World.getPlayer(name);
		if(blockedPlayer != null)
		{
			if(blockedPlayer.isGM())
			{
				_owner.sendPacket(SystemMsg.YOU_MAY_NOT_IMPOSE_A_BLOCK_ON_A_GM);
				return;
			}
			blockedObjId = blockedPlayer.getObjectId();
		}
		else
		{
			blockedObjId = CharacterDAO.getInstance().getObjectIdByName(name);
			if(blockedObjId == 0)
			{
				_owner.sendPacket(SystemMsg.YOU_HAVE_FAILED_TO_REGISTER_THE_USER_TO_YOUR_IGNORE_LIST);
				return;
			}

			if(Config.gmlist.containsKey(blockedObjId) && Config.gmlist.get(blockedObjId).IsGM)
			{
				_owner.sendPacket(SystemMsg.YOU_MAY_NOT_IMPOSE_A_BLOCK_ON_A_GM);
				return;
			}
		}

		_owner.sendPacket(new SystemMessagePacket(SystemMsg.S1_HAS_BEEN_ADDED_TO_YOUR_IGNORE_LIST).addString(name));
		_owner.sendPacket(new ExBlockAddResult(name));

		_blockList.put(blockedObjId, new Block(blockedObjId, name));

		CharacterBlockListDAO.getInstance().insert(_owner, blockedObjId);
	}

	public void remove(String name)
	{
		if(isNullOrEmpty(name))
			return;

		int blockedObjId = 0;
		for(Block b : values())
		{
			if(name.equalsIgnoreCase(b.getName()))
			{
				blockedObjId = b.getObjectId();
				break;
			}
		}

		if(blockedObjId == 0)
		{
			_owner.sendPacket(SystemMsg.YOU_HAVE_FAILED_TO_DELETE_THE_CHARACTER_);
			return;
		}

		_owner.sendPacket(new SystemMessagePacket(SystemMsg.S1_HAS_BEEN_REMOVED_FROM_YOUR_IGNORE_LIST).addString(name));
		_owner.sendPacket(new ExBlockRemoveResult(name));

		_blockList.remove(blockedObjId);

		CharacterBlockListDAO.getInstance().delete(_owner, blockedObjId);
	}

	public boolean updateMemo(String name, String memo)
	{
		if(memo.length() > 50)
			return false;

		Block block = get(name);
		if(block == null)
			return false;

		block.setMemo(memo);
		_owner.sendPacket(new ExBlockDefailInfo(name, memo));
		return CharacterBlockListDAO.getInstance().updateMemo(_owner, block.getObjectId(), memo);
	}

	@Override
	public String toString()
	{
		return "BlockList[owner=" + _owner.getName() + "]";
	}
}