package l2s.gameserver.templates;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import l2s.commons.time.cron.SchedulingPattern;
import l2s.commons.util.Rnd;
import l2s.gameserver.model.Party;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Territory;
import l2s.gameserver.templates.spawn.SpawnTemplate;
import l2s.gameserver.utils.Location;

import org.napile.primitive.maps.IntObjectMap;

public class InstantZone
{
	public static class DoorInfo
	{
		private final DoorTemplate _template;
		private final boolean _opened;
		private final boolean _invul;

		public DoorInfo(DoorTemplate template, boolean opened, boolean invul)
		{
			_template = template;
			_opened = opened;
			_invul = invul;
		}

		public DoorTemplate getTemplate()
		{
			return _template;
		}

		public boolean isOpened()
		{
			return _opened;
		}

		public boolean isInvulnerable()
		{
			return _invul;
		}
	}

	public static class ZoneInfo
	{
		private final ZoneTemplate _template;
		private final boolean _active;

		public ZoneInfo(ZoneTemplate template, boolean opened)
		{
			_template = template;
			_active = opened;
		}

		public ZoneTemplate getTemplate()
		{
			return _template;
		}

		public boolean isActive()
		{
			return _active;
		}
	}

	public static class SpawnInfo2
	{
		private List<SpawnTemplate> _template;
		private boolean _spawned;

		public SpawnInfo2(List<SpawnTemplate> template, boolean spawned)
		{
			_template = template;
			_spawned = spawned;
		}

		public List<SpawnTemplate> getTemplates()
		{
			return _template;
		}

		public boolean isSpawned()
		{
			return _spawned;
		}
	}

	//@Deprecated
	public static class SpawnInfo
	{
		private final int _spawnType;
		private final int _npcId;
		private final int _count;
		private final int _respawn;
		private final int _respawnRnd;
		private final List<Location> _coords;
		private final Territory _territory;

		public SpawnInfo(int spawnType, int npcId, int count, int respawn, int respawnRnd, Territory territory)
		{
			this(spawnType, npcId, count, respawn, respawnRnd, null, territory);
		}

		public SpawnInfo(int spawnType, int npcId, int count, int respawn, int respawnRnd, List<Location> coords)
		{
			this(spawnType, npcId, count, respawn, respawnRnd, coords, null);
		}

		public SpawnInfo(int spawnType, int npcId, int count, int respawn, int respawnRnd, List<Location> coords, Territory territory)
		{
			_spawnType = spawnType;
			_npcId = npcId;
			_count = count;
			_respawn = respawn;
			_respawnRnd = respawnRnd;
			_coords = coords;
			_territory = territory;
		}

		public int getSpawnType()
		{
			return _spawnType;
		}

		public int getNpcId()
		{
			return _npcId;
		}

		public int getCount()
		{
			return _count;
		}

		public int getRespawnDelay()
		{
			return _respawn;
		}

		public int getRespawnRnd()
		{
			return _respawnRnd;
		}

		public List<Location> getCoords()
		{
			return _coords;
		}

		public Territory getLoc()
		{
			return _territory;
		}
	}

	private final int _id;
	private final String _name;
	private final SchedulingPattern _resetReuse;
	private final int _sharedReuseGroup;
	private final int _timelimit;
	private boolean _dispelBuffs;
	private final int _minLevel;
	private final int _maxLevel;
	private final int _minParty;
	private final int _maxParty;
	private final boolean _onPartyDismiss;
	private final int _timer;
	private final List<Location> _teleportCoords;
	private final Location _returnCoords;

	private final IntObjectMap<DoorInfo> _doors;
	private final Map<String, ZoneInfo> _zones;
	private final Map<String, SpawnInfo2> _spawns;

	private final List<SpawnInfo> _spawnsInfo;
	private final int _collapseIfEmpty;
	private final int _maxChannels;
	private final int _removedItemId;
	private final int _removedItemCount;
	private final boolean _removedItemNecessity;
	private final int _giveItemId;
	private final int _givedItemCount;
	private final int _requiredQuestId;
	private final boolean _setReuseUponEntry;
	private final StatsSet _addParams;

    private final List<InstantZoneEntryType> _entryTypes = new ArrayList<InstantZoneEntryType>();

	public InstantZone(int id, String name, SchedulingPattern resetReuse, int sharedReuseGroup, int timelimit, boolean dispelBuffs, int minLevel, int maxLevel, int minParty, int maxParty, int timer, boolean onPartyDismiss, List<Location> tele, Location ret, IntObjectMap<DoorInfo> doors, Map<String, ZoneInfo> zones, Map<String, SpawnInfo2> spawns, List<SpawnInfo> spawnsInfo, int collapseIfEmpty, int maxChannels, int removedItemId, int removedItemCount, boolean removedItemNecessity, int giveItemId, int givedItemCount, int requiredQuestId, boolean setReuseUponEntry, StatsSet params)
	{
		_id = id;
		_name = name;
		_resetReuse = resetReuse;
		_sharedReuseGroup = sharedReuseGroup;
		_timelimit = timelimit;
		_dispelBuffs = dispelBuffs;
		_minLevel = minLevel;
		_maxLevel = maxLevel;
		_teleportCoords = tele;
		_returnCoords = ret;
		_minParty = minParty;
		_maxParty = maxParty;
		_onPartyDismiss = onPartyDismiss;
		_timer = timer;
		_doors = doors;
		_zones = zones;
		_spawnsInfo = spawnsInfo;
		_spawns = spawns;
		_collapseIfEmpty = collapseIfEmpty;
		_maxChannels = maxChannels;
		_removedItemId = removedItemId;
		_removedItemCount = removedItemCount;
		_removedItemNecessity = removedItemNecessity;
		_giveItemId = giveItemId;
		_givedItemCount = givedItemCount;
		_requiredQuestId = requiredQuestId;
		_setReuseUponEntry = setReuseUponEntry;
		_addParams = params;

        if(getMinParty() == 1)
            _entryTypes.add(InstantZoneEntryType.SOLO);
        if(getMinParty() <= Party.MAX_SIZE && getMaxParty() > 1)
            _entryTypes.add(InstantZoneEntryType.PARTY);
        if(getMaxParty() > Party.MAX_SIZE)
            _entryTypes.add(InstantZoneEntryType.COMMAND_CHANNEL);
	}

	public int getId()
	{
		return _id;
	}

	public String getName()
	{
		return _name;
	}

	public SchedulingPattern getResetReuse()
	{
		return _resetReuse;
	}

	public boolean isDispelBuffs()
	{
		return _dispelBuffs;
	}

	public int getTimelimit()
	{
		return _timelimit;
	}

	public int getMinLevel()
	{
		return _minLevel;
	}

	public int getMaxLevel()
	{
		return _maxLevel;
	}

	public int getMinParty()
	{
		return _minParty;
	}

	public int getMaxParty()
	{
		return _maxParty;
	}

	public int getTimerOnCollapse()
	{
		return _timer;
	}

	public boolean isCollapseOnPartyDismiss()
	{
		return _onPartyDismiss;
	}

	public Location getTeleportCoord()
	{
		if (_teleportCoords == null || _teleportCoords.size() == 0)
			return null;			
		if(_teleportCoords.size() == 1) // fast hack?
			return _teleportCoords.get(0);
		return _teleportCoords.get(Rnd.get(_teleportCoords.size()));
	}

	public Location getReturnCoords()
	{
		return _returnCoords;
	}

	public List<SpawnInfo> getSpawnsInfo()
	{
		return _spawnsInfo;
	}

	public int getSharedReuseGroup()
	{
		return _sharedReuseGroup;
	}

	public int getCollapseIfEmpty()
	{
		return _collapseIfEmpty;
	}

	public int getRemovedItemId()
	{
		return _removedItemId;
	}

	public int getRemovedItemCount()
	{
		return _removedItemCount;
	}

	public boolean getRemovedItemNecessity()
	{
		return _removedItemNecessity;
	}

	public int getGiveItemId()
	{
		return _giveItemId;
	}

	public int getGiveItemCount()
	{
		return _givedItemCount;
	}

	public int getRequiredQuestId()
	{
		return _requiredQuestId;
	}

	public boolean getSetReuseUponEntry()
	{
		return _setReuseUponEntry;
	}

	public int getMaxChannels()
	{
		return _maxChannels;
	}

    public InstantZoneEntryType getEntryType(Player player)
    {
        Party party = player.getParty();
        if(party != null)
        {
            if(party.getCommandChannel() != null && _entryTypes.contains(InstantZoneEntryType.COMMAND_CHANNEL))
                return InstantZoneEntryType.COMMAND_CHANNEL;
	        else if(_entryTypes.contains(InstantZoneEntryType.PARTY))
                return InstantZoneEntryType.PARTY;
        }

        if(_entryTypes.contains(InstantZoneEntryType.SOLO))
            return InstantZoneEntryType.SOLO;
        else if(_entryTypes.contains(InstantZoneEntryType.PARTY))
            return InstantZoneEntryType.PARTY;
        else if(_entryTypes.contains(InstantZoneEntryType.COMMAND_CHANNEL))
            return InstantZoneEntryType.COMMAND_CHANNEL;

        return null;
    }

	public IntObjectMap<DoorInfo> getDoors()
	{
		return _doors;
	}

	public Map<String, ZoneInfo> getZones()
	{
		return _zones;
	}

	public List<Location> getTeleportCoords()
	{
		return _teleportCoords;
	}

	public Map<String, SpawnInfo2> getSpawns()
	{
		return _spawns;
	}

	public StatsSet getAddParams()
	{
		return _addParams;
	}
}