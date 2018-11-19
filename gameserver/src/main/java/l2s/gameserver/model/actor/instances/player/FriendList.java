package l2s.gameserver.model.actor.instances.player;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.Collection;

import l2s.gameserver.network.l2.s2c.*;
import org.apache.commons.lang3.StringUtils;
import l2s.gameserver.dao.CharacterFriendDAO;
import l2s.gameserver.model.GameObject;
import l2s.gameserver.model.GameObjectsStorage;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Request;
import l2s.gameserver.model.Request.L2RequestType;
import l2s.gameserver.model.World;
import l2s.gameserver.network.l2.components.IBroadcastPacket;
import l2s.gameserver.network.l2.components.SystemMsg;

public class FriendList
{
	public static final int MAX_FRIEND_SIZE = 128;

	private TIntObjectMap<Friend> _friendList = new TIntObjectHashMap<Friend>(0);
	private final Player _owner;

	public FriendList(Player owner)
	{
		_owner = owner;
	}

	public void restore()
	{
		_friendList = CharacterFriendDAO.getInstance().select(_owner);
	}

	public void add(Player friendPlayer)
	{
		_friendList.put(friendPlayer.getObjectId(), new Friend(friendPlayer));

		CharacterFriendDAO.getInstance().insert(_owner, friendPlayer);
	}

	public Friend get(int objectId)
	{
		return _friendList.get(objectId);
	}

	public Friend get(String name)
	{
		if(StringUtils.isEmpty(name))
			return null;

		for(Friend friend : values())
		{
			if(name.equalsIgnoreCase(friend.getName()))
				return friend;
		}
		return null;
	}

	public boolean contains(int objectId)
	{
		return _friendList.containsKey(objectId);
	}

	public int size()
	{
		return _friendList.size();
	}

	public Friend[] values()
	{
		return _friendList.values(new Friend[_friendList.size()]);
	}

	public Collection<Friend> valueCollection()
	{
		return _friendList.valueCollection();
	}

	public boolean isEmpty()
	{
		return _friendList.isEmpty();
	}

	public void remove(int objectId)
	{
		_friendList.remove(objectId);
		CharacterFriendDAO.getInstance().delete(_owner.getObjectId(), objectId);
	}

	public void remove(String name)
	{
		if(StringUtils.isEmpty(name))
			return;

		int objectId = remove0(name);
		if(objectId > 0)
		{
			Player friendChar = World.getPlayer(objectId);

			_owner.sendPacket(new SystemMessage(SystemMessage.S1_HAS_BEEN_REMOVED_FROM_YOUR_FRIEND_LIST).addString(name), new FriendRemove(name));

			if(friendChar != null)
				friendChar.sendPacket(new SystemMessage(SystemMessage.S1__HAS_BEEN_DELETED_FROM_YOUR_FRIENDS_LIST).addString(_owner.getName()), new L2FriendListPacket(friendChar));
		}
		else
			_owner.sendPacket(new SystemMessage(SystemMessage.S1_IS_NOT_ON_YOUR_FRIEND_LIST).addString(name));
	}

	private int remove0(String name)
	{
		if(StringUtils.isEmpty(name))
			return 0;

		int objectId = 0;
		for(Friend friend : values())
		{
			if(name.equalsIgnoreCase(friend.getName()))
			{
				objectId = friend.getObjectId();
				break;
			}
		}

		if(objectId > 0)
		{
			remove(objectId);

			Player friendPlayer = GameObjectsStorage.getPlayer(objectId);
			if(friendPlayer != null)
				friendPlayer.getFriendList().remove(_owner.getObjectId());
			else
				CharacterFriendDAO.getInstance().delete(objectId, _owner.getObjectId());
			return objectId;
		}
		return 0;
	}

	public void notifyChangeName()
	{
		//TODO: [Bonux] ExFriendNotifyNameChange.
	}

	public void notifyFriends(boolean login)
	{
		for(Friend friend : values())
		{
			Player friendPlayer = GameObjectsStorage.getPlayer(friend.getObjectId());
			if(friendPlayer == null)
				continue;

			Friend thisFriend = friendPlayer.getFriendList().get(_owner.getObjectId());
			if(thisFriend == null)
				continue;

			thisFriend.update(_owner, login);

			if(login)
				friendPlayer.sendPacket(new SystemMessage(SystemMessage.S1_FRIEND_HAS_LOGGED_IN).addString(_owner.getName()));

			friendPlayer.sendPacket(new FriendStatus(thisFriend, login));

			friend.update(friendPlayer, login);
		}
	}

	public boolean updateMemo(String name, String memo)
	{
		if(memo.length() > 50)
			return false;

		Friend friend = get(name);
		if(friend == null)
			return false;

		friend.setMemo(memo);

		_owner.sendPacket(new ExFriendDetailInfo(_owner, friend));

		return CharacterFriendDAO.getInstance().updateMemo(_owner, friend.getObjectId(), memo);
	}

	public IBroadcastPacket requestFriendInvite(GameObject target)
	{
		if(_owner.isProcessingRequest())
			return SystemMsg.WAITING_FOR_ANOTHER_REPLY;

		if(size() >= MAX_FRIEND_SIZE)
			return SystemMsg.YOU_CAN_ONLY_ENTER_UP_128_NAMES_IN_YOUR_FRIENDS_LIST;

		if(target == null)
			return SystemMsg.THE_USER_WHO_REQUESTED_TO_BECOME_FRIENDS_IS_NOT_FOUND_IN_THE_GAME;

		if(!target.isPlayer())
			return null;

		Player player = target.getPlayer();
		if(player == _owner)
			return SystemMsg.YOU_CANNOT_ADD_YOURSELF_TO_YOUR_OWN_FRIEND_LIST;

		if(player.isBlockAll() || player.getBlockList().contains(_owner) || player.getMessageRefusal())
			return SystemMsg.THAT_PERSON_IS_IN_MESSAGE_REFUSAL_MODE;

		if(contains(player.getObjectId()))
			return new SystemMessagePacket(SystemMsg.C1_IS_ALREADY_ON_YOUR_FRIEND_LIST).addName(player);

		if(player.getFriendList().size() >= MAX_FRIEND_SIZE)
			return SystemMsg.THE_FRIENDS_LIST_OF_THE_PERSON_YOU_ARE_TRYING_TO_ADD_IS_FULL_SO_REGISTRATION_IS_NOT_POSSIBLE;

		if(player.isInOlympiadMode())
			return SystemMsg.A_USER_CURRENTLY_PARTICIPATING_IN_THE_OLYMPIAD_CANNOT_SEND_PARTY_AND_FRIEND_INVITATIONS;

		new Request(L2RequestType.FRIEND, _owner, player).setTimeout(10000L);

		player.sendPacket(new FriendAddRequest(_owner.getName()));
		return null;
	}

	@Override
	public String toString()
	{
		return "FriendList[owner=" + _owner.getName() + "]";
	}
}