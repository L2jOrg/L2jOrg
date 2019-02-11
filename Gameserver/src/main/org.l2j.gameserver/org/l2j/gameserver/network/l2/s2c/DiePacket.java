package org.l2j.gameserver.network.l2.s2c;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.actor.instances.creature.Abnormal;
import org.l2j.gameserver.model.base.RestartType;
import org.l2j.gameserver.model.entity.events.Event;
import org.l2j.gameserver.model.instances.MonsterInstance;
import org.l2j.gameserver.model.pledge.Clan;
import org.l2j.gameserver.network.l2.GameClient;

public class DiePacket extends L2GameServerPacket
{
	private final int _objectId;
	private final boolean _hideDieAnimation;
	private boolean _sweepable = false;
	private int _blessingFeatherDelay = 0;

	private final Map<RestartType, Boolean> _types = new HashMap<RestartType, Boolean>(RestartType.VALUES.length);

	public DiePacket(Creature cha, boolean hideDieAnimation)
	{
		_hideDieAnimation = hideDieAnimation;
		_objectId = cha.getObjectId();

		if(cha.isMonster())
			_sweepable = ((MonsterInstance) cha).isSweepActive();
		else if(cha.isPlayer())
		{
			Player player = (Player) cha;

			put(RestartType.FIXED, player.canFixedRessurect());
			put(RestartType.AGATHION, player.isAgathionResAvailable());
			put(RestartType.TO_VILLAGE, true);
			put(RestartType.ADVENTURES_SONG, player.getAbnormalList().contains(22410) || player.getAbnormalList().contains(22411));

			for(Abnormal effect : player.getAbnormalList())
			{
				if(effect.getSkill().getId() == 7008)
				{
					_blessingFeatherDelay = effect.getTimeLeft();
					break;
				}
			}

			Clan clan = null;
			if(get(RestartType.TO_VILLAGE))
				clan = player.getClan();
			if(clan != null)
			{
				put(RestartType.TO_CLANHALL, clan.getHasHideout() != 0);
				put(RestartType.TO_CASTLE, clan.getCastle() != 0);
			}

			for(Event e : cha.getEvents())
				e.checkRestartLocs(player, _types);
		}
	}

	public DiePacket(Creature cha)
	{
		this(cha, false);
	}

	@Override
	protected final void writeImpl(GameClient client, ByteBuffer buffer)
	{
		buffer.putInt(_objectId);
		buffer.putInt(get(RestartType.TO_VILLAGE) ? 1 : 0); // to nearest village
		buffer.putInt(get(RestartType.TO_CLANHALL) ? 1 : 0); // to hide away
		buffer.putInt(get(RestartType.TO_CASTLE) ? 1 : 0); // to castle
		buffer.putInt(get(RestartType.TO_FLAG) ? 1 : 0);// to siege HQ
		buffer.putInt(_sweepable ? 0x01 : 0x00); // sweepable  (blue glow)
		buffer.putInt(get(RestartType.FIXED) ? 1 : 0);// FIXED
		buffer.putInt(0x00); //unk
		buffer.putInt(_blessingFeatherDelay);
		buffer.putInt(get(RestartType.ADVENTURES_SONG) ? 1 : 0);
		buffer.put((byte) (_hideDieAnimation ? 0x01 : 0x00));
		buffer.putInt(get(RestartType.AGATHION) ? 1 : 0);//agathion ress button

		int itemsCount = 0;
		buffer.putInt(itemsCount);
		for(int i = 0; i < itemsCount; i++)
			buffer.putInt(0x00); //additional free space
	}

	private void put(RestartType t, boolean b)
	{
		_types.put(t, b);
	}

	private boolean get(RestartType t)
	{
		Boolean b = _types.get(t);
		return b != null && b;
	}
}