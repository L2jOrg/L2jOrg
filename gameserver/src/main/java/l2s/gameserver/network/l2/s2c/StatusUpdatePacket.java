package l2s.gameserver.network.l2.s2c;

import l2s.gameserver.model.Creature;

import java.util.ArrayList;
import java.util.List;

/**
 * Даные параметры актуальны для С6(Interlude), 04/10/2007, протокол 746
 */
public class StatusUpdatePacket extends L2GameServerPacket
{
	/**
	 * Даный параметр отсылается оффом в паре с MAX_HP
	 * Сначала CUR_HP, потом MAX_HP
	 */
	public final static int CUR_HP = 0x09;
	public final static int MAX_HP = 0x0a;

	/**
	 * Даный параметр отсылается оффом в паре с MAX_MP
	 * Сначала CUR_MP, потом MAX_MP
	 */
	public final static int CUR_MP = 0x0b;
	public final static int MAX_MP = 0x0c;

	/**
	 * Меняется отображение только в инвентаре, для статуса требуется UserInfo
	 */
	public final static int CUR_LOAD = 0x0e;

	/**
	 * Меняется отображение только в инвентаре, для статуса требуется UserInfo
	 */
	public final static int MAX_LOAD = 0x0f;

	public final static int PVP_FLAG = 0x1a;
	public final static int KARMA = 0x1b;

	/**
	 * Даный параметр отсылается оффом в паре с MAX_CP
	 * Сначала CUR_CP, потом MAX_CP
	 */
	public final static int CUR_CP = 0x21;
	public final static int MAX_CP = 0x22;

	private final int _objectId, _casterId;
	private final boolean _playable;
	private boolean _visible;
	private final List<Attribute> _attributes = new ArrayList<Attribute>();

	class Attribute
	{
		public final int id;
		public final int value;

		Attribute(int id, int value)
		{
			this.id = id;
			this.value = value;
		}
	}

	public StatusUpdatePacket(Creature creature)
	{
		_objectId = creature.getObjectId();
		_playable = creature.isPlayable();
		_casterId = 0;
	}

	public StatusUpdatePacket(Creature creature, Creature caster)
	{
		_objectId = creature.getObjectId();
		_playable = creature.isPlayable();
		_casterId = caster == null ? 0 : caster.getObjectId();
	}

	public StatusUpdatePacket addAttribute(int id, int level)
	{
		_attributes.add(new Attribute(id, level));
		if(_playable)
		{
			switch(id)
			{

				case CUR_HP:
				case CUR_MP:
				case CUR_CP:
					_visible = true;
					break;
			}
		}
		return this;
	}

	@Override
	protected final void writeImpl()
	{
		writeD(_objectId);
		writeD(_visible ? _casterId : 0x00);
		writeC(_visible ? 0x01 : 0x00); // при 1 идет рег хп
		writeC(_attributes.size());

		for(Attribute temp : _attributes)
		{
			writeC(temp.id);
			writeD(temp.value);
		}
	}

	public boolean hasAttributes()
	{
		return !_attributes.isEmpty();
	}
}