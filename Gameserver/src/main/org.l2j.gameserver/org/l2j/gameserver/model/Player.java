package org.l2j.gameserver.model;

import io.github.joealisson.primitive.Containers;
import io.github.joealisson.primitive.lists.IntList;
import io.github.joealisson.primitive.lists.impl.ArrayIntList;
import io.github.joealisson.primitive.maps.IntLongMap;
import io.github.joealisson.primitive.maps.IntObjectMap;
import io.github.joealisson.primitive.maps.impl.CHashIntObjectMap;
import io.github.joealisson.primitive.maps.impl.CTreeIntObjectMap;
import io.github.joealisson.primitive.maps.impl.HashIntObjectMap;
import io.github.joealisson.primitive.pair.IntLongPair;
import io.github.joealisson.primitive.pair.IntObjectPair;
import io.github.joealisson.primitive.pair.impl.IntObjectPairImpl;
import org.l2j.commons.collections.CollectionUtils;
import org.l2j.commons.database.DatabaseFactory;
import org.l2j.commons.dbutils.DbUtils;
import org.l2j.commons.lang.reference.HardReference;
import org.l2j.commons.lang.reference.HardReferences;
import org.l2j.commons.threading.RunnableImpl;
import org.l2j.commons.util.Converter;
import org.l2j.commons.util.Rnd;
import org.l2j.commons.util.Util;
import org.l2j.commons.util.concurrent.atomic.AtomicState;
import org.l2j.gameserver.*;
import org.l2j.gameserver.Contants.Items;
import org.l2j.gameserver.ai.CtrlEvent;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.ai.PlayableAI.AINextAction;
import org.l2j.gameserver.ai.PlayerAI;
import org.l2j.gameserver.data.QuestHolder;
import org.l2j.gameserver.data.dao.*;
import org.l2j.gameserver.data.database.mysql;
import org.l2j.gameserver.data.xml.holder.*;
import org.l2j.gameserver.handler.items.IItemHandler;
import org.l2j.gameserver.handler.onshiftaction.OnShiftActionHolder;
import org.l2j.gameserver.idfactory.IdFactory;
import org.l2j.gameserver.instancemanager.*;
import org.l2j.gameserver.instancemanager.BotCheckManager.BotCheckQuestion;
import org.l2j.gameserver.listener.actor.player.OnAnswerListener;
import org.l2j.gameserver.listener.actor.player.OnPlayerChatMessageReceive;
import org.l2j.gameserver.listener.actor.player.impl.BotCheckAnswerListner;
import org.l2j.gameserver.listener.actor.player.impl.ReviveAnswerListener;
import org.l2j.gameserver.listener.actor.player.impl.SummonAnswerListener;
import org.l2j.gameserver.listener.hooks.ListenerHook;
import org.l2j.gameserver.listener.hooks.ListenerHookType;
import org.l2j.gameserver.model.GameObjectTasks.*;
import org.l2j.gameserver.model.Request.L2RequestType;
import org.l2j.gameserver.model.Zone.ZoneType;
import org.l2j.gameserver.model.actor.basestats.PlayerBaseStats;
import org.l2j.gameserver.model.actor.flags.PlayerFlags;
import org.l2j.gameserver.model.actor.instances.creature.Abnormal;
import org.l2j.gameserver.model.actor.instances.player.FriendList;
import org.l2j.gameserver.model.actor.instances.player.*;
import org.l2j.gameserver.model.actor.instances.player.tasks.EnableUserRelationTask;
import org.l2j.gameserver.model.actor.listener.PlayerListenerList;
import org.l2j.gameserver.model.actor.recorder.PlayerStatsChangeRecorder;
import org.l2j.gameserver.model.base.*;
import org.l2j.gameserver.model.entity.Hero;
import org.l2j.gameserver.model.entity.Reflection;
import org.l2j.gameserver.model.entity.events.Event;
import org.l2j.gameserver.model.entity.events.impl.*;
import org.l2j.gameserver.model.entity.olympiad.Olympiad;
import org.l2j.gameserver.model.entity.olympiad.OlympiadGame;
import org.l2j.gameserver.model.entity.olympiad.OlympiadParticipiantData;
import org.l2j.gameserver.model.entity.residence.Castle;
import org.l2j.gameserver.model.entity.residence.ClanHall;
import org.l2j.gameserver.model.entity.residence.Residence;
import org.l2j.gameserver.model.entity.residence.ResidenceSide;
import org.l2j.gameserver.model.instances.*;
import org.l2j.gameserver.model.instances.SummonInstance.RestoredSummon;
import org.l2j.gameserver.model.instances.residences.SiegeToggleNpcInstance;
import org.l2j.gameserver.model.items.*;
import org.l2j.gameserver.model.items.Warehouse.WarehouseType;
import org.l2j.gameserver.model.items.attachment.FlagItemAttachment;
import org.l2j.gameserver.model.items.attachment.PickableAttachment;
import org.l2j.gameserver.model.matching.MatchingRoom;
import org.l2j.gameserver.model.petition.PetitionMainGroup;
import org.l2j.gameserver.model.pledge.*;
import org.l2j.gameserver.model.quest.Quest;
import org.l2j.gameserver.model.quest.QuestEventType;
import org.l2j.gameserver.model.quest.QuestState;
import org.l2j.gameserver.network.authcomm.AuthServerCommunication;
import org.l2j.gameserver.network.authcomm.gs2as.ReduceAccountPoints;
import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.network.l2.components.*;
import org.l2j.gameserver.network.l2.s2c.*;
import org.l2j.gameserver.network.l2.s2c.updatetype.IUpdateTypeComponent;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.skills.AbnormalType;
import org.l2j.gameserver.skills.EffectType;
import org.l2j.gameserver.skills.SkillEntry;
import org.l2j.gameserver.skills.TimeStamp;
import org.l2j.gameserver.skills.skillclasses.Summon;
import org.l2j.gameserver.stats.Formulas;
import org.l2j.gameserver.stats.Stats;
import org.l2j.gameserver.stats.funcs.FuncTemplate;
import org.l2j.gameserver.tables.ClanTable;
import org.l2j.gameserver.tables.GmListTable;
import org.l2j.gameserver.taskmanager.AutoSaveManager;
import org.l2j.gameserver.taskmanager.LazyPrecisionTaskManager;
import org.l2j.gameserver.templates.CreatureTemplate;
import org.l2j.gameserver.templates.InstantZone;
import org.l2j.gameserver.templates.OptionDataTemplate;
import org.l2j.gameserver.templates.item.ArmorTemplate.ArmorType;
import org.l2j.gameserver.templates.item.ItemTemplate;
import org.l2j.gameserver.templates.item.ItemType;
import org.l2j.gameserver.templates.item.RecipeTemplate;
import org.l2j.gameserver.templates.item.WeaponTemplate;
import org.l2j.gameserver.templates.item.WeaponTemplate.WeaponType;
import org.l2j.gameserver.templates.item.data.ItemData;
import org.l2j.gameserver.templates.npc.NpcTemplate;
import org.l2j.gameserver.templates.pet.PetData;
import org.l2j.gameserver.templates.player.PlayerTemplate;
import org.l2j.gameserver.templates.player.transform.TransformTemplate;
import org.l2j.gameserver.templates.premiumaccount.PremiumAccountTemplate;
import org.l2j.gameserver.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.l2j.commons.configuration.Configurator.getSettings;
import static org.l2j.commons.database.DatabaseAccess.getDAO;
import static org.l2j.gameserver.network.l2.s2c.ExSetCompassZoneCode.*;

public final class Player extends Playable implements PlayerGroup
{
    public static final int DEFAULT_NAME_COLOR = 0xFFFFFF;
    public static final int DEFAULT_TITLE_COLOR = 0xFFFF77;
    public static final int MAX_POST_FRIEND_SIZE = 100;

    private static final Logger _log = LoggerFactory.getLogger(Player.class);

    public static final String NO_TRADERS_VAR = "notraders";
    public static final String NO_ANIMATION_OF_CAST_VAR = "notShowBuffAnim";
    public static final String MY_BIRTHDAY_RECEIVE_YEAR = "MyBirthdayReceiveYear";
    private static final String NOT_CONNECTED = "<not connected>";
    private static final String RECENT_PRODUCT_LIST_VAR = "recentProductList";
    private static final String LVL_UP_REWARD_VAR = "@lvl_up_reward";
    private static final String ACADEMY_GRADUATED_VAR = "@academy_graduated";
    private static final String JAILED_VAR = "jailed";
    private static final String PA_ITEMS_RECIEVED = "pa_items_recieved";
    private static final String FREE_PA_RECIEVED = "free_pa_recieved";
    private static final String ACTIVE_SHOT_ID_VAR = "@active_shot_id";
    private static final String PC_BANG_POINTS_VAR = "pc_bang_poins";
    private static final String PK_KILL_VAR = "@pk_kill";

    public final static int OBSERVER_NONE = 0;
    public final static int OBSERVER_STARTING = 1;
    public final static int OBSERVER_STARTED = 3;
    public final static int OBSERVER_LEAVING = 2;

    public static final int STORE_PRIVATE_NONE = 0;
    public static final int STORE_PRIVATE_SELL = 1;
    public static final int STORE_PRIVATE_BUY = 3;
    public static final int STORE_PRIVATE_MANUFACTURE = 5;
    public static final int STORE_OBSERVING_GAMES = 7;
    public static final int STORE_PRIVATE_SELL_PACKAGE = 8;

    public static final int[] EXPERTISE_LEVELS = { 0, 20, 40, 52, 61, 76, 80, 84, 85, 95, 99, Integer.MAX_VALUE };

    private PlayerTemplate _baseTemplate;

    private GameClient _connection;
    private String _login;

    private int _karma, _pkKills, _pvpKills;
    private int _face, _hairStyle, _hairColor;
    private int _beautyFace, _beautyHairStyle, _beautyHairColor;
    private int _recomHave, _recomLeftToday, _fame, _raidPoints;
    private int _recomLeft = 0;
    private int _deleteTimer;
    private boolean _isVoting = false;

    private long _createTime, _onlineTime, _onlineBeginTime, _leaveClanTime, _deleteClanTime, _NoChannel, _NoChannelBegin;
    private long _uptime;
    /**
     * Time on login in game
     */
    private long _lastAccess;

    /**
     * The Color of players name / title (white is 0xFFFFFF)
     */
    private int _nameColor = DEFAULT_NAME_COLOR, _titlecolor = DEFAULT_TITLE_COLOR;

    private boolean _overloaded;

    boolean sittingTaskLaunched;

    /**
     * Time counter when L2Player is sitting
     */
    private int _waitTimeWhenSit;

    private boolean _autoLoot = Config.AUTO_LOOT, AutoLootHerbs = Config.AUTO_LOOT_HERBS, _autoLootOnlyAdena = Config.AUTO_LOOT_ONLY_ADENA;

    private final PcInventory _inventory = new PcInventory(this);
    private final Warehouse _warehouse = new PcWarehouse(this);
    private final ItemContainer _refund = new PcRefund(this);
    private final PcFreight _freight = new PcFreight(this);

    private final BookMarkList _bookmarks = new BookMarkList(this, 0);

    public Location bookmarkLocation = null;

    private final AntiFlood _antiFlood = new AntiFlood(this);

    private final Map<Integer, RecipeTemplate> _recipebook = new TreeMap<Integer, RecipeTemplate>();
    private final Map<Integer, RecipeTemplate> _commonrecipebook = new TreeMap<Integer, RecipeTemplate>();

    /**
     * The table containing all Quests began by the L2Player
     */
    private final IntObjectMap<QuestState> _quests = new HashIntObjectMap<QuestState>();

    /**
     * The list containing all shortCuts of this L2Player
     */
    private final ShortCutList _shortCuts = new ShortCutList(this);

    /**
     * The list containing all macroses of this L2Player
     */
    private final MacroList _macroses = new MacroList(this);

    /**
     * The list containing all subclasses of this L2Player
     */
    private final SubClassList _subClassList = new SubClassList(this);

    /**
     * The Private Store type of the L2Player (STORE_PRIVATE_NONE=0, STORE_PRIVATE_SELL=1, sellmanage=2, STORE_PRIVATE_BUY=3, buymanage=4, STORE_PRIVATE_MANUFACTURE=5)
     */
    private int _privatestore;
    /**
     * Данные для магазина рецептов
     */
    private String _manufactureName;
    private List<ManufactureItem> _createList = Collections.emptyList();
    /**
     * Данные для магазина продажи
     */
    private String _sellStoreName;
    private String _packageSellStoreName;
    private List<TradeItem> _sellList = Collections.emptyList();
    private List<TradeItem> _packageSellList = Collections.emptyList();
    /**
     * Данные для магазина покупки
     */
    private String _buyStoreName;
    private List<TradeItem> _buyList = Collections.emptyList();
    /**
     * Данные для обмена
     */
    private List<TradeItem> _tradeList = Collections.emptyList();

    private Party _party;
    private Location _lastPartyPosition;

    private Clan _clan;
    private PledgeRank _pledgeRank = PledgeRank.VAGABOND;
    private int _pledgeType = Clan.SUBUNIT_NONE, _powerGrade = 0, _lvlJoinedAcademy = 0, _apprentice = 0;

    /**
     * GM Stuff
     */
    private int _accessLevel;
    private PlayerAccess _playerAccess = new PlayerAccess();

    private boolean _messageRefusal = false, _tradeRefusal = false, _blockAll = false;

    /**
     * The L2Summon of the L2Player
     */
    private SummonInstance _summon = null; // objId is index
    private PetInstance _pet = null;
    private SymbolInstance _symbol = null;

    private boolean _riding;

    private int _botRating;

    private List<DecoyInstance> _decoys = new CopyOnWriteArrayList<DecoyInstance>();

    private IntObjectMap<Cubic> _cubics = null;
    private int _agathionId = 0;

    private Request _request;

    private ItemInstance _arrowItem;

    /**
     * The fists L2Weapon of the L2Player (used when no weapon is equipped)
     */
    private WeaponTemplate _fistsWeaponItem;

    private Map<Integer, String> _chars = new HashMap<Integer, String>(8);

    private ItemInstance _enchantScroll = null;

    private WarehouseType _usingWHType;

    private boolean _isOnline = false;

    private final AtomicBoolean _isLogout = new AtomicBoolean();

    /**
     * The L2NpcInstance corresponding to the last Folk which one the player talked.
     */
    private HardReference<NpcInstance> _lastNpc = HardReferences.emptyRef();
    /**
     * тут храним мультиселл с которым работаем
     */
    private MultiSellListContainer _multisell = null;

    private IntObjectMap<SoulShotType> _activeAutoShots = new CHashIntObjectMap<SoulShotType>();

    private ObservePoint _observePoint;
    private AtomicInteger _observerMode = new AtomicInteger(0);

    public int _telemode = 0;

    public boolean entering = true;

    /**
     * Эта точка проверяется при нештатном выходе чара, и если не равна null чар возвращается в нее
     * Используется например для возвращения при падении с виверны
     * Поле heading используется для хранения денег возвращаемых при сбое
     */
    private Location _stablePoint = null;

    /**
     * new loto ticket *
     */
    public int _loto[] = new int[5];
    /**
     * new race ticket *
     */
    public int _race[] = new int[2];

    private final BlockList _blockList = new BlockList(this);
    private final FriendList _friendList = new FriendList(this);
    private final PremiumItemList _premiumItemList = new PremiumItemList(this);
    private final ProductHistoryList _productHistoryList = new ProductHistoryList(this);
    private final HennaList _hennaList = new HennaList(this);

    private final AttendanceRewards _attendanceRewards = new AttendanceRewards(this);
    private final DailyMissionList _dailiyMissionList = new DailyMissionList(this);

    private boolean _hero = false;

    private PremiumAccountTemplate _premiumAccount = PremiumAccountHolder.getInstance().getPremiumAccount(0);
    private Future<?> _premiumAccountExpirationTask;

    private boolean _isSitting;

    private ChairInstance _chairObject;

    private boolean _inOlympiadMode;
    private OlympiadGame _olympiadGame;
    private ObservableArena _observableArena;

    private int _olympiadSide = -1;

    /**
     * ally with ketra or varka related wars
     */
    private int _varka = 0;
    private int _ketra = 0;
    private int _ram = 0;

    private byte[] _keyBindings = Util.BYTE_ARRAY_EMPTY;

    private final Fishing _fishing = new Fishing(this);

    private Future<?> _taskWater;
    private Future<?> _autoSaveTask;

    private Future<?> _pcCafePointsTask;
    private Future<?> _unjailTask;
    private Future<?> _trainingCampTask;

    private final Lock _storeLock = new ReentrantLock();

    private int _zoneMask;

    private boolean _registeredInEvent = false;

    private int _pcBangPoints;

    private int _expandInventory = 0;
    private int _expandWarehouse = 0;
    private int _battlefieldChatId;
    private int _lectureMark;

    private AtomicState _gmInvisible = new AtomicState();
    private AtomicState _gmUndying = new AtomicState();

    private IntObjectMap<String> _postFriends = Containers.emptyIntObjectMap();

    private List<String> _blockedActions = new ArrayList<String>();

    private BypassStorage _bypassStorage = new BypassStorage();

    private boolean _notShowBuffAnim = false;
    private boolean _notShowTraders = false;
    private boolean _canSeeAllShouts = false;
    private boolean _debug = false;

    private long _dropDisabled;
    private long _lastItemAuctionInfoRequest;

    private IntObjectPair<OnAnswerListener> _askDialog = null;

    private boolean _matchingRoomWindowOpened = false;
    private MatchingRoom _matchingRoom;
    private PetitionMainGroup _petitionGroup;
    private final Map<Integer, Long> _instancesReuses = new ConcurrentHashMap<Integer, Long>();

    private Language _language = Language.ENGLISH;

    private int _npcDialogEndTime = 0;

    private Mount _mount = null;

    private final Map<String, CharacterVariable> _variables = new ConcurrentHashMap<String, CharacterVariable>();

    private List<RestoredSummon> _restoredSummons = null;

    private boolean _autoSearchParty;
    private Future<?> _substituteTask;

    private TransformTemplate _transform = null;

    private final IntObjectMap<SkillEntry> _transformSkills = new CHashIntObjectMap<SkillEntry>();

    private long _lastMultisellBuyTime = 0L;
    private long _lastEnchantItemTime = 0L;
    private long _lastAttributeItemTime = 0L;

    private Future<?> _enableRelationTask;

    private boolean _isInReplaceTeleport = false;

    private int _armorSetEnchant = 0;

    private int _usedWorldChatPoints = 0;

    private boolean _hideHeadAccessories = false;

    private ItemInstance _synthesisItem1 = null;
    private ItemInstance _synthesisItem2 = null;

    private List<TrapInstance> _traps = Collections.emptyList();
    private boolean _isInJail = false;
    private final IntObjectMap<OptionDataTemplate> _options = new CTreeIntObjectMap<OptionDataTemplate>();
    private long _receivedExp = 0L;
    private Reflection _activeReflection = null;
    private int _questZoneId = -1;
    private ClassId _selectedMultiClassId = null;

    /**
     * Конструктор для L2Player. Напрямую не вызывается, для создания игрока используется PlayerManager.create
     */
    public Player(final int objectId, final PlayerTemplate template, final String accountName)
    {
        super(objectId, template);

        _baseTemplate = template;
        _login = accountName;
    }

    /**
     * Constructor<?> of L2Player (use L2Character constructor).<BR><BR>
     * <p/>
     * <B><U> Actions</U> :</B><BR><BR>
     * <li>Call the L2Character constructor to create an empty _skills slot and copy basic Calculator set to this L2Player </li>
     * <li>Create a L2Radar object</li>
     * <li>Retrieve from the database all items of this L2Player and add them to _inventory </li>
     * <p/>
     * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method DOESN'T SET the account name of the L2Player</B></FONT><BR><BR>
     *  @param objectId Identifier of the object to initialized
     * @param template The L2PlayerTemplate to apply to the L2Player
     */
    private Player(final int objectId, final PlayerTemplate template)
    {
        this(objectId, template, null);

        if(GameObjectsStorage.getPlayers().size() >= GameServer.getInstance().getOnlineLimit())
        {
            kick();
            return;
        }

        _baseTemplate = template;

        _ai = new PlayerAI(this);

        if(!getSettings(ServerSettings.class).isEveryBodyIsAdmin())
            setPlayerAccess(Config.gmlist.get(objectId));
        else
            setPlayerAccess(Config.gmlist.get(0));
    }

    @SuppressWarnings("unchecked")
    @Override
    public HardReference<Player> getRef()
    {
        return (HardReference<Player>) super.getRef();
    }

    public String getAccountName()
    {
        if(_connection == null)
            return _login;
        return _connection.getLogin();
    }

    public String getIP()
    {
        if(_connection == null)
            return NOT_CONNECTED;
        return _connection.getIpAddr();
    }

    public String getLogin()
    {
        return _login;
    }

    public void setLogin(String val)
    {
        _login = val;
    }

    /**
     * Возвращает список персонажей на аккаунте, за исключением текущего
     *
     * @return Список персонажей
     */
    public Map<Integer, String> getAccountChars()
    {
        return _chars;
    }

    @Override
    public final PlayerTemplate getTemplate()
    {
        return (PlayerTemplate) super.getTemplate();
    }

    @Override
    public final void setTemplate(CreatureTemplate template)
    {
        if(isBaseClassActive())
            _baseTemplate = (PlayerTemplate) template;

        super.setTemplate(template);
    }

    public final PlayerTemplate getBaseTemplate()
    {
        return _baseTemplate;
    }

    @Override
    public final boolean isTransformed()
    {
        return _transform != null;
    }

    @Override
    public final TransformTemplate getTransform()
    {
        return _transform;
    }

    @Override
    public final void setTransform(int id)
    {
        TransformTemplate template = id > 0 ? TransformTemplateHolder.getInstance().getTemplate(getSex(), id) : null;
        setTransform(template);
    }

    @Override
    public final void setTransform(TransformTemplate transform)
    {
        if(transform == _transform || transform != null && _transform != null)
            return;

        boolean isFlying = false;
        final boolean isVisible = isVisible();

        synchronized (_transform)
        {
            // Для каждой трансформации свой набор скилов
            if(transform == null) // Обычная форма
            {
                isFlying = _transform.getType() == TransformType.FLYING;

                if(isFlying)
                {
                    decayMe();
                    setFlying(false);
                    setLoc(getLoc().correctGeoZ());
                }

                if(!_transformSkills.isEmpty())
                {
                    // Удаляем скилы трансформации
                    for(SkillEntry skillEntry : _transformSkills.values())
                    {
                        if(!SkillAcquireHolder.getInstance().isSkillPossible(this, skillEntry.getTemplate()))
                            super.removeSkill(skillEntry);
                    }
                    _transformSkills.clear();
                }

                if(_transform.getItemCheckType() != LockType.NONE)
                    getInventory().unlock();

                _transform = transform;

                checkActiveToggleEffects();

                // Останавливаем текущий эффект трансформации
                getAbnormalList().stop(AbnormalType.transform);
            }
            else
            {
                isFlying = transform.getType() == TransformType.FLYING;

                if(isFlying)
                {
                    for(Servitor servitor : getServitors())
                        servitor.unSummon(false);

                    decayMe();
                    setFlying(true);
                    setLoc(getLoc().changeZ(transform.getSpawnHeight())); // Немного поднимаем чара над землей
                }

                // Добавляем скиллы трансформации
                for(SkillLearn sl : transform.getSkills())
                {
                    SkillEntry skillEntry = SkillHolder.getInstance().getSkillEntry(sl.getId(), sl.getLevel());
                    if(skillEntry == null)
                        continue;

                    _transformSkills.put(skillEntry.getId(), skillEntry);
                }

                // Добавляем скиллы трансформации зависящие от уровня персонажа
                for(SkillLearn sl : transform.getAddtionalSkills())
                {
                    if(sl.getMinLevel() > getLevel())
                        continue;

                    SkillEntry skillEntry = _transformSkills.get(sl.getId());
                    if(skillEntry != null && skillEntry.getLevel() >= sl.getLevel())
                        continue;

                    skillEntry = SkillHolder.getInstance().getSkillEntry(sl.getId(), sl.getLevel());
                    if(skillEntry == null)
                        continue;

                    _transformSkills.put(skillEntry.getId(), skillEntry);
                }

                for(SkillEntry skillEntry : _transformSkills.values())
                    addSkill(skillEntry, false);

                if(transform.getItemCheckType() != LockType.NONE)
                {
                    getInventory().unlock();
                    getInventory().lockItems(transform.getItemCheckType(), transform.getItemCheckIDs());
                }

                checkActiveToggleEffects();

                _transform = transform;
            }
        }

        sendPacket(new ExBasicActionList(this));
        sendSkillList();
        sendPacket(new ShortCutInitPacket(this));

        sendActiveAutoShots();

        if(isFlying && isVisible)
            spawnMe();

        sendChanges();
    }

    public void changeSex()
    {
        PlayerTemplate template = PlayerTemplateHolder.getInstance().getPlayerTemplate(getRace(), getClassId(), getSex().revert());
        if(template == null)
            return;

        setTemplate(template);
        if(isTransformed())
        {
            int transformId = getTransform().getId();
            setTransform(null);
            setTransform(transformId);
        }
    }

    @Override
    public PlayerAI getAI()
    {
        return (PlayerAI) _ai;
    }

    @Override
    public void doCast(final SkillEntry skillEntry, final Creature target, boolean forceUse)
    {
        if(skillEntry == null)
            return;

        super.doCast(skillEntry, target, forceUse);
    }

    @Override
    public void sendReuseMessage(Skill skill)
    {
        if(isCastingNow())
            return;

        TimeStamp sts = getSkillReuse(skill);
        if(sts == null || !sts.hasNotPassed())
            return;
        long timeleft = sts.getReuseCurrent();
        if(!Config.ALT_SHOW_REUSE_MSG && timeleft < 10000 || timeleft < 500)
            return;
        long hours = timeleft / 3600000;
        long minutes = (timeleft - hours * 3600000) / 60000;
        long seconds = (long) Math.ceil((timeleft - hours * 3600000 - minutes * 60000) / 1000.);
        if(hours > 0)
            sendPacket(new SystemMessage(SystemMessage.THERE_ARE_S2_HOURS_S3_MINUTES_AND_S4_SECONDS_REMAINING_IN_S1S_REUSE_TIME).addSkillName(skill.getId(), skill.getDisplayLevel()).addNumber(hours).addNumber(minutes).addNumber(seconds));
        else if(minutes > 0)
            sendPacket(new SystemMessage(SystemMessage.THERE_ARE_S2_MINUTES_S3_SECONDS_REMAINING_IN_S1S_REUSE_TIME).addSkillName(skill.getId(), skill.getDisplayLevel()).addNumber(minutes).addNumber(seconds));
        else
            sendPacket(new SystemMessage(SystemMessage.THERE_ARE_S2_SECONDS_REMAINING_IN_S1S_REUSE_TIME).addSkillName(skill.getId(), skill.getDisplayLevel()).addNumber(seconds));
    }

    @Override
    public final int getLevel()
    {
        return getActiveSubClass() == null ? 1 : getActiveSubClass().getLevel();
    }

    @Override
    public final Sex getSex()
    {
        return getTemplate().getSex();
    }

    public int getFace()
    {
        return _face;
    }

    public void setFace(int face)
    {
        _face = face;
    }

    public int getBeautyFace()
    {
        return _beautyFace;
    }

    public void setBeautyFace(int face)
    {
        _beautyFace = face;
    }

    public int getHairColor()
    {
        return _hairColor;
    }

    public void setHairColor(int hairColor)
    {
        _hairColor = hairColor;
    }

    public int getBeautyHairColor()
    {
        return _beautyHairColor;
    }

    public void setBeautyHairColor(int hairColor)
    {
        _beautyHairColor = hairColor;
    }

    public int getHairStyle()
    {
        return _hairStyle;
    }

    public void setHairStyle(int hairStyle)
    {
        _hairStyle = hairStyle;
    }

    public int getBeautyHairStyle()
    {
        return _beautyHairStyle;
    }

    public void setBeautyHairStyle(int hairStyle)
    {
        _beautyHairStyle = hairStyle;
    }

    /**
     * Соединение закрывается, клиент закрывается, персонаж сохраняется и удаляется из игры
     */
    public void kick()
    {
        prepareToLogout1();
        if(_connection != null)
        {
            _connection.close(LogOutOkPacket.STATIC);
            setNetConnection(null);
        }
        prepareToLogout2();
        deleteMe();
    }

    /**
     * Соединение не закрывается, клиент не закрывается, персонаж сохраняется и удаляется из игры
     */
    public void restart()
    {
        prepareToLogout1();
        if(_connection != null)
        {
            _connection.setActiveChar(null);
            setNetConnection(null);
        }
        prepareToLogout2();
        deleteMe();
    }

    /**
     * Соединение закрывается, клиент не закрывается, персонаж сохраняется и удаляется из игры
     */
    public void logout()
    {
        prepareToLogout1();
        if(_connection != null)
        {
            _connection.close(ServerCloseSocketPacket.STATIC);
            setNetConnection(null);
        }
        prepareToLogout2();
        deleteMe();
    }

    private void prepareToLogout1()
    {
        for(Servitor servitor : getServitors())
            sendPacket(new PetDeletePacket(servitor.getObjectId(), servitor.getServitorType()));

        if(isProcessingRequest())
        {
            Request request = getRequest();
            if(isInTrade())
            {
                Player parthner = request.getOtherPlayer(this);
                parthner.sendPacket(SystemMsg.THAT_PLAYER_IS_NOT_ONLINE);
                parthner.sendPacket(TradeDonePacket.FAIL);
            }
            request.cancel();
        }
        World.removeObjectsFromPlayer(this);
    }

    private void prepareToLogout2()
    {
        if(_isLogout.getAndSet(true))
            return;

        for(ListenerHook hook : getListenerHooks(ListenerHookType.PLAYER_QUIT_GAME))
            hook.onPlayerQuitGame(this);

        for(ListenerHook hook : ListenerHook.getGlobalListenerHooks(ListenerHookType.PLAYER_QUIT_GAME))
            hook.onPlayerQuitGame(this);

        FlagItemAttachment attachment = getActiveWeaponFlagAttachment();
        if(attachment != null)
            attachment.onLogout(this);

        setNetConnection(null);
        setIsOnline(false);

        if(isGM()) {
            GmListTable.remove(this);
        }

        getListeners().onExit();

        if(isFlying() && !checkLandingState())
            _stablePoint = TeleportUtils.getRestartPoint(this, RestartType.TO_VILLAGE).getLoc();

        if(isCastingNow())
            abortCast(true, true);

        Party party = getParty();
        if(party != null)
            leaveParty();

        if(_observableArena != null)
            _observableArena.removeObserver(_observePoint);

        Olympiad.logoutPlayer(this);

        if(isFishing())
            getFishing().stop();

        if(_stablePoint != null)
            teleToLocation(_stablePoint);

        for(Servitor servitor : getServitors())
            servitor.unSummon(true);

        if(isMounted())
            _mount.onLogout();

        _friendList.notifyFriends(false);

        if(getClan() != null)
            getClan().loginClanCond(this, false);

        if(isProcessingRequest())
            getRequest().cancel();

        stopAllTimers();

        if(isInBoat())
            getBoat().removePlayer(this);

        SubUnit unit = getSubUnit();
        UnitMember member = unit == null ? null : unit.getUnitMember(getObjectId());
        if(member != null)
        {
            int sponsor = member.getSponsor();
            int apprentice = getApprentice();
            PledgeShowMemberListUpdatePacket memberUpdate = new PledgeShowMemberListUpdatePacket(this);
            for(Player clanMember : _clan.getOnlineMembers(getObjectId()))
            {
                clanMember.sendPacket(memberUpdate);
                if(clanMember.getObjectId() == sponsor)
                    clanMember.sendPacket(new SystemMessage(SystemMessage.S1_YOUR_CLAN_ACADEMYS_APPRENTICE_HAS_LOGGED_OUT).addString(_name));
                else if(clanMember.getObjectId() == apprentice)
                    clanMember.sendPacket(new SystemMessage(SystemMessage.S1_YOUR_CLAN_ACADEMYS_SPONSOR_HAS_LOGGED_OUT).addString(_name));
            }
            member.setPlayerInstance(this, true);
        }

        MatchingRoom room = getMatchingRoom();
        if(room != null)
        {
            if(room.getLeader() == this)
                room.disband();
            else
                room.removeMember(this, false);
        }
        setMatchingRoom(null);

        MatchingRoomManager.getInstance().removeFromWaitingList(this);

        destroyAllTraps();

        if(!_decoys.isEmpty())
        {
            for(DecoyInstance decoy : getDecoys())
            {
                decoy.unSummon();
                removeDecoy(decoy);
            }
        }

        stopPvPFlag();

        Reflection ref = getReflection();

        if(!ref.isMain())
        {
            if(ref.getReturnLoc() != null)
                _stablePoint = ref.getReturnLoc();

            ref.removeObject(this);
        }

        try
        {
            getInventory().store();
            getRefund().clear();
        }
        catch(Throwable t)
        {
            _log.error("", t);
        }

        try
        {
            store(false);
        }
        catch(Throwable t)
        {
            _log.error("", t);
        }
    }

    /**
     * @return a table containing all L2RecipeList of the L2Player.<BR><BR>
     */
    public Collection<RecipeTemplate> getDwarvenRecipeBook()
    {
        return _recipebook.values();
    }

    public Collection<RecipeTemplate> getCommonRecipeBook()
    {
        return _commonrecipebook.values();
    }

    public int recipesCount()
    {
        return _commonrecipebook.size() + _recipebook.size();
    }

    public boolean hasRecipe(final RecipeTemplate id)
    {
        return _recipebook.containsValue(id) || _commonrecipebook.containsValue(id);
    }

    public boolean findRecipe(final int id)
    {
        return _recipebook.containsKey(id) || _commonrecipebook.containsKey(id);
    }

    /**
     * Add a new L2RecipList to the table _recipebook containing all L2RecipeList of the L2Player
     */
    public void registerRecipe(final RecipeTemplate recipe, boolean saveDB)
    {
        if(recipe == null)
            return;

        if(recipe.isCommon())
            _commonrecipebook.put(recipe.getId(), recipe);
        else
            _recipebook.put(recipe.getId(), recipe);

        if(saveDB)
            mysql.set("REPLACE INTO character_recipebook (char_id, id) VALUES(?,?)", getObjectId(), recipe.getId());
    }

    /**
     * Remove a L2RecipList from the table _recipebook containing all L2RecipeList of the L2Player
     */
    public void unregisterRecipe(final int RecipeID)
    {
        if(_recipebook.containsKey(RecipeID))
        {
            mysql.set("DELETE FROM `character_recipebook` WHERE `char_id`=? AND `id`=? LIMIT 1", getObjectId(), RecipeID);
            _recipebook.remove(RecipeID);
        }
        else if(_commonrecipebook.containsKey(RecipeID))
        {
            mysql.set("DELETE FROM `character_recipebook` WHERE `char_id`=? AND `id`=? LIMIT 1", getObjectId(), RecipeID);
            _commonrecipebook.remove(RecipeID);
        }
        else
            _log.warn("Attempted to remove unknown RecipeList" + RecipeID);
    }

    public QuestState getQuestState(int id)
    {
        questRead.lock();
        try
        {
            return _quests.get(id);
        }
        finally
        {
            questRead.unlock();
        }
    }

    public QuestState getQuestState(Quest quest)
    {
        return getQuestState(quest.getId());
    }

    public boolean isQuestCompleted(int id)
    {
        QuestState qs = getQuestState(id);
        return qs != null && qs.isCompleted();
    }

    public boolean isQuestCompleted(Quest quest)
    {
        return isQuestCompleted(quest.getId());
    }

    public void setQuestState(QuestState qs)
    {
        questWrite.lock();
        try
        {
            _quests.put(qs.getQuest().getId(), qs);
        }
        finally
        {
            questWrite.unlock();
        }
    }

    public void removeQuestState(int id)
    {
        questWrite.lock();
        try
        {
            _quests.remove(id);
        }
        finally
        {
            questWrite.unlock();
        }
    }

    public void removeQuestState(Quest quest)
    {
        removeQuestState(quest.getId());
    }

    public Quest[] getAllActiveQuests()
    {
        List<Quest> quests = new ArrayList<Quest>(_quests.size());
        questRead.lock();
        try
        {
            for(final QuestState qs : _quests.values())
                if(qs.isStarted())
                    quests.add(qs.getQuest());
        }
        finally
        {
            questRead.unlock();
        }
        return quests.toArray(new Quest[quests.size()]);
    }

    public QuestState[] getAllQuestsStates()
    {
        questRead.lock();
        try
        {
            return _quests.values().toArray(new QuestState[_quests.size()]);
        }
        finally
        {
            questRead.unlock();
        }
    }

    public List<QuestState> getQuestsForEvent(NpcInstance npc, QuestEventType event)
    {
        List<QuestState> states = new ArrayList<QuestState>();
        Set<Quest> quests = npc.getTemplate().getEventQuests(event);
        if(quests != null)
        {
            QuestState qs;
            for(Quest quest : quests)
            {
                qs = getQuestState(quest);
                if(qs != null && !qs.isCompleted())
                    states.add(getQuestState(quest));
            }
        }
        return states;
    }

    public void processQuestEvent(int questId, String event, NpcInstance npc)
    {
        if(event == null)
            event = "";
        QuestState qs = getQuestState(questId);
        if(qs == null)
        {
            Quest q = QuestHolder.getInstance().getQuest(questId);
            if(q == null)
            {
                _log.warn("Quest ID[" + questId + "] not found!");
                return;
            }
            qs = q.newQuestState(this);
        }
        if(qs == null || qs.isCompleted())
            return;
        qs.getQuest().notifyEvent(event, qs, npc);
        sendPacket(new QuestListPacket(this));
    }

    public boolean isInventoryFull()
    {
        if(getWeightPenalty() >= 3 || getInventoryLimit() * 0.8 < getInventory().getSize())
            return true;

        return false;
    }

    /**
     * Проверка на переполнение инвентаря и перебор в весе для квестов и эвентов
     *
     * @return true если ве проверки прошли успешно
     */
    public boolean isQuestContinuationPossible(boolean msg)
    {
        if(isInventoryFull() || Config.QUEST_INVENTORY_MAXIMUM * 0.8 < getInventory().getQuestSize())
        {
            if(msg)
                sendPacket(SystemMsg.PROGRESS_IN_A_QUEST_IS_POSSIBLE_ONLY_WHEN_YOUR_INVENTORYS_WEIGHT_AND_SLOT_COUNT_ARE_LESS_THAN_80_PERCENT_OF_CAPACITY);
            return false;
        }
        return true;
    }

    /**
     * Останавливаем и запоминаем все квестовые таймеры
     */
    public void stopQuestTimers()
    {
        for(QuestState qs : getAllQuestsStates())
            if(qs.isStarted())
                qs.pauseQuestTimers();
            else
                qs.stopQuestTimers();
    }

    /**
     * Восстанавливаем все квестовые таймеры
     */
    public void resumeQuestTimers()
    {
        for(QuestState qs : getAllQuestsStates())
            qs.resumeQuestTimers();
    }

    // ----------------- End of Quest Engine -------------------

    public Collection<ShortCut> getAllShortCuts()
    {
        return _shortCuts.getAllShortCuts();
    }

    public ShortCut getShortCut(int slot, int page)
    {
        return _shortCuts.getShortCut(slot, page);
    }

    public void registerShortCut(ShortCut shortcut)
    {
        _shortCuts.registerShortCut(shortcut);
    }

    public void deleteShortCut(int slot, int page)
    {
        _shortCuts.deleteShortCut(slot, page);
    }

    public void registerMacro(Macro macro)
    {
        _macroses.registerMacro(macro);
    }

    public void deleteMacro(int id)
    {
        _macroses.deleteMacro(id);
    }

    public MacroList getMacroses()
    {
        return _macroses;
    }

    public boolean isCastleLord(int castleId)
    {
        return _clan != null && isClanLeader() && _clan.getCastle() == castleId;
    }

    public int getPkKills()
    {
        return _pkKills;
    }

    public void setPkKills(final int pkKills)
    {
        _pkKills = pkKills;
    }

    public long getCreateTime()
    {
        return _createTime;
    }

    public void setCreateTime(final long createTime)
    {
        _createTime = createTime;
    }

    public int getDeleteTimer()
    {
        return _deleteTimer;
    }

    public void setDeleteTimer(final int deleteTimer)
    {
        _deleteTimer = deleteTimer;
    }

    @Override
    public int getCurrentLoad()
    {
        return getInventory().getTotalWeight();
    }

    public long getLastAccess()
    {
        return _lastAccess;
    }

    public void setLastAccess(long value)
    {
        _lastAccess = value;
    }

    public int getRecomHave()
    {
        return _recomHave;
    }

    public void setRecomHave(int value)
    {
        if(value > 255)
            _recomHave = 255;
        else if(value < 0)
            _recomHave = 0;
        else
            _recomHave = value;
    }

    public int getRecomLeft()
    {
        return _recomLeft;
    }

    public void setRecomLeft(final int value)
    {
        _recomLeft = value;
    }

    public void giveRecom(final Player target)
    {
        int targetRecom = target.getRecomHave();
        if(targetRecom < 255)
            target.addRecomHave(1);
        if(getRecomLeft() > 0)
            setRecomLeft(getRecomLeft() - 1);

        sendUserInfo(true);
    }

    public void addRecomHave(final int val)
    {
        setRecomHave(getRecomHave() + val);
        broadcastUserInfo(true);
    }

    @Override
    public int getKarma()
    {
        return _karma;
    }

    public void setKarma(int karma)
    {
        if(_karma == karma)
            return;

        _karma = Math.min(0, karma);

        sendChanges();

        for(Servitor servitor : getServitors())
            servitor.broadcastCharInfo();
    }

    @Override
    public int getMaxLoad()
    {
        return (int) calcStat(Stats.MAX_LOAD, 69000, this, null);
    }

    @Override
    public void updateAbnormalIcons()
    {
        if(entering || isLogoutStarted())
            return;

        super.updateAbnormalIcons();
    }

    @Override
    public void updateAbnormalIconsImpl()
    {
        Abnormal[] effects = getAbnormalList().toArray();
        Arrays.sort(effects, AbnormalsComparator.getInstance());

        PartySpelledPacket ps = new PartySpelledPacket(this, false);
        AbnormalStatusUpdatePacket abnormalStatus = new AbnormalStatusUpdatePacket();

        for(Abnormal effect : effects)
        {
            if(effect == null)
                continue;
            if(effect.checkAbnormalType(AbnormalType.hp_recover))
                sendPacket(new ShortBuffStatusUpdatePacket(effect));
            else
                effect.addIcon(abnormalStatus);
            if(_party != null)
                effect.addPartySpelledIcon(ps);
        }

        sendPacket(abnormalStatus);
        if(_party != null)
            _party.broadCast(ps);

        if(isInOlympiadMode() && isOlympiadCompStart())
        {
            OlympiadGame olymp_game = _olympiadGame;
            if(olymp_game != null)
            {
                ExOlympiadSpelledInfoPacket olympiadSpelledInfo = new ExOlympiadSpelledInfoPacket();

                for(Abnormal effect : effects)
                    if(effect != null)
                        effect.addOlympiadSpelledIcon(this, olympiadSpelledInfo);

                sendPacket(olympiadSpelledInfo);

                for(ObservePoint observer : olymp_game.getObservers())
                    observer.sendPacket(olympiadSpelledInfo);
            }
        }

        final List<SingleMatchEvent> events = getEvents(SingleMatchEvent.class);
        for(SingleMatchEvent event : events)
            event.onEffectIconsUpdate(this, effects);

        super.updateAbnormalIconsImpl();
    }

    @Override
    public int getWeightPenalty()
    {
        return getSkillLevel(4270, 0);
    }

    public void refreshOverloaded()
    {
        if(isLogoutStarted() || getMaxLoad() <= 0)
            return;

        setOverloaded(getCurrentLoad() > getMaxLoad());
        double weightproc = 100. * (getCurrentLoad() - calcStat(Stats.MAX_NO_PENALTY_LOAD, 0, this, null)) / getMaxLoad();
        int newWeightPenalty = 0;

        if(weightproc < 50)
            newWeightPenalty = 0;
        else if(weightproc < 66.6)
            newWeightPenalty = 1;
        else if(weightproc < 80)
            newWeightPenalty = 2;
        else if(weightproc < 100)
            newWeightPenalty = 3;
        else
            newWeightPenalty = 4;

        int current = getWeightPenalty();
        if(current == newWeightPenalty)
            return;

        if(newWeightPenalty > 0)
            addSkill(SkillHolder.getInstance().getSkillEntry(4270, newWeightPenalty));
        else
            super.removeSkill(getKnownSkill(4270));

        sendSkillList();
        sendEtcStatusUpdate();
        updateStats();
    }

    public int getArmorsExpertisePenalty()
    {
        return getSkillLevel(6213, 0);
    }

    public int getWeaponsExpertisePenalty()
    {
        return getSkillLevel(6209, 0);
    }

    public int getExpertisePenalty(ItemInstance item)
    {
        if(item.getTemplate().getType2() == ItemTemplate.TYPE2_WEAPON)
            return getWeaponsExpertisePenalty();
        else if(item.getTemplate().getType2() == ItemTemplate.TYPE2_SHIELD_ARMOR || item.getTemplate().getType2() == ItemTemplate.TYPE2_ACCESSORY)
            return getArmorsExpertisePenalty();
        return 0;
    }

    public void refreshExpertisePenalty()
    {
        if(isLogoutStarted())
            return;

        // Calculate the current higher Expertise of the L2Player
        int level = (int) calcStat(Stats.GRADE_EXPERTISE_LEVEL, getLevel(), null, null);
        int skillLvl = 0;
        for(skillLvl = 0; skillLvl < EXPERTISE_LEVELS.length; skillLvl++)
            if(level < EXPERTISE_LEVELS[skillLvl + 1])
                break;

        skillLvl = Math.max(skillLvl, (int) calcStat(Stats.ADDITIONAL_EXPERTISE_INDEX));
        if(skillLvl == 7)
            skillLvl--;

        boolean skillUpdate = false; // Для того, чтобы лишний раз не посылать пакеты

        if(skillLvl > 0)
        {
            while(skillLvl >= 1)
            {
                SkillEntry skill = SkillHolder.getInstance().getSkillEntry(239, skillLvl);
                if(skill != null)
                {
                    if(addSkill(skill, false) != skill)
                        skillUpdate = true;
                    break;
                }
                else
                    skillLvl--;
            }
        }

        if(Config.EXPERTISE_PENALTY_ENABLED)
        {
            int expertiseIndex = getExpertiseIndex();
            int newWeaponPenalty = 0;
            int newArmorPenalty = 0;
            ItemInstance[] items = getInventory().getPaperdollItems();
            for(ItemInstance item : items)
                if(item != null)
                {
                    int crystaltype = item.getTemplate().getGrade().ordinal();
                    if(item.getTemplate().getType2() == ItemTemplate.TYPE2_WEAPON)
                    {
                        if(crystaltype > newWeaponPenalty)
                            newWeaponPenalty = crystaltype;
                    }
                    else if(item.getTemplate().getType2() == ItemTemplate.TYPE2_SHIELD_ARMOR || item.getTemplate().getType2() == ItemTemplate.TYPE2_ACCESSORY)
                    {
                        if(crystaltype > expertiseIndex)
                        {
                            if(item.getBodyPart() == ItemTemplate.SLOT_FULL_ARMOR)
                                newArmorPenalty++;
                            newArmorPenalty++;
                        }
                    }
                }

            //Уровень штрафа оружия равен разнице между рангом оружия и допустим рангом для персонажа.
            newWeaponPenalty = newWeaponPenalty - expertiseIndex;
            newWeaponPenalty = Math.max(0, Math.min(4, newWeaponPenalty));

            //Уровень штрафа брони равен количеству одетой брони на персонажа не по рангу.
            newArmorPenalty = Math.max(0, Math.min(4, newArmorPenalty));

            int weaponExpertise = getWeaponsExpertisePenalty();
            int armorExpertise = getArmorsExpertisePenalty();

            if(weaponExpertise != newWeaponPenalty)
            {
                weaponExpertise = newWeaponPenalty;
                if(newWeaponPenalty > 0)
                    addSkill(SkillHolder.getInstance().getSkillEntry(6209, weaponExpertise));
                else
                    super.removeSkill(getKnownSkill(6209));
                skillUpdate = true;
            }
            if(armorExpertise != newArmorPenalty)
            {
                armorExpertise = newArmorPenalty;
                if(newArmorPenalty > 0)
                    addSkill(SkillHolder.getInstance().getSkillEntry(6213, armorExpertise));
                else
                    super.removeSkill(getKnownSkill(6213));
                skillUpdate = true;
            }
        }

        if(skillUpdate)
        {
            getInventory().validateItemsSkills();

            sendSkillList();
            sendEtcStatusUpdate();
            updateStats();
        }
    }

    public int getPvpKills()
    {
        return _pvpKills;
    }

    public void setPvpKills(int pvpKills)
    {
        _pvpKills = pvpKills;
    }

    public ClassLevel getClassLevel()
    {
        return getClassId().getClassLevel();
    }

    public boolean isAcademyGraduated()
    {
        return getVarBoolean(ACADEMY_GRADUATED_VAR, false);
    }

    /**
     * Set the template of the L2Player.
     *
     * @param id The Identifier of the L2PlayerTemplate to set to the L2Player
     */
    public synchronized void setClassId(final int id, boolean noban)
    {
        ClassId classId = ClassId.VALUES[id];
        if(classId.isDummy())
            return;
        if(!noban && !(classId.equalsOrChildOf(getClassId()) || getPlayerAccess().CanChangeClass || getSettings(ServerSettings.class).isEveryBodyIsAdmin()))
        {
            Thread.dumpStack();
            return;
        }

        PlayerTemplate template = PlayerTemplateHolder.getInstance().getPlayerTemplate(getRace(), classId, getSex());
        if(template == null)
        {
            _log.error("Missing template for classId: " + id);
            return;
        }
        setTemplate(template);

        //Если новый ID не принадлежит имеющимся классам значит это новая профа
        if(!_subClassList.containsClassId(id))
        {
            final SubClass cclass = getActiveSubClass();
            final ClassId oldClass = ClassId.VALUES[cclass.getClassId()];

            _subClassList.changeSubClassId(oldClass.getId(), id);
            changeClassInDb(oldClass.getId(), id);

            onReceiveNewClassId(oldClass, classId);

            storeCharSubClasses();

            getListeners().onClassChange(oldClass, classId);

            for(QuestState qs : getAllQuestsStates())
                qs.getQuest().notifyTutorialEvent("CE", false, "100", qs);
        }
        else
            getListeners().onClassChange(null, classId);

        broadcastUserInfo(true);

        // Update class icon in party and clan
        if(isInParty())
            getParty().broadCast(new PartySmallWindowUpdatePacket(this));
        if(getClan() != null)
            getClan().broadcastToOnlineMembers(new PledgeShowMemberListUpdatePacket(this));
        if(_matchingRoom != null)
            _matchingRoom.broadcastPlayerUpdate(this);
    }

    private void onReceiveNewClassId(ClassId oldClass, ClassId newClass)
    {
        if(oldClass != null)
        {
            if(isBaseClassActive())
            {
                OlympiadParticipiantData participant = Olympiad.getParticipantInfo(getObjectId());
                if(participant != null)
                    participant.setClassId(newClass.getId());
            }

            if(!newClass.equalsOrChildOf(oldClass))
            {
                removeAllSkills();
                restoreSkills();
                rewardSkills(false);

                checkSkills();

                refreshExpertisePenalty();

                getInventory().refreshEquip();
                getInventory().validateItems();

                getHennaList().refreshStats(true);

                sendSkillList();

                updateStats();
            }
            else
                rewardSkills(true);
        }
    }

    public long getExp()
    {
        return getActiveSubClass() == null ? 0 : getActiveSubClass().getExp();
    }

    public long getMaxExp()
    {
        return getActiveSubClass() == null ? Experience.getExpForLevel(Experience.getMaxLevel() + 1) : getActiveSubClass().getMaxExp();
    }

    public void setEnchantScroll(final ItemInstance scroll)
    {
        _enchantScroll = scroll;
    }

    public ItemInstance getEnchantScroll()
    {
        return _enchantScroll;
    }

    public void addExpAndCheckBonus(MonsterInstance mob, final double noRateExp, double noRateSp)
    {
        if(getActiveSubClass() == null)
            return;

        // Начисление душ камаэлям
        double neededExp = calcStat(Stats.SOULS_CONSUME_EXP, 0, mob, null);
        if(neededExp > 0 && noRateExp > neededExp)
        {
            mob.broadcastPacket(new ExSpawnEmitterPacket(mob, this));
            ThreadPoolManager.getInstance().schedule(new GameObjectTasks.SoulConsumeTask(this), 1000);
        }

        var serverSettings = getSettings(ServerSettings.class);
        if(noRateExp > 0)
        {
            if(!(getVarBoolean("NoExp") && getExp() == Experience.getExpForLevel(getLevel() + 1) - 1))
            {
                Clan clan = getClan();
                if(clan != null)
                {
                    int huntingPoints = Math.max((int)(noRateExp * (getRateExp() / serverSettings.rateXP()) / Math.pow(getLevel(), 2.0) * Config.CLAN_HUNTING_PROGRESS_RATE), 1);
                    clan.addHuntingProgress(huntingPoints);
                }
            }
        }

        long normalExp = (long) (noRateExp * getRateExp() * (mob.isRaid() ? serverSettings.rateXpRaidbossModifier() : 1.0));
        long normalSp = (long) (noRateSp * getRateSp());


        long expWithoutBonus = (long) (noRateExp * serverSettings.rateXP());
        long spWithoutBonus = (long) (noRateSp * serverSettings.rateSP());

        addExpAndSp(normalExp, normalSp, normalExp - expWithoutBonus, normalSp - spWithoutBonus, false, true, false, true, true);
    }

    @Override
    public void addExpAndSp(long exp, long sp)
    {
        addExpAndSp(exp, sp, -1, -1, false, false, Config.ALT_DELEVEL_ON_DEATH_PENALTY_MIN_LEVEL > -1 && getLevel() >= Config.ALT_DELEVEL_ON_DEATH_PENALTY_MIN_LEVEL, true, true);
    }

    public void addExpAndSp(long exp, long sp, boolean delevel)
    {
        addExpAndSp(exp, sp, -1, -1, false, false, delevel, true, true);
    }

    public void addExpAndSp(long addToExp, long addToSp, long bonusAddExp, long bonusAddSp, boolean applyRate, boolean applyToPet, boolean delevel, boolean clearKarma, boolean sendMsg)
    {
        if(getActiveSubClass() == null)
            return;

        if(addToExp < 0 && isFakePlayer())
            return;

        if(applyRate)
        {
            addToExp *= getRateExp();
            addToSp *= getRateSp();
        }

        PetInstance pet = getPet();
        if(addToExp > 0)
        {
            if(applyToPet)
            {
                if(pet != null && !pet.isDead() && !pet.getData().isOfType(PetType.SPECIAL))
                {
                    // Sin Eater забирает всю экспу у персонажа
                    if(pet.getData().isOfType(PetType.KARMA))
                    {
                        pet.addExpAndSp(addToExp, 0);
                        addToExp = 0;
                    }
                    else if(pet.getExpPenalty() > 0f)
                    {
                        if(pet.getLevel() > getLevel() - 20 && pet.getLevel() < getLevel() + 5)
                        {
                            pet.addExpAndSp((long) (addToExp * pet.getExpPenalty()), 0);
                            addToExp *= 1. - pet.getExpPenalty();
                        }
                        else
                        {
                            pet.addExpAndSp((long) (addToExp * pet.getExpPenalty() / 5.), 0);
                            addToExp *= 1. - pet.getExpPenalty() / 5.;
                        }
                    }
                    else if(pet.isSummon())
                        addToExp *= 1. - pet.getExpPenalty();
                }
            }

            // Remove Karma when the player kills L2MonsterInstance
            //TODO [G1ta0] двинуть в метод начисления наград при убйистве моба
            if(clearKarma && isPK() && !isInZoneBattle())
            {
                int karmaLost = Formulas.calculateKarmaLost(this, addToExp);
                if(karmaLost > 0)
                {
                    _karma += karmaLost;
                    if(_karma > 0)
                        _karma = 0;

                    if(sendMsg)
                        sendPacket(new SystemMessagePacket(SystemMsg.YOUR_FAME_HAS_BEEN_CHANGED_TO_S1).addInteger(_karma));
                }
            }

            long max_xp = getVarBoolean("NoExp") || isInDuel() ? Experience.getExpForLevel(getLevel() + 1) - 1 : getMaxExp();
            addToExp = Math.min(addToExp, max_xp - getExp());
        }

        int oldLvl = getActiveSubClass().getLevel();

        getActiveSubClass().addExp(addToExp, delevel);
        getActiveSubClass().addSp(addToSp);

        if(addToExp > 0)
            _receivedExp += addToExp;

        if(sendMsg)
        {
            if((addToExp > 0 || addToSp > 0) && bonusAddExp >= 0 && bonusAddSp >= 0)
                sendPacket(new SystemMessagePacket(SystemMsg.YOU_HAVE_ACQUIRED_S1_EXP_BONUS_S2_AND_S3_SP_BONUS_S4).addLong(addToExp).addLong(bonusAddExp).addInteger(addToSp).addInteger((int) bonusAddSp));
            else if(addToSp > 0 && addToExp == 0)
                sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_ACQUIRED_S1_SP).addNumber(addToSp));
            else if(addToSp > 0 && addToExp > 0)
                sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_EARNED_S1_EXPERIENCE_AND_S2_SP).addNumber(addToExp).addNumber(addToSp));
            else if(addToSp == 0 && addToExp > 0)
                sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_EARNED_S1_EXPERIENCE).addNumber(addToExp));
        }

        int level = getActiveSubClass().getLevel();
        if(level != oldLvl)
        {
            levelSet(level - oldLvl);
            getListeners().onLevelChange(oldLvl, level);

            for(ListenerHook hook : ListenerHook.getGlobalListenerHooks(ListenerHookType.PLAYER_GLOBAL_LEVEL_UP))
                hook.onPlayerGlobalLevelUp(this, oldLvl, level);
        }

        if(pet != null && pet.getData().isOfType(PetType.SPECIAL))
        {
            pet.setLevel(getLevel());
            pet.setExp(pet.getExpForNextLevel());
            pet.broadcastStatusUpdate();
        }

        updateStats();
    }

    private boolean _dontRewardSkills = false; // Глупая заглушка, но спасает.

    public void rewardSkills(boolean send)
    {
        rewardSkills(send, true, Config.AUTO_LEARN_SKILLS, true);
    }

    public int rewardSkills(boolean send, boolean checkShortCuts, boolean learnAllSkills, boolean checkRequiredItems)
    {
        if(_dontRewardSkills)
            return 0;

        List<SkillLearn> skillLearns = new ArrayList<SkillLearn>(SkillAcquireHolder.getInstance().getAvailableNextLevelsSkills(this, AcquireType.NORMAL));
        Collections.sort(skillLearns);
        Collections.reverse(skillLearns);

        IntObjectMap<SkillLearn> skillsToLearnMap = new HashIntObjectMap<SkillLearn>();
        for(SkillLearn sl : skillLearns)
        {
            if(!(sl.isAutoGet() && ((learnAllSkills && (!checkRequiredItems || !sl.haveRequiredItemsForLearn(AcquireType.NORMAL))) || sl.isFreeAutoGet(AcquireType.NORMAL))))
            {
                // Если предыдущий уровень умения учится НЕ БЕСПЛАТНО, то не учим бесплатно больший уровень умения.
                skillsToLearnMap.remove(sl.getId());
                continue;
            }

            if(!skillsToLearnMap.containsKey(sl.getId()))
                skillsToLearnMap.put(sl.getId(), sl);
        }

        boolean update = false;
        int addedSkillsCount = 0;

        for(SkillLearn sl : skillsToLearnMap.values())
        {
            SkillEntry skillEntry = SkillHolder.getInstance().getSkillEntry(sl.getId(), sl.getLevel());
            if(skillEntry == null)
                continue;

            if(addSkill(skillEntry, true) == null)
                addedSkillsCount++;

            if(checkShortCuts && getAllShortCuts().size() > 0 && skillEntry.getLevel() > 1)
                updateSkillShortcuts(skillEntry.getId(), skillEntry.getLevel());

            update = true;
        }

        if(isTransformed())
        {
            boolean added = false;
            // Добавляем скиллы трансформации зависящие от уровня персонажа
            for(SkillLearn sl : _transform.getAddtionalSkills())
            {
                if(sl.getMinLevel() > getLevel())
                    continue;

                SkillEntry skillEntry = _transformSkills.get(sl.getId());
                if(skillEntry != null && skillEntry.getLevel() >= sl.getLevel())
                    continue;

                skillEntry = SkillHolder.getInstance().getSkillEntry(sl.getId(), sl.getLevel());
                if(skillEntry == null)
                    continue;

                _transformSkills.remove(skillEntry.getId());
                _transformSkills.put(skillEntry.getId(), skillEntry);

                update = true;
                added = true;
            }

            if(added)
            {
                for(SkillEntry skillEntry : _transformSkills.values())
                {
                    if(addSkill(skillEntry, false) == null)
                        addedSkillsCount++;
                }
            }
        }

        updateStats();

        if(send && update)
            sendSkillList();

        return addedSkillsCount;
    }

    public Race getRace()
    {
        return ClassId.VALUES[getBaseClassId()].getRace();
    }

    public ClassType getBaseClassType()
    {
        return ClassId.VALUES[getBaseClassId()].getType();
    }

    public long getSp()
    {
        return getActiveSubClass() == null ? 0 : getActiveSubClass().getSp();
    }

    public void setSp(long sp)
    {
        if(getActiveSubClass() != null)
            getActiveSubClass().setSp(sp);
    }

    public int getClanId()
    {
        return _clan == null ? 0 : _clan.getClanId();
    }

    public long getLeaveClanTime()
    {
        return _leaveClanTime;
    }

    public long getDeleteClanTime()
    {
        return _deleteClanTime;
    }

    public void setLeaveClanTime(final long time)
    {
        _leaveClanTime = time;
    }

    public void setDeleteClanTime(final long time)
    {
        _deleteClanTime = time;
    }

    public void setOnlineTime(final long time)
    {
        _onlineTime = time;
        _onlineBeginTime = System.currentTimeMillis();
    }

    public int getOnlineTime()
    {
        return (int) (_onlineBeginTime > 0 ? (_onlineTime + System.currentTimeMillis() - _onlineBeginTime) / 1000L : _onlineTime / 1000L);
    }

    public long getOnlineBeginTime()
    {
        return _onlineBeginTime;
    }

    public void setNoChannel(final long time)
    {
        _NoChannel = time;
        if(_NoChannel > 2145909600000L || _NoChannel < 0)
            _NoChannel = -1;

        if(_NoChannel > 0)
            _NoChannelBegin = System.currentTimeMillis();
        else
            _NoChannelBegin = 0;
    }

    public long getNoChannel()
    {
        return _NoChannel;
    }

    public long getNoChannelRemained()
    {
        if(_NoChannel == 0)
            return 0;
        else if(_NoChannel < 0)
            return -1;
        else
        {
            long remained = _NoChannel - System.currentTimeMillis() + _NoChannelBegin;
            if(remained < 0)
                return 0;

            return remained;
        }
    }

    public boolean isChatBlocked()
    {
        return getFlags().getChatBlocked().get();
    }

    public boolean isEscapeBlocked()
    {
        return getFlags().getEscapeBlocked().get();
    }

    public boolean isPartyBlocked()
    {
        return getFlags().getPartyBlocked().get();
    }

    public boolean isVioletBoy()
    {
        return getFlags().getVioletBoy().get();
    }

    public void setLeaveClanCurTime()
    {
        _leaveClanTime = System.currentTimeMillis();
    }

    public void setDeleteClanCurTime()
    {
        _deleteClanTime = System.currentTimeMillis();
    }

    public boolean canJoinClan()
    {
        if(_leaveClanTime == 0)
            return true;
        if(System.currentTimeMillis() - _leaveClanTime >= Config.ALT_CLAN_LEAVE_PENALTY_TIME * 60 * 60 * 1000L)
        {
            _leaveClanTime = 0;
            return true;
        }
        return false;
    }

    public boolean canCreateClan()
    {
        if(_deleteClanTime == 0)
            return true;
        if(System.currentTimeMillis() - _deleteClanTime >= Config.ALT_CLAN_CREATE_PENALTY_TIME * 60 * 60 * 1000L)
        {
            _deleteClanTime = 0;
            return true;
        }
        return false;
    }

    public IBroadcastPacket canJoinParty(Player inviter)
    {
        Request request = getRequest();
        if(request != null && request.isInProgress() && request.getOtherPlayer(this) != inviter)
            return SystemMsg.WAITING_FOR_ANOTHER_REPLY.packet(inviter); // занят
        if(isBlockAll() || getMessageRefusal()) // всех нафиг
            return SystemMsg.THAT_PERSON_IS_IN_MESSAGE_REFUSAL_MODE.packet(inviter);
        if(isInParty()) // уже
            return new SystemMessagePacket(SystemMsg.C1_IS_A_MEMBER_OF_ANOTHER_PARTY_AND_CANNOT_BE_INVITED).addName(this);
        if(isPartyBlocked())
            return new SystemMessagePacket(SystemMsg.C1_HAS_BEEN_REPORTED_AS_AN_ILLEGAL_PROGRAM_USER_AND_CANNOT_JOIN_A_PARTY).addName(this);
        if(inviter.getReflection() != getReflection()) // в разных инстантах
            if(!inviter.getReflection().isMain() && !getReflection().isMain())
                return SystemMsg.INVALID_TARGET.packet(inviter);
        if(inviter.isInOlympiadMode() || isInOlympiadMode()) // олимпиада
            return SystemMsg.A_USER_CURRENTLY_PARTICIPATING_IN_THE_OLYMPIAD_CANNOT_SEND_PARTY_AND_FRIEND_INVITATIONS.packet(inviter);
        if(!inviter.getPlayerAccess().CanJoinParty || !getPlayerAccess().CanJoinParty) // низя
            return SystemMsg.INVALID_TARGET.packet(inviter);
        return null;
    }

    @Override
    public PcInventory getInventory()
    {
        return _inventory;
    }

    @Override
    public long getWearedMask()
    {
        return _inventory.getWearedMask();
    }

    public PcFreight getFreight()
    {
        return _freight;
    }

    public void removeItemFromShortCut(final int objectId)
    {
        _shortCuts.deleteShortCutByObjectId(objectId);
    }

    public void removeSkillFromShortCut(final int skillId)
    {
        _shortCuts.deleteShortCutBySkillId(skillId);
    }

    @Override
    public boolean isSitting()
    {
        return _isSitting;
    }

    public void setSitting(boolean val)
    {
        _isSitting = val;
    }

    public boolean getSittingTask()
    {
        return sittingTaskLaunched;
    }

    public ChairInstance getChairObject()
    {
        return _chairObject;
    }

    @Override
    public void sitDown(ChairInstance chair)
    {
        if(isSitting() || sittingTaskLaunched || isAlikeDead())
            return;

        if(isStunned() || isSleeping() || isDecontrolled() || isAttackingNow() || isCastingNow() || isMoving)
        {
            getAI().setNextAction(AINextAction.REST, null, null, false, false);
            return;
        }

        resetWaitSitTime();
        getAI().setIntention(CtrlIntention.AI_INTENTION_REST, null, null);

        if(chair == null)
            broadcastPacket(new ChangeWaitTypePacket(this, ChangeWaitTypePacket.WT_SITTING));
        else
        {
            chair.setSeatedPlayer(this);
            broadcastPacket(new ChairSitPacket(this, chair));
        }

        _chairObject = chair;
        setSitting(true);
        sittingTaskLaunched = true;
        ThreadPoolManager.getInstance().schedule(new EndSitDownTask(this), 2500);
    }

    @Override
    public void standUp()
    {
        if(!isSitting() || sittingTaskLaunched || isInStoreMode() || isAlikeDead())
            return;

        //FIXME [G1ta0] эффект сам отключается во время действия, если персонаж не сидит, возможно стоит убрать
        getAbnormalList().stop(EffectType.Relax);

        getAI().clearNextAction();
        broadcastPacket(new ChangeWaitTypePacket(this, ChangeWaitTypePacket.WT_STANDING));

        if(_chairObject != null)
            _chairObject.setSeatedPlayer(this);

        _chairObject = null;
        sittingTaskLaunched = true;
        ThreadPoolManager.getInstance().schedule(new EndStandUpTask(this), 2500);
    }

    public void updateWaitSitTime()
    {
        if(_waitTimeWhenSit < 200)
            _waitTimeWhenSit += 2;
    }

    public int getWaitSitTime()
    {
        return _waitTimeWhenSit;
    }

    public void resetWaitSitTime()
    {
        _waitTimeWhenSit = 0;
    }

    public Warehouse getWarehouse()
    {
        return _warehouse;
    }

    public ItemContainer getRefund()
    {
        return _refund;
    }

    public long getAdena()
    {
        return getInventory().getAdena();
    }

    public boolean reduceAdena(long adena)
    {
        return reduceAdena(adena, false);
    }

    /**
     * Забирает адену у игрока.<BR><BR>
     *
     * @param adena  - сколько адены забрать
     * @param notify - отображать системное сообщение
     * @return true если сняли
     */
    public boolean reduceAdena(long adena, boolean notify)
    {
        if(adena < 0)
            return false;
        if(adena == 0)
            return true;
        boolean result = getInventory().reduceAdena(adena);
        if(notify && result)
            sendPacket(SystemMessagePacket.removeItems(Items.ADENA, adena));
        return result;
    }

    public ItemInstance addAdena(long adena)
    {
        return addAdena(adena, false);
    }

    /**
     * Добавляет адену игроку.<BR><BR>
     *
     * @param adena  - сколько адены дать
     * @param notify - отображать системное сообщение
     * @return L2ItemInstance - новое количество адены
     */
    public ItemInstance addAdena(long adena, boolean notify)
    {
        if(adena < 1)
            return null;
        ItemInstance item = getInventory().addAdena(adena);
        if(item != null && notify)
            sendPacket(SystemMessagePacket.obtainItems(Items.ADENA, adena, 0));
        return item;
    }

    public GameClient getNetConnection()
    {
        return _connection;
    }

    public int getRevision()
    {
        return _connection == null ? 0 : _connection.getRevision();
    }

    public void setNetConnection(final GameClient connection)
    {
        _connection = connection;
    }

    public boolean isConnected()
    {
        return _connection != null && _connection.isConnected();
    }

    @Override
    public void onAction(final Player player, boolean shift)
    {
        if(!isTargetable(player))
        {
            player.sendActionFailed();
            return;
        }

        if(isFrozen())
        {
            player.sendPacket(ActionFailPacket.STATIC);
            return;
        }

        if(shift && OnShiftActionHolder.getInstance().callShiftAction(player, Player.class, this, true))
            return;

        // Check if the other player already target this L2Player
        if(player.getTarget() != this)
        {
            player.setTarget(this);
            if(player.getTarget() != this)
                player.sendPacket(ActionFailPacket.STATIC);
        }
        else if(getPrivateStoreType() != Player.STORE_PRIVATE_NONE)
        {
            if(!player.checkInteractionDistance(this) && player.getAI().getIntention() != CtrlIntention.AI_INTENTION_INTERACT)
            {
                if(!shift)
                    player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this, null);
                else
                    player.sendPacket(ActionFailPacket.STATIC);
            }
            else
                player.doInteract(this);
        }
        else if(isAutoAttackable(player))
            player.getAI().Attack(this, false, shift);
        else if(player != this)
        {
            if(player.getAI().getIntention() != CtrlIntention.AI_INTENTION_FOLLOW)
            {
                if(!shift)
                    player.getAI().setIntention(CtrlIntention.AI_INTENTION_FOLLOW, this, Config.FOLLOW_RANGE);
                else
                    player.sendPacket(ActionFailPacket.STATIC);
            }
            else
                player.sendPacket(ActionFailPacket.STATIC);
        }
        else
            player.sendPacket(ActionFailPacket.STATIC);
    }

    @Override
    public void broadcastStatusUpdate()
    {
        //if(!needStatusUpdate()) //По идее еше должно срезать траффик. Будут глюки с отображением - убрать это условие.
        //return;

        sendPacket(makeStatusUpdate(null, StatusUpdatePacket.MAX_HP, StatusUpdatePacket.MAX_MP, StatusUpdatePacket.MAX_CP, StatusUpdatePacket.CUR_HP, StatusUpdatePacket.CUR_MP, StatusUpdatePacket.CUR_CP));
        broadcastPacketToOthers(makeStatusUpdate(null, StatusUpdatePacket.MAX_HP, StatusUpdatePacket.MAX_MP, StatusUpdatePacket.MAX_CP, StatusUpdatePacket.CUR_HP, StatusUpdatePacket.CUR_MP, StatusUpdatePacket.CUR_CP));

        // Check if a party is in progress
        if(isInParty())
            // Send the Server->Client packet PartySmallWindowUpdatePacket with current HP, MP and Level to all other L2Player of the Party
            getParty().broadcastToPartyMembers(this, new PartySmallWindowUpdatePacket(this));

        final List<SingleMatchEvent> events = getEvents(SingleMatchEvent.class);
        for(SingleMatchEvent event : events)
            event.onStatusUpdate(this);

        if(isInOlympiadMode() && isOlympiadCompStart())
        {
            if(_olympiadGame != null)
                _olympiadGame.broadcastInfo(this, null, false);
        }
    }

    private ScheduledFuture<?> _broadcastCharInfoTask;

    public class BroadcastCharInfoTask extends RunnableImpl
    {
        @Override
        public void runImpl() throws Exception
        {
            broadcastCharInfoImpl();
            _broadcastCharInfoTask = null;
        }
    }

    @Override
    public void broadcastCharInfo()
    {
        broadcastUserInfo(false);
    }

    /**
     * Отправляет UserInfo даному игроку и CIPacket всем окружающим.<BR><BR>
     * <p/>
     * <B><U> Концепт</U> :</B><BR><BR>
     * Сервер шлет игроку UserInfo.
     * Сервер вызывает метод {@link Creature#broadcastPacketToOthers(L2GameServerPacket...)} для рассылки CIPacket<BR><BR>
     * <p/>
     * <B><U> Действия</U> :</B><BR><BR>
     * <li>Отсылка игроку UserInfo(личные и общие данные)</li>
     * <li>Отсылка другим игрокам CIPacket(Public data only)</li><BR><BR>
     * <p/>
     * <FONT COLOR=#FF0000><B> <U>Внимание</U> : НЕ ПОСЫЛАЙТЕ UserInfo другим игрокам либо CIPacket даному игроку.<BR>
     * НЕ ВЫЗЫВАЕЙТЕ ЭТОТ МЕТОД КРОМЕ ОСОБЫХ ОБСТОЯТЕЛЬСТВ(смена сабкласса к примеру)!!! Траффик дико кушается у игроков и начинаются лаги.<br>
     * Используйте метод {@link Player#sendChanges()}</B></FONT><BR><BR>
     */
    public void broadcastUserInfo(boolean force)
    {
        sendUserInfo(force);

        if(!isVisible())
            return;

        if(Config.BROADCAST_CHAR_INFO_INTERVAL == 0)
            force = true;

        if(force)
        {
            if(_broadcastCharInfoTask != null)
            {
                _broadcastCharInfoTask.cancel(false);
                _broadcastCharInfoTask = null;
            }
            broadcastCharInfoImpl();
            return;
        }

        if(_broadcastCharInfoTask != null)
            return;

        _broadcastCharInfoTask = ThreadPoolManager.getInstance().schedule(new BroadcastCharInfoTask(), Config.BROADCAST_CHAR_INFO_INTERVAL);
    }

    private int _polyNpcId;

    public void setPolyId(int polyid)
    {
        _polyNpcId = polyid;

        teleToLocation(getLoc());
        broadcastUserInfo(true);
    }

    public boolean isPolymorphed()
    {
        return _polyNpcId != 0;
    }

    public int getPolyId()
    {
        return _polyNpcId;
    }

    @Override
    public void broadcastCharInfoImpl(IUpdateTypeComponent... components)
    {
        if(!isVisible())
            return;

        for(Player target : World.getAroundObservers(this))
        {
            if(isInvisible(target))
                continue;

            target.sendPacket(isPolymorphed() ? new NpcInfoPoly(this) : new CIPacket(this, target));
            target.sendPacket(new RelationChangedPacket(this, target));
        }
    }

    public void sendEtcStatusUpdate()
    {
        if(!isVisible())
            return;

        sendPacket(new EtcStatusUpdatePacket(this));
    }

    private Future<?> _userInfoTask;

    private class UserInfoTask extends RunnableImpl
    {
        @Override
        public void runImpl() throws Exception
        {
            sendUserInfoImpl();
            _userInfoTask = null;
        }
    }

    private void sendUserInfoImpl()
    {
        sendPacket(new UIPacket(this));
    }

    public void sendUserInfo()
    {
        sendUserInfo(false);
    }

    public void sendUserInfo(boolean force)
    {
        if(!isVisible() || entering || isLogoutStarted() || isFakePlayer())
            return;

        if(Config.USER_INFO_INTERVAL == 0 || force)
        {
            if(_userInfoTask != null)
            {
                _userInfoTask.cancel(false);
                _userInfoTask = null;
            }
            sendUserInfoImpl();
            return;
        }

        if(_userInfoTask != null)
            return;

        _userInfoTask = ThreadPoolManager.getInstance().schedule(new UserInfoTask(), Config.USER_INFO_INTERVAL);
    }

    public void sendSkillList(int learnedSkillId)
    {
        sendPacket(new SkillListPacket(this, learnedSkillId));
        sendPacket(new AcquireSkillListPacket(this));
    }

    public void sendSkillList()
    {
        sendSkillList(0);
    }

    public void updateSkillShortcuts(int skillId, int skillLevel)
    {
        for(ShortCut sc : getAllShortCuts())
        {
            if(sc.getId() == skillId && sc.getType() == ShortCut.TYPE_SKILL)
            {
                ShortCut newsc = new ShortCut(sc.getSlot(), sc.getPage(), sc.getType(), sc.getId(), skillLevel, 1);
                sendPacket(new ShortCutRegisterPacket(this, newsc));
                registerShortCut(newsc);
            }
        }
    }

    @Override
    public StatusUpdatePacket makeStatusUpdate(Creature caster, int... fields)
    {
        StatusUpdatePacket su = new StatusUpdatePacket(this, caster);
        for(int field : fields)
            switch(field)
            {
                case StatusUpdatePacket.CUR_HP:
                    su.addAttribute(field, (int) getCurrentHp());
                    break;
                case StatusUpdatePacket.MAX_HP:
                    su.addAttribute(field, getMaxHp());
                    break;
                case StatusUpdatePacket.CUR_MP:
                    su.addAttribute(field, (int) getCurrentMp());
                    break;
                case StatusUpdatePacket.MAX_MP:
                    su.addAttribute(field, getMaxMp());
                    break;
                case StatusUpdatePacket.CUR_LOAD:
                    su.addAttribute(field, getCurrentLoad());
                    break;
                case StatusUpdatePacket.MAX_LOAD:
                    su.addAttribute(field, getMaxLoad());
                    break;
                case StatusUpdatePacket.PVP_FLAG:
                    su.addAttribute(field, getPvpFlag());
                    break;
                case StatusUpdatePacket.KARMA:
                    su.addAttribute(field, getKarma());
                    break;
                case StatusUpdatePacket.CUR_CP:
                    su.addAttribute(field, (int) getCurrentCp());
                    break;
                case StatusUpdatePacket.MAX_CP:
                    su.addAttribute(field, getMaxCp());
                    break;
            }
        return su;
    }

    public void sendStatusUpdate(boolean broadCast, boolean withPet, int... fields)
    {
        if(fields.length == 0 || entering && !broadCast)
            return;

        StatusUpdatePacket su = makeStatusUpdate(null, fields);
        if(!su.hasAttributes())
            return;

        List<L2GameServerPacket> packets = new ArrayList<L2GameServerPacket>(withPet ? 2 : 1);
        if(withPet)
        {
            for(Servitor servitor : getServitors())
                packets.add(servitor.makeStatusUpdate(null, fields));
        }

        packets.add(su);

        if(!broadCast)
            sendPacket(packets);
        else if(entering)
            broadcastPacketToOthers(packets);
        else
            broadcastPacket(packets);
    }

    /**
     * @return the Alliance Identifier of the L2Player.<BR><BR>
     */
    public int getAllyId()
    {
        return _clan == null ? 0 : _clan.getAllyId();
    }

    @Override
    public void sendPacket(IBroadcastPacket p)
    {
        if(p == null)
            return;

        if(isPacketIgnored(p))
            return;

        GameClient connection = getNetConnection();
        if(connection != null && connection.isConnected())
            _connection.sendPacket(p.packet(this));
    }

    @Override
    public void sendPacket(IBroadcastPacket... packets)
    {
        for(IBroadcastPacket p : packets)
            sendPacket(p);
    }

    @Override
    public void sendPacket(List<? extends IBroadcastPacket> packets)
    {
        for(IBroadcastPacket p : packets)
            sendPacket(p);
    }

    private boolean isPacketIgnored(IBroadcastPacket p)
    {
        if(p == null)
            return true;

        //if(_notShowTraders && (p.getClass() == PrivateStoreBuyMsg.class || p.getClass() == PrivateStoreMsg.class || p.getClass() == RecipeShopMsgPacket.class))
        //		return true;

        return false;
    }

    public void doInteract(GameObject target)
    {
        if(target == null || isActionsDisabled())
        {
            sendActionFailed();
            return;
        }
        if(target.isPlayer())
        {
            if(checkInteractionDistance(target))
            {
                Player temp = (Player) target;

                if(temp.getPrivateStoreType() == STORE_PRIVATE_SELL || temp.getPrivateStoreType() == STORE_PRIVATE_SELL_PACKAGE)
                    sendPacket(new PrivateStoreList(this, temp));
                else if(temp.getPrivateStoreType() == STORE_PRIVATE_BUY)
                    sendPacket(new PrivateStoreBuyList(this, temp));
                else if(temp.getPrivateStoreType() == STORE_PRIVATE_MANUFACTURE)
                    sendPacket(new RecipeShopSellListPacket(this, temp));

                sendActionFailed();
            }
            else if(getAI().getIntention() != CtrlIntention.AI_INTENTION_INTERACT)
                getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this, null);
        }
        else
            target.onAction(this, false);
    }

    public void doAutoLootOrDrop(ItemInstance item, NpcInstance fromNpc)
    {
        boolean forceAutoloot = fromNpc.isFlying() || getReflection().isAutolootForced();

        if((fromNpc.isRaid() || fromNpc instanceof ReflectionBossInstance) && !Config.AUTO_LOOT_FROM_RAIDS && !item.isHerb() && !forceAutoloot)
        {
            item.dropToTheGround(this, fromNpc);
            return;
        }

        // Herbs
        if(item.isHerb())
        {
            if(!AutoLootHerbs && !forceAutoloot)
            {
                item.dropToTheGround(this, fromNpc);
                return;
            }
            for(SkillEntry skillEntry : item.getTemplate().getAttachedSkills())
            {
                altUseSkill(skillEntry.getTemplate(), this);

                for(Servitor servitor : getServitors())
                {
                    if(servitor.isSummon() && !servitor.isDead())
                        servitor.altUseSkill(skillEntry.getTemplate(), servitor);
                }
            }
            item.deleteMe();
            return;
        }

        if(!forceAutoloot && !(_autoLoot && (Config.AUTO_LOOT_ITEM_ID_LIST.isEmpty() || Config.AUTO_LOOT_ITEM_ID_LIST.contains(item.getItemId()))) && !(_autoLootOnlyAdena && item.getTemplate().isAdena()))
        {
            item.dropToTheGround(this, fromNpc);
            return;
        }

        // Check if the L2Player is in a Party
        if(!isInParty())
        {
            if(!pickupItem(item, Log.Pickup))
            {
                item.dropToTheGround(this, fromNpc);
                return;
            }
        }
        else
            getParty().distributeItem(this, item, fromNpc);

        broadcastPickUpMsg(item);
    }

    @Override
    public void doPickupItem(final GameObject object)
    {
        // Check if the L2Object to pick up is a L2ItemInstance
        if(!object.isItem())
        {
            _log.warn("trying to pickup wrong target." + getTarget());
            return;
        }

        sendActionFailed();
        stopMove();

        ItemInstance item = (ItemInstance) object;

        synchronized (item)
        {
            if(!item.isVisible())
                return;

            // Check if me not owner of item and, if in party, not in owner party and nonowner pickup delay still active
            if(!ItemFunctions.checkIfCanPickup(this, item))
            {
                SystemMessage sm;
                if(item.getItemId() == 57)
                {
                    sm = new SystemMessage(SystemMessage.YOU_HAVE_FAILED_TO_PICK_UP_S1_ADENA);
                    sm.addNumber(item.getCount());
                }
                else
                {
                    sm = new SystemMessage(SystemMessage.YOU_HAVE_FAILED_TO_PICK_UP_S1);
                    sm.addItemName(item.getItemId());
                }
                sendPacket(sm);
                return;
            }

            // Herbs
            if(item.isHerb())
            {
                for(SkillEntry skillEntry : item.getTemplate().getAttachedSkills())
                    altUseSkill(skillEntry.getTemplate(), this);

                broadcastPacket(new GetItemPacket(item, getObjectId()));
                item.deleteMe();
                return;
            }

            FlagItemAttachment attachment = item.getAttachment() instanceof FlagItemAttachment ? (FlagItemAttachment) item.getAttachment() : null;

            if(!isInParty() || attachment != null)
            {
                if(pickupItem(item, Log.Pickup))
                {
                    broadcastPacket(new GetItemPacket(item, getObjectId()));
                    broadcastPickUpMsg(item);
                    item.pickupMe();
                }
            }
            else
                getParty().distributeItem(this, item, null);
        }
    }

    public boolean pickupItem(ItemInstance item, String log)
    {
        PickableAttachment attachment = item.getAttachment() instanceof PickableAttachment ? (PickableAttachment) item.getAttachment() : null;

        if(!ItemFunctions.canAddItem(this, item))
            return false;

        Log.LogItem(this, log, item);
        sendPacket(SystemMessagePacket.obtainItems(item));
        getInventory().addItem(item);

        if(attachment != null)
            attachment.pickUp(this);

        getListeners().onPickupItem(item);

        sendChanges();
        return true;
    }

    public void setNpcTarget(GameObject target)
    {
        setTarget(target);
        if(target == null)
            return;

        if(target == getTarget())
        {
            if(target.isNpc())
            {
                NpcInstance npc = (NpcInstance) target;
                sendPacket(npc.makeStatusUpdate(null, StatusUpdatePacket.CUR_HP, StatusUpdatePacket.MAX_HP));
                sendPacket(new ValidateLocationPacket(npc), ActionFailPacket.STATIC);
            }
        }
    }

    @Override
    public void setTarget(GameObject newTarget)
    {
        // Check if the new target is visible
        if(newTarget != null && !newTarget.isVisible())
            newTarget = null;

        GameObject oldTarget = getTarget();

        if(oldTarget != null)
        {
            if(oldTarget.equals(newTarget))
                return;

            broadcastPacket(new TargetUnselectedPacket(this));
        }

        if(newTarget != null)
        {
            broadcastTargetSelected(newTarget);

            if(newTarget.isCreature())
                sendPacket(((Creature) newTarget).getAbnormalStatusUpdate());
        }

        if(newTarget != null && newTarget != this && getDecoys() != null && !getDecoys().isEmpty() && newTarget.isCreature())
        {
            for(DecoyInstance dec : getDecoys())
            {
                if(dec == null)
                    continue;
                if(dec.getAI() == null)
                {
                    _log.info("This decoy has NULL AI");
                    continue;
                }
                if(newTarget.isCreature())
                {
                    Creature _nt = (Creature) newTarget;
                    if(_nt.isInPeaceZone()) //won't attack in peace zone anyone.
                        continue;
                }
                dec.getAI().notifyEvent(CtrlEvent.EVT_AGGRESSION, newTarget, 1000);
                //dec.getAI().checkAggression(((Creature)newTarget));
                //dec.getAI().Attack(newTarget, true, false);
            }
        }

        super.setTarget(newTarget);
    }

    public void broadcastTargetSelected(GameObject newTarget)
    {
        sendPacket(new MyTargetSelectedPacket(this, newTarget));
        broadcastPacket(new TargetSelectedPacket(getObjectId(), newTarget.getObjectId(), getLoc()));
    }

    /**
     * @return the active weapon instance (always equipped in the right hand).<BR><BR>
     */
    @Override
    public ItemInstance getActiveWeaponInstance()
    {
        return getInventory().getPaperdollItem(Inventory.PAPERDOLL_RHAND);
    }

    /**
     * @return the active weapon item (always equipped in the right hand).<BR><BR>
     */
    @Override
    public WeaponTemplate getActiveWeaponTemplate()
    {
        final ItemInstance weapon = getActiveWeaponInstance();

        if(weapon == null)
            return null;

        ItemTemplate template = weapon.getTemplate();
        if(template == null)
            return null;

        if(!(template instanceof WeaponTemplate))
        {
            _log.warn("Template in active weapon not WeaponTemplate! (Item ID[" + weapon.getItemId() + "])");
            return null;
        }

        return (WeaponTemplate) template;
    }

    /**
     * @return the secondary weapon instance (always equipped in the left hand).<BR><BR>
     */
    @Override
    public ItemInstance getSecondaryWeaponInstance()
    {
        return getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND);
    }

    /**
     * @return the secondary weapon item (always equipped in the left hand) or the fists weapon.<BR><BR>
     */
    @Override
    public WeaponTemplate getSecondaryWeaponTemplate()
    {
        final ItemInstance weapon = getSecondaryWeaponInstance();

        if(weapon == null)
            return null;

        final ItemTemplate item = weapon.getTemplate();

        if(item instanceof WeaponTemplate)
            return (WeaponTemplate) item;

        return null;
    }

    public ArmorType getWearingArmorType()
    {
        final ItemInstance chest = getInventory().getPaperdollItem(Inventory.PAPERDOLL_CHEST);
        if(chest == null)
            return ArmorType.NONE;

        final ItemType chestItemType = chest.getItemType();
        if(!(chestItemType instanceof ArmorType))
            return ArmorType.NONE;

        final ArmorType chestArmorType = (ArmorType) chestItemType;
        if(chest.getBodyPart() == ItemTemplate.SLOT_FULL_ARMOR)
            return chestArmorType;

        final ItemInstance legs = getInventory().getPaperdollItem(Inventory.PAPERDOLL_LEGS);
        if(legs == null)
            return ArmorType.NONE;

        if(legs.getItemType() != chestArmorType)
            return ArmorType.NONE;

        return chestArmorType;
    }

    @Override
    public void reduceCurrentHp(double damage, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp, boolean canReflectAndAbsorb, boolean transferDamage, boolean isDot, boolean sendReceiveMessage, boolean sendGiveMessage, boolean crit, boolean miss, boolean shld)
    {
        if(attacker == null || isDead() || (attacker.isDead() && !isDot))
            return;

        // 5182 = Blessing of protection, работает если разница уровней больше 10 и не в зоне осады
        if(attacker.isPlayer() && Math.abs(attacker.getLevel() - getLevel()) > 10)
        {
            // ПК не может нанести урон чару с блессингом
            if(attacker.isPK() && getAbnormalList().contains(5182) && !isInSiegeZone())
                return;
            // чар с блессингом не может нанести урон ПК
            if(isPK() && attacker.getAbnormalList().contains(5182) && !attacker.isInSiegeZone())
                return;
        }

        // Reduce the current HP of the L2Player
        super.reduceCurrentHp(damage, attacker, skill, awake, standUp, directHp, canReflectAndAbsorb, transferDamage, isDot, sendReceiveMessage, sendGiveMessage, crit, miss, shld);
    }

    @Override
    protected void onReduceCurrentHp(double damage, Creature attacker, Skill skill, boolean awake, boolean standUp, boolean directHp, boolean isDot)
    {
        if(damage <= 0)
            return;

        if(standUp)
        {
            standUp();
            if(isFakeDeath())
                breakFakeDeath();
        }

        final double originDamage = damage;

        if(attacker.isPlayable())
        {
            if(!directHp && getCurrentCp() > 0)
            {
                double cp = getCurrentCp();
                if(cp >= damage)
                {
                    cp -= damage;
                    damage = 0;
                }
                else
                {
                    damage -= cp;
                    cp = 0;
                }

                setCurrentCp(cp);
            }
        }

        double hp = getCurrentHp();

        DuelEvent duelEvent = getEvent(DuelEvent.class);
        if(duelEvent != null)
        {
            if(hp - damage <= 1 && !isDeathImmune()) // если хп <= 1 - убит
            {
                setCurrentHp(1, false);
                duelEvent.onDie(this);
                return;
            }
        }

        if(isInOlympiadMode())
        {
            OlympiadGame game = _olympiadGame;
            if(this != attacker && (skill == null || skill.isOffensive())) // считаем дамаг от простых ударов и атакующих скиллов
                game.addDamage(this, Math.min(hp, originDamage));

            if(hp - damage <= 1 && !isDeathImmune()) // если хп <= 1 - убит
            {
                game.setWinner(getOlympiadSide() == 1 ? 2 : 1);
                game.endGame(20000, false);
                setCurrentHp(1, false);
                attacker.getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE);
                attacker.sendActionFailed();
                return;
            }
        }

        if(calcStat(Stats.RestoreHPGiveDamage) == 1 && Rnd.chance(1))
            setCurrentHp(getCurrentHp() + getMaxHp() / 10, false);

        super.onReduceCurrentHp(damage, attacker, skill, awake, standUp, directHp, isDot);
    }

    private void altDeathPenalty(final Creature killer)
    {
        // Reduce the Experience of the L2Player in function of the calculated Death Penalty
        if(!Config.ALT_GAME_DELEVEL)
            return;
        if(isInZoneBattle())
            return;
        deathPenalty(killer);
    }

    public final boolean atWarWith(final Player player)
    {
        return _clan != null && player.getClan() != null && getPledgeType() != -1 && player.getPledgeType() != -1 && _clan.isAtWarWith(player.getClan().getClanId());
    }

    public boolean atMutualWarWith(Player player)
    {
        return _clan != null && player.getClan() != null && getPledgeType() != -1 && player.getPledgeType() != -1 && _clan.isAtWarWith(player.getClan().getClanId()) && player.getClan().isAtWarWith(_clan.getClanId());
    }

    public final void doPurePk(final Player killer)
    {
        // Check if the attacker has a PK counter greater than 0
        final int pkCountMulti = (int) Math.max(killer.getPkKills() * Config.KARMA_PENALTY_DURATION_INCREASE, 1);

        // Calculate the level difference Multiplier between attacker and killed L2Player
        //final int lvlDiffMulti = Math.max(killer.getLevel() / _level, 1);

        // Calculate the new Karma of the attacker : newKarma = baseKarma*pkCountMulti*lvlDiffMulti
        // Add karma to attacker and increase its PK counter
        killer.decreaseKarma(Config.KARMA_MIN_KARMA * pkCountMulti); // * lvlDiffMulti);
        killer.setPkKills(killer.getPkKills() + 1);
    }

    public final void doKillInPeace(final Player killer) // Check if the L2Player killed haven't Karma
    {
        if(!isPK())
            doPurePk(killer);
        else
        {
            String var = PK_KILL_VAR + "_" + getObjectId();
            if(!killer.getVarBoolean(var))
            {
                // В течении 30 минут не выдаем карму за убийство данного ПК. (TODO: [Bonux] Проверить время на оффе.)
                long expirationTime = System.currentTimeMillis() + (30 * 60 * 1000);
                killer.setVar(var, true, expirationTime);
            }
        }
    }

    public void checkAddItemToDrop(List<ItemInstance> array, List<ItemInstance> items, int maxCount)
    {
        for(int i = 0; i < maxCount && !items.isEmpty(); i++)
            array.add(items.remove(Rnd.get(items.size())));
    }

    public FlagItemAttachment getActiveWeaponFlagAttachment()
    {
        ItemInstance item = getActiveWeaponInstance();
        if(item == null || !(item.getAttachment() instanceof FlagItemAttachment))
            return null;
        return (FlagItemAttachment) item.getAttachment();
    }

    protected void doPKPVPManage(Creature killer)
    {
        FlagItemAttachment attachment = getActiveWeaponFlagAttachment();
        if(attachment != null)
            attachment.onDeath(this, killer);

        if(killer == null || isMyServitor(killer.getObjectId()) || killer == this)
            return;

        if(killer.isServitor() && (killer = killer.getPlayer()) == null)
            return;

        if(killer.isPlayer())
            PvPRewardManager.tryGiveReward(this, killer.getPlayer());

        if(isInZoneBattle() || killer.isInZoneBattle())
            return;

        if(killer.getTeam() != TeamType.NONE && getTeam() != TeamType.NONE) //in events or duels we don't increase pvp/pk/karma
            return;

        // Processing Karma/PKCount/PvPCount for killer
        if(killer.isPlayer() || killer instanceof FakePlayer) //addon if killer is clone instance should do also this method.
        {
            final Player pk = killer.getPlayer();
            boolean war = atMutualWarWith(pk);

            //TODO [VISTALL] fix it
            if(war /*|| _clan.getSiege() != null && _clan.getSiege() == pk.getClan().getSiege() && (_clan.isDefender() && pk.getClan().isAttacker() || _clan.isAttacker() && pk.getClan().isDefender())*/)
            {
                ClanWar clanWar = _clan.getClanWar(pk.getClan());
                if(clanWar != null)
                    clanWar.onKill(pk, this);
            }

            if(isInSiegeZone())
                return;

            Castle castle = getCastle();
            if(getPvpFlag() > 0 || war || castle != null && castle.getResidenceSide() == ResidenceSide.DARK)
                pk.setPvpKills(pk.getPvpKills() + 1);
            else
                doKillInPeace(pk);

            pk.sendChanges();
        }

        int karma = _karma;
        if(isPK())
        {
            increaseKarma(Config.KARMA_LOST_BASE);
            if(_karma > 0)
                _karma = 0;
        }

        // в нормальных условиях вещи теряются только при смерти от гварда или игрока
        // кроме того, альт на потерю вещей при сметри позволяет терять вещи при смтери от монстра
        boolean isPvP = killer.isPlayable() || killer instanceof GuardInstance;

        if(isFakePlayer() // если фейк плейер
                || killer.isMonster() && !Config.DROP_ITEMS_ON_DIE // если убил монстр и альт выключен
                || isPvP // если убил игрок или гвард и
                && (_pkKills < Config.MIN_PK_TO_ITEMS_DROP // количество пк слишком мало
                || karma >= 0 && Config.KARMA_NEEDED_TO_DROP) // кармы нет
                || !killer.isMonster() && !isPvP) // в прочих случаях тоже
            return;

        // No drop from GM's
        if(!Config.KARMA_DROP_GM && isGM())
            return;

        final int max_drop_count = isPvP ? Config.KARMA_DROP_ITEM_LIMIT : 1;

        double dropRate; // базовый шанс в процентах
        if(isPvP)
            dropRate = _pkKills * Config.KARMA_DROPCHANCE_MOD + Config.KARMA_DROPCHANCE_BASE;
        else
            dropRate = Config.NORMAL_DROPCHANCE_BASE;

        int dropEquipCount = 0, dropWeaponCount = 0, dropItemCount = 0;

        for(int i = 0; i < Math.ceil(dropRate / 100) && i < max_drop_count; i++)
            if(Rnd.chance(dropRate))
            {
                int rand = Rnd.get(Config.DROPCHANCE_EQUIPPED_WEAPON + Config.DROPCHANCE_EQUIPMENT + Config.DROPCHANCE_ITEM) + 1;
                if(rand > Config.DROPCHANCE_EQUIPPED_WEAPON + Config.DROPCHANCE_EQUIPMENT)
                    dropItemCount++;
                else if(rand > Config.DROPCHANCE_EQUIPPED_WEAPON)
                    dropEquipCount++;
                else
                    dropWeaponCount++;
            }

        List<ItemInstance> drop = new ArrayList<>(), // общий массив с результатами выбора
                dropItem = new ArrayList<>(), dropEquip = new ArrayList<>(), dropWeapon = new ArrayList<>(); // временные

        getInventory().writeLock();
        try
        {
            for(ItemInstance item : getInventory().getItems())
            {
                if(!item.canBeDropped(this, true) || Config.KARMA_LIST_NONDROPPABLE_ITEMS.contains(item.getItemId()))
                    continue;

                if(item.getTemplate().getType2() == ItemTemplate.TYPE2_WEAPON)
                    dropWeapon.add(item);
                else if(item.getTemplate().getType2() == ItemTemplate.TYPE2_SHIELD_ARMOR || item.getTemplate().getType2() == ItemTemplate.TYPE2_ACCESSORY)
                    dropEquip.add(item);
                else if(item.getTemplate().getType2() == ItemTemplate.TYPE2_OTHER)
                    dropItem.add(item);
            }

            checkAddItemToDrop(drop, dropWeapon, dropWeaponCount);
            checkAddItemToDrop(drop, dropEquip, dropEquipCount);
            checkAddItemToDrop(drop, dropItem, dropItemCount);

            // Dropping items, if present
            if(drop.isEmpty())
                return;

            for(ItemInstance item : drop)
            {
                if(item.isAugmented() && !Config.ALT_ALLOW_DROP_AUGMENTED)
                {
                    item.setVariationStoneId(0);
                    item.setVariation1Id(0);
                    item.setVariation2Id(0);
                }

                item = getInventory().removeItem(item);
                Log.LogItem(this, Log.PvPDrop, item);

                if(item.getEnchantLevel() > 0)
                    sendPacket(new SystemMessage(SystemMessage.DROPPED__S1_S2).addNumber(item.getEnchantLevel()).addItemName(item.getItemId()));
                else
                    sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_DROPPED_S1).addItemName(item.getItemId()));

                if(killer.isPlayable() && ((Config.AUTO_LOOT && Config.AUTO_LOOT_PK) || isInFlyingTransform()))
                {
                    killer.getPlayer().getInventory().addItem(item);
                    Log.LogItem(this, Log.Pickup, item);

                    killer.getPlayer().sendPacket(SystemMessagePacket.obtainItems(item));
                }
                else
                    item.dropToTheGround(this, Location.findAroundPosition(this, Config.KARMA_RANDOM_DROP_LOCATION_LIMIT));
            }
        }
        finally
        {
            getInventory().writeUnlock();
        }
    }

    @Override
    protected void onDeath(Creature killer)
    {
        if(isInStoreMode())
        {
            setPrivateStoreType(Player.STORE_PRIVATE_NONE);
            storePrivateStore();
        }
        if(isProcessingRequest())
        {
            Request request = getRequest();
            if(isInTrade())
            {
                Player parthner = request.getOtherPlayer(this);
                sendPacket(TradeDonePacket.FAIL);
                parthner.sendPacket(TradeDonePacket.FAIL);
            }
            request.cancel();
        }

        setAgathion(0);

        boolean checkPvp = true;
        final Player killerPlayer = killer.getPlayer();
        if(killerPlayer != null)
        {
            for(SingleMatchEvent event : getEvents(SingleMatchEvent.class))
            {
                if(!event.canIncreasePvPPKCounter(killerPlayer, this))
                {
                    checkPvp = false;
                    break;
                }
            }
        }

        if(checkPvp)
        {
            doPKPVPManage(killer);
            altDeathPenalty(killer);
        }

        setIncreasedForce(0);

        stopWaterTask();

        if(!isSalvation() && isInSiegeZone() && isCharmOfCourage())
        {
            ask(new ConfirmDlgPacket(SystemMsg.YOUR_CHARM_OF_COURAGE_IS_TRYING_TO_RESURRECT_YOU, 60000), new ReviveAnswerListener(this, 100, false));
            setCharmOfCourage(false);
        }

        for(QuestState qs : getAllQuestsStates())
            qs.getQuest().notifyTutorialEvent("CE", false, "200", qs);

        if(isMounted())
            _mount.onDeath();

        for(Servitor servitor : getServitors())
            servitor.notifyMasterDeath();

        for(ListenerHook hook : getListenerHooks(ListenerHookType.PLAYER_DIE))
            hook.onPlayerDie(this, killer);

        for(ListenerHook hook : ListenerHook.getGlobalListenerHooks(ListenerHookType.PLAYER_DIE))
            hook.onPlayerDie(this, killer);

        super.onDeath(killer);
    }

    public void restoreExp()
    {
        restoreExp(100.);
    }

    public void restoreExp(double percent)
    {
        if(percent == 0)
            return;

        long lostexp = 0;

        String lostexps = getVar("lostexp");
        if(lostexps != null)
        {
            lostexp = Long.parseLong(lostexps);
            unsetVar("lostexp");
        }

        if(lostexp != 0)
            addExpAndSp((long) (lostexp * percent / 100), 0);
    }

    public void deathPenalty(Creature killer)
    {
        if(killer == null)
            return;

        final boolean atwar = killer.getPlayer() != null && atWarWith(killer.getPlayer());

        final int level = getLevel();

        // The death steal you some Exp: 10-40 lvl 8% loose
        double percentLost = Config.PERCENT_LOST_ON_DEATH[getLevel()];
        if(isPK())
            percentLost *= Config.PERCENT_LOST_ON_DEATH_MOD_FOR_PK;
        else if(isInPeaceZone())
            percentLost *= Config.PERCENT_LOST_ON_DEATH_MOD_IN_PEACE_ZONE;
        else
        {
            if(atwar) // TODO: Проверить, должен ли влиять данный подификатор на ПК!
                percentLost *= Config.PERCENT_LOST_ON_DEATH_MOD_IN_WAR;
            else if(killer.getPlayer() != null && killer.getPlayer() != this)
                percentLost *= Config.PERCENT_LOST_ON_DEATH_MOD_IN_PVP;
        }

        if(percentLost <= 0)
            return;

        // Calculate the Experience loss
        long lostexp = (long) ((Experience.getExpForLevel(level + 1) - Experience.getExpForLevel(level)) * percentLost / 100);

        lostexp = (long) calcStat(Stats.EXP_LOST, lostexp, killer, null);

        // На зарегистрированной осаде нет потери опыта, на чужой осаде - как при обычной смерти от *моба*
        if(isInSiegeZone())
        {
            SiegeEvent<?, ?> siegeEvent = getEvent(SiegeEvent.class);
            if(siegeEvent != null)
                lostexp = 0;

            if(siegeEvent != null)
            {
                int syndromeLvl = 0;
                for(Abnormal e : getAbnormalList())
                {
                    if(e.getSkill().getId() == Skill.SKILL_BATTLEFIELD_DEATH_SYNDROME)
                    {
                        syndromeLvl = e.getSkill().getLevel();
                        break;
                    }
                }

                if(syndromeLvl == 0)
                {
                    Skill skill = SkillHolder.getInstance().getSkill(Skill.SKILL_BATTLEFIELD_DEATH_SYNDROME, 1);
                    if(skill != null)
                        skill.getEffects(this, this);
                }
                else if(syndromeLvl < 5)
                {
                    getAbnormalList().stop(Skill.SKILL_BATTLEFIELD_DEATH_SYNDROME);
                    Skill skill = SkillHolder.getInstance().getSkill(Skill.SKILL_BATTLEFIELD_DEATH_SYNDROME, syndromeLvl + 1);
                    skill.getEffects(this, this);
                }
                else if(syndromeLvl == 5)
                {
                    getAbnormalList().stop(Skill.SKILL_BATTLEFIELD_DEATH_SYNDROME);
                    Skill skill = SkillHolder.getInstance().getSkill(Skill.SKILL_BATTLEFIELD_DEATH_SYNDROME, 5);
                    skill.getEffects(this, this);
                }
            }
        }

        long before = getExp();
        addExpAndSp(-lostexp, 0);
        long lost = before - getExp();

        if(lost > 0)
            setVar("lostexp", lost);
    }

    public void setRequest(Request transaction)
    {
        _request = transaction;
    }

    public Request getRequest()
    {
        return _request;
    }

    /**
     * Проверка, занят ли игрок для ответа на зарос
     *
     * @return true, если игрок не может ответить на запрос
     */
    public boolean isBusy()
    {
        return isProcessingRequest() || isOutOfControl() || isInOlympiadMode() || getTeam() != TeamType.NONE || isInStoreMode() || isInDuel() || getMessageRefusal() || isBlockAll() || isInvisible(null);
    }

    public boolean isProcessingRequest()
    {
        if(_request == null)
            return false;
        if(!_request.isInProgress())
            return false;
        return true;
    }

    public boolean isInTrade()
    {
        return isProcessingRequest() && getRequest().isTypeOf(L2RequestType.TRADE);
    }

    public List<L2GameServerPacket> addVisibleObject(GameObject object, Creature dropper)
    {
        if(isLogoutStarted() || object == null || object.getObjectId() == getObjectId() || !object.isVisible() || object.isObservePoint())
            return Collections.emptyList();

        return object.addPacketList(this, dropper);
    }

    @Override
    public List<L2GameServerPacket> addPacketList(Player forPlayer, Creature dropper)
    {
        if(isInvisible(forPlayer) && forPlayer.getObjectId() != getObjectId())
            return Collections.emptyList();

        if(isInStoreMode() && forPlayer.getVarBoolean(NO_TRADERS_VAR))
            return Collections.emptyList();

        List<L2GameServerPacket> list = new ArrayList<L2GameServerPacket>();
        if(forPlayer.getObjectId() != getObjectId())
            list.add(isPolymorphed() ? new NpcInfoPoly(this) : new CIPacket(this, forPlayer));

        if(isSitting() && _chairObject != null)
            list.add(new ChairSitPacket(this, _chairObject));

        if(isInStoreMode())
            list.add(getPrivateStoreMsgPacket(forPlayer));

        if(isCastingNow())
        {
            Creature castingTarget = getCastingTarget();
            Skill castingSkill = getCastingSkill();
            long animationEndTime = getAnimationEndTime();
            if(castingSkill != null && !castingSkill.isNotBroadcastable() && castingTarget != null && castingTarget.isCreature() && animationEndTime > 0)
                list.add(new MagicSkillUse(this, castingTarget, castingSkill.getId(), castingSkill.getLevel(), (int) (animationEndTime - System.currentTimeMillis()), 0));
        }

        if(isInCombat())
            list.add(new AutoAttackStartPacket(getObjectId()));

        list.add(new RelationChangedPacket(this, forPlayer));

        if(isInBoat())
            list.add(getBoat().getOnPacket(this, getInBoatPosition()));
        else
        {
            if(isMoving || isFollow)
                list.add(movePacket());
        }

        // VISTALL: во время ездовой трансформы, нужно послать второй раз при появлении обьекта
        // DS: для магазина то же самое, иначе иногда не виден после входа в игру
        if(/*isInMountTransform() || */(isInStoreMode() && entering))
        {
            list.add(new CIPacket(this, forPlayer));
            //list.add(new ExBR_ExtraUserInfo(this));
        }

        return list;
    }

    public List<L2GameServerPacket> removeVisibleObject(GameObject object, List<L2GameServerPacket> list)
    {
        if(isLogoutStarted() || object == null || object.getObjectId() == getObjectId() || object.isObservePoint()) // FIXME  || isTeleporting()
            return Collections.emptyList();

        List<L2GameServerPacket> result = list == null ? object.deletePacketList(this) : list;

        if(getParty() != null && object instanceof Creature)
            getParty().removeTacticalSign((Creature) object);

        if(!isInObserverMode())
            getAI().notifyEvent(CtrlEvent.EVT_FORGET_OBJECT, object);
        return result;
    }

    private void levelSet(int levels)
    {
        if(levels > 0)
        {
            final int level = getLevel();

            checkLevelUpReward(false);

            sendPacket(SystemMsg.YOUR_LEVEL_HAS_INCREASED);
            broadcastPacket(new SocialActionPacket(getObjectId(), SocialActionPacket.LEVEL_UP));

            setCurrentHpMp(getMaxHp(), getMaxMp());
            setCurrentCp(getMaxCp());

            for(QuestState qs : getAllQuestsStates())
                qs.getQuest().notifyTutorialEvent("CE", false, "300", qs);

            // Give Expertise skill of this level
            rewardSkills(false);
            notifyNewSkills();
        }
        else if(levels < 0)
            checkSkills();

        sendUserInfo(true);
        sendSkillList();

        // Recalculate the party level
        if(isInParty())
            getParty().recalculatePartyData();

        if(_clan != null)
            _clan.broadcastToOnlineMembers(new PledgeShowMemberListUpdatePacket(this));

        if(_matchingRoom != null)
            _matchingRoom.broadcastPlayerUpdate(this);
    }

    public boolean notifyNewSkills()
    {
        final Collection<SkillLearn> skills = SkillAcquireHolder.getInstance().getAvailableSkills(this, AcquireType.NORMAL);
        for(SkillLearn s : skills)
        {
            if(s.isFreeAutoGet(AcquireType.NORMAL))
                continue;

            Skill sk = SkillHolder.getInstance().getSkill(s.getId(), s.getLevel());
            if(sk == null)
                continue;

            sendPacket(ExNewSkillToLearnByLevelUp.STATIC);
            return true;
        }
        return false;
    }

    /**
     * Удаляет все скиллы, которые учатся на уровне большем, чем текущий+maxDiff
     */
    public boolean checkSkills()
    {
        boolean update = false;
        for(SkillEntry sk : getAllSkillsArray())
        {
            if(SkillUtils.checkSkill(this, sk))
                update = true;
        }
        return update;
    }

    public void startTimers()
    {
        startAutoSaveTask();
        startPcBangPointsTask();
        startPremiumAccountTask();
        getInventory().startTimers();
        resumeQuestTimers();
        getAttendanceRewards().startTasks();
    }

    public void stopAllTimers()
    {
        setAgathion(0);
        stopWaterTask();
        stopPremiumAccountTask();
        stopHourlyTask();
        stopPcBangPointsTask();
        stopTrainingCampTask();
        stopAutoSaveTask();
        getInventory().stopAllTimers();
        stopQuestTimers();
        stopEnableUserRelationTask();
        getHennaList().stopHennaRemoveTask();
        getAttendanceRewards().stopTasks();
    }

    @Override
    public boolean isMyServitor(int objId)
    {
        if(_summon != null && _summon.getObjectId() == objId)
            return true;

        if(_pet != null && _pet.getObjectId() == objId)
            return true;

        return false;
    }

    public int getServitorsCount()
    {
        int count = 0;
        if(_summon != null)
            count++;
        if(_pet != null)
            count++;
        return count;
    }

    public boolean hasServitor()
    {
        return getServitorsCount() > 0;
    }

    @Override
    public List<Servitor> getServitors()
    {
        List<Servitor> servitors = new ArrayList<Servitor>();
        if(_summon != null)
            servitors.add(_summon);

        if(_pet != null)
            servitors.add(_pet);

        Collections.sort(servitors, Servitor.ServitorComparator.getInstance());
        return servitors;
    }

    public Servitor getAnyServitor()
    {
        return getServitors().stream().findAny().orElse(null);
    }

    public Servitor getFirstServitor()
    {
        return getServitors().stream().findFirst().orElse(null);
    }

    public Servitor getServitor(int objId)
    {
        if(_summon != null && _summon.getObjectId() == objId)
            return _summon;
        if(_pet != null && _pet.getObjectId() == objId)
            return _pet;

        return null;
    }

    public boolean hasSummon()
    {
        return _summon != null;
    }

    public SummonInstance getSummon()
    {
        return _summon;
    }

    public void setSummon(SummonInstance summon)
    {
        if(_summon == summon)
            return;

        _summon = summon;
        if(_summon == null && _pet == null)
        {
            removeAutoShot(SoulShotType.BEAST_SOULSHOT);
            removeAutoShot(SoulShotType.BEAST_SPIRITSHOT);
        }

        autoShot();
        if(summon == null)
            getAbnormalList().stop(4140); //TODO: [Bonux] Нужно ли у петов?
    }

    public void deleteServitor(int objId)
    {
        if(_summon != null && _summon.getObjectId() == objId)
            setSummon(null);
        else if(_pet != null && _pet.getObjectId() == objId)
            setPet(null);
    }

    public PetInstance getPet()
    {
        return _pet;
    }

    public void setPet(PetInstance pet)
    {
        boolean petDeleted = _pet != null;
        _pet = pet;
        unsetVar("pet");

        if(pet == null)
        {
            if(petDeleted)
            {
                if(isLogoutStarted())
                {
                    if(getPetControlItem() != null)
                        setVar("pet", getPetControlItem().getObjectId());
                }
                setPetControlItem(null);
                if(_summon == null && _pet == null)
                {
                    removeAutoShot(SoulShotType.BEAST_SOULSHOT);
                    removeAutoShot(SoulShotType.BEAST_SPIRITSHOT);
                }
            }
            getAbnormalList().stop(4140);
        }
        autoShot();
    }

    public void scheduleDelete()
    {
        long time = 0L;

        if(Config.SERVICES_ENABLE_NO_CARRIER)
            time = Converter.stringToInt(getVar("noCarrier"), Config.SERVICES_NO_CARRIER_DEFAULT_TIME);

        scheduleDelete(time * 1000L);
    }

    /**
     * Удалит персонажа из мира через указанное время, если на момент истечения времени он не будет присоединен.
     * <br><br>
     * TODO: через минуту делать его неуязвимым.<br>
     * TODO: сделать привязку времени к контексту, для зон с лимитом времени оставлять в игре на все время в зоне.<br>
     * <br>
     *
     * @param time время в миллисекундах
     */
    public void scheduleDelete(long time)
    {
        if(isLogoutStarted())
            return;

        broadcastCharInfo();

        ThreadPoolManager.getInstance().schedule(() ->
        {
            if(!isConnected())
            {
                prepareToLogout1();
                prepareToLogout2();
                deleteMe();
            }
        }, time);
    }

    @Override
    protected void onDelete()
    {
        deleteCubics();
        super.onDelete();

        // Убираем фэйк в точке наблюдения
        if(_observePoint != null)
            _observePoint.deleteMe();

        //Send friendlists to friends that this player has logged off
        _friendList.notifyFriends(false);

        getBookMarkList().clear();

        _inventory.clear();
        _warehouse.clear();
        _summon = null;
        _pet = null;
        _arrowItem = null;
        _fistsWeaponItem = null;
        _chars = null;
        _enchantScroll = null;
        _lastNpc = HardReferences.emptyRef();
        _observePoint = null;
    }

    public void setTradeList(List<TradeItem> list)
    {
        _tradeList = list;
    }

    public List<TradeItem> getTradeList()
    {
        return _tradeList;
    }

    public String getSellStoreName()
    {
        return _sellStoreName;
    }

    public void setSellStoreName(String name)
    {
        _sellStoreName = Strings.stripToSingleLine(name);
    }

    public String getPackageSellStoreName()
    {
        return _packageSellStoreName;
    }

    public void setPackageSellStoreName(String name)
    {
        _packageSellStoreName = Strings.stripToSingleLine(name);
    }

    public void setSellList(boolean packageSell, List<TradeItem> list)
    {
        if(packageSell)
            _packageSellList = list;
        else
            _sellList = list;
    }

    public List<TradeItem> getSellList()
    {
        return getSellList(_privatestore == STORE_PRIVATE_SELL_PACKAGE);
    }

    public List<TradeItem> getSellList(boolean packageSell)
    {
        return packageSell ? _packageSellList : _sellList;
    }

    public String getBuyStoreName()
    {
        return _buyStoreName;
    }

    public void setBuyStoreName(String name)
    {
        _buyStoreName = Strings.stripToSingleLine(name);
    }

    public void setBuyList(List<TradeItem> list)
    {
        _buyList = list;
    }

    public List<TradeItem> getBuyList()
    {
        return _buyList;
    }

    public void setManufactureName(String name)
    {
        _manufactureName = Strings.stripToSingleLine(name);
    }

    public String getManufactureName()
    {
        return _manufactureName;
    }

    public List<ManufactureItem> getCreateList()
    {
        return _createList;
    }

    public void setCreateList(List<ManufactureItem> list)
    {
        _createList = list;
    }

    public void setPrivateStoreType(final int type)
    {
        _privatestore = type;
    }

    public boolean isInStoreMode() {
        return _privatestore != STORE_PRIVATE_NONE;
    }

    public int getPrivateStoreType()
    {
        return _privatestore;
    }

    public L2GameServerPacket getPrivateStoreMsgPacket(Player forPlayer)
    {
        switch(getPrivateStoreType())
        {
            case STORE_PRIVATE_BUY:
                return new PrivateStoreBuyMsg(this, canTalkWith(forPlayer));
            case STORE_PRIVATE_SELL:
                return new PrivateStoreMsg(this, canTalkWith(forPlayer));
            case STORE_PRIVATE_SELL_PACKAGE:
                return new ExPrivateStoreWholeMsg(this, canTalkWith(forPlayer));
            case STORE_PRIVATE_MANUFACTURE:
                return new RecipeShopMsgPacket(this, canTalkWith(forPlayer));
        }

        return null;
    }

    public void broadcastPrivateStoreInfo()
    {
        if(!isVisible() || _privatestore == STORE_PRIVATE_NONE)
            return;

        sendPacket(getPrivateStoreMsgPacket(this));
        for(Player target : World.getAroundObservers(this))
            target.sendPacket(getPrivateStoreMsgPacket(target));
    }

    /**
     * Set the _clan object, _clanId, _clanLeader Flag and title of the L2Player.<BR><BR>
     *
     * @param clan the clat to set
     */
    public void setClan(Clan clan)
    {
        if(_clan != clan && _clan != null)
            unsetVar("canWhWithdraw");

        Clan oldClan = _clan;
        if(oldClan != null && clan == null)
            for(SkillEntry skillEntry : oldClan.getAllSkills())
                removeSkill(skillEntry, false);

        _clan = clan;

        if(clan == null)
        {
            _pledgeType = Clan.SUBUNIT_NONE;
            _pledgeRank = PledgeRank.VAGABOND;
            _powerGrade = 0;
            _apprentice = 0;
            _lvlJoinedAcademy = 0;
            getInventory().validateItems();
            return;
        }

        if(!clan.isAnyMember(getObjectId()))
        {
            setClan(null);
            setTitle("");
        }
    }

    @Override
    public Clan getClan()
    {
        return _clan;
    }

    public SubUnit getSubUnit()
    {
        return _clan == null ? null : _clan.getSubUnit(_pledgeType);
    }

    public ClanHall getClanHall()
    {
        int id = _clan != null ? _clan.getHasHideout() : 0;
        return ResidenceHolder.getInstance().getResidence(ClanHall.class, id);
    }

    public Castle getCastle()
    {
        int id = _clan != null ? _clan.getCastle() : 0;
        return ResidenceHolder.getInstance().getResidence(Castle.class, id);
    }

    public Alliance getAlliance()
    {
        return _clan == null ? null : _clan.getAlliance();
    }

    public boolean isClanLeader()
    {
        return _clan != null && getObjectId() == _clan.getLeaderId();
    }

    public boolean isAllyLeader()
    {
        return getAlliance() != null && getAlliance().getLeader().getLeaderId() == getObjectId();
    }

    @Override
    public void reduceArrowCount()
    {
        if(_arrowItem != null && _arrowItem.getTemplate().isQuiver())
            return;

        sendPacket(SystemMsg.YOU_CAREFULLY_NOCK_AN_ARROW);
        if(!getInventory().destroyItemByObjectId(getInventory().getPaperdollObjectId(Inventory.PAPERDOLL_LHAND), 1L))
        {
            getInventory().setPaperdollItem(Inventory.PAPERDOLL_LHAND, null);
            _arrowItem = null;
        }
    }

    /**
     * Equip arrows needed in left hand and send a Server->Client packet ItemListPacket to the L2Player then return True.
     */
    public boolean checkAndEquipArrows()
    {
        // Check if nothing is equipped in left hand
        if(getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND) == null)
        {
            ItemInstance activeWeapon = getActiveWeaponInstance();
            if(activeWeapon != null)
            {
                if(activeWeapon.getItemType() == WeaponType.BOW)
                    _arrowItem = getInventory().findArrowForBow(activeWeapon.getTemplate());
                else if(activeWeapon.getItemType() == WeaponType.CROSSBOW || activeWeapon.getItemType() == WeaponType.TWOHANDCROSSBOW)
                    _arrowItem = getInventory().findArrowForCrossbow(activeWeapon.getTemplate());
            }

            // Equip arrows needed in left hand
            if(_arrowItem != null)
                getInventory().setPaperdollItem(Inventory.PAPERDOLL_LHAND, _arrowItem);
        }
        else
            // Get the L2ItemInstance of arrows equipped in left hand
            _arrowItem = getInventory().getPaperdollItem(Inventory.PAPERDOLL_LHAND);

        return _arrowItem != null;
    }

    public void setUptime(final long time)
    {
        _uptime = time;
    }

    public long getUptime()
    {
        return System.currentTimeMillis() - _uptime;
    }

    public boolean isInParty()
    {
        return _party != null;
    }

    public void setParty(final Party party)
    {
        _party = party;
    }

    public void joinParty(final Party party)
    {
        if(party != null)
            party.addPartyMember(this);
    }

    public void leaveParty()
    {
        if(isInParty())
            _party.removePartyMember(this, false);
    }

    public Party getParty()
    {
        return _party;
    }

    public void setLastPartyPosition(Location loc)
    {
        _lastPartyPosition = loc;
    }

    public Location getLastPartyPosition()
    {
        return _lastPartyPosition;
    }

    public boolean isGM()
    {
        return _playerAccess == null ? false : _playerAccess.IsGM;
    }

    /**
     * Нигде не используется, но может пригодиться для БД
     */
    public void setAccessLevel(final int level)
    {
        _accessLevel = level;
    }

    /**
     * Нигде не используется, но может пригодиться для БД
     */
    @Override
    public int getAccessLevel()
    {
        return _accessLevel;
    }

    public void setPlayerAccess(final PlayerAccess pa)
    {
        if(pa != null)
            _playerAccess = pa;
        else
            _playerAccess = new PlayerAccess();

        setAccessLevel(isGM() || _playerAccess.Menu ? 100 : 0);
    }

    public PlayerAccess getPlayerAccess()
    {
        return _playerAccess;
    }

    /**
     * Update Stats of the L2Player client side by sending Server->Client packet UserInfo/StatusUpdatePacket to this L2Player and CIPacket/StatusUpdatePacket to all players around (broadcast).<BR><BR>
     */
    @Override
    public void updateStats()
    {
        if(entering || isLogoutStarted())
            return;

        refreshOverloaded();
        refreshExpertisePenalty();
        super.updateStats();
        for(Servitor servitor : getServitors())
            servitor.updateStats();
    }

    @Override
    public void sendChanges()
    {
        if(entering || isLogoutStarted())
            return;
        super.sendChanges();
    }

    /**
     * Send a Server->Client StatusUpdatePacket packet with Karma to the L2Player and all L2Player to inform (broadcast).
     */
    public void updateKarma(boolean flagChanged)
    {
        sendStatusUpdate(true, true, StatusUpdatePacket.KARMA);
        if(flagChanged)
            broadcastRelation();
    }

    public boolean isOnline()
    {
        return _isOnline;
    }

    public void setIsOnline(boolean isOnline)
    {
        _isOnline = isOnline;
    }

    public void setOnlineStatus(boolean isOnline)
    {
        _isOnline = isOnline;
        updateOnlineStatus();
    }

    private void updateOnlineStatus()
    {
        Connection con = null;
        PreparedStatement statement = null;
        try
        {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("UPDATE characters SET online=?, lastAccess=? WHERE obj_id=?");
            statement.setInt(1, isOnline() ? 1 : 0);
            statement.setLong(2, System.currentTimeMillis() / 1000L);
            statement.setInt(3, getObjectId());
            statement.execute();
        }
        catch(final Exception e)
        {
            _log.error("", e);
        }
        finally
        {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public void decreaseKarma(final long val)
    {
        boolean flagChanged = _karma >= 0;
        long new_karma = _karma - val;

        if(new_karma < Integer.MIN_VALUE)
            new_karma = Integer.MIN_VALUE;

        if(_karma >= 0 && new_karma < 0 && _pvpFlag > 0)
        {
            _pvpFlag = 0;
            if(_PvPRegTask != null)
            {
                _PvPRegTask.cancel(true);
                _PvPRegTask = null;
            }
            sendStatusUpdate(true, true, StatusUpdatePacket.PVP_FLAG);
        }

        setKarma((int) new_karma);

        updateKarma(flagChanged);
    }

    public void increaseKarma(final int val)
    {
        boolean flagChanged = _karma < 0;
        long new_karma = _karma + val;
        if(new_karma > Integer.MAX_VALUE)
            new_karma = Integer.MAX_VALUE;

        setKarma((int) new_karma);
        if(_karma > 0)
            updateKarma(flagChanged);
        else
            updateKarma(false);
    }

    public static Player create(int classId, int sex, String accountName, final String name, final int hairStyle, final int hairColor, final int face)
    {
        if(classId < 0 || classId >= ClassId.VALUES.length)
            return null;

        ClassId classID = ClassId.VALUES[classId];
        if(classID.isDummy() || !classID.isOfLevel(ClassLevel.NONE))
            return null;

        PlayerTemplate template = PlayerTemplateHolder.getInstance().getPlayerTemplate(classID.getRace(), classID, Sex.VALUES[sex]);

        // Create a new L2Player with an account name
        Player player = new Player(IdFactory.getInstance().getNextId(), template, accountName);

        player.setName(name);
        player.setTitle("");
        player.setHairStyle(hairStyle);
        player.setHairColor(hairColor);
        player.setFace(face);
        player.setCreateTime(System.currentTimeMillis());

        if(Config.PC_BANG_POINTS_BY_ACCOUNT)
            player.setPcBangPoints(Integer.parseInt(AccountVariablesDAO.getInstance().select(player.getAccountName(), PC_BANG_POINTS_VAR, "0")));

        // Add the player in the characters table of the database
        if(!CharacterDAO.getInstance().insert(player))
            return null;

        int level = Config.STARTING_LVL;
        double hp = classID.getBaseHp(level);
        double mp = classID.getBaseMp(level);
        double cp = classID.getBaseCp(level);
        long exp = Experience.getExpForLevel(level);
        long sp = Config.STARTING_SP;
        boolean active = true;
        SubClassType type = SubClassType.BASE_CLASS;

        // Add the player subclass in the character_subclasses table of the database
        if(!CharacterSubclassDAO.getInstance().insert(player.getObjectId(), classId, exp, sp, hp, mp, cp, hp, mp, cp, level, active, type))
            return null;

        return player;
    }

    public static Player restore(final int objectId)
    {
        Player player = null;
        Connection con = null;
        Statement statement = null;
        Statement statement2 = null;
        PreparedStatement statement3 = null;
        ResultSet rset = null;
        ResultSet rset2 = null;
        ResultSet rset3 = null;
        try
        {
            // Retrieve the L2Player from the characters table of the database
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.createStatement();
            statement2 = con.createStatement();
            rset = statement.executeQuery("SELECT * FROM `characters` WHERE `obj_Id`=" + objectId + " LIMIT 1");
            rset2 = statement2.executeQuery("SELECT `class_id` FROM `character_subclasses` WHERE `char_obj_id`=" + objectId + " AND `type`=" + SubClassType.BASE_CLASS.ordinal() + " LIMIT 1");

            if(rset.next() && rset2.next())
            {
                final ClassId classId = ClassId.VALUES[rset2.getInt("class_id")];
                final PlayerTemplate template = PlayerTemplateHolder.getInstance().getPlayerTemplate(classId.getRace(), classId, Sex.VALUES[rset.getInt("sex")]);;

                player = new Player(objectId, template);

                player.getSubClassList().restore();

                player.restoreVariables();
                player.loadInstanceReuses();
                player.getBookMarkList().setCapacity(rset.getInt("bookmarks"));
                player.getBookMarkList().restore();
                player.setBotRating(rset.getInt("bot_rating"));
                player.getFriendList().restore();
                player.getBlockList().restore();
                player.getPremiumItemList().restore();
                player.getProductHistoryList().restore();
                player.setPostFriends(CharacterPostFriendDAO.getInstance().select(player));
                CharacterGroupReuseDAO.getInstance().select(player);

                player.setLogin(rset.getString("account_name"));
                player.setName(rset.getString("char_name"));

                player.setFace(rset.getInt("face"));
                player.setBeautyFace(rset.getInt("beautyFace"));
                player.setHairStyle(rset.getInt("hairStyle"));
                player.setBeautyHairStyle(rset.getInt("beautyHairStyle"));
                player.setHairColor(rset.getInt("hairColor"));
                player.setBeautyHairColor(rset.getInt("beautyHairColor"));
                player.setHeading(0);

                player.setKarma(rset.getInt("karma"));
                player.setPvpKills(rset.getInt("pvpkills"));
                player.setPkKills(rset.getInt("pkkills"));
                player.setLeaveClanTime(rset.getLong("leaveclan") * 1000L);
                if(player.getLeaveClanTime() > 0 && player.canJoinClan())
                    player.setLeaveClanTime(0);
                player.setDeleteClanTime(rset.getLong("deleteclan") * 1000L);
                if(player.getDeleteClanTime() > 0 && player.canCreateClan())
                    player.setDeleteClanTime(0);

                player.setNoChannel(rset.getLong("nochannel") * 1000L);
                if(player.getNoChannel() > 0 && player.getNoChannelRemained() < 0)
                    player.setNoChannel(0);

                player.setOnlineTime(rset.getLong("onlinetime") * 1000L);

                final int clanId = rset.getInt("clanid");
                if(clanId > 0)
                {
                    player.setClan(ClanTable.getInstance().getClan(clanId));
                    player.setPledgeType(rset.getInt("pledge_type"));
                    player.setPowerGrade(rset.getInt("pledge_rank"));
                    player.setLvlJoinedAcademy(rset.getInt("lvl_joined_academy"));
                    player.setApprentice(rset.getInt("apprentice"));
                }

                player.setCreateTime(rset.getLong("createtime") * 1000L);
                player.setDeleteTimer(rset.getInt("deletetime"));

                player.setTitle(rset.getString("title"));

                if(player.getVar("titlecolor") != null)
                    player.setTitleColor(Integer.decode("0x" + player.getVar("titlecolor")));

                if(player.getVar("namecolor") == null)
                    if(player.isGM())
                        player.setNameColor(Config.GM_NAME_COLOUR);
                    else if(player.getClan() != null && player.getClan().getLeaderId() == player.getObjectId())
                        player.setNameColor(Config.CLANLEADER_NAME_COLOUR);
                    else
                        player.setNameColor(Config.NORMAL_NAME_COLOUR);
                else
                    player.setNameColor(Integer.decode("0x" + player.getVar("namecolor")));

                if(Config.AUTO_LOOT_INDIVIDUAL)
                {
                    player._autoLoot = player.getVarBoolean("AutoLoot", Config.AUTO_LOOT);
                    player._autoLootOnlyAdena = player.getVarBoolean("AutoLootOnlyAdena", Config.AUTO_LOOT);
                    player.AutoLootHerbs = player.getVarBoolean("AutoLootHerbs", Config.AUTO_LOOT_HERBS);
                }

                player.setUptime(System.currentTimeMillis());
                player.setLastAccess(rset.getLong("lastAccess"));

                player.setRecomHave(rset.getInt("rec_have"));
                player.setRecomLeft(rset.getInt("rec_left"));

                if(!Config.USE_CLIENT_LANG)
                    player.setLanguage(player.getVar(Language.LANG_VAR));

                player.setKeyBindings(rset.getBytes("key_bindings"));
                if(Config.PC_BANG_POINTS_BY_ACCOUNT)
                    player.setPcBangPoints(Integer.parseInt(AccountVariablesDAO.getInstance().select(player.getAccountName(), PC_BANG_POINTS_VAR, "0")));
                else
                    player.setPcBangPoints(rset.getInt("pcBangPoints"));

                player.setFame(rset.getInt("fame"), null, false);

                player.setUsedWorldChatPoints(rset.getInt("used_world_chat_points"));

                player.setHideHeadAccessories(rset.getInt("hide_head_accessories") > 0);

                player.restoreRecipeBook();

                if(Config.ENABLE_OLYMPIAD)
                    player.setHero(Hero.getInstance().isHero(player.getObjectId()));

                player.updatePledgeRank();

                player.setXYZ(rset.getInt("x"), rset.getInt("y"), rset.getInt("z"));

                int reflection = 0;

                long jailExpireTime = player.getVarExpireTime(JAILED_VAR);
                if(jailExpireTime > System.currentTimeMillis())
                {
                    reflection = ReflectionManager.JAIL.getId();
                    if(!player.isInZone("[gm_prison]"))
                        player.setLoc(Location.findPointToStay(player, AdminFunctions.JAIL_SPAWN, 50, 200));
                    player.setIsInJail(true);
                    player.startUnjailTask(player, (int) (jailExpireTime - System.currentTimeMillis() / 60000));
                }
                else
                {
                    //Если игрок вышел во время прыжка, то возвращаем его в стабильную точку (стартовую).
                    String jumpSafeLoc = player.getVar("@safe_jump_loc");
                    if(jumpSafeLoc != null)
                    {
                        player.setLoc(Location.parseLoc(jumpSafeLoc));
                        player.unsetVar("@safe_jump_loc");
                    }

                    String ref = player.getVar("reflection");
                    if(ref != null)
                    {
                        reflection = Integer.parseInt(ref);
                        if(reflection != ReflectionManager.PARNASSUS.getId() && reflection != ReflectionManager.GIRAN_HARBOR.getId()) // не портаем назад из ГХ, парнаса
                        {
                            String back = player.getVar("backCoords");
                            if(back != null)
                            {
                                player.setLoc(Location.parseLoc(back));
                                player.unsetVar("backCoords");
                            }
                            reflection = 0;
                        }
                    }
                }

                player.setReflection(reflection);

                EventHolder.getInstance().findEvent(player);

                //TODO [G1ta0] запускать на входе
                Quest.restoreQuestStates(player);

                player.getInventory().restore();

                player.setActiveSubClass(player.getActiveClassId(), false, true);

                player.getAttendanceRewards().restore();

                player.restoreSummons();

                try
                {
                    String var = player.getVar("ExpandInventory");
                    if(var != null)
                        player.setExpandInventory(Integer.parseInt(var));
                }
                catch(Exception e)
                {
                    _log.error("", e);
                }

                try
                {
                    String var = player.getVar("ExpandWarehouse");
                    if(var != null)
                        player.setExpandWarehouse(Integer.parseInt(var));
                }
                catch(Exception e)
                {
                    _log.error("", e);
                }

                try
                {
                    String var = player.getVar(NO_ANIMATION_OF_CAST_VAR);
                    if(var != null)
                        player.setNotShowBuffAnim(Boolean.parseBoolean(var));
                }
                catch(Exception e)
                {
                    _log.error("", e);
                }

                try
                {
                    String var = player.getVar(NO_TRADERS_VAR);
                    if(var != null)
                        player.setNotShowTraders(Boolean.parseBoolean(var));
                }
                catch(Exception e)
                {
                    _log.error("", e);
                }

                try
                {
                    String var = player.getVar("pet");
                    if(var != null)
                        player.setPetControlItem(Integer.parseInt(var));
                }
                catch(Exception e)
                {
                    _log.error("", e);
                }

                statement3 = con.prepareStatement("SELECT obj_Id, char_name FROM characters WHERE account_name=? AND obj_Id!=?");
                statement3.setString(1, player._login);
                statement3.setInt(2, objectId);
                rset3 = statement3.executeQuery();
                while(rset3.next())
                {
                    final Integer charId = rset3.getInt("obj_Id");
                    final String charName = rset3.getString("char_name");
                    player._chars.put(charId, charName);
                }

                DbUtils.close(statement3, rset3);

                //if(!player.isGM())
                {
                    List<Zone> zones = CollectionUtils.pooledList();

                    World.getZones(zones, player.getLoc(), player.getReflection());

                    if(!zones.isEmpty())
                        for(Zone zone : zones)
                            if(zone.getType() == ZoneType.no_restart)
                            {
                                if(System.currentTimeMillis() / 1000L - player.getLastAccess() > zone.getRestartTime())
                                {
                                    player.sendMessage(new CustomMessage("org.l2j.gameserver.network.l2.c2s.EnterWorld.TeleportedReasonNoRestart"));
                                    player.setLoc(TeleportUtils.getRestartPoint(player, RestartType.TO_VILLAGE).getLoc());
                                }
                            }
                            else if(zone.getType() == ZoneType.SIEGE)
                            {
                                SiegeEvent<?, ?> siegeEvent = player.getEvent(SiegeEvent.class);
                                if(siegeEvent != null)
                                    player.setLoc(siegeEvent.getEnterLoc(player, zone));
                                else
                                {
                                    Residence r = ResidenceHolder.getInstance().getResidence(zone.getParams().getInteger("residence"));
                                    player.setLoc(r.getNotOwnerRestartPoint(player));
                                }
                            }

                    CollectionUtils.recycle(zones);
                }

                player.getMacroses().restore();

                //FIXME [VISTALL] нужно ли?
                player.refreshExpertisePenalty();
                player.refreshOverloaded();

                player.getWarehouse().restore();
                player.getFreight().restore();

                player.restorePrivateStore();

                player.updateKetraVarka();
                player.updateRam();
                player.checkDailyCounters();
                player.checkWeeklyCounters();
            }
        }
        catch(final Exception e)
        {
            _log.error("Could not restore char data!", e);
        }
        finally
        {
            DbUtils.closeQuietly(statement2, rset2);
            DbUtils.closeQuietly(statement3, rset3);
            DbUtils.closeQuietly(con, statement, rset);
        }
        return player;
    }

    /**
     * Update L2Player stats in the characters table of the database.
     */
    public void store(boolean fast)
    {
        if(!_storeLock.tryLock())
            return;

        try
        {
            Connection con = null;
            PreparedStatement statement = null;
            try
            {
                con = DatabaseFactory.getInstance().getConnection();
                statement = con.prepareStatement(//
                        "UPDATE characters SET face=?,beautyFace=?,hairStyle=?,beautyHairStyle=?,hairColor=?,beautyHairColor=?,sex=?,x=?,y=?,z=?" + //
                                ",karma=?,pvpkills=?,pkkills=?,rec_have=?,rec_left=?,clanid=?,deletetime=?," + //
                                "title=?,accesslevel=?,online=?,leaveclan=?,deleteclan=?,nochannel=?," + //
                                "onlinetime=?,pledge_type=?,pledge_rank=?,lvl_joined_academy=?,apprentice=?,key_bindings=?,pcBangPoints=?,char_name=?,fame=?,bookmarks=?,bot_rating=?,used_world_chat_points=?,hide_head_accessories=? WHERE obj_Id=? LIMIT 1");
                statement.setInt(1, getFace());
                statement.setInt(2, getBeautyFace());
                statement.setInt(3, getHairStyle());
                statement.setInt(4, getBeautyHairStyle());
                statement.setInt(5, getHairColor());
                statement.setInt(6, getBeautyHairColor());
                statement.setInt(7, getSex().ordinal());
                if(_stablePoint == null) // если игрок находится в точке в которой его сохранять не стоит (например на виверне) то сохраняются последние координаты
                {
                    statement.setInt(8, getX());
                    statement.setInt(9, getY());
                    statement.setInt(10, getZ());
                }
                else
                {
                    statement.setInt(8, _stablePoint.x);
                    statement.setInt(9, _stablePoint.y);
                    statement.setInt(10, _stablePoint.z);
                }
                statement.setInt(11, getKarma());
                statement.setInt(12, getPvpKills());
                statement.setInt(13, getPkKills());
                statement.setInt(14, getRecomHave());
                statement.setInt(15, getRecomLeft());
                statement.setInt(16, getClanId());
                statement.setInt(17, getDeleteTimer());
                statement.setString(18, _title);
                statement.setInt(19, _accessLevel);
                statement.setInt(20, isOnline() ? 1 : 0);
                statement.setLong(21, getLeaveClanTime() / 1000L);
                statement.setLong(22, getDeleteClanTime() / 1000L);
                statement.setLong(23, _NoChannel > 0 ? getNoChannelRemained() / 1000 : _NoChannel);
                statement.setInt(24, getOnlineTime());
                statement.setInt(25, getPledgeType());
                statement.setInt(26, getPowerGrade());
                statement.setInt(27, getLvlJoinedAcademy());
                statement.setInt(28, getApprentice());
                statement.setBytes(29, getKeyBindings());
                statement.setInt(30, Config.PC_BANG_POINTS_BY_ACCOUNT ? 0 : getPcBangPoints());
                statement.setString(31, getName());
                statement.setInt(32, getFame());
                statement.setInt(33, getBookMarkList().getCapacity());
                statement.setInt(34, getBotRating());
                statement.setInt(35, getUsedWorldChatPoints());
                statement.setInt(36, hideHeadAccessories() ? 1 : 0);
                statement.setInt(37, getObjectId());

                statement.executeUpdate();
                GameStats.increaseUpdatePlayerBase();

                if(!fast)
                {
                    EffectsDAO.getInstance().insert(this);
                    CharacterGroupReuseDAO.getInstance().insert(this);
                    storeDisableSkills();
                }

                storeCharSubClasses();
                getBookMarkList().store();

                getDailyMissionList().store();
                if(Config.PC_BANG_POINTS_BY_ACCOUNT)
                    AccountVariablesDAO.getInstance().insert(getAccountName(), PC_BANG_POINTS_VAR, String.valueOf(getPcBangPoints()));
            }
            catch(Exception e)
            {
                _log.error("Could not store char data: " + this + "!", e);
            }
            finally
            {
                DbUtils.closeQuietly(con, statement);
            }
        }
        finally
        {
            _storeLock.unlock();
        }
    }

    /**
     * Add a skill to the L2Player _skills and its Func objects to the calculator set of the L2Player and save update in the character_skills table of the database.
     *
     * @return The L2Skill replaced or null if just added a new L2Skill
     */
    public SkillEntry addSkill(SkillEntry newSkillEntry, final boolean store)
    {
        if(newSkillEntry == null)
            return null;

        // Add a skill to the L2Player _skills and its Func objects to the calculator set of the L2Player
        SkillEntry oldSkillEntry = addSkill(newSkillEntry);

        if(newSkillEntry.equals(oldSkillEntry))
            return oldSkillEntry;

        // Add or update a L2Player skill in the character_skills table of the database
        if(store)
            storeSkill(newSkillEntry);

        return oldSkillEntry;
    }

    public SkillEntry removeSkill(SkillEntry skillEntry, boolean fromDB)
    {
        if(skillEntry == null)
            return null;
        return removeSkill(skillEntry.getId(), fromDB);
    }

    /**
     * Remove a skill from the L2Character and its Func objects from calculator set of the L2Character and save update in the character_skills table of the database.
     *
     * @return The L2Skill removed
     */
    public SkillEntry removeSkill(int id, boolean fromDB)
    {
        // Remove a skill from the L2Character and its Func objects from calculator set of the L2Character
        SkillEntry oldSkillEntry = removeSkillById(id);

        if(!fromDB)
            return oldSkillEntry;

        if(oldSkillEntry != null)
        {
            Connection con = null;
            PreparedStatement statement = null;
            try
            {
                // Remove or update a L2Player skill from the character_skills table of the database
                con = DatabaseFactory.getInstance().getConnection();
                statement = con.prepareStatement("DELETE FROM character_skills WHERE skill_id=? AND char_obj_id=? AND (class_index=? OR class_index=-1 OR class_index=-2)");
                statement.setInt(1, oldSkillEntry.getId());
                statement.setInt(2, getObjectId());
                statement.setInt(3, getActiveClassId());
                statement.execute();
            }
            catch(final Exception e)
            {
                _log.error("Could not delete skill!", e);
            }
            finally
            {
                DbUtils.closeQuietly(con, statement);
            }
        }

        return oldSkillEntry;
    }

    /**
     * Add or update a L2Player skill in the character_skills table of the database.
     */
    private void storeSkill(final SkillEntry newSkillEntry)
    {
        if(newSkillEntry == null) // вообще-то невозможно
        {
            _log.warn("could not store new skill. its NULL");
            return;
        }

        Connection con = null;
        PreparedStatement statement = null;
        try
        {
            con = DatabaseFactory.getInstance().getConnection();

            statement = con.prepareStatement("REPLACE INTO character_skills (char_obj_id,skill_id,skill_level,class_index) values(?,?,?,?)");
            statement.setInt(1, getObjectId());
            statement.setInt(2, newSkillEntry.getId());
            statement.setInt(3, newSkillEntry.getLevel());
            // Скиллы сертификации доступны на всех саб-классах.
            statement.setInt(4, getActiveClassId());

            statement.execute();
        }
        catch(final Exception e)
        {
            _log.error("Error could not store skills!", e);
        }
        finally
        {
            DbUtils.closeQuietly(con, statement);
        }
    }

    private void restoreSkills()
    {
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;
        try
        {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("SELECT skill_id,skill_level FROM character_skills WHERE char_obj_id=? AND (class_index=? OR class_index=-1 OR class_index=-2)");
            statement.setInt(1, getObjectId());
            statement.setInt(2, getActiveClassId());
            rset = statement.executeQuery();

            while(rset.next())
            {
                final SkillEntry skillEntry = SkillHolder.getInstance().getSkillEntry(rset.getInt("skill_id"), rset.getInt("skill_level"));
                if(skillEntry == null)
                    continue;

                if(!isGM())
                {
                    Skill skill = skillEntry.getTemplate();
                    if(!SkillAcquireHolder.getInstance().isSkillPossible(this, skill))
                    {
                        removeSkill(skillEntry, true);
                        //removeSkillFromShortCut(skill.getId());
                        //TODO audit
                        continue;
                    }
                }
                addSkill(skillEntry);
            }

            // Restore Hero skills at main class only
            checkHeroSkills();

            // Restore clan skills
            if(_clan != null)
                _clan.addSkillsQuietly(this);

            if(Config.UNSTUCK_SKILL && getSkillLevel(1050) < 0)
                addSkill(SkillHolder.getInstance().getSkillEntry(2099, 1));

            if(isGM())
                giveGMSkills();
        }
        catch(final Exception e)
        {
            _log.warn("Could not restore skills for player objId: " + getObjectId());
            _log.error("", e);
        }
        finally
        {
            DbUtils.closeQuietly(con, statement, rset);
        }
    }

    public void storeDisableSkills()
    {
        Connection con = null;
        Statement statement = null;
        try
        {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.createStatement();
            statement.executeUpdate("DELETE FROM character_skills_save WHERE char_obj_id = " + getObjectId() + " AND class_index=" + getActiveClassId() + " AND `end_time` < " + System.currentTimeMillis());

            if(_skillReuses.isEmpty())
                return;

            SqlBatch b = new SqlBatch("REPLACE INTO `character_skills_save` (`char_obj_id`,`skill_id`,`skill_level`,`class_index`,`end_time`,`reuse_delay_org`) VALUES");
            synchronized (_skillReuses)
            {
                StringBuilder sb;
                for(TimeStamp timeStamp : _skillReuses.values())
                {
                    if(timeStamp.hasNotPassed())
                    {
                        sb = new StringBuilder("(");
                        sb.append(getObjectId()).append(",");
                        sb.append(timeStamp.getId()).append(",");
                        sb.append(timeStamp.getLevel()).append(",");
                        sb.append(getActiveClassId()).append(",");
                        sb.append(timeStamp.getEndTime()).append(",");
                        sb.append(timeStamp.getReuseBasic()).append(")");
                        b.write(sb.toString());
                    }
                }
            }
            if(!b.isEmpty())
                statement.executeUpdate(b.close());
        }
        catch(final Exception e)
        {
            _log.warn("Could not store disable skills data: " + e);
        }
        finally
        {
            DbUtils.closeQuietly(con, statement);
        }
    }

    public void restoreDisableSkills()
    {
        _skillReuses.clear();

        Connection con = null;
        Statement statement = null;
        ResultSet rset = null;
        try
        {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.createStatement();
            rset = statement.executeQuery("SELECT skill_id,skill_level,end_time,reuse_delay_org FROM character_skills_save WHERE char_obj_id=" + getObjectId() + " AND class_index=" + getActiveClassId());
            while(rset.next())
            {
                int skillId = rset.getInt("skill_id");
                int skillLevel = rset.getInt("skill_level");
                long endTime = rset.getLong("end_time");
                long rDelayOrg = rset.getLong("reuse_delay_org");
                long curTime = System.currentTimeMillis();

                Skill skill = SkillHolder.getInstance().getSkill(skillId, skillLevel);

                if(skill != null && endTime - curTime > 500)
                    _skillReuses.put(skill.getReuseHash(), new TimeStamp(skill, endTime, rDelayOrg));
            }
            DbUtils.close(statement);

            statement = con.createStatement();
            statement.executeUpdate("DELETE FROM character_skills_save WHERE char_obj_id = " + getObjectId() + " AND class_index=" + getActiveClassId() + " AND `end_time` < " + System.currentTimeMillis());
        }
        catch(Exception e)
        {
            _log.error("Could not restore active skills data!", e);
        }
        finally
        {
            DbUtils.closeQuietly(con, statement, rset);
        }
    }

    @Override
    public boolean consumeItem(int itemConsumeId, long itemCount, boolean sendMessage)
    {
        return ItemFunctions.deleteItem(this, itemConsumeId, itemCount, sendMessage);
    }

    @Override
    public boolean consumeItemMp(int itemId, int mp)
    {
        for(ItemInstance item : getInventory().getPaperdollItems())
            if(item != null && item.getItemId() == itemId)
            {
                final int newMp = item.getLifeTime() - mp;
                if(newMp >= 0)
                {
                    item.setLifeTime(newMp);
                    sendPacket(new InventoryUpdatePacket().addModifiedItem(this, item));
                    return true;
                }
                break;
            }
        return false;
    }

    /**
     * @return True if the L2Player is a Mage.<BR><BR>
     */
    @Override
    public boolean isMageClass()
    {
        return getClassId().isMage();
    }

    /**
     * Проверяет, можно ли приземлиться в этой зоне.
     *
     * @return можно ли приземлится
     */
    public boolean checkLandingState()
    {
        if(isInZone(ZoneType.no_landing))
            return false;

        SiegeEvent<?, ?> siege = getEvent(SiegeEvent.class);
        if(siege != null)
        {
            Residence unit = siege.getResidence();
            if(unit != null && getClan() != null && isClanLeader() && getClan().getCastle() == unit.getId())
                return true;
            return false;
        }

        return true;
    }

    public void setMount(int controlItemObjId, int npcId, int level, int currentFeed)
    {
        Mount mount = Mount.create(this, controlItemObjId, npcId, level, currentFeed);
        if(mount != null)
            setMount(mount);
    }

    public void setMount(Mount mount)
    {
        if(_mount == mount)
            return;

        Mount oldMount = _mount;
        _mount = null;
        if(oldMount != null) // Dismount
            oldMount.onUnride();

        if(mount != null)
        {
            _mount = mount;
            _mount.onRide();
        }
    }

    public boolean isMounted()
    {
        return _mount != null;
    }

    public Mount getMount()
    {
        return _mount;
    }

    public int getMountControlItemObjId()
    {
        return isMounted() ? _mount.getControlItemObjId() : 0;
    }

    public int getMountNpcId()
    {
        return isMounted() ? _mount.getNpcId() : 0;
    }

    public int getMountLevel()
    {
        return isMounted() ? _mount.getLevel() : 0;
    }

    public int getMountCurrentFeed()
    {
        return isMounted() ? _mount.getCurrentFeed() : 0;
    }

    public void unEquipWeapon()
    {
        ItemInstance wpn = getSecondaryWeaponInstance();
        if(wpn != null)
        {
            sendDisarmMessage(wpn);
            getInventory().unEquipItem(wpn);
        }

        wpn = getActiveWeaponInstance();
        if(wpn != null)
        {
            sendDisarmMessage(wpn);
            getInventory().unEquipItem(wpn);
        }

        abortAttack(true, true);
        abortCast(true, true);
    }

    public void sendDisarmMessage(ItemInstance wpn)
    {
        if(wpn.getEnchantLevel() > 0)
        {
            SystemMessage sm = new SystemMessage(SystemMessage.EQUIPMENT_OF__S1_S2_HAS_BEEN_REMOVED);
            sm.addNumber(wpn.getEnchantLevel());
            sm.addItemName(wpn.getItemId());
            sendPacket(sm);
        }
        else
        {
            SystemMessage sm = new SystemMessage(SystemMessage.S1__HAS_BEEN_DISARMED);
            sm.addItemName(wpn.getItemId());
            sendPacket(sm);
        }
    }

    /**
     * Устанавливает тип используемого склада.
     *
     * @param type тип склада:<BR>
     *             <ul>
     *             <li>WarehouseType.PRIVATE
     *             <li>WarehouseType.CLAN
     *             <li>WarehouseType.CASTLE
     *             </ul>
     */
    public void setUsingWarehouseType(final WarehouseType type)
    {
        _usingWHType = type;
    }

    /**
     * Р’РѕР·РІСЂР°С‰Р°РµС‚ С‚РёРї РёСЃРїРѕР»СЊР·СѓРµРјРѕРіРѕ СЃРєР»Р°РґР°.
     *
     * @return null РёР»Рё С‚РёРї СЃРєР»Р°РґР°:<br>
     *         <ul>
     *         <li>WarehouseType.PRIVATE
     *         <li>WarehouseType.CLAN
     *         <li>WarehouseType.CASTLE
     *         </ul>
     */
    public WarehouseType getUsingWarehouseType()
    {
        return _usingWHType;
    }

    public Collection<Cubic> getCubics()
    {
        return _cubics == null ? Collections.<Cubic>emptyList() : _cubics.values();
    }

    @Override
    public void deleteCubics()
    {
        for(Cubic cubic : getCubics())
            cubic.delete();
    }

    public void addCubic(Cubic cubic)
    {
        if(_cubics == null)
            _cubics = new CHashIntObjectMap<Cubic>(3);
        Cubic oldCubic = _cubics.get(cubic.getSlot());
        if(oldCubic != null)
            oldCubic.delete();

        _cubics.put(cubic.getSlot(), cubic);

        sendPacket(new ExUserInfoCubic(this));
    }

    public void removeCubic(int slot)
    {
        if(_cubics != null)
            _cubics.remove(slot);

        sendPacket(new ExUserInfoCubic(this));
    }

    public Cubic getCubic(int slot)
    {
        return _cubics == null ? null : _cubics.get(slot);
    }

    @Override
    public String toString()
    {
        return getName() + "[" + getObjectId() + "]";
    }

    /**
     * @return the modifier corresponding to the Enchant Effect of the Active Weapon (Min : 127).<BR><BR>
     */
    @Override
    public int getEnchantEffect()
    {
        final ItemInstance wpn = getActiveWeaponInstance();

        if(wpn == null)
            return 0;

        return Math.min(127, wpn.getFixedEnchantLevel(this));
    }

    /**
     * Set the _lastFolkNpc of the L2Player corresponding to the last Folk witch one the player talked.<BR><BR>
     */
    public void setLastNpc(final NpcInstance npc)
    {
        if(npc == null)
            _lastNpc = HardReferences.emptyRef();
        else
            _lastNpc = npc.getRef();
    }

    /**
     * @return the _lastFolkNpc of the L2Player corresponding to the last Folk witch one the player talked.<BR><BR>
     */
    public NpcInstance getLastNpc()
    {
        return _lastNpc.get();
    }

    public void setMultisell(MultiSellListContainer multisell)
    {
        _multisell = multisell;
    }

    public MultiSellListContainer getMultisell()
    {
        return _multisell;
    }

    @Override
    public boolean unChargeShots(boolean spirit)
    {
        ItemInstance weapon = getActiveWeaponInstance();
        if(weapon == null)
            return false;

        if(spirit)
            weapon.setChargedSpiritshotPower(0);
        else
            weapon.setChargedSoulshotPower(0);

        autoShot();
        return true;
    }

    public boolean unChargeFishShot()
    {
        ItemInstance weapon = getActiveWeaponInstance();
        if(weapon == null)
            return false;

        weapon.setChargedFishshotPower(0);

        autoShot();
        return true;
    }

    public void autoShot()
    {
        for(IntObjectPair<SoulShotType> entry : _activeAutoShots.entrySet())
        {
            int shotId = entry.getKey();

            ItemInstance item = getInventory().getItemByItemId(shotId);
            if(item == null)
            {
                removeAutoShot(shotId, false, entry.getValue());
                continue;
            }
            useItem(item, false, false);
        }
    }

    @Override
    public double getChargedSoulshotPower()
    {
        ItemInstance weapon = getActiveWeaponInstance();
        if(weapon != null && weapon.getChargedSoulshotPower() > 0)
            return calcStat(Stats.SOULSHOT_POWER, weapon.getChargedSoulshotPower());
        return 0;
    }

    @Override
    public void setChargedSoulshotPower(double val)
    {
        ItemInstance weapon = getActiveWeaponInstance();
        if(weapon != null)
            weapon.setChargedSoulshotPower(val);
    }

    @Override
    public double getChargedSpiritshotPower()
    {
        ItemInstance weapon = getActiveWeaponInstance();
        if(weapon != null && weapon.getChargedSpiritshotPower() > 0)
            return calcStat(Stats.SPIRITSHOT_POWER, weapon.getChargedSpiritshotPower());
        return 0;
    }

    @Override
    public void setChargedSpiritshotPower(double val)
    {
        ItemInstance weapon = getActiveWeaponInstance();
        if(weapon != null)
            weapon.setChargedSpiritshotPower(val);
    }

    public double getChargedFishshotPower()
    {
        ItemInstance weapon = getActiveWeaponInstance();
        if(weapon != null)
            return weapon.getChargedFishshotPower();
        return 0;
    }

    public void setChargedFishshotPower(double val)
    {
        ItemInstance weapon = getActiveWeaponInstance();
        if(weapon != null)
            weapon.setChargedFishshotPower(val);
    }

    public boolean addAutoShot(int itemId, boolean sendMessage, SoulShotType type)
    {
        if(Config.EX_USE_AUTO_SOUL_SHOT)
        {
            for(IntObjectPair<SoulShotType> entry : _activeAutoShots.entrySet())
            {
                if(entry.getValue() == type)
                    _activeAutoShots.remove(entry.getKey());
            }
            if(type == SoulShotType.SOULSHOT || type == SoulShotType.SPIRITSHOT)
            {
                WeaponTemplate weaponTemplate = getActiveWeaponTemplate();
                if(weaponTemplate == null)
                    return false;

                ItemTemplate shotTemplate = ItemHolder.getInstance().getTemplate(itemId);
                if(shotTemplate == null)
                    return false;

                if(shotTemplate.getGrade().extGrade() != weaponTemplate.getGrade().extGrade())
                    return false;
            }
            else if((type == SoulShotType.BEAST_SOULSHOT || type == SoulShotType.BEAST_SPIRITSHOT) && getServitorsCount() == 0)
                return false;
        }

        if(_activeAutoShots.put(itemId, type) != type)
        {
            if(!Config.EX_USE_AUTO_SOUL_SHOT)
                sendPacket(new ExAutoSoulShot(itemId, 1, type));

            if(sendMessage)
                sendPacket(new SystemMessagePacket(SystemMsg.THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_ACTIVATED).addItemName(itemId));

            return true;
        }
        return false;
    }

    public boolean manuallyAddAutoShot(int itemId, SoulShotType type, boolean save)
    {
        if(addAutoShot(itemId, true, type))
        {
            if(Config.EX_USE_AUTO_SOUL_SHOT)
            {
                if(save)
                    setVar(ACTIVE_SHOT_ID_VAR + "_" + type.ordinal(), itemId);
                else
                    unsetVar(ACTIVE_SHOT_ID_VAR + "_" + type.ordinal());
            }
            return true;
        }
        return false;
    }

    public void sendActiveAutoShots()
    {
        if(Config.EX_USE_AUTO_SOUL_SHOT)
            return;

        for(IntObjectPair<SoulShotType> entry : _activeAutoShots.entrySet())
            sendPacket(new ExAutoSoulShot(entry.getKey(), 1, entry.getValue()));
    }

    public void initActiveAutoShots()
    {
        if(!Config.EX_USE_AUTO_SOUL_SHOT)
            return;

        for(SoulShotType type : SoulShotType.VALUES)
        {
            if(!initSavedActiveShot(type))
                sendPacket(new ExAutoSoulShot(0, 1, type));
        }
    }

    public boolean initSavedActiveShot(SoulShotType type)
    {
        if(!Config.EX_USE_AUTO_SOUL_SHOT)
            return false;

        int shotId = getVarInt(ACTIVE_SHOT_ID_VAR + "_" + type.ordinal(), 0);
        if(shotId > 0)
        {
            ItemInstance item = getInventory().getItemByItemId(shotId);
            if(item != null)
            {
                IItemHandler handler = item.getTemplate().getHandler();
                if(handler != null && handler.isAutoUse() && addAutoShot(shotId, true, type))
                {
                    sendPacket(new ExAutoSoulShot(shotId, 3, type));
                    ItemFunctions.useItem(this, item, false, false);
                    return true;
                }
            }
        }
        else if(shotId == -1)
        {
            sendPacket(new ExAutoSoulShot(0, 2, type));
            return true;
        }
        return false;
    }

    public void removeAutoShots(boolean uncharge)
    {
        if(Config.EX_USE_AUTO_SOUL_SHOT)
            return;

        for(IntObjectPair<SoulShotType> entry : _activeAutoShots.entrySet())
            removeAutoShot(entry.getKey(), false, entry.getValue());

        if(uncharge)
        {
            ItemInstance weapon = getActiveWeaponInstance();
            if(weapon != null)
            {
                weapon.setChargedSoulshotPower(0);
                weapon.setChargedSpiritshotPower(0);
                weapon.setChargedFishshotPower(0);
            }
        }
    }

    public boolean removeAutoShot(int itemId, boolean sendMessage, SoulShotType type)
    {
        if(_activeAutoShots.remove(itemId) != null)
        {
            if(!Config.EX_USE_AUTO_SOUL_SHOT)
                sendPacket(new ExAutoSoulShot(itemId, 0, type));

            if(sendMessage)
                sendPacket(new SystemMessagePacket(SystemMsg.THE_AUTOMATIC_USE_OF_S1_HAS_BEEN_DEACTIVATED).addItemName(itemId));

            return true;
        }
        return false;
    }

    public boolean manuallyRemoveAutoShot(int itemId, SoulShotType type, boolean save)
    {
        if(removeAutoShot(itemId, true, type))
        {
            if(Config.EX_USE_AUTO_SOUL_SHOT)
            {
                if(save)
                    setVar(ACTIVE_SHOT_ID_VAR + "_" + type.ordinal(), -1);
                else
                    unsetVar(ACTIVE_SHOT_ID_VAR + "_" + type.ordinal());
            }
            return true;
        }
        return false;
    }

    public void removeAutoShot(SoulShotType type)
    {
        if(!Config.EX_USE_AUTO_SOUL_SHOT)
            return;

        for(IntObjectPair<SoulShotType> entry : _activeAutoShots.entrySet())
        {
            if(entry.getValue() == type)
            {
                removeAutoShot(entry.getKey(), false, entry.getValue());
                sendPacket(new ExAutoSoulShot(entry.getKey(), 1, entry.getValue()));
            }
        }
    }

    public boolean isAutoShot(int itemId)
    {
        return _activeAutoShots.containsKey(itemId);
    }

    public boolean isAutoShot(SoulShotType type)
    {
        return _activeAutoShots.containsValue( type);
    }

    @Override
    public boolean isInvisible(GameObject observer)
    {
        if(observer != null)
        {
            if(isMyServitor(observer.getObjectId()))
                return false;

            if(observer.isPlayer())
            {
                Player observPlayer = (Player) observer;
                if(isInSameParty(observPlayer))
                    return false;
            }
        }
        return super.isInvisible(observer) || isGMInvisible();
    }

    @Override
    public boolean startInvisible(Object owner, boolean withServitors)
    {
        if(super.startInvisible(owner, withServitors))
        {
            sendUserInfo(true);
            return true;
        }
        return false;
    }

    @Override
    public boolean stopInvisible(Object owner, boolean withServitors)
    {
        if(super.stopInvisible(owner, withServitors))
        {
            sendUserInfo(true);
            return true;
        }
        return false;
    }

    public boolean isGMInvisible()
    {
        return getPlayerAccess().GodMode && _gmInvisible.get();
    }

    public boolean setGMInvisible(boolean value)
    {
        if(value)
            return _gmInvisible.getAndSet(true);
        return _gmInvisible.setAndGet(false);
    }

    @Override
    public boolean isUndying()
    {
        return super.isUndying() || isGMUndying();
    }

    public boolean isGMUndying()
    {
        return getPlayerAccess().GodMode && _gmUndying.get();
    }

    public boolean setGMUndying(boolean value)
    {
        if(value)
            return _gmUndying.getAndSet(true);
        return _gmUndying.setAndGet(false);
    }

    public int getClanPrivileges()
    {
        if(_clan == null)
            return 0;
        if(isClanLeader())
            return Clan.CP_ALL;
        if(_powerGrade < 1 || _powerGrade > 9)
            return 0;
        RankPrivs privs = _clan.getRankPrivs(_powerGrade);
        if(privs != null)
            return privs.getPrivs();
        return 0;
    }

    public void teleToClosestTown()
    {
        TeleportPoint teleportPoint = TeleportUtils.getRestartPoint(this, RestartType.TO_VILLAGE);
        teleToLocation(teleportPoint.getLoc(), teleportPoint.getReflection());
    }

    public void teleToCastle()
    {
        TeleportPoint teleportPoint = TeleportUtils.getRestartPoint(this, RestartType.TO_CASTLE);
        teleToLocation(teleportPoint.getLoc(), teleportPoint.getReflection());
    }

    public void teleToClanhall()
    {
        TeleportPoint teleportPoint = TeleportUtils.getRestartPoint(this, RestartType.TO_CLANHALL);
        teleToLocation(teleportPoint.getLoc(), teleportPoint.getReflection());
    }

    @Override
    public void sendMessage(CustomMessage message)
    {
        sendPacket(message);
    }

    public void teleToLocation(Location loc, boolean replace)
    {
        _isInReplaceTeleport = replace;

        teleToLocation(loc);

        _isInReplaceTeleport = false;
    }

    @Override
    public boolean onTeleported()
    {
        if(!super.onTeleported())
            return false;

        if(isFakeDeath())
            breakFakeDeath();

        if(isInBoat())
            setLoc(getBoat().getLoc());

        // 15 секунд после телепорта на персонажа не агрятся мобы
        setNonAggroTime(System.currentTimeMillis() + Config.NONAGGRO_TIME_ONTELEPORT);
        setNonPvpTime(System.currentTimeMillis() + Config.NONPVP_TIME_ONTELEPORT);

        spawnMe();

        setLastClientPosition(getLoc());
        setLastServerPosition(getLoc());

        if(isPendingRevive())
            doRevive();

        sendActionFailed();

        getAI().notifyEvent(CtrlEvent.EVT_TELEPORTED);

        if(isLockedTarget() && getTarget() != null)
            sendPacket(new MyTargetSelectedPacket(this, getTarget()));

        sendUserInfo(true);

        if(!_isInReplaceTeleport)
        {
            for(Servitor servitor : getServitors())
                servitor.teleportToOwner();
        }

        getListeners().onTeleported();

        for(ListenerHook hook : getListenerHooks(ListenerHookType.PLAYER_TELEPORT))
            hook.onPlayerTeleport(this, getReflectionId());

        for(ListenerHook hook : ListenerHook.getGlobalListenerHooks(ListenerHookType.PLAYER_TELEPORT))
            hook.onPlayerTeleport(this, getReflectionId());

        return true;
    }

    public boolean enterObserverMode(Location loc)
    {
        WorldRegion observerRegion = World.getRegion(loc);
        if(observerRegion == null)
            return false;
        if(!_observerMode.compareAndSet(OBSERVER_NONE, OBSERVER_STARTING))
            return false;

        setTarget(null);
        stopMove();
        sitDown(null);
        setFlying(true);

        // Очищаем все видимые обьекты
        World.removeObjectsFromPlayer(this);

        _observePoint = new ObservePoint(this);
        _observePoint.setLoc(loc);
        _observePoint.getFlags().getImmobilized().start();

        // Отображаем надпись над головой
        broadcastCharInfoImpl();

        // Переходим в режим обсервинга
        sendPacket(new ObserverStartPacket(loc));

        return true;
    }

    public boolean enterArenaObserverMode(ObservableArena arena)
    {
        Location enterPoint = arena.getObserverEnterPoint(this);
        WorldRegion observerRegion = World.getRegion(enterPoint);
        if(observerRegion == null)
            return false;

        if(!_observerMode.compareAndSet(isInArenaObserverMode() ? 3 : 0, 1))
            return false;

        sendPacket(new TeleportToLocationPacket(this, enterPoint));
        setTarget(null);
        stopMove();

        World.removeObjectsFromPlayer(this);

        if(_observableArena != null)
        {
            _observableArena.removeObserver(_observePoint);
            _observableArena.onChangeObserverArena(this);
            _observePoint.decayMe();
        }
        else
        {
            broadcastCharInfoImpl();
            arena.onEnterObserverArena(this);
            _observePoint = new ObservePoint(this);
        }

        _observePoint.setLoc(enterPoint);
        _observePoint.setReflection(arena.getReflection());

        _observableArena = arena;

        sendPacket(new ExTeleportToLocationActivate(this, enterPoint));

        return true;
    }

    public void appearObserverMode()
    {
        if(!_observerMode.compareAndSet(OBSERVER_STARTING, OBSERVER_STARTED))
            return;

        _observePoint.spawnMe();
        sendUserInfo(true);
        if(_observableArena != null)
        {
            _observableArena.addObserver(_observePoint);
            _observableArena.onAppearObserver(_observePoint);
        }
    }

    public void leaveObserverMode()
    {
        if(!_observerMode.compareAndSet(OBSERVER_STARTED, OBSERVER_LEAVING))
            return;

        ObservableArena arena = _observableArena;
        if(arena != null)
        {
            sendPacket(new TeleportToLocationPacket(this, getLoc()));
            _observableArena.removeObserver(_observePoint);
            _observableArena = null;
        }

        _observePoint.deleteMe();
        _observePoint = null;

        setTarget(null);
        stopMove();

        if(arena != null)
        {
            arena.onExitObserverArena(this);
            sendPacket(new ExTeleportToLocationActivate(this, getLoc()));
        }
        else // Выходим из режима обсервинга
            sendPacket(new ObserverEndPacket(getLoc()));
    }

    public void returnFromObserverMode()
    {
        if(!_observerMode.compareAndSet(OBSERVER_LEAVING, OBSERVER_NONE))
            return;

        // Нужно при телепорте с более высокой точки на более низкую, иначе наносится вред от "падения"
        setLastClientPosition(null);
        setLastServerPosition(null);

        standUp();
        setFlying(false);

        broadcastUserInfo(true);

        World.showObjectsToPlayer(this);
    }

    public void setOlympiadSide(final int i)
    {
        _olympiadSide = i;
    }

    public int getOlympiadSide()
    {
        return _olympiadSide;
    }

    public boolean isInObserverMode()
    {
        return getObserverMode() > 0;
    }

    public boolean isInArenaObserverMode()
    {
        return _observableArena != null;
    }

    public ObservableArena getObservableArena()
    {
        return _observableArena;
    }

    public int getObserverMode()
    {
        return _observerMode.get();
    }

    public ObservePoint getObservePoint()
    {
        return _observePoint;
    }

    public int getTeleMode()
    {
        return _telemode;
    }

    public void setTeleMode(final int mode)
    {
        _telemode = mode;
    }

    public void setLoto(final int i, final int val)
    {
        _loto[i] = val;
    }

    public int getLoto(final int i)
    {
        return _loto[i];
    }

    public void setRace(final int i, final int val)
    {
        _race[i] = val;
    }

    public int getRace(final int i)
    {
        return _race[i];
    }

    public boolean getMessageRefusal()
    {
        return _messageRefusal;
    }

    public void setMessageRefusal(final boolean mode)
    {
        _messageRefusal = mode;
    }

    public void setTradeRefusal(final boolean mode)
    {
        _tradeRefusal = mode;
    }

    public boolean getTradeRefusal()
    {
        return _tradeRefusal;
    }

    public boolean isBlockAll()
    {
        return _blockAll;
    }

    public void setBlockAll(final boolean state)
    {
        _blockAll = state;
    }

    public void setHero(final boolean hero)
    {
        _hero = hero;
    }

    @Override
    public boolean isHero()
    {
        return _hero;
    }

    public void setIsInOlympiadMode(final boolean b)
    {
        _inOlympiadMode = b;
    }

    public boolean isInOlympiadMode()
    {
        return _inOlympiadMode;
    }

    public boolean isOlympiadGameStart()
    {
        return _olympiadGame != null && _olympiadGame.getState() == 1;
    }

    public boolean isOlympiadCompStart()
    {
        return _olympiadGame != null && _olympiadGame.getState() == 2;
    }

    public int getSubLevel()
    {
        return isBaseClassActive() ? 0 : getLevel();
    }

    /* varka silenos and ketra orc quests related functions */
    public void updateKetraVarka()
    {
        if(ItemFunctions.getItemCount(this, 7215) > 0)
            _ketra = 5;
        else if(ItemFunctions.getItemCount(this, 7214) > 0)
            _ketra = 4;
        else if(ItemFunctions.getItemCount(this, 7213) > 0)
            _ketra = 3;
        else if(ItemFunctions.getItemCount(this, 7212) > 0)
            _ketra = 2;
        else if(ItemFunctions.getItemCount(this, 7211) > 0)
            _ketra = 1;
        else if(ItemFunctions.getItemCount(this, 7225) > 0)
            _varka = 5;
        else if(ItemFunctions.getItemCount(this, 7224) > 0)
            _varka = 4;
        else if(ItemFunctions.getItemCount(this, 7223) > 0)
            _varka = 3;
        else if(ItemFunctions.getItemCount(this, 7222) > 0)
            _varka = 2;
        else if(ItemFunctions.getItemCount(this, 7221) > 0)
            _varka = 1;
        else
        {
            _varka = 0;
            _ketra = 0;
        }
    }

    public int getVarka()
    {
        return _varka;
    }

    public int getKetra()
    {
        return _ketra;
    }

    public void updateRam()
    {
        if(ItemFunctions.getItemCount(this, 7247) > 0)
            _ram = 2;
        else if(ItemFunctions.getItemCount(this, 7246) > 0)
            _ram = 1;
        else
            _ram = 0;
    }

    public int getRam()
    {
        return _ram;
    }

    public void setPledgeType(final int typeId)
    {
        _pledgeType = typeId;
    }

    public int getPledgeType()
    {
        return _pledgeType;
    }

    public void setLvlJoinedAcademy(int lvl)
    {
        _lvlJoinedAcademy = lvl;
    }

    public int getLvlJoinedAcademy()
    {
        return _lvlJoinedAcademy;
    }

    public PledgeRank getPledgeRank()
    {
        return _pledgeRank;
    }

    public void updatePledgeRank()
    {
        if(isGM()) // Хай все ГМы будут императорами мира Lineage 2 ;)
        {
            _pledgeRank = PledgeRank.EMPEROR;
            return;
        }

        int CLAN_LEVEL = _clan == null ? -1 : _clan.getLevel();
        boolean IN_ACADEMY = _clan != null && Clan.isAcademy(_pledgeType);
        boolean IS_GUARD = _clan != null && Clan.isRoyalGuard(_pledgeType);
        boolean IS_KNIGHT = _clan != null && Clan.isOrderOfKnights(_pledgeType);

        boolean IS_GUARD_CAPTAIN = false, IS_KNIGHT_COMMANDER = false, IS_LEADER = false;

        SubUnit unit = getSubUnit();
        if(unit != null)
        {
            UnitMember unitMember = unit.getUnitMember(getObjectId());
            if(unitMember == null)
            {
                _log.warn("Player: unitMember null, clan: " + _clan.getClanId() + "; pledgeType: " + unit.getType());
                return;
            }
            IS_GUARD_CAPTAIN = Clan.isRoyalGuard(unitMember.isLeaderOf());
            IS_KNIGHT_COMMANDER = Clan.isOrderOfKnights(unitMember.isLeaderOf());
            IS_LEADER = unitMember.isLeaderOf() == Clan.SUBUNIT_MAIN_CLAN;
        }

        switch(CLAN_LEVEL)
        {
            case -1:
                _pledgeRank = PledgeRank.VAGABOND;
                break;
            case 0:
            case 1:
            case 2:
            case 3:
                _pledgeRank = PledgeRank.VASSAL;
                break;
            case 4:
                if(IS_LEADER)
                    _pledgeRank = PledgeRank.KNIGHT;
                else
                    _pledgeRank = PledgeRank.VASSAL;
                break;
            case 5:
                if(IS_LEADER)
                    _pledgeRank = PledgeRank.WISEMAN;
                else if(IN_ACADEMY)
                    _pledgeRank = PledgeRank.VASSAL;
                else
                    _pledgeRank = PledgeRank.HEIR;
                break;
            case 6:
                if(IS_LEADER)
                    _pledgeRank = PledgeRank.BARON;
                else if(IN_ACADEMY)
                    _pledgeRank = PledgeRank.VASSAL;
                else if(IS_GUARD_CAPTAIN)
                    _pledgeRank = PledgeRank.WISEMAN;
                else if(IS_GUARD)
                    _pledgeRank = PledgeRank.HEIR;
                else
                    _pledgeRank = PledgeRank.KNIGHT;
                break;
            case 7:
                if(IS_LEADER)
                    _pledgeRank = PledgeRank.COUNT;
                else if(IN_ACADEMY)
                    _pledgeRank = PledgeRank.VASSAL;
                else if(IS_GUARD_CAPTAIN)
                    _pledgeRank = PledgeRank.VISCOUNT;
                else if(IS_GUARD)
                    _pledgeRank = PledgeRank.KNIGHT;
                else if(IS_KNIGHT_COMMANDER)
                    _pledgeRank = PledgeRank.BARON;
                else if(IS_KNIGHT)
                    _pledgeRank = PledgeRank.HEIR;
                else
                    _pledgeRank = PledgeRank.WISEMAN;
                break;
            case 8:
                if(IS_LEADER)
                    _pledgeRank = PledgeRank.MARQUIS;
                else if(IN_ACADEMY)
                    _pledgeRank = PledgeRank.VASSAL;
                else if(IS_GUARD_CAPTAIN)
                    _pledgeRank = PledgeRank.COUNT;
                else if(IS_GUARD)
                    _pledgeRank = PledgeRank.WISEMAN;
                else if(IS_KNIGHT_COMMANDER)
                    _pledgeRank = PledgeRank.VISCOUNT;
                else if(IS_KNIGHT)
                    _pledgeRank = PledgeRank.KNIGHT;
                else
                    _pledgeRank = PledgeRank.BARON;
                break;
            case 9:
                if(IS_LEADER)
                    _pledgeRank = PledgeRank.DUKE;
                else if(IN_ACADEMY)
                    _pledgeRank = PledgeRank.VASSAL;
                else if(IS_GUARD_CAPTAIN)
                    _pledgeRank = PledgeRank.MARQUIS;
                else if(IS_GUARD)
                    _pledgeRank = PledgeRank.BARON;
                else if(IS_KNIGHT_COMMANDER)
                    _pledgeRank = PledgeRank.COUNT;
                else if(IS_KNIGHT)
                    _pledgeRank = PledgeRank.WISEMAN;
                else
                    _pledgeRank = PledgeRank.VISCOUNT;
                break;
            case 10:
                if(IS_LEADER)
                    _pledgeRank = PledgeRank.GRAND_DUKE;
                else if(IN_ACADEMY)
                    _pledgeRank = PledgeRank.VASSAL;
                else if(IS_GUARD)
                    _pledgeRank = PledgeRank.VISCOUNT;
                else if(IS_KNIGHT)
                    _pledgeRank = PledgeRank.BARON;
                else if(IS_GUARD_CAPTAIN)
                    _pledgeRank = PledgeRank.DUKE;
                else if(IS_KNIGHT_COMMANDER)
                    _pledgeRank = PledgeRank.MARQUIS;
                else
                    _pledgeRank = PledgeRank.COUNT;
                break;
            case 11:
                if(IS_LEADER)
                    _pledgeRank = PledgeRank.DISTINGUISHED_KING;
                else if(IN_ACADEMY)
                    _pledgeRank = PledgeRank.VASSAL;
                else if(IS_GUARD)
                    _pledgeRank = PledgeRank.COUNT;
                else if(IS_KNIGHT)
                    _pledgeRank = PledgeRank.VISCOUNT;
                else if(IS_GUARD_CAPTAIN)
                    _pledgeRank = PledgeRank.GRAND_DUKE;
                else if(IS_KNIGHT_COMMANDER)
                    _pledgeRank = PledgeRank.DUKE;
                else
                    _pledgeRank = PledgeRank.MARQUIS;
                break;
        }

        if(isHero() && _pledgeRank.ordinal() < PledgeRank.MARQUIS.ordinal())
            _pledgeRank = PledgeRank.MARQUIS;
    }

    public void setPowerGrade(final int grade)
    {
        _powerGrade = grade;
    }

    public int getPowerGrade()
    {
        return _powerGrade;
    }

    public void setApprentice(final int apprentice)
    {
        _apprentice = apprentice;
    }

    public int getApprentice()
    {
        return _apprentice;
    }

    public int getSponsor()
    {
        return _clan == null ? 0 : _clan.getAnyMember(getObjectId()).getSponsor();
    }

    @Override
    public int getNameColor()
    {
        if(isInObserverMode())
            return 0x00;

        return _nameColor;
    }

    public void setNameColor(final int nameColor)
    {
        if(nameColor != Config.NORMAL_NAME_COLOUR && nameColor != Config.CLANLEADER_NAME_COLOUR && nameColor != Config.GM_NAME_COLOUR)
            setVar("namecolor", Integer.toHexString(nameColor));
        else if(nameColor == Config.NORMAL_NAME_COLOUR)
            unsetVar("namecolor");
        _nameColor = nameColor;
    }

    public void setNameColor(final int red, final int green, final int blue)
    {
        _nameColor = (red & 0xFF) + ((green & 0xFF) << 8) + ((blue & 0xFF) << 16);
        if(_nameColor != Config.NORMAL_NAME_COLOUR && _nameColor != Config.CLANLEADER_NAME_COLOUR && _nameColor != Config.GM_NAME_COLOUR)
            setVar("namecolor", Integer.toHexString(_nameColor));
        else
            unsetVar("namecolor");
    }

    private void restoreVariables()
    {
        List<CharacterVariable> variables = CharacterVariablesDAO.getInstance().restore(getObjectId());
        for(CharacterVariable var : variables)
            _variables.put(var.getName(), var);
    }

    public Collection<CharacterVariable> getVariables()
    {
        return _variables.values();
    }

    public boolean setVar(String name, String value)
    {
        return setVar(name, value, -1);
    }

    public boolean setVar(String name, String value, long expirationTime)
    {
        CharacterVariable var = new CharacterVariable(name, value, expirationTime);
        if(CharacterVariablesDAO.getInstance().insert(getObjectId(), var))
        {
            _variables.put(name, var);
            return true;
        }
        return false;
    }

    public boolean setVar(String name, int value)
    {
        return setVar(name, value, -1);
    }

    public boolean setVar(String name, int value, long expirationTime)
    {
        return setVar(name, String.valueOf(value), expirationTime);
    }

    public boolean setVar(String name, long value)
    {
        return setVar(name, value, -1);
    }

    public boolean setVar(String name, long value, long expirationTime)
    {
        return setVar(name, String.valueOf(value), expirationTime);
    }

    public boolean setVar(String name, double value)
    {
        return setVar(name, value, -1);
    }

    public boolean setVar(String name, double value, long expirationTime)
    {
        return setVar(name, String.valueOf(value), expirationTime);
    }

    public boolean setVar(String name, boolean value)
    {
        return setVar(name, value, -1);
    }

    public boolean setVar(String name, boolean value, long expirationTime)
    {
        return setVar(name, String.valueOf(value), expirationTime);
    }

    public boolean unsetVar(String name)
    {
        if(name == null || name.isEmpty())
            return false;

        if(_variables.containsKey(name) && CharacterVariablesDAO.getInstance().delete(getObjectId(), name))
            return _variables.remove(name) != null;

        return false;
    }

    public String getVar(String name)
    {
        return getVar(name, null);
    }

    public String getVar(String name, String defaultValue)
    {
        CharacterVariable var = _variables.get(name);
        if(var != null && !var.isExpired())
            return var.getValue();

        return defaultValue;
    }

    public long getVarExpireTime(String name)
    {
        CharacterVariable var = _variables.get(name);
        if(var != null)
            return var.getExpireTime();

        return 0;
    }

    public int getVarInt(String name)
    {
        return getVarInt(name, 0);
    }

    public int getVarInt(String name, int defaultValue)
    {
        String var = getVar(name);
        if(var != null)
            return Integer.parseInt(var);

        return defaultValue;
    }

    public long getVarLong(String name)
    {
        return getVarLong(name, 0L);
    }

    public long getVarLong(String name, long defaultValue)
    {
        String var = getVar(name);
        if(var != null)
            return Long.parseLong(var);

        return defaultValue;
    }

    public double getVarDouble(String name)
    {
        return getVarDouble(name, 0.);
    }

    public double getVarDouble(String name, double defaultValue)
    {
        String var = getVar(name);
        if(var != null)
            return Double.parseDouble(var);

        return defaultValue;
    }

    public boolean getVarBoolean(String name)
    {
        return getVarBoolean(name, false);
    }

    public boolean getVarBoolean(String name, boolean defaultValue)
    {
        String var = getVar(name);
        if(var != null)
            return !(var.equals("0") || var.equalsIgnoreCase("false"));

        return defaultValue;
    }

    public void setLanguage(String val)
    {
        _language = Language.getLanguage(val);
        setVar(Language.LANG_VAR, _language.getShortName(), -1);
    }

    public Language getLanguage()
    {
        if(Config.USE_CLIENT_LANG && getNetConnection() != null)
            return getNetConnection().getLanguage();
        return _language;
    }

    public int getLocationId()
    {
        if(getNetConnection() != null)
            return getNetConnection().getLanguage().getId();
        return -1;
    }

    public int isAtWarWith(int id)
    {
        return _clan == null || !_clan.isAtWarWith(id) ? 0 : 1;
    }

    public void stopWaterTask()
    {
        if(_taskWater != null)
        {
            _taskWater.cancel(false);
            _taskWater = null;
            sendPacket(new SetupGaugePacket(this, SetupGaugePacket.Colors.BLUE, 0));
            sendChanges();
        }
    }

    public void startWaterTask()
    {
        if(isDead())
            stopWaterTask();
        else if(Config.ALLOW_WATER && _taskWater == null)
        {
            int timeinwater = (int) (calcStat(Stats.BREATH, getBaseStats().getBreathBonus(), null, null) * 1000L);
            sendPacket(new SetupGaugePacket(this, SetupGaugePacket.Colors.BLUE, timeinwater));
            if(isTransformed() && !getTransform().isCanSwim())
                setTransform(null);

            _taskWater = ThreadPoolManager.getInstance().scheduleAtFixedRate(new WaterTask(this), timeinwater, 1000L);
            sendChanges();
        }
    }

    public void doRevive(double percent)
    {
        restoreExp(percent);
        doRevive();
    }

    @Override
    public void doRevive()
    {
        super.doRevive();
        unsetVar("lostexp");
        updateAbnormalIcons();
        autoShot();
        if(isMounted())
            _mount.onRevive();
    }

    public void reviveRequest(Player reviver, double percent, boolean pet)
    {
        ReviveAnswerListener reviveAsk = _askDialog != null && _askDialog.getValue() instanceof ReviveAnswerListener ? (ReviveAnswerListener) _askDialog.getValue() : null;
        if(reviveAsk != null)
        {
            if(reviveAsk.isForPet() == pet && reviveAsk.getPower() >= percent)
            {
                reviver.sendPacket(SystemMsg.RESURRECTION_HAS_ALREADY_BEEN_PROPOSED);
                return;
            }
            if(pet && !reviveAsk.isForPet())
            {
                reviver.sendPacket(SystemMsg.A_PET_CANNOT_BE_RESURRECTED_WHILE_ITS_OWNER_IS_IN_THE_PROCESS_OF_RESURRECTING);
                return;
            }
            if(pet && isDead())
            {
                reviver.sendPacket(SystemMsg.WHILE_A_PET_IS_BEING_RESURRECTED_IT_CANNOT_HELP_IN_RESURRECTING_ITS_MASTER);
                return;
            }
        }

        if(pet && getPet() != null && getPet().isDead() || !pet && isDead())
        {

            ConfirmDlgPacket pkt = new ConfirmDlgPacket(SystemMsg.C1_IS_MAKING_AN_ATTEMPT_TO_RESURRECT_YOU_IF_YOU_CHOOSE_THIS_PATH_S2_EXPERIENCE_WILL_BE_RETURNED_FOR_YOU, 0);
            pkt.addName(reviver).addInteger(Math.round(percent));

            ask(pkt, new ReviveAnswerListener(this, percent, pet));
        }
    }

    public void requestCheckBot()
    {
        BotCheckQuestion question = BotCheckManager.generateRandomQuestion();
        int qId = question.getId();
        String qDescr = question.getDescr();

        ConfirmDlgPacket pkt = new ConfirmDlgPacket(SystemMsg.S1, 60000).addString(qDescr);
        //ConfirmDlgPacket pkt = new ConfirmDlgPacket(qDescr, 60000);
        ask(pkt, new BotCheckAnswerListner(this, qId));
    }

    public void increaseBotRating()
    {
        int bot_points = getBotRating();
        if(bot_points + 1 >= Config.MAX_BOT_POINTS)
            return;
        setBotRating(bot_points + 1);
    }

    public void decreaseBotRating()
    {
        int bot_points = getBotRating();
        if(bot_points - 1 <= Config.MINIMAL_BOT_RATING_TO_BAN)
        {
            if(toJail(Config.AUTO_BOT_BAN_JAIL_TIME))
            {
                sendMessage("You moved to jail, time to escape - " + Config.AUTO_BOT_BAN_JAIL_TIME + " minutes, reason - botting .");
                if(Config.ANNOUNCE_AUTO_BOT_BAN)
                    Announcements.announceToAll("Player " + getName() + " jailed for botting!");
            }
        }
        else
        {
            setBotRating(bot_points - 1);
            if(Config.ON_WRONG_QUESTION_KICK)
                kick();
        }
    }

    public void setBotRating(int rating)
    {
        _botRating = rating;
    }

    public int getBotRating()
    {
        return _botRating;
    }

    public boolean isInJail()
    {
        return _isInJail;
    }

    public void setIsInJail(boolean value)
    {
        _isInJail = value;
    }

    public boolean toJail(int time)
    {
        if(isInJail())
            return false;

        setIsInJail(true);
        setVar(JAILED_VAR, true, System.currentTimeMillis() + (time * 60000));
        startUnjailTask(this, time);

        if(getReflection().isMain())
            setVar("backCoords", getLoc().toXYZString(), -1);

        if(isInStoreMode())
        {
            setPrivateStoreType(Player.STORE_PRIVATE_NONE);
            storePrivateStore();
        }

        teleToLocation(Location.findPointToStay(this, AdminFunctions.JAIL_SPAWN, 50, 200), ReflectionManager.JAIL);

        return true;
    }

    public boolean fromJail()
    {
        if(!isInJail())
            return false;

        setIsInJail(false);
        unsetVar(JAILED_VAR);
        stopUnjailTask();

        String back = getVar("backCoords");
        if(back != null)
        {
            teleToLocation(Location.parseLoc(back), ReflectionManager.MAIN);
            unsetVar("backCoords");
        }
        return true;
    }

    public void summonCharacterRequest(final Creature summoner, final Location loc, final int summonConsumeCrystal)
    {
        ConfirmDlgPacket cd = new ConfirmDlgPacket(SystemMsg.C1_WISHES_TO_SUMMON_YOU_FROM_S2, 60000);
        cd.addName(summoner).addZoneName(loc);

        ask(cd, new SummonAnswerListener(this, loc, summonConsumeCrystal));
    }

    public void updateNoChannel(final long time)
    {
        setNoChannel(time);

        Connection con = null;
        PreparedStatement statement = null;
        try
        {
            con = DatabaseFactory.getInstance().getConnection();

            final String stmt = "UPDATE characters SET nochannel = ? WHERE obj_Id=?";
            statement = con.prepareStatement(stmt);
            statement.setLong(1, _NoChannel > 0 ? _NoChannel / 1000 : _NoChannel);
            statement.setInt(2, getObjectId());
            statement.executeUpdate();
        }
        catch(final Exception e)
        {
            _log.warn("Could not activate nochannel:" + e);
        }
        finally
        {
            DbUtils.closeQuietly(con, statement);
        }

        sendPacket(new EtcStatusUpdatePacket(this));
    }

    public boolean canTalkWith(Player player)
    {
        return _NoChannel >= 0 || player == this;
    }

    private void checkDailyCounters()
    {
        Calendar temp = Calendar.getInstance();
        temp.set(Calendar.HOUR_OF_DAY, 6);
        temp.set(Calendar.MINUTE, 30);
        temp.set(Calendar.SECOND, 0);
        temp.set(Calendar.MILLISECOND, 0);
        long daysPassed = Math.round((System.currentTimeMillis() / 1000 - _lastAccess) / 86400);
        if(daysPassed == 0 && _lastAccess < temp.getTimeInMillis() / 1000 && System.currentTimeMillis() > temp.getTimeInMillis())
            daysPassed++;

        if(daysPassed > 0)
            restartDailyCounters(true);
    }

    public void restartDailyCounters(boolean onRestore)
    {
        if(getSettings(ServerSettings.class).isWorldChatAllowed())
        {
            setUsedWorldChatPoints(0);
            if(!onRestore)
                sendPacket(new ExWorldChatCnt(this));
        }
    }

    private void checkWeeklyCounters()
    {
        Calendar temp = Calendar.getInstance();
        if(temp.get(Calendar.DAY_OF_WEEK) > Calendar.WEDNESDAY)
            temp.add(Calendar.DAY_OF_MONTH, 7);

        temp.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
        temp.set(Calendar.HOUR_OF_DAY, 6);
        temp.set(Calendar.MINUTE, 30);
        temp.set(Calendar.SECOND, 0);
        temp.set(Calendar.MILLISECOND, 0);
        if(_lastAccess < temp.getTimeInMillis() / 1000 && System.currentTimeMillis() > temp.getTimeInMillis())
            restartWeeklyCounters(true);
    }

    public void restartWeeklyCounters(boolean onRestore)
    {
        //
    }

    public SubClassList getSubClassList()
    {
        return _subClassList;
    }

    public SubClass getBaseSubClass()
    {
        return _subClassList.getBaseSubClass();
    }

    public int getBaseClassId()
    {
        if(getBaseSubClass() != null)
            return getBaseSubClass().getClassId();

        return -1;
    }

    public SubClass getActiveSubClass()
    {
        if(_subClassList != null)
            return _subClassList.getActiveSubClass();
        return null;
    }

    public int getActiveClassId()
    {
        if(getActiveSubClass() != null)
            return getActiveSubClass().getClassId();

        return -1;
    }

    public boolean isBaseClassActive()
    {
        return getActiveSubClass().isBase();
    }

    public ClassId getClassId()
    {
        return ClassId.VALUES[getActiveClassId()];
    }

    public int getMaxLevel()
    {
        if(getActiveSubClass() != null)
            return getActiveSubClass().getMaxLevel();

        return Experience.getMaxLevel();
    }

    /**
     * Changing index of class in DB, used for changing class when finished professional quests
     *
     * @param oldclass
     * @param newclass
     */
    private synchronized void changeClassInDb(final int oldclass, final int newclass)
    {
        Connection con = null;
        PreparedStatement statement = null;
        try
        {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("UPDATE character_subclasses SET class_id=? WHERE char_obj_id=? AND class_id=?");
            statement.setInt(1, newclass);
            statement.setInt(2, getObjectId());
            statement.setInt(3, oldclass);
            statement.executeUpdate();
            DbUtils.close(statement);

            statement = con.prepareStatement("DELETE FROM character_hennas WHERE char_obj_id=? AND class_index=?");
            statement.setInt(1, getObjectId());
            statement.setInt(2, newclass);
            statement.executeUpdate();
            DbUtils.close(statement);

            statement = con.prepareStatement("UPDATE character_hennas SET class_index=? WHERE char_obj_id=? AND class_index=?");
            statement.setInt(1, newclass);
            statement.setInt(2, getObjectId());
            statement.setInt(3, oldclass);
            statement.executeUpdate();
            DbUtils.close(statement);

            statement = con.prepareStatement("DELETE FROM character_shortcuts WHERE object_id=? AND class_index=?");
            statement.setInt(1, getObjectId());
            statement.setInt(2, newclass);
            statement.executeUpdate();
            DbUtils.close(statement);

            statement = con.prepareStatement("UPDATE character_shortcuts SET class_index=? WHERE object_id=? AND class_index=?");
            statement.setInt(1, newclass);
            statement.setInt(2, getObjectId());
            statement.setInt(3, oldclass);
            statement.executeUpdate();
            DbUtils.close(statement);

            statement = con.prepareStatement("DELETE FROM character_skills WHERE char_obj_id=? AND class_index=?");
            statement.setInt(1, getObjectId());
            statement.setInt(2, newclass);
            statement.executeUpdate();
            DbUtils.close(statement);

            statement = con.prepareStatement("UPDATE character_skills SET class_index=? WHERE char_obj_id=? AND class_index=?");
            statement.setInt(1, newclass);
            statement.setInt(2, getObjectId());
            statement.setInt(3, oldclass);
            statement.executeUpdate();
            DbUtils.close(statement);

            statement = con.prepareStatement("DELETE FROM character_effects_save WHERE object_id=? AND id=?");
            statement.setInt(1, getObjectId());
            statement.setInt(2, newclass);
            statement.executeUpdate();
            DbUtils.close(statement);

            statement = con.prepareStatement("UPDATE character_effects_save SET id=? WHERE object_id=? AND id=?");
            statement.setInt(1, newclass);
            statement.setInt(2, getObjectId());
            statement.setInt(3, oldclass);
            statement.executeUpdate();
            DbUtils.close(statement);

            statement = con.prepareStatement("DELETE FROM character_skills_save WHERE char_obj_id=? AND class_index=?");
            statement.setInt(1, getObjectId());
            statement.setInt(2, newclass);
            statement.executeUpdate();
            DbUtils.close(statement);

            statement = con.prepareStatement("UPDATE character_skills_save SET class_index=? WHERE char_obj_id=? AND class_index=?");
            statement.setInt(1, newclass);
            statement.setInt(2, getObjectId());
            statement.setInt(3, oldclass);
            statement.executeUpdate();
            DbUtils.close(statement);
        }
        catch(final SQLException e)
        {
            _log.error("", e);
        }
        finally
        {
            DbUtils.closeQuietly(con, statement);
        }
    }

    /**
     * Сохраняет информацию о классах в БД
     */
    public void storeCharSubClasses()
    {
        SubClass main = getActiveSubClass();
        if(main != null)
        {
            main.setCp(getCurrentCp());
            main.setHp(getCurrentHp());
            main.setMp(getCurrentMp());
        }
        else
            _log.warn("Could not store char sub data, main class " + getActiveClassId() + " not found for " + this);

        CharacterSubclassDAO.getInstance().store(this);
    }

    /**
     * Добавить класс, используется только для сабклассов
     *
     * @param storeOld
     */
    public boolean addSubClass(final int classId, boolean storeOld, long exp, long sp)
    {
        return addSubClass(classId, storeOld, SubClassType.SUBCLASS, exp, sp);
    }

    public boolean addSubClass(final int classId, boolean storeOld, SubClassType type, long exp, long sp)
    {
        return addSubClass(-1, classId, storeOld, type, exp, sp);
    }

    private boolean addSubClass(final int oldClassId, final int classId, boolean storeOld, SubClassType type, long exp, long sp)
    {
        final ClassId newId = ClassId.VALUES[classId];
        if(newId.isDummy() || newId.isOfLevel(ClassLevel.NONE) || newId.isOfLevel(ClassLevel.FIRST))
            return false;

        final SubClass newClass = new SubClass(this);
        newClass.setType(type);
        newClass.setClassId(classId);
        if(exp > 0L)
            newClass.setExp(exp, true);
        if(sp > 0)
            newClass.setSp(sp);
        if(!getSubClassList().add(newClass))
            return false;

        final int level = newClass.getLevel();
        final double hp = newId.getBaseHp(level);
        final double mp = newId.getBaseMp(level);
        final double cp = newId.getBaseCp(level);
        if(!CharacterSubclassDAO.getInstance().insert(getObjectId(), newClass.getClassId(), newClass.getExp(), newClass.getSp(), hp, mp, cp, hp, mp, cp, level, false, type))
            return false;

        setActiveSubClass(classId, storeOld, false);

        rewardSkills(true, false, true, false);

        sendSkillList();

        sendSkillList();
        setCurrentHpMp(getMaxHp(), getMaxMp(), true);
        setCurrentCp(getMaxCp());

        final ClassId oldId = oldClassId >= 0 ? ClassId.VALUES[oldClassId] : null;
        onReceiveNewClassId(oldId, newId);

        return true;
    }

    /**
     * Удаляет всю информацию о классе и добавляет новую, только для сабклассов
     */
    public boolean modifySubClass(final int oldClassId, final int newClassId, final boolean safeExpSp)
    {
        final SubClass originalClass = getSubClassList().getByClassId(oldClassId);
        if(originalClass == null || originalClass.isBase())
            return false;

        final SubClassType type = originalClass.getType();
        long exp = 0L;
        long sp = 0;
        if(safeExpSp)
        {
            exp = originalClass.getExp();
            sp = originalClass.getSp();
        }

        TrainingCamp trainingCamp = TrainingCampManager.getInstance().getTrainingCamp(this);
        if(trainingCamp != null && trainingCamp.getClassIndex() == originalClass.getIndex())
            TrainingCampManager.getInstance().removeTrainingCamp(this);

        Connection con = null;
        PreparedStatement statement = null;
        try
        {
            con = DatabaseFactory.getInstance().getConnection();
            // Remove all basic info stored about this sub-class.
            statement = con.prepareStatement("DELETE FROM character_subclasses WHERE char_obj_id=? AND class_id=? AND type != " + SubClassType.BASE_CLASS.ordinal());
            statement.setInt(1, getObjectId());
            statement.setInt(2, oldClassId);
            statement.execute();
            DbUtils.close(statement);

            // Remove all skill info stored for this sub-class.
            statement = con.prepareStatement("DELETE FROM character_skills WHERE char_obj_id=? AND class_index=? ");
            statement.setInt(1, getObjectId());
            statement.setInt(2, oldClassId);
            statement.execute();
            DbUtils.close(statement);

            // Remove all saved skills info stored for this sub-class.
            statement = con.prepareStatement("DELETE FROM character_skills_save WHERE char_obj_id=? AND class_index=? ");
            statement.setInt(1, getObjectId());
            statement.setInt(2, oldClassId);
            statement.execute();
            DbUtils.close(statement);

            // Remove all saved effects stored for this sub-class.
            statement = con.prepareStatement("DELETE FROM character_effects_save WHERE object_id=? AND id=? ");
            statement.setInt(1, getObjectId());
            statement.setInt(2, oldClassId);
            statement.execute();
            DbUtils.close(statement);

            // Remove all henna info stored for this sub-class.
            statement = con.prepareStatement("DELETE FROM character_hennas WHERE char_obj_id=? AND class_index=? ");
            statement.setInt(1, getObjectId());
            statement.setInt(2, oldClassId);
            statement.execute();
            DbUtils.close(statement);

            // Remove all shortcuts info stored for this sub-class.
            statement = con.prepareStatement("DELETE FROM character_shortcuts WHERE object_id=? AND class_index=? ");
            statement.setInt(1, getObjectId());
            statement.setInt(2, oldClassId);
            statement.execute();
            DbUtils.close(statement);
        }
        catch(final Exception e)
        {
            _log.warn("Could not delete char sub-class: " + e);
            _log.error("", e);
        }
        finally
        {
            DbUtils.closeQuietly(con, statement);
        }
        getSubClassList().removeByClassId(oldClassId);

        return newClassId <= 0 || addSubClass(oldClassId, newClassId, false, type, exp, sp);
    }

    public void setActiveSubClass(final int subId, final boolean store, final boolean onRestore)
    {
        if(!onRestore)
        {
            SubClass oldActiveSub = getActiveSubClass();
            if(oldActiveSub != null)
            {
                storeDisableSkills();

                if(store)
                {
                    oldActiveSub.setCp(getCurrentCp());
                    oldActiveSub.setHp(getCurrentHp());
                    oldActiveSub.setMp(getCurrentMp());
                }
            }
        }

        SubClass newActiveSub = _subClassList.changeActiveSubClass(subId);

        setClassId(subId, false);

        removeAllSkills();

        getAbnormalList().stopAll();
        deleteCubics();

        for(Servitor servitor : getServitors())
        {
            if(servitor != null && servitor.isSummon())
                servitor.unSummon(false);
        }

        restoreSkills();
        rewardSkills(false);

        checkSkills();

        refreshExpertisePenalty();

        getInventory().refreshEquip();
        getInventory().validateItems();

        getHennaList().restore();

        getDailyMissionList().restore();

        EffectsDAO.getInstance().restoreEffects(this);
        restoreDisableSkills();

        setCurrentHpMp(newActiveSub.getHp(), newActiveSub.getMp());
        setCurrentCp(newActiveSub.getCp());

        _shortCuts.restore();
        sendPacket(new ShortCutInitPacket(this));
        sendActiveAutoShots();

        broadcastPacket(new SocialActionPacket(getObjectId(), SocialActionPacket.LEVEL_UP));

        setIncreasedForce(0);

        startHourlyTask();

        sendSkillList();

        broadcastCharInfo();
        updateAbnormalIcons();
        updateStats();
    }

    public boolean givePremiumAccount(PremiumAccountTemplate premiumAccount, int delay)
    {
        if(getNetConnection() == null)
            return false;

        int type = premiumAccount.getType();
        if(type == 0)
            return false;

        int expireTime = (delay > 0) ? (int) ((delay * 60 * 60) + (System.currentTimeMillis() / 1000)) : Integer.MAX_VALUE;
        boolean extended = false;
        int oldAccountType = getNetConnection().getPremiumAccountType();
        long oldAccountExpire = getNetConnection().getPremiumAccountExpire();
        if(oldAccountType == type && oldAccountExpire > (System.currentTimeMillis() / 1000))
        {
            expireTime += (int) (oldAccountExpire - (System.currentTimeMillis() / 1000));
            extended = true;
        }


        getDAO(AccountInfoDAO.class).save(getAccountName(), type, expireTime);

        getNetConnection().setPremiumAccountType(type);
        getNetConnection().setPremiumAccountExpire(expireTime);

        if(startPremiumAccountTask())
        {
            if(!extended)
            {
                if(getParty() != null)
                    getParty().recalculatePartyData();

                getAttendanceRewards().onReceivePremiumAccount();
                sendPacket(new ExBR_PremiumStatePacket(this, hasPremiumAccount()));
            }
            return true;
        }
        return false;
    }

    public boolean removePremiumAccount()
    {
        PremiumAccountTemplate oldPremiumAccount = getPremiumAccount();
        if(oldPremiumAccount.getType() == 0)
            return false;

        double currentHpRatio = getCurrentHpRatio();
        double currentMpRatio = getCurrentMpRatio();
        double currentCpRatio = getCurrentCpRatio();

        removeStatsOwner(oldPremiumAccount);
        removeTriggers(oldPremiumAccount);

        SkillEntry[] skills = _premiumAccount.getAttachedSkills();
        for(SkillEntry skill : skills)
            removeSkill(skill);

        if(skills.length > 0)
            sendSkillList();

        setCurrentHp(getMaxHp() * currentHpRatio, false);
        setCurrentMp(getMaxMp() * currentMpRatio);
        setCurrentCp(getMaxCp() * currentCpRatio);

        updateStats();

        _premiumAccount = PremiumAccountHolder.getInstance().getPremiumAccount(0);

        if(getParty() != null)
            getParty().recalculatePartyData();

        getDAO(AccountInfoDAO.class).delete(getAccountName());

        if(getNetConnection() != null)
        {
            getNetConnection().setPremiumAccountType(0);
            getNetConnection().setPremiumAccountExpire(0);
        }

        stopPremiumAccountTask();
        removePremiumAccountItems(true);
        sendPacket(new ExBR_PremiumStatePacket(this, hasPremiumAccount()));
        getAttendanceRewards().onRemovePremiumAccount();
        return true;
    }

    private boolean tryGiveFreePremiumAccount()
    {
        if(Config.FREE_PA_TYPE == 0 || Config.FREE_PA_DELAY <= 0)
            return false;

        PremiumAccountTemplate premiumAccount = PremiumAccountHolder.getInstance().getPremiumAccount(Config.FREE_PA_TYPE);
        if(premiumAccount == null)
            return false;

        boolean recieved = Boolean.parseBoolean(AccountVariablesDAO.getInstance().select(getAccountName(), FREE_PA_RECIEVED, "false"));
        if(recieved)
            return false;

        if(givePremiumAccount(premiumAccount, Config.FREE_PA_DELAY))
        {
            AccountVariablesDAO.getInstance().insert(getAccountName(), FREE_PA_RECIEVED, "true");
            if(Config.ENABLE_FREE_PA_NOTIFICATION)
            {
                CustomMessage message = null;
                long accountExpire = getNetConnection().getPremiumAccountExpire();
                if(accountExpire != Integer.MAX_VALUE)
                {
                    message = new CustomMessage("org.l2j.gameserver.model.Player.GiveFreePA");
                    message.addString(TimeUtils.toSimpleFormat(accountExpire * 1000L));
                }
                else
                    message = new CustomMessage("org.l2j.gameserver.model.Player.GiveUnlimFreePA");

                sendPacket(new ExShowScreenMessage(message.toString(this), 15000, ExShowScreenMessage.ScreenMessageAlign.TOP_CENTER, true));
            }
            return true;
        }
        return false;
    }

    private boolean startPremiumAccountTask()
    {
        if(!Config.PREMIUM_ACCOUNT_ENABLED)
            return false;

        stopPremiumAccountTask();

        if(getNetConnection() == null)
            return false;

        int accountType = getNetConnection().getPremiumAccountType();

        PremiumAccountTemplate premiumAccount = accountType == 0 ? null : PremiumAccountHolder.getInstance().getPremiumAccount(accountType);
        if(premiumAccount != null)
        {
            long accountExpire = getNetConnection().getPremiumAccountExpire();
            if(accountExpire > System.currentTimeMillis() / 1000L)
            {
                _premiumAccount = premiumAccount;

                double currentHpRatio = getCurrentHpRatio();
                double currentMpRatio = getCurrentMpRatio();
                double currentCpRatio = getCurrentCpRatio();

                addTriggers(_premiumAccount);
                addStatFuncs(_premiumAccount.getStatFuncs());

                SkillEntry[] skills = _premiumAccount.getAttachedSkills();
                for(SkillEntry skill : skills)
                    addSkill(skill);

                if(skills.length > 0)
                    sendSkillList();

                setCurrentHp(getMaxHp() * currentHpRatio, false);
                setCurrentMp(getMaxMp() * currentMpRatio);
                setCurrentCp(getMaxCp() * currentCpRatio);

                updateStats();

                int itemsReceivedType = getVarInt(PA_ITEMS_RECIEVED);
                if(itemsReceivedType != premiumAccount.getType())
                {
                    removePremiumAccountItems(false);
                    ItemData[] items = premiumAccount.getGiveItemsOnStart();
                    if(items.length > 0)
                    {
                        if(!isInventoryFull())
                        {
                            sendPacket(SystemMsg.THE_PREMIUM_ITEM_FOR_THIS_ACCOUNT_WAS_PROVIDED_IF_THE_PREMIUM_ACCOUNT_IS_TERMINATED_THIS_ITEM_WILL_BE_DELETED);
                            for(ItemData item : items)
                                ItemFunctions.addItem(this, item.getId(), item.getCount(), true);

                            setVar(PA_ITEMS_RECIEVED, accountType);
                        }
                        else
                            sendPacket(SystemMsg.THE_PREMIUM_ITEM_CANNOT_BE_RECEIVED_BECAUSE_THE_INVENTORY_WEIGHTQUANTITY_LIMIT_HAS_BEEN_EXCEEDED);
                    }
                }
                if(accountExpire != Integer.MAX_VALUE)
                    _premiumAccountExpirationTask = LazyPrecisionTaskManager.getInstance().startPremiumAccountExpirationTask(this, accountExpire);

                return true;
            }
        }

        removePremiumAccountItems(true);
        if(tryGiveFreePremiumAccount())
            return false;

        getDAO(AccountInfoDAO.class).delete(getAccountName());

        if(getNetConnection() != null)
        {
            getNetConnection().setPremiumAccountType(0);
            getNetConnection().setPremiumAccountExpire(0);
        }

        return false;
    }

    private void stopPremiumAccountTask()
    {
        if(_premiumAccountExpirationTask != null)
        {
            _premiumAccountExpirationTask.cancel(false);
            _premiumAccountExpirationTask = null;
        }
    }

    private void removePremiumAccountItems(boolean notify)
    {
        PremiumAccountTemplate premiumAccount = PremiumAccountHolder.getInstance().getPremiumAccount(getVarInt(PA_ITEMS_RECIEVED));
        if(premiumAccount != null)
        {
            ItemData[] items = premiumAccount.getTakeItemsOnEnd();
            if(items.length > 0)
            {
                if(notify)
                    sendPacket(SystemMsg.THE_PREMIUM_ACCOUNT_HAS_BEEN_TERMINATED_THE_PROVIDED_PREMIUM_ITEM_WAS_DELETED);

                for(ItemData item : items)
                    ItemFunctions.deleteItem(this, item.getId(), item.getCount(), notify);

                for(ItemData item : items)
                    ItemFunctions.deleteItemsEverywhere(this, item.getId());
            }
        }

        unsetVar(PA_ITEMS_RECIEVED);
    }

    @Override
    public int getInventoryLimit()
    {
        return (int) calcStat(Stats.INVENTORY_LIMIT, 0, null, null);
    }

    public int getWarehouseLimit()
    {
        return (int) calcStat(Stats.STORAGE_LIMIT, 0, null, null);
    }

    public int getTradeLimit()
    {
        return (int) calcStat(Stats.TRADE_LIMIT, 0, null, null);
    }

    public int getDwarvenRecipeLimit()
    {
        return (int) calcStat(Stats.DWARVEN_RECIPE_LIMIT, 50, null, null) + Config.ALT_ADD_RECIPES;
    }

    public int getCommonRecipeLimit()
    {
        return (int) calcStat(Stats.COMMON_RECIPE_LIMIT, 50, null, null) + Config.ALT_ADD_RECIPES;
    }

    public boolean getAndSetLastItemAuctionRequest()
    {
        if(_lastItemAuctionInfoRequest + 2000L < System.currentTimeMillis())
        {
            _lastItemAuctionInfoRequest = System.currentTimeMillis();
            return true;
        }
        else
        {
            _lastItemAuctionInfoRequest = System.currentTimeMillis();
            return false;
        }
    }

    @Override
    public int getNpcId()
    {
        return -2;
    }

    public GameObject getVisibleObject(int id)
    {
        if(getObjectId() == id)
            return this;

        GameObject target = null;

        if(getTargetId() == id)
            target = getTarget();

        if(target == null && isInParty())
            for(Player p : _party.getPartyMembers())
                if(p != null && p.getObjectId() == id)
                {
                    target = p;
                    break;
                }

        if(target == null)
            target = World.getAroundObjectById(this, id);

        return target == null || target.isInvisible(this) ? null : target;
    }

    @Override
    public String getTitle()
    {
        return super.getTitle();
    }

    public int getTitleColor()
    {
        return _titlecolor;
    }

    public void setTitleColor(final int titlecolor)
    {
        if(titlecolor != DEFAULT_TITLE_COLOR)
            setVar("titlecolor", Integer.toHexString(titlecolor), -1);
        else
            unsetVar("titlecolor");
        _titlecolor = titlecolor;
    }

    @Override
    public boolean isImmobilized()
    {
        return super.isImmobilized() || isOverloaded() || isSitting() || isFishing() || isInTrainingCamp();
    }

    @Override
    public boolean isBlocked()
    {
        return super.isBlocked() || isInMovie() || isInObserverMode() || isTeleporting() || isLogoutStarted() || isInTrainingCamp();
    }

    @Override
    public boolean isInvulnerable()
    {
        return super.isInvulnerable() || isInMovie() || isInTrainingCamp();
    }

    /**
     * if True, the L2Player can't take more item
     */
    public void setOverloaded(boolean overloaded)
    {
        _overloaded = overloaded;
    }

    public boolean isOverloaded()
    {
        return _overloaded;
    }

    public boolean isFishing()
    {
        return _fishing.inStarted();
    }

    public Fishing getFishing()
    {
        return _fishing;
    }

    public PremiumAccountTemplate getPremiumAccount()
    {
        return _premiumAccount;
    }

    public boolean hasPremiumAccount()
    {
        return _premiumAccount.getType() > 0;
    }

    public int getPremiumAccountLeftTime()
    {
        if(hasPremiumAccount())
        {
            GameClient client = this.getNetConnection();
            if(client != null)
                return (int) Math.max(0, client.getPremiumAccountExpire() - System.currentTimeMillis() / 1000L);
        }
        return 0;
    }

    public double getRateAdena()
    {
        double rate = getSettings(ServerSettings.class).rateAdena();
        rate *= isInParty() ? _party._rateAdena : getPremiumAccount().getRates().getAdena();
        rate *= 1. + calcStat(Stats.ADENA_RATE_MULTIPLIER, 0, null, null);
        return rate;
    }

    public double getRateItems()
    {
        double rate = getSettings(ServerSettings.class).rateItems();
        rate *= isInParty() ? _party._rateDrop : getPremiumAccount().getRates().getDrop();
        rate *= 1. + calcStat(Stats.DROP_RATE_MULTIPLIER, 0, null, null);
        return rate;
    }

    public double getRateExp()
    {
        final double baseRate = getSettings(ServerSettings.class).rateXP() * (isInParty() ? _party._rateExp : getPremiumAccount().getRates().getExp());
        double rate = baseRate;
        rate += baseRate * calcStat(Stats.EXP_RATE_MULTIPLIER, 0, null, null);
        return rate;
    }

    public double getRateSp()
    {
        final double baseRate = getSettings(ServerSettings.class).rateSP() * (isInParty() ? _party._rateSp : getPremiumAccount().getRates().getSp());
        double rate = baseRate;
        rate += baseRate * calcStat(Stats.SP_RATE_MULTIPLIER, 0, null, null);
        return rate;
    }

    public double getRateSpoil()
    {
        double rate = getSettings(ServerSettings.class).rateSpoil();
        rate *= isInParty() ? _party._rateSpoil : getPremiumAccount().getRates().getSpoil();
        rate *= 1. + calcStat(Stats.SPOIL_RATE_MULTIPLIER, 0, null, null);
        return rate;
    }

    public double getRateQuestsDrop()
    {
        double rate = getSettings(ServerSettings.class).rateQuestDrop();
        rate *= getPremiumAccount().getRates().getQuestDrop();
        return rate;
    }

    public double getRateQuestsReward()
    {
        double rate = getSettings(ServerSettings.class).rateQuestReward();
        rate *= getPremiumAccount().getRates().getQuestReward();
        return rate;
    }

    public double getDropChanceMod()
    {
        double mod = getSettings(ServerSettings.class).dropChanceModifier();
        mod *= isInParty() ? _party._dropChanceMod : getPremiumAccount().getModifiers().getDropChance();
        mod *= 1. + calcStat(Stats.DROP_CHANCE_MODIFIER, 0, null, null);
        return mod;
    }

    public double getSpoilChanceMod()
    {
        double mod = getSettings(ServerSettings.class).spoilChanceModifier();
        mod *= isInParty() ? _party._spoilChanceMod : getPremiumAccount().getModifiers().getSpoilChance();
        mod *= 1. + calcStat(Stats.SPOIL_CHANCE_MODIFIER, 0, null, null);
        return mod;
    }

    private boolean _maried = false;
    private int _partnerId = 0;
    private int _coupleId = 0;
    private boolean _maryrequest = false;
    private boolean _maryaccepted = false;

    public boolean isMaried()
    {
        return _maried;
    }

    public void setMaried(boolean state)
    {
        _maried = state;
    }

    public void setMaryRequest(boolean state)
    {
        _maryrequest = state;
    }

    public boolean isMaryRequest()
    {
        return _maryrequest;
    }

    public void setMaryAccepted(boolean state)
    {
        _maryaccepted = state;
    }

    public boolean isMaryAccepted()
    {
        return _maryaccepted;
    }

    public int getPartnerId()
    {
        return _partnerId;
    }

    public void setPartnerId(int partnerid)
    {
        _partnerId = partnerid;
    }

    public int getCoupleId()
    {
        return _coupleId;
    }

    public void setCoupleId(int coupleId)
    {
        _coupleId = coupleId;
    }

    private OnPlayerChatMessageReceive _snoopListener = null;
    private List<Player> _snoopListenerPlayers = new ArrayList<Player>();

    public void addSnooper(Player pci)
    {
        if(!_snoopListenerPlayers.contains(pci))
            _snoopListenerPlayers.add(pci);

        if(!_snoopListenerPlayers.isEmpty() && _snoopListener == null)
            addListener(_snoopListener = new SnoopListener());
    }

    public void removeSnooper(Player pci)
    {
        _snoopListenerPlayers.remove(pci);
        if(_snoopListenerPlayers.isEmpty() && _snoopListener != null)
        {
            removeListener(_snoopListener);
            _snoopListener = null;
        }
    }

    private class SnoopListener implements OnPlayerChatMessageReceive
    {
        @Override
        public void onChatMessageReceive(Player player, ChatType type, String charName, String text)
        {
            if(_snoopListenerPlayers.size() > 0)
            {
                SnoopPacket sn = new SnoopPacket(getObjectId(), getName(), type.ordinal(), charName, text);
                for(Player pci : _snoopListenerPlayers)
                {
                    if(pci != null)
                        pci.sendPacket(sn);
                }
            }
        }
    }

    /**
     * Сброс реюза всех скилов персонажа.
     */
    public void resetReuse()
    {
        _skillReuses.clear();
        _sharedGroupReuses.clear();
    }

    private boolean _charmOfCourage = false;

    public boolean isCharmOfCourage()
    {
        return _charmOfCourage;
    }

    public void setCharmOfCourage(boolean val)
    {
        _charmOfCourage = val;

        sendEtcStatusUpdate();
    }

    private int _increasedForce = 0;
    private int _consumedSouls = 0;

    @Override
    public int getIncreasedForce()
    {
        return _increasedForce;
    }

    @Override
    public int getConsumedSouls()
    {
        return _consumedSouls;
    }

    @Override
    public void setConsumedSouls(int i, NpcInstance monster)
    {
        if(i == _consumedSouls)
            return;

        int max = (int) calcStat(Stats.SOULS_LIMIT, 0, monster, null);

        if(i > max)
            i = max;

        if(i <= 0)
        {
            _consumedSouls = 0;
            sendEtcStatusUpdate();
            return;
        }

        if(_consumedSouls != i)
        {
            int diff = i - _consumedSouls;
            if(diff > 0)
            {
                SystemMessage sm = new SystemMessage(SystemMessage.YOUR_SOUL_HAS_INCREASED_BY_S1_SO_IT_IS_NOW_AT_S2);
                sm.addNumber(diff);
                sm.addNumber(i);
                sendPacket(sm);
            }
        }
        else if(max == i)
        {
            sendPacket(SystemMsg.SOUL_CANNOT_BE_ABSORBED_ANYMORE);
            return;
        }

        _consumedSouls = i;
        sendPacket(new EtcStatusUpdatePacket(this));
    }

    @Override
    public void setIncreasedForce(int i)
    {
        i = Math.min(i, getMaxIncreasedForce());
        i = Math.max(i, 0);

        if(i != 0 && i > _increasedForce)
            sendPacket(new SystemMessage(SystemMessage.YOUR_FORCE_HAS_INCREASED_TO_S1_LEVEL).addNumber(i));

        _increasedForce = i;
        sendEtcStatusUpdate();
    }

    private long _lastFalling;

    public boolean isFalling()
    {
        return System.currentTimeMillis() - _lastFalling < 5000;
    }

    public void falling(int height)
    {
        if(!Config.DAMAGE_FROM_FALLING || isDead() || isFlying() || isInWater() || isInBoat())
            return;

        _lastFalling = System.currentTimeMillis();
        int damage = (int) calcStat(Stats.FALL, getMaxHp() / 2000. * height, null, null);
        if(damage > 0)
        {
            int curHp = (int) getCurrentHp();
            if(curHp - damage < 1)
                setCurrentHp(1, false);
            else
                setCurrentHp(curHp - damage, false);
            sendPacket(new SystemMessage(SystemMessage.YOU_RECEIVED_S1_DAMAGE_FROM_TAKING_A_HIGH_FALL).addNumber(damage));
        }
    }

    /**
     * Системные сообщения о текущем состоянии хп
     */
    @Override
    public void checkHpMessages(double curHp, double newHp)
    {
        //сюда пасивные скиллы
        int[] _hp = { 30, 30 };
        int[] skills = { 290, 291 };

        //сюда активные эффекты
        int[] _effects_skills_id = { 139, 176, 292, 292, 420 };
        int[] _effects_hp = { 30, 30, 30, 60, 30 };

        double percent = getMaxHp() / 100;
        double _curHpPercent = curHp / percent;
        double _newHpPercent = newHp / percent;
        boolean needsUpdate = false;

        //check for passive skills
        for(int i = 0; i < skills.length; i++)
        {
            int level = getSkillLevel(skills[i]);
            if(level > 0)
                if(_curHpPercent > _hp[i] && _newHpPercent <= _hp[i])
                {
                    sendPacket(new SystemMessage(SystemMessage.SINCE_HP_HAS_DECREASED_THE_EFFECT_OF_S1_CAN_BE_FELT).addSkillName(skills[i], level));
                    needsUpdate = true;
                }
                else if(_curHpPercent <= _hp[i] && _newHpPercent > _hp[i])
                {
                    sendPacket(new SystemMessage(SystemMessage.SINCE_HP_HAS_INCREASED_THE_EFFECT_OF_S1_WILL_DISAPPEAR).addSkillName(skills[i], level));
                    needsUpdate = true;
                }
        }

        //check for active effects
        for(Integer i = 0; i < _effects_skills_id.length; i++)
            if(getAbnormalList().contains(_effects_skills_id[i]))
                if(_curHpPercent > _effects_hp[i] && _newHpPercent <= _effects_hp[i])
                {
                    sendPacket(new SystemMessage(SystemMessage.SINCE_HP_HAS_DECREASED_THE_EFFECT_OF_S1_CAN_BE_FELT).addSkillName(_effects_skills_id[i], 1));
                    needsUpdate = true;
                }
                else if(_curHpPercent <= _effects_hp[i] && _newHpPercent > _effects_hp[i])
                {
                    sendPacket(new SystemMessage(SystemMessage.SINCE_HP_HAS_INCREASED_THE_EFFECT_OF_S1_WILL_DISAPPEAR).addSkillName(_effects_skills_id[i], 1));
                    needsUpdate = true;
                }

        if(needsUpdate)
            sendChanges();
    }

    /**
     * Системные сообщения для темных эльфов о вкл/выкл ShadowSence (skill id = 294)
     */
    public void checkDayNightMessages()
    {
        int level = getSkillLevel(294);
        if(level > 0)
            if(GameTimeController.getInstance().isNowNight())
                sendPacket(new SystemMessage(SystemMessage.IT_IS_NOW_MIDNIGHT_AND_THE_EFFECT_OF_S1_CAN_BE_FELT).addSkillName(294, level));
            else
                sendPacket(new SystemMessage(SystemMessage.IT_IS_DAWN_AND_THE_EFFECT_OF_S1_WILL_NOW_DISAPPEAR).addSkillName(294, level));
        sendChanges();
    }

    public int getZoneMask()
    {
        return _zoneMask;
    }

    //TODO [G1ta0] переработать в лисенер?
    @Override
    protected void onUpdateZones(List<Zone> leaving, List<Zone> entering)
    {
        super.onUpdateZones(leaving, entering);

        if((leaving == null || leaving.isEmpty()) && (entering == null || entering.isEmpty()))
            return;

        boolean lastInCombatZone = (_zoneMask & ZONE_PVP_FLAG) == ZONE_PVP_FLAG;
        boolean lastInDangerArea = (_zoneMask & ZONE_ALTERED_FLAG) == ZONE_ALTERED_FLAG;
        boolean lastOnSiegeField = (_zoneMask & ZONE_SIEGE_FLAG) == ZONE_SIEGE_FLAG;
        boolean lastInPeaceZone = (_zoneMask & ZONE_PEACE_FLAG) == ZONE_PEACE_FLAG;
        //FIXME G1ta0 boolean lastInSSQZone = (_zoneMask & ZONE_SSQ_FLAG) == ZONE_SSQ_FLAG;

        boolean isInCombatZone = isInZoneBattle();
        boolean isInDangerArea = isInDangerArea() || isInZone(ZoneType.CHANGED_ZONE);
        boolean isOnSiegeField = isInSiegeZone();
        boolean isInPeaceZone = isInPeaceZone();
        boolean isInSSQZone = isInSSQZone();

        // обновляем компас, только если персонаж в мире
        int lastZoneMask = _zoneMask;
        _zoneMask = 0;

        if(isInCombatZone)
            _zoneMask |= ZONE_PVP_FLAG;
        if(isInDangerArea)
            _zoneMask |= ZONE_ALTERED_FLAG;
        if(isOnSiegeField)
            _zoneMask |= ZONE_SIEGE_FLAG;
        if(isInPeaceZone)
            _zoneMask |= ZONE_PEACE_FLAG;
        if(isInSSQZone)
            _zoneMask |= ZONE_SSQ_FLAG;

        if(lastZoneMask != _zoneMask)
            sendPacket(new ExSetCompassZoneCode(this));
        boolean broadcastRelation = false;
        if(lastInCombatZone != isInCombatZone)
            broadcastRelation = true;

        if(lastInDangerArea != isInDangerArea)
            sendPacket(new EtcStatusUpdatePacket(this));

        if(lastOnSiegeField != isOnSiegeField)
        {
            broadcastRelation = true;
            if(isOnSiegeField)
                sendPacket(SystemMsg.YOU_HAVE_ENTERED_A_COMBAT_ZONE);
            else
            {
                //Если игрок выходит за территорию осады и у него есть флаг, то отбираем его и спавним в дефолтное место.
                //TODO: [Bonux] Проверить как на оффе.
                FlagItemAttachment attachment = getActiveWeaponFlagAttachment();
                if(attachment != null)
                    attachment.onLeaveSiegeZone(this);

                sendPacket(SystemMsg.YOU_HAVE_LEFT_A_COMBAT_ZONE);
                if(!isTeleporting() && getPvpFlag() == 0)
                    startPvPFlag(null);
            }
        }

        if(broadcastRelation)
            broadcastRelation();

        if(isInWater())
            startWaterTask();
        else
            stopWaterTask();
    }

    public void startAutoSaveTask()
    {
        if(!Config.AUTOSAVE)
            return;
        if(_autoSaveTask == null)
            _autoSaveTask = AutoSaveManager.getInstance().addAutoSaveTask(this);
    }

    public void stopAutoSaveTask()
    {
        if(_autoSaveTask != null)
            _autoSaveTask.cancel(false);
        _autoSaveTask = null;
    }

    public void startPcBangPointsTask()
    {
        if(!Config.ALT_PCBANG_POINTS_ENABLED || Config.ALT_PCBANG_POINTS_DELAY <= 0)
            return;
        if(_pcCafePointsTask == null)
            _pcCafePointsTask = LazyPrecisionTaskManager.getInstance().addPCCafePointsTask(this);
    }

    public void stopPcBangPointsTask()
    {
        if(_pcCafePointsTask != null)
            _pcCafePointsTask.cancel(false);
        _pcCafePointsTask = null;
    }

    public void startUnjailTask(Player player, int time)
    {
        if(_unjailTask != null)
            _unjailTask.cancel(false);
        _unjailTask = ThreadPoolManager.getInstance().schedule(new UnJailTask(player), time * 60000);
    }

    public void stopUnjailTask()
    {
        if(_unjailTask != null)
            _unjailTask.cancel(false);
        _unjailTask = null;
    }

    public void startTrainingCampTask(long timeRemaining)
    {
        if(_trainingCampTask == null && isInTrainingCamp())
            _trainingCampTask = ThreadPoolManager.getInstance().schedule(() -> TrainingCampManager.getInstance().onExitTrainingCamp(this), timeRemaining);
    }

    public void stopTrainingCampTask()
    {
        if(_trainingCampTask != null)
        {
            _trainingCampTask.cancel(false);
            _trainingCampTask = null;
        }
    }

    public boolean isInTrainingCamp()
    {
        TrainingCamp trainingCamp = TrainingCampManager.getInstance().getTrainingCamp(this);
        return trainingCamp != null && trainingCamp.isTraining() && trainingCamp.isValid(this);
    }

    @Override
    public void sendMessage(String message)
    {
        sendPacket(new SystemMessage(message));
    }

    private Location _lastClientPosition;
    private Location _lastServerPosition;

    public void setLastClientPosition(Location position)
    {
        _lastClientPosition = position;
    }

    public Location getLastClientPosition()
    {
        return _lastClientPosition;
    }

    public void setLastServerPosition(Location position)
    {
        _lastServerPosition = position;
    }

    public Location getLastServerPosition()
    {
        return _lastServerPosition;
    }

    private int _useSeed = 0;

    public void setUseSeed(int id)
    {
        _useSeed = id;
    }

    public int getUseSeed()
    {
        return _useSeed;
    }

    @Override
    public int getRelation(Player target)
    {
        int result = 0;

        if(getClan() != null)
        {
            result |= RelationChangedPacket.RELATION_CLAN_MEMBER;
            if(getClan() == target.getClan())
                result |= RelationChangedPacket.RELATION_CLAN_MATE;
            if(getClan().getAllyId() != 0)
                result |= RelationChangedPacket.RELATION_ALLY_MEMBER;
        }

        if(isClanLeader())
            result |= RelationChangedPacket.RELATION_LEADER;

        Party party = getParty();
        if(party != null && party == target.getParty())
        {
            result |= RelationChangedPacket.RELATION_HAS_PARTY;

            switch(party.getPartyMembers().indexOf(this))
            {
                case 0:
                    result |= RelationChangedPacket.RELATION_PARTYLEADER; // 0x10
                    break;
                case 1:
                    result |= RelationChangedPacket.RELATION_PARTY4; // 0x8
                    break;
                case 2:
                    result |= RelationChangedPacket.RELATION_PARTY3 + RelationChangedPacket.RELATION_PARTY2 + RelationChangedPacket.RELATION_PARTY1; // 0x7
                    break;
                case 3:
                    result |= RelationChangedPacket.RELATION_PARTY3 + RelationChangedPacket.RELATION_PARTY2; // 0x6
                    break;
                case 4:
                    result |= RelationChangedPacket.RELATION_PARTY3 + RelationChangedPacket.RELATION_PARTY1; // 0x5
                    break;
                case 5:
                    result |= RelationChangedPacket.RELATION_PARTY3; // 0x4
                    break;
                case 6:
                    result |= RelationChangedPacket.RELATION_PARTY2 + RelationChangedPacket.RELATION_PARTY1; // 0x3
                    break;
                case 7:
                    result |= RelationChangedPacket.RELATION_PARTY2; // 0x2
                    break;
                case 8:
                    result |= RelationChangedPacket.RELATION_PARTY1; // 0x1
                    break;
            }
        }

        Clan clan1 = getClan();
        Clan clan2 = target.getClan();
        if(clan1 != null && clan2 != null)
        {
            if((target.getPledgeType() != Clan.SUBUNIT_ACADEMY || target.getLevel() >= 70) && (getPledgeType() != Clan.SUBUNIT_ACADEMY || getLevel() >= 70))
                if(clan2.isAtWarWith(clan1.getClanId()))
                {
                    result |= RelationChangedPacket.RELATION_1SIDED_WAR;
                    if(clan1.isAtWarWith(clan2.getClanId()))
                        result |= RelationChangedPacket.RELATION_MUTUAL_WAR;
                }
        }

        for(Event e : getEvents())
            result = e.getRelation(this, target, result);

        return result;
    }

    /**
     * 0=White, 1=Purple, 2=PurpleBlink
     */
    protected int _pvpFlag;

    private Future<?> _PvPRegTask;
    private long _lastPvPAttack;

    public long getLastPvPAttack()
    {
        return isVioletBoy() ? System.currentTimeMillis() : _lastPvPAttack;
    }

    public void setLastPvPAttack(long time)
    {
        _lastPvPAttack = time;
    }

    @Override
    public void startPvPFlag(Creature target)
    {
        if(isPK() || isVioletBoy())
            return;

        long startTime = System.currentTimeMillis();
        if(target != null && target.getPvpFlag() != 0)
            startTime -= Config.PVP_TIME / 2;
        if(getPvpFlag() != 0 && getLastPvPAttack() >= startTime)
            return;

        _lastPvPAttack = startTime;

        updatePvPFlag(1);

        if(_PvPRegTask == null)
            _PvPRegTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new PvPFlagTask(this), 1000, 1000);
    }

    public void stopPvPFlag()
    {
        if(_PvPRegTask != null)
        {
            _PvPRegTask.cancel(false);
            _PvPRegTask = null;
        }
        updatePvPFlag(0);
    }

    public void updatePvPFlag(int value)
    {
        if(getPvpFlag() == value)
            return;

        setPvpFlag(value);

        sendStatusUpdate(true, true, StatusUpdatePacket.PVP_FLAG);

        broadcastRelation();
    }

    public void setPvpFlag(int pvpFlag)
    {
        _pvpFlag = pvpFlag;
    }

    @Override
    public int getPvpFlag()
    {
        return isVioletBoy() ? 1 : _pvpFlag;
    }

    public boolean isInDuel()
    {
        return getEvent(DuelEvent.class) != null;
    }

    private long _lastAttackPacket = 0;

    public long getLastAttackPacket()
    {
        return _lastAttackPacket;
    }

    public void setLastAttackPacket()
    {
        _lastAttackPacket = System.currentTimeMillis();
    }

    private long _lastMovePacket = 0;

    public long getLastMovePacket()
    {
        return _lastMovePacket;
    }

    public void setLastMovePacket()
    {
        _lastMovePacket = System.currentTimeMillis();
    }

    public byte[] getKeyBindings()
    {
        return _keyBindings;
    }

    public void setKeyBindings(byte[] keyBindings)
    {
        if(keyBindings == null)
            keyBindings = Util.BYTE_ARRAY_EMPTY;
        _keyBindings = keyBindings;
    }

    /**
     * Возвращает коллекцию скиллов, с учетом текущей трансформации
     */
    @Override
    public final Collection<SkillEntry> getAllSkills()
    {
        // Трансформация неактивна
        if(!isTransformed())
            return super.getAllSkills();

        // Трансформация активна
        IntObjectMap<SkillEntry> temp = new HashIntObjectMap<SkillEntry>();
        for(SkillEntry skillEntry : super.getAllSkills())
        {
            Skill skill = skillEntry.getTemplate();
            if(!skill.isActive() && !skill.isToggle())
                temp.put(skillEntry.getId(), skillEntry);
        }

        temp.putAll(_transformSkills); // Добавляем к пассивкам скилы текущей трансформации
        return temp.values();
    }

    public final void addTransformSkill(SkillEntry skillEntry)
    {
        _transformSkills.put(skillEntry.getId(), skillEntry);
    }

    public final void removeTransformSkill(SkillEntry skillEntry)
    {
        _transformSkills.remove(skillEntry.getId());
    }

    public void setAgathion(int id)
    {
        if(_agathionId == id)
            return;

        _agathionId = id;

        sendPacket(new ExUserInfoCubic(this));
        broadcastCharInfo();
    }

    public int getAgathionId()
    {
        return _agathionId;
    }

    /**
     * Возвращает количество PcBangPoint'ов даного игрока
     *
     * @return количество PcCafe Bang Points
     */
    public int getPcBangPoints()
    {
        return _pcBangPoints;
    }

    /**
     * Устанавливает количество Pc Cafe Bang Points для даного игрока
     *
     * @param val новое количество PcCafeBangPoints
     */
    public void setPcBangPoints(int val)
    {
        _pcBangPoints = val;
    }

    public void addPcBangPoints(int count, boolean doublePoints, boolean notify)
    {
        if(doublePoints)
            count *= 2;

        _pcBangPoints += count;

        if(count > 0 && notify)
            sendPacket(new SystemMessage(doublePoints ? SystemMessage.DOUBLE_POINTS_YOU_AQUIRED_S1_PC_BANG_POINT : SystemMessage.YOU_ACQUIRED_S1_PC_BANG_POINT).addNumber(count));
        sendPacket(new ExPCCafePointInfoPacket(this, count, 1, 2, 12));
    }

    public boolean reducePcBangPoints(int count)
    {
        if(_pcBangPoints < count)
            return false;

        _pcBangPoints -= count;
        sendPacket(new SystemMessage(SystemMessage.YOU_ARE_USING_S1_POINT).addNumber(count));
        sendPacket(new ExPCCafePointInfoPacket(this, 0, 1, 2, 12));
        return true;
    }

    private Location _groundSkillLoc;

    public void setGroundSkillLoc(Location location)
    {
        _groundSkillLoc = location;
    }

    public Location getGroundSkillLoc()
    {
        return _groundSkillLoc;
    }

    /**
     * Персонаж в процессе выхода из игры
     *
     * @return возвращает true если процесс выхода уже начался
     */
    public boolean isLogoutStarted()
    {
        if(_isLogout == null)
            return false;

        return _isLogout.get();
    }

    public void storePrivateStore()
    {
        int storeType = getPrivateStoreType();
        if(storeType == 0)
            unsetVar("storemode");
        else if(Config.ALT_SAVE_PRIVATE_STORE)
            setVar("storemode", storeType);

        if(_sellList != null && !_sellList.isEmpty() && (Config.ALT_SAVE_PRIVATE_STORE ))
        {
            StringBuilder items = new StringBuilder();

            for(TradeItem i : _sellList)
            {
                items.append(i.getObjectId());
                items.append(";");
                items.append(i.getCount());
                items.append(";");
                items.append(i.getOwnersPrice());
                items.append(":");
            }
            setVar("selllist", items.toString(), -1);
            String title = getSellStoreName();
            if(title != null && !title.isEmpty())
                setVar("sellstorename", title, -1);
            else
                unsetVar("sellstorename");
        }
        else
        {
            unsetVar("selllist");
            unsetVar("sellstorename");
        }

        if(_packageSellList != null && !_packageSellList.isEmpty() && (Config.ALT_SAVE_PRIVATE_STORE ))
        {
            StringBuilder items = new StringBuilder();
            for(TradeItem i : _packageSellList)
            {
                items.append(i.getObjectId());
                items.append(";");
                items.append(i.getCount());
                items.append(";");
                items.append(i.getOwnersPrice());
                items.append(":");
            }
            setVar("packageselllist", items.toString(), -1);
            String title = getPackageSellStoreName();
            if(title != null && !title.isEmpty())
                setVar("packagesellstorename", title, -1);
            else
                unsetVar("packagesellstorename");
        }
        else
        {
            unsetVar("packageselllist");
            unsetVar("packagesellstorename");
        }

        if(_buyList != null && !_buyList.isEmpty() && (Config.ALT_SAVE_PRIVATE_STORE))
        {
            StringBuilder items = new StringBuilder();
            for(TradeItem i : _buyList)
            {
                items.append(i.getItemId());
                items.append(";");
                items.append(i.getCount());
                items.append(";");
                items.append(i.getOwnersPrice());
                items.append(";");
                items.append(i.getEnchantLevel());
                items.append(":");
            }
            setVar("buylist", items.toString(), -1);
            String title = getBuyStoreName();
            if(title != null && !title.isEmpty())
                setVar("buystorename", title, -1);
            else
                unsetVar("buystorename");
        }
        else
        {
            unsetVar("buylist");
            unsetVar("buystorename");
        }

        if(_createList != null && !_createList.isEmpty() && (Config.ALT_SAVE_PRIVATE_STORE))
        {
            StringBuilder items = new StringBuilder();
            for(ManufactureItem i : _createList)
            {
                items.append(i.getRecipeId());
                items.append(";");
                items.append(i.getCost());
                items.append(":");
            }
            setVar("createlist", items.toString(), -1);
            String title = getManufactureName();
            if(title != null && !title.isEmpty())
                setVar("manufacturename", title, -1);
            else
                unsetVar("manufacturename");
        }
        else
        {
            unsetVar("createlist");
            unsetVar("manufacturename");
        }
    }

    public void restorePrivateStore()
    {
        String var;
        var = getVar("selllist");
        if(var != null)
        {
            _sellList = new CopyOnWriteArrayList<TradeItem>();
            String[] items = var.split(":");
            for(String item : items)
            {
                if(item.equals(""))
                    continue;
                String[] values = item.split(";");
                if(values.length < 3)
                    continue;

                int oId = Integer.parseInt(values[0]);
                long count = Long.parseLong(values[1]);
                long price = Long.parseLong(values[2]);

                ItemInstance itemToSell = getInventory().getItemByObjectId(oId);

                if(count < 1 || itemToSell == null)
                    continue;

                if(count > itemToSell.getCount())
                    count = itemToSell.getCount();

                TradeItem i = new TradeItem(itemToSell);
                i.setCount(count);
                i.setOwnersPrice(price);

                _sellList.add(i);
            }
            var = getVar("sellstorename");
            if(var != null)
                setSellStoreName(var);
        }
        var = getVar("packageselllist");
        if(var != null)
        {
            _packageSellList = new CopyOnWriteArrayList<TradeItem>();
            String[] items = var.split(":");
            for(String item : items)
            {
                if(item.equals(""))
                    continue;
                String[] values = item.split(";");
                if(values.length < 3)
                    continue;

                int oId = Integer.parseInt(values[0]);
                long count = Long.parseLong(values[1]);
                long price = Long.parseLong(values[2]);

                ItemInstance itemToSell = getInventory().getItemByObjectId(oId);

                if(count < 1 || itemToSell == null)
                    continue;

                if(count > itemToSell.getCount())
                    count = itemToSell.getCount();

                TradeItem i = new TradeItem(itemToSell);
                i.setCount(count);
                i.setOwnersPrice(price);

                _packageSellList.add(i);
            }
            var = getVar("packagesellstorename");
            if(var != null)
                setPackageSellStoreName(var);
        }
        var = getVar("buylist");
        if(var != null)
        {
            _buyList = new CopyOnWriteArrayList<TradeItem>();
            String[] items = var.split(":");
            for(String item : items)
            {
                if(item.equals(""))
                    continue;
                String[] values = item.split(";");
                if(values.length < 3)
                    continue;
                TradeItem i = new TradeItem();
                i.setItemId(Integer.parseInt(values[0]));
                i.setCount(Long.parseLong(values[1]));
                i.setOwnersPrice(Long.parseLong(values[2]));
                if(values.length > 3)
                    i.setEnchantLevel(Integer.parseInt(values[3]));

                _buyList.add(i);
            }
            var = getVar("buystorename");
            if(var != null)
                setBuyStoreName(var);
        }
        var = getVar("createlist");
        if(var != null)
        {
            _createList = new CopyOnWriteArrayList<ManufactureItem>();
            String[] items = var.split(":");
            for(String item : items)
            {
                if(item.equals(""))
                    continue;
                String[] values = item.split(";");
                if(values.length < 2)
                    continue;
                int recId = Integer.parseInt(values[0]);
                long price = Long.parseLong(values[1]);
                if(findRecipe(recId))
                    _createList.add(new ManufactureItem(recId, price));
            }
            var = getVar("manufacturename");
            if(var != null)
                setManufactureName(var);
        }

        int storeType = getVarInt("storemode", 0);
        if(storeType != 0)
        {
            setPrivateStoreType(storeType);
            setSitting(true);
        }
    }

    public void restoreRecipeBook()
    {
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rset = null;
        try
        {
            con = DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("SELECT id FROM character_recipebook WHERE char_id=?");
            statement.setInt(1, getObjectId());
            rset = statement.executeQuery();

            while(rset.next())
            {
                int id = rset.getInt("id");
                RecipeTemplate recipe = RecipeHolder.getInstance().getRecipeByRecipeId(id);
                registerRecipe(recipe, false);
            }
        }
        catch(Exception e)
        {
            _log.warn("count not recipe skills:" + e);
            _log.error("", e);
        }
        finally
        {
            DbUtils.closeQuietly(con, statement, rset);
        }
    }

    public List<DecoyInstance> getDecoys()
    {
        return _decoys;
    }

    public void addDecoy(DecoyInstance decoy)
    {
        _decoys.add(decoy);
    }

    public void removeDecoy(DecoyInstance decoy)
    {
        _decoys.remove(decoy);
    }

    public MountType getMountType()
    {
        return _mount == null ? MountType.NONE : _mount.getType();
    }

    @Override
    public boolean setReflection(Reflection reflection)
    {
        if(getReflection() == reflection)
            return true;

        if(!super.setReflection(reflection))
            return false;

        for(Servitor servitor : getServitors())
        {
            if(!servitor.isDead())
                servitor.setReflection(reflection);
        }

        if(!reflection.isMain())
        {
            String var = getVar("reflection");
            if(var == null || !var.equals(String.valueOf(reflection.getId())))
                setVar("reflection", String.valueOf(reflection.getId()), -1);
        }
        else
            unsetVar("reflection");

        return true;
    }

    private int _buyListId;

    public void setBuyListId(int listId)
    {
        _buyListId = listId;
    }

    public int getBuyListId()
    {
        return _buyListId;
    }

    public int getFame()
    {
        return _fame;
    }

    public void setFame(int fame, String log, boolean notify)
    {
        fame = Math.min(Config.LIM_FAME, fame);
        if(log != null && !log.isEmpty())
            Log.add(_name + "|" + (fame - _fame) + "|" + fame + "|" + log, "fame");
        if(fame > _fame && notify)
            sendPacket(new SystemMessage(SystemMessage.YOU_HAVE_ACQUIRED_S1_REPUTATION_SCORE).addNumber(fame - _fame));
        _fame = fame;
        sendChanges();
    }

    private final int _incorrectValidateCount = 0;

    public int getIncorrectValidateCount()
    {
        return _incorrectValidateCount;
    }

    public int setIncorrectValidateCount(int count)
    {
        return _incorrectValidateCount;
    }

    public int getExpandInventory()
    {
        return _expandInventory;
    }

    public void setExpandInventory(int inventory)
    {
        _expandInventory = inventory;
    }

    public int getExpandWarehouse()
    {
        return _expandWarehouse;
    }

    public void setExpandWarehouse(int warehouse)
    {
        _expandWarehouse = warehouse;
    }

    public boolean isNotShowBuffAnim()
    {
        return _notShowBuffAnim;
    }

    public void setNotShowBuffAnim(boolean value)
    {
        _notShowBuffAnim = value;
    }

    public boolean canSeeAllShouts()
    {
        return _canSeeAllShouts;
    }

    public void setCanSeeAllShouts(boolean b)
    {
        _canSeeAllShouts = b;
    }

    public void enterMovieMode()
    {
        if(isInMovie()) //already in movie
            return;

        setTarget(null);
        stopMove();
        setMovieId(-1);
        sendPacket(new CameraModePacket(1));
    }

    public void leaveMovieMode()
    {
        setMovieId(0);
        sendPacket(new CameraModePacket(0));
        broadcastCharInfo();
    }

    public void specialCamera(GameObject target, int dist, int yaw, int pitch, int time, int duration)
    {
        sendPacket(new SpecialCameraPacket(target.getObjectId(), dist, yaw, pitch, time, duration));
    }

    public void specialCamera(GameObject target, int dist, int yaw, int pitch, int time, int duration, int turn, int rise, int widescreen, int unk)
    {
        sendPacket(new SpecialCameraPacket(target.getObjectId(), dist, yaw, pitch, time, duration, turn, rise, widescreen, unk));
    }

    private int _movieId = 0;

    public void setMovieId(int id)
    {
        _movieId = id;
    }

    public int getMovieId()
    {
        return _movieId;
    }

    public boolean isInMovie()
    {
        return _movieId != 0 && !isFakePlayer();
    }

    public void startScenePlayer(SceneMovie movie)
    {
        if(isInMovie()) //already in movie
            return;

        sendActionFailed();
        setTarget(null);
        stopMove();
        setMovieId(movie.getId());
        sendPacket(movie.packet(this));
    }

    public void startScenePlayer(int movieId)
    {
        if(isInMovie()) //already in movie
            return;

        sendActionFailed();
        setTarget(null);
        stopMove();
        setMovieId(movieId);
        sendPacket(new ExStartScenePlayer(movieId));
    }

    public void endScenePlayer()
    {
        if(!isInMovie())
            return;

        setMovieId(0);
        decayMe();
        spawnMe();
    }

    public void setAutoLoot(boolean enable)
    {
        if(Config.AUTO_LOOT_INDIVIDUAL)
        {
            _autoLoot = enable;
            setVar("AutoLoot", String.valueOf(enable), -1);
        }
    }

    public void setAutoLootOnlyAdena(boolean enable)
    {
        if(Config.AUTO_LOOT_INDIVIDUAL && Config.AUTO_LOOT_ONLY_ADENA)
        {
            _autoLootOnlyAdena = enable;
            setVar("AutoLootOnlyAdena", String.valueOf(enable), -1);
        }
    }

    public void setAutoLootHerbs(boolean enable)
    {
        if(Config.AUTO_LOOT_INDIVIDUAL)
        {
            AutoLootHerbs = enable;
            setVar("AutoLootHerbs", String.valueOf(enable), -1);
        }
    }

    public boolean isAutoLootEnabled()
    {
        return _autoLoot;
    }

    public boolean isAutoLootOnlyAdenaEnabled()
    {
        return _autoLootOnlyAdena;
    }

    public boolean isAutoLootHerbsEnabled()
    {
        return AutoLootHerbs;
    }

    public final void reName(String name, boolean saveToDB)
    {
        setName(name);
        if(saveToDB)
        {
            saveNameToDB();
            OlympiadParticipiantData participant = Olympiad.getParticipantInfo(getObjectId());
            if(participant != null)
                participant.setName(name);
        }
        broadcastUserInfo(true);
    }

    public final void reName(String name)
    {
        reName(name, false);
    }

    public final void saveNameToDB()
    {
        Connection con = null;
        PreparedStatement st = null;
        try
        {
            con = DatabaseFactory.getInstance().getConnection();
            st = con.prepareStatement("UPDATE characters SET char_name = ? WHERE obj_Id = ?");
            st.setString(1, getName());
            st.setInt(2, getObjectId());
            st.executeUpdate();
        }
        catch(Exception e)
        {
            _log.error("", e);
        }
        finally
        {
            DbUtils.closeQuietly(con, st);
        }
    }

    @Override
    public Player getPlayer()
    {
        return this;
    }

    public BypassStorage getBypassStorage()
    {
        return _bypassStorage;
    }

    public int getTalismanCount()
    {
        return (int) calcStat(Stats.TALISMANS_LIMIT, 0, null, null);
    }

    public int getJewelsLimit()
    {
        return (int) calcStat(Stats.JEWELS_LIMIT, 0, null, null);
    }

    public final void disableDrop(int time)
    {
        _dropDisabled = System.currentTimeMillis() + time;
    }

    public final boolean isDropDisabled()
    {
        return _dropDisabled > System.currentTimeMillis();
    }

    private ItemInstance _petControlItem = null;

    public void setPetControlItem(int itemObjId)
    {
        setPetControlItem(getInventory().getItemByObjectId(itemObjId));
    }

    public void setPetControlItem(ItemInstance item)
    {
        _petControlItem = item;
    }

    public ItemInstance getPetControlItem()
    {
        return _petControlItem;
    }

    private AtomicBoolean isActive = new AtomicBoolean();

    public boolean isActive()
    {
        return isActive.get();
    }

    public void setActive()
    {
        setNonAggroTime(0);
        setNonPvpTime(0);

        if(isActive.getAndSet(true))
            return;

        onActive();
    }

    private void onActive()
    {
        sendPacket(SystemMsg.YOU_ARE_NO_LONGER_PROTECTED_FROM_AGGRESSIVE_MONSTERS);

        if(getPetControlItem() != null || _restoredSummons != null && !_restoredSummons.isEmpty())
        {
            ThreadPoolManager.getInstance().execute(() ->
            {
                if(getPetControlItem() != null)
                    summonPet();

                if(_restoredSummons != null && !_restoredSummons.isEmpty())
                    spawnRestoredSummons();
            });
        }
    }

    public void summonPet()
    {
        if(getPet() != null)
            return;

        ItemInstance controlItem = getInventory().getItemByObjectId(getPetControlItem().getObjectId());
        if(controlItem == null)
        {
            setPetControlItem(null);
            return;
        }

        PetData petTemplate = PetDataHolder.getInstance().getTemplateByItemId(controlItem.getItemId());
        if(petTemplate == null)
        {
            setPetControlItem(null);
            return;
        }

        NpcTemplate npcTemplate = NpcHolder.getInstance().getTemplate(petTemplate.getNpcId());
        if(npcTemplate == null)
        {
            setPetControlItem(null);
            return;
        }

        PetInstance pet = PetInstance.restore(controlItem, npcTemplate, this);
        if(pet == null)
        {
            setPetControlItem(null);
            return;
        }

        setPet(pet);
        pet.setTitle(Servitor.TITLE_BY_OWNER_NAME);

        if(!pet.isRespawned())
        {
            pet.setCurrentHp(pet.getMaxHp(), false);
            pet.setCurrentMp(pet.getMaxMp());
            pet.setCurrentFed(pet.getMaxFed(), false);
            pet.updateControlItem();
            pet.store();
        }

        pet.getInventory().restore();

        pet.setNonAggroTime(System.currentTimeMillis() + Config.NONAGGRO_TIME_ONTELEPORT);
        pet.setNonPvpTime(System.currentTimeMillis() + Config.NONPVP_TIME_ONTELEPORT);
        pet.setReflection(getReflection());
        pet.spawnMe(Location.findPointToStay(this, 50, 70));
        pet.setRunning();
        pet.setFollowMode(true);
        pet.getInventory().validateItems();

        if(pet instanceof PetBabyInstance)
            ((PetBabyInstance) pet).startBuffTask();

        getListeners().onSummonServitor(pet);
    }

    public void restoreSummons()
    {
        _restoredSummons = SummonsDAO.getInstance().restore(this);
    }

    private void spawnRestoredSummons()
    {
        if(_restoredSummons == null || _restoredSummons.isEmpty())
            return;

        for(RestoredSummon summon : _restoredSummons)
        {
            Skill skill = SkillHolder.getInstance().getSkill(summon.skillId, summon.skillLvl);
            if(skill == null)
                continue;

            if(skill instanceof Summon)
                ((Summon) skill).summon(this, null, summon);
        }
        _restoredSummons.clear();
        _restoredSummons = null;
    }

    public List<TrapInstance> getTraps()
    {
        return _traps;
    }

    public void addTrap(TrapInstance trap)
    {
        if(_traps == Collections.<TrapInstance>emptyList())
            _traps = new CopyOnWriteArrayList<TrapInstance>();
        _traps.add(trap);
    }

    public void removeTrap(TrapInstance trap)
    {
        _traps.remove(trap);
    }

    public void destroyAllTraps()
    {
        for(TrapInstance t : _traps)
            t.deleteMe();
    }

    @Override
    public PlayerListenerList getListeners()
    {
        if(listeners == null)
            synchronized (this)
            {
                if(listeners == null)
                    listeners = new PlayerListenerList(this);
            }
        return (PlayerListenerList) listeners;
    }

    @Override
    public PlayerStatsChangeRecorder getStatsRecorder()
    {
        if(_statsRecorder == null)
            synchronized (this)
            {
                if(_statsRecorder == null)
                    _statsRecorder = new PlayerStatsChangeRecorder(this);
            }
        return (PlayerStatsChangeRecorder) _statsRecorder;
    }

    private Future<?> _hourlyTask;
    private int _hoursInGame = 0;

    public int getHoursInGame()
    {
        _hoursInGame++;
        return _hoursInGame;
    }

    public void startHourlyTask()
    {
        _hourlyTask = ThreadPoolManager.getInstance().scheduleAtFixedRate(new HourlyTask(this), 3600000L, 3600000L);
    }

    public void stopHourlyTask()
    {
        if(_hourlyTask != null)
        {
            _hourlyTask.cancel(false);
            _hourlyTask = null;
        }
    }

    public long getPremiumPoints()
    {
        if(Config.IM_PAYMENT_ITEM_ID > 0)
            return ItemFunctions.getItemCount(this, Config.IM_PAYMENT_ITEM_ID);

        if(getNetConnection() != null)
            return getNetConnection().getPoints();

        return 0;
    }

    public boolean reducePremiumPoints(final int val)
    {
        if(Config.IM_PAYMENT_ITEM_ID > 0)
        {
            if(ItemFunctions.deleteItem(this, Config.IM_PAYMENT_ITEM_ID, val, true))
                return true;
            return false;
        }

        if(getNetConnection() != null)
        {
            getNetConnection().setPoints((int) (getPremiumPoints() - val));
            AuthServerCommunication.getInstance().sendPacket(new ReduceAccountPoints(getAccountName(), val));
            return true;
        }
        return false;
    }

    private boolean _agathionResAvailable = false;

    public boolean isAgathionResAvailable()
    {
        return _agathionResAvailable;
    }

    public void setAgathionRes(boolean val)
    {
        _agathionResAvailable = val;
    }

    /**
     * _userSession - испольюзуется для хранения временных переменных.
     */
    private Map<String, String> _userSession;

    public String getSessionVar(String key)
    {
        if(_userSession == null)
            return null;
        return _userSession.get(key);
    }

    public void setSessionVar(String key, String val)
    {
        if(_userSession == null)
            _userSession = new ConcurrentHashMap<String, String>();

        if(val == null || val.isEmpty())
            _userSession.remove(key);
        else
            _userSession.put(key, val);
    }

    public BlockList getBlockList()
    {
        return _blockList;
    }

    public FriendList getFriendList()
    {
        return _friendList;
    }

    public PremiumItemList getPremiumItemList()
    {
        return _premiumItemList;
    }

    public ProductHistoryList getProductHistoryList()
    {
        return _productHistoryList;
    }

    public HennaList getHennaList()
    {
        return _hennaList;
    }

    public AttendanceRewards getAttendanceRewards()
    {
        return _attendanceRewards;
    }

    public DailyMissionList getDailyMissionList()
    {
        return _dailiyMissionList;
    }

    public boolean isNotShowTraders()
    {
        return _notShowTraders;
    }

    public void setNotShowTraders(boolean notShowTraders)
    {
        _notShowTraders = notShowTraders;
    }

    public boolean isDebug()
    {
        return _debug;
    }

    public void setDebug(boolean b)
    {
        _debug = b;
    }

    public void sendItemList(boolean show)
    {
        final ItemInstance[] items = getInventory().getItems();
        final LockType lockType = getInventory().getLockType();
        final int[] lockItems = getInventory().getLockItems();

        int allSize = items.length;
        int questItemsSize = 0;
        int agathionItemsSize = 0;
        for(ItemInstance item : items)
        {
            if(item.getTemplate().isQuest())
                questItemsSize++;
        }

        sendPacket(new ItemListPacket(1, this, allSize - questItemsSize, items, show, lockType, lockItems));
        sendPacket(new ItemListPacket(2, this, allSize - questItemsSize, items, show, lockType, lockItems));
        sendPacket(new ExQuestItemListPacket(1, questItemsSize, items, lockType, lockItems));
        sendPacket(new ExQuestItemListPacket(2, questItemsSize, items, lockType, lockItems));
    }

    public int getBeltInventoryIncrease()
    {
        ItemInstance item = getInventory().getPaperdollItem(Inventory.PAPERDOLL_BELT);
        if(item != null && item.getTemplate().getAttachedSkills() != null)
        {
            for(SkillEntry skillEntry : item.getTemplate().getAttachedSkills())
            {
                for(FuncTemplate func : skillEntry.getTemplate().getAttachedFuncs())
                {
                    if(func._stat == Stats.INVENTORY_LIMIT)
                        return (int) func._value;
                }
            }
        }
        return 0;
    }

    @Override
    public boolean isPlayer()
    {
        return true;
    }

    public boolean checkCoupleAction(Player target)
    {
        if(target.getPrivateStoreType() != Player.STORE_PRIVATE_NONE)
        {
            sendPacket(new SystemMessage(SystemMessage.COUPLE_ACTION_CANNOT_C1_TARGET_IN_PRIVATE_STORE).addName(target));
            return false;
        }
        if(target.isFishing())
        {
            sendPacket(new SystemMessage(SystemMessage.COUPLE_ACTION_CANNOT_C1_TARGET_IS_FISHING).addName(target));
            return false;
        }
        if(target.isInTrainingCamp())
        {
            sendPacket(SystemMsg.YOU_CANNOT_REQUEST_TO_A_CHARACTER_WHO_IS_ENTERING_THE_TRAINING_CAMP);
            return false;
        }
        if(target.isTransformed())
        {
            sendPacket(new SystemMessage(SystemMessage.COUPLE_ACTION_CANNOT_C1_TARGET_IS_IN_TRANSFORM).addName(target));
            return false;
        }
        if(target.isInCombat() || target.isVisualTransformed())
        {
            sendPacket(new SystemMessage(SystemMessage.COUPLE_ACTION_CANNOT_C1_TARGET_IS_IN_COMBAT).addName(target));
            return false;
        }
        if(target.isInOlympiadMode())
        {
            sendPacket(new SystemMessage(SystemMessage.COUPLE_ACTION_CANNOT_C1_TARGET_IS_IN_OLYMPIAD).addName(target));
            return false;
        }
        if(target.isInSiegeZone())
        {
            sendPacket(new SystemMessage(SystemMessage.COUPLE_ACTION_CANNOT_C1_TARGET_IS_IN_SIEGE).addName(target));
            return false;
        }
        if(target.isInBoat() || target.getMountNpcId() != 0)
        {
            sendPacket(new SystemMessage(SystemMessage.COUPLE_ACTION_CANNOT_C1_TARGET_IS_IN_VEHICLE_MOUNT_OTHER).addName(target));
            return false;
        }
        if(target.isTeleporting())
        {
            sendPacket(new SystemMessage(SystemMessage.COUPLE_ACTION_CANNOT_C1_TARGET_IS_TELEPORTING).addName(target));
            return false;
        }
        if(target.isDead())
        {
            sendPacket(new SystemMessage(SystemMessage.COUPLE_ACTION_CANNOT_C1_TARGET_IS_DEAD).addName(target));
            return false;
        }
        return true;
    }

    @Override
    public void startAttackStanceTask()
    {
        startAttackStanceTask0();
        for(Servitor servitor : getServitors())
            servitor.startAttackStanceTask0();
    }

    @Override
    public void displayGiveDamageMessage(Creature target, Skill skill, int damage, Servitor servitorTransferedDamage, int transferedDamage, boolean crit, boolean miss, boolean shld, boolean blocked)
    {
        super.displayGiveDamageMessage(target, skill, damage, servitorTransferedDamage, transferedDamage, crit, miss, shld, blocked);

        if(miss)
        {
            if(skill == null)
                sendPacket(new SystemMessage(SystemMessage.C1S_ATTACK_WENT_ASTRAY).addName(this));
            else
                sendPacket(new ExMagicAttackInfo(getObjectId(), target.getObjectId(), ExMagicAttackInfo.EVADED));
            return;
        }

        if(crit)
            if(skill != null)
            {
                if(skill.isMagic())
                    sendPacket(SystemMsg.MAGIC_CRITICAL_HIT);

                sendPacket(new ExMagicAttackInfo(getObjectId(), target.getObjectId(), ExMagicAttackInfo.CRITICAL));
            }
            else
                sendPacket(new SystemMessage(SystemMessage.C1_HAD_A_CRITICAL_HIT).addName(this));

        if(blocked)
        {
            sendPacket(SystemMsg.THE_ATTACK_HAS_BEEN_BLOCKED);
            sendPacket(new ExMagicAttackInfo(getObjectId(), target.getObjectId(), target.isInvulnerable() ? ExMagicAttackInfo.IMMUNE : ExMagicAttackInfo.BLOCKED));
        }
        else if(target.isDoor() || (target instanceof SiegeToggleNpcInstance))
            sendPacket(new SystemMessagePacket(SystemMsg.YOU_HIT_FOR_S1_DAMAGE).addInteger(damage));
        else
        {
            if(servitorTransferedDamage != null && transferedDamage > 0)
            {
                SystemMessagePacket sm = new SystemMessagePacket(SystemMsg.C1_INFLICTED_S3_DAMAGE_ON_C2_AND_S4_DAMAGE_ON_THE_DAMAGE_TRANSFER_TARGET);
                sm.addName(this);
                sm.addInteger(damage);
                sm.addName(target);
                sm.addInteger(transferedDamage);
                sm.addHpChange(target.getObjectId(), getObjectId(), -damage);
                sm.addHpChange(servitorTransferedDamage.getObjectId(), getObjectId(), -transferedDamage);
                sendPacket(sm);
            }
            else
                sendPacket(new SystemMessagePacket(SystemMsg.C1_HAS_DONE_S3_POINTS_OF_DAMAGE_TO_C2).addName(this).addName(target).addInteger(damage).addHpChange(target.getObjectId(), getObjectId(), -damage));

            if(shld)
            {
                if(damage == Config.EXCELLENT_SHIELD_BLOCK_RECEIVED_DAMAGE)
                {
                    if(skill != null && skill.isMagic())
                    {
                        sendPacket(new SystemMessagePacket(SystemMsg.C1_RESISTED_C2S_MAGIC).addName(target).addName(this));
                        sendPacket(new ExMagicAttackInfo(getObjectId(), target.getObjectId(), ExMagicAttackInfo.RESISTED));
                    }
                }
                else if(damage > 0 && skill != null && skill.isMagic())
                    sendPacket(new SystemMessagePacket(SystemMsg.YOUR_OPPONENT_HAS_RESISTANCE_TO_MAGIC_THE_DAMAGE_WAS_DECREASED));
            }
        }
    }

    @Override
    public void displayReceiveDamageMessage(Creature attacker, int damage)
    {
        if(attacker != this)
            sendPacket(new SystemMessagePacket(SystemMsg.C1_HAS_RECEIVED_S3_DAMAGE_FROM_C2).addName(this).addName(attacker).addInteger(damage).addHpChange(getObjectId(), attacker.getObjectId(), -damage));
    }

    public IntObjectMap<String> getPostFriends()
    {
        return _postFriends;
    }

    public void setPostFriends(IntObjectMap<String> val)
    {
        _postFriends = val;
    }

    public void sendReuseMessage(ItemInstance item)
    {
        TimeStamp sts = getSharedGroupReuse(item.getTemplate().getReuseGroup());
        if(sts == null || !sts.hasNotPassed())
            return;

        long timeleft = sts.getReuseCurrent();
        long hours = timeleft / 3600000;
        long minutes = (timeleft - hours * 3600000) / 60000;
        long seconds = (long) Math.ceil((timeleft - hours * 3600000 - minutes * 60000) / 1000.);

        if(hours > 0)
            sendPacket(new SystemMessagePacket(item.getTemplate().getReuseType().getMessages()[2]).addItemName(item.getTemplate().getItemId()).addInteger(hours).addInteger(minutes).addInteger(seconds));
        else if(minutes > 0)
            sendPacket(new SystemMessagePacket(item.getTemplate().getReuseType().getMessages()[1]).addItemName(item.getTemplate().getItemId()).addInteger(minutes).addInteger(seconds));
        else
            sendPacket(new SystemMessagePacket(item.getTemplate().getReuseType().getMessages()[0]).addItemName(item.getTemplate().getItemId()).addInteger(seconds));
    }

    public void ask(ConfirmDlgPacket dlg, OnAnswerListener listener)
    {
        if(_askDialog != null)
            return;
        int rnd = Rnd.nextInt();
        _askDialog = new IntObjectPairImpl<OnAnswerListener>(rnd, listener);
        dlg.setRequestId(rnd);
        sendPacket(dlg);
    }

    public IntObjectPair<OnAnswerListener> getAskListener(boolean clear)
    {
        if(!clear)
            return _askDialog;
        else
        {
            IntObjectPair<OnAnswerListener> ask = _askDialog;
            _askDialog = null;
            return ask;
        }
    }

    @Override
    public boolean isDead()
    {
        return (isInOlympiadMode() || isInDuel()) ? getCurrentHp() <= 1. : super.isDead();
    }

    public boolean hasPrivilege(Privilege privilege)
    {
        return _clan != null && (getClanPrivileges() & privilege.mask()) == privilege.mask();
    }

    public MatchingRoom getMatchingRoom()
    {
        return _matchingRoom;
    }

    public void setMatchingRoom(MatchingRoom matchingRoom)
    {
        _matchingRoom = matchingRoom;
        if(matchingRoom == null)
            _matchingRoomWindowOpened = false;
    }

    public boolean isMatchingRoomWindowOpened()
    {
        return _matchingRoomWindowOpened;
    }

    public void setMatchingRoomWindowOpened(boolean b)
    {
        _matchingRoomWindowOpened = b;
    }

    public void dispelBuffs()
    {
        for(Abnormal e : getAbnormalList())
            if(e.isOffensive() && !e.getSkill().isNewbie() && e.isCancelable() && !e.getSkill().isPreservedOnDeath() && !isSpecialAbnormal(e.getSkill()))
            {
                sendPacket(new SystemMessagePacket(SystemMsg.THE_EFFECT_OF_S1_HAS_BEEN_REMOVED).addSkillName(e.getSkill().getId(), e.getSkill().getLevel()));
                e.exit();
            }


        for(Servitor servitor : getServitors())
        {
            for(Abnormal e : servitor.getAbnormalList())
            {
                if(!e.isOffensive() && !e.getSkill().isNewbie() && e.isCancelable() && !e.getSkill().isPreservedOnDeath() && !servitor.isSpecialAbnormal(e.getSkill()))
                    e.exit();
            }
        }
    }

    public void setInstanceReuse(int id, long time)
    {
        final SystemMessage msg = new SystemMessage(SystemMessage.INSTANT_ZONE_FROM_HERE__S1_S_ENTRY_HAS_BEEN_RESTRICTED_YOU_CAN_CHECK_THE_NEXT_ENTRY_POSSIBLE).addString(getName());
        sendPacket(msg);
        _instancesReuses.put(id, time);
        mysql.set("REPLACE INTO character_instances (obj_id, id, reuse) VALUES (?,?,?)", getObjectId(), id, time);
    }

    public void removeInstanceReuse(int id)
    {
        if(_instancesReuses.remove(id) != null)
            mysql.set("DELETE FROM `character_instances` WHERE `obj_id`=? AND `id`=? LIMIT 1", getObjectId(), id);
    }

    public void removeAllInstanceReuses()
    {
        _instancesReuses.clear();
        mysql.set("DELETE FROM `character_instances` WHERE `obj_id`=?", getObjectId());
    }

    public void removeInstanceReusesByGroupId(int groupId)
    {
        for(int i : InstantZoneHolder.getInstance().getSharedReuseInstanceIdsByGroup(groupId))
            if(getInstanceReuse(i) != null)
                removeInstanceReuse(i);
    }

    public Long getInstanceReuse(int id)
    {
        return _instancesReuses.get(id);
    }

    public Map<Integer, Long> getInstanceReuses()
    {
        return _instancesReuses;
    }

    private void loadInstanceReuses()
    {
        Connection con = null;
        PreparedStatement offline = null;
        ResultSet rs = null;
        try
        {
            con = DatabaseFactory.getInstance().getConnection();
            offline = con.prepareStatement("SELECT * FROM character_instances WHERE obj_id = ?");
            offline.setInt(1, getObjectId());
            rs = offline.executeQuery();
            while(rs.next())
            {
                int id = rs.getInt("id");
                long reuse = rs.getLong("reuse");
                _instancesReuses.put(id, reuse);
            }
        }
        catch(Exception e)
        {
            _log.error("", e);
        }
        finally
        {
            DbUtils.closeQuietly(con, offline, rs);
        }
    }

    public void setActiveReflection(Reflection reflection)
    {
        _activeReflection = reflection;
    }

    public Reflection getActiveReflection()
    {
        return _activeReflection;
    }

    public boolean canEnterInstance(int instancedZoneId)
    {
        InstantZone iz = InstantZoneHolder.getInstance().getInstantZone(instancedZoneId);

        if(isDead())
            return false;

        if(ReflectionManager.getInstance().size() > Config.MAX_REFLECTIONS_COUNT)
        {
            sendPacket(SystemMsg.THE_MAXIMUM_NUMBER_OF_INSTANCE_ZONES_HAS_BEEN_EXCEEDED);
            return false;
        }

        if(iz == null)
        {
            sendPacket(SystemMsg.SYSTEM_ERROR);
            return false;
        }

        if(ReflectionManager.getInstance().getCountByIzId(instancedZoneId) >= iz.getMaxChannels())
        {
            sendPacket(SystemMsg.THE_MAXIMUM_NUMBER_OF_INSTANCE_ZONES_HAS_BEEN_EXCEEDED);
            return false;
        }

        return iz.getEntryType(this).canEnter(this, iz);
    }

    public boolean canReenterInstance(int instancedZoneId)
    {
        if((getActiveReflection() != null && getActiveReflection().getInstancedZoneId() != instancedZoneId) || !getReflection().isMain())
        {
            sendPacket(SystemMsg.YOU_HAVE_ENTERED_ANOTHER_INSTANCE_ZONE_THEREFORE_YOU_CANNOT_ENTER_CORRESPONDING_DUNGEON);
            return false;
        }
        InstantZone iz = InstantZoneHolder.getInstance().getInstantZone(instancedZoneId);
        if(iz.isDispelBuffs())
            dispelBuffs();
        return iz.getEntryType(this).canReEnter(this, iz);
    }

    public int getBattlefieldChatId()
    {
        return _battlefieldChatId;
    }

    public void setBattlefieldChatId(int battlefieldChatId)
    {
        _battlefieldChatId = battlefieldChatId;
    }

    @Override
    public void broadCast(IBroadcastPacket... packet)
    {
        sendPacket(packet);
    }

    @Override
    public int getMemberCount()
    {
        return 1;
    }

    @Override
    public Player getGroupLeader()
    {
        return this;
    }

    @Override
    public Iterator<Player> iterator()
    {
        return Collections.singleton(this).iterator();
    }

    public PlayerGroup getPlayerGroup()
    {
        if(getParty() != null)
        {
            if(getParty().getCommandChannel() != null)
                return getParty().getCommandChannel();
            else
                return getParty();
        }
        else
            return this;
    }

    public boolean isActionBlocked(String action)
    {
        return _blockedActions.contains(action);
    }

    public void blockActions(String... actions)
    {
        Collections.addAll(_blockedActions, actions);
    }

    public void unblockActions(String... actions)
    {
        for(String action : actions)
            _blockedActions.remove(action);
    }

    public OlympiadGame getOlympiadGame()
    {
        return _olympiadGame;
    }

    public void setOlympiadGame(OlympiadGame olympiadGame)
    {
        _olympiadGame = olympiadGame;
    }

    public void addRadar(int x, int y, int z)
    {
        sendPacket(new RadarControlPacket(0, 1, x, y, z));
    }

    public void addRadarWithMap(int x, int y, int z)
    {
        sendPacket(new RadarControlPacket(0, 2, x, y, z));
    }

    public PetitionMainGroup getPetitionGroup()
    {
        return _petitionGroup;
    }

    public void setPetitionGroup(PetitionMainGroup petitionGroup)
    {
        _petitionGroup = petitionGroup;
    }

    public int getLectureMark()
    {
        return _lectureMark;
    }

    public void setLectureMark(int lectureMark)
    {
        _lectureMark = lectureMark;
    }

    public boolean isUserRelationActive()
    {
        return _enableRelationTask == null;
    }

    public void startEnableUserRelationTask(long time, SiegeEvent<?, ?> siegeEvent)
    {
        if(_enableRelationTask != null)
            return;

        _enableRelationTask = ThreadPoolManager.getInstance().schedule(new EnableUserRelationTask(this, siegeEvent), time);
    }

    public void stopEnableUserRelationTask()
    {
        if(_enableRelationTask != null)
        {
            _enableRelationTask.cancel(false);
            _enableRelationTask = null;
        }
    }

    public void broadcastRelation()
    {
        if(!isVisible())
            return;

        for(Player target : World.getAroundObservers(this))
        {
            if(isInvisible(target))
                continue;

            RelationChangedPacket relationChanged = new RelationChangedPacket(this, target);
            for(Servitor servitor : getServitors())
                relationChanged.add(servitor, target);

            target.sendPacket(relationChanged);
        }
    }

    private int[] _recentProductList = null;

    public int[] getRecentProductList()
    {
        if(_recentProductList == null)
        {
            String value = getVar(RECENT_PRODUCT_LIST_VAR);
            if(value == null)
                return null;

            String[] products_str = value.split(";");
            IntList result = new ArrayIntList();
            for(int i = 0; i < products_str.length; i++)
            {
                int productId = Integer.parseInt(products_str[i]);
                if(ProductDataHolder.getInstance().getProduct(productId) == null)
                    continue;

                result.add(productId);
            }
            _recentProductList = result.toArray();
        }
        return _recentProductList;
    }

    public void updateRecentProductList(final int productId)
    {
        if(_recentProductList == null)
        {
            _recentProductList = new int[1];
            _recentProductList[0] = productId;
        }
        else
        {
            IntList newProductList = new ArrayIntList(_recentProductList.length);
            newProductList.add(productId);
            for(int i = 0; i < _recentProductList.length; i++) {
                if(newProductList.size() >= Config.IM_MAX_ITEMS_IN_RECENT_LIST)
                    break;

                int itemId = _recentProductList[i];

                if(newProductList.contains(itemId))
                    continue;

                newProductList.add(itemId);
            }

            _recentProductList = newProductList.toArray();
        }

        String valueToUpdate = "";
        for(int itemId : _recentProductList)
        {
            valueToUpdate += itemId + ";";
        }
        setVar(RECENT_PRODUCT_LIST_VAR, valueToUpdate, -1);
    }

    @Override
    public int getINT()
    {
        return Math.max(getTemplate().getMinINT(), Math.min(getTemplate().getMaxINT(), super.getINT()));
    }

    @Override
    public int getSTR()
    {
        return Math.max(getTemplate().getMinSTR(), Math.min(getTemplate().getMaxSTR(), super.getSTR()));
    }

    @Override
    public int getCON()
    {
        return Math.max(getTemplate().getMinCON(), Math.min(getTemplate().getMaxCON(), super.getCON()));
    }

    @Override
    public int getMEN()
    {
        return Math.max(getTemplate().getMinMEN(), Math.min(getTemplate().getMaxMEN(), super.getMEN()));
    }

    @Override
    public int getDEX()
    {
        return Math.max(getTemplate().getMinDEX(), Math.min(getTemplate().getMaxDEX(), super.getDEX()));
    }

    @Override
    public int getWIT()
    {
        return Math.max(getTemplate().getMinWIT(), Math.min(getTemplate().getMaxWIT(), super.getWIT()));
    }

    public BookMarkList getBookMarkList()
    {
        return _bookmarks;
    }

    public AntiFlood getAntiFlood()
    {
        return _antiFlood;
    }

    public int getNpcDialogEndTime()
    {
        return _npcDialogEndTime;
    }

    public void setNpcDialogEndTime(int val)
    {
        _npcDialogEndTime = val;
    }

    @Override
    public boolean useItem(ItemInstance item, boolean ctrlPressed, boolean force)
    {
        if(item == null)
            return false;

        ItemTemplate template = item.getTemplate();
        IItemHandler handler = template.getHandler();
        if(handler == null)
        {
            //logger.warn("Fail while use item. Not found handler for item ID[" + item.getItemId() + "]!");
            return false;
        }

        boolean success = force ? handler.forceUseItem(this, item, ctrlPressed) : handler.useItem(this, item, ctrlPressed);
        if(success)
        {
            long nextTimeUse = template.getReuseType().next(item);
            if(nextTimeUse > System.currentTimeMillis())
            {
                TimeStamp timeStamp = new TimeStamp(item.getItemId(), nextTimeUse, template.getReuseDelay());
                addSharedGroupReuse(template.getReuseGroup(), timeStamp);

                if(template.getReuseDelay() > 0)
                    sendPacket(new ExUseSharedGroupItem(template.getDisplayReuseGroup(), timeStamp));
            }
        }
        return success;
    }

    public int getSkillsElementID()
    {
        return (int) calcStat(Stats.SKILLS_ELEMENT_ID, -1, null, null);
    }

    public Location getStablePoint()
    {
        return _stablePoint;
    }

    public void setStablePoint(Location point)
    {
        _stablePoint = point;
    }

    public boolean isInSameParty(Player target)
    {
        return getParty() != null && target.getParty() != null && getParty() == target.getParty();
    }

    public boolean isInSameChannel(Player target)
    {
        Party activeCharP = getParty();
        Party targetP = target.getParty();
        if(activeCharP != null && targetP != null)
        {
            CommandChannel chan = activeCharP.getCommandChannel();
            if(chan != null && chan == targetP.getCommandChannel())
                return true;
        }
        return false;
    }

    public boolean isInSameClan(Player target)
    {
        return getClanId() != 0 && getClanId() == target.getClanId();
    }

    public final boolean isInSameAlly(Player target)
    {
        return getAllyId() != 0 && getAllyId() == target.getAllyId();
    }

    public boolean isInPvPEvent()
    {
        PvPEvent event = getEvent(PvPEvent.class);
        if(event != null && event.isBattleActive())
            return true;

        return false;
    }

    public boolean isRelatedTo(Creature character)
    {
        if(character == this)
            return true;

        if(character.isServitor())
        {
            if(isMyServitor(character.getObjectId()))
                return true;
            else if(character.getPlayer() != null)
            {
                Player Spc = character.getPlayer();
                if(isInSameParty(Spc) || isInSameChannel(Spc) || isInSameClan(Spc) || isInSameAlly(Spc))
                    return true;
            }
        }
        else if(character.isPlayer())
        {
            Player pc = character.getPlayer();
            if(isInSameParty(pc) || isInSameChannel(pc) || isInSameClan(pc) || isInSameAlly(pc))
                return true;
        }
        return false;
    }

    public boolean isAutoSearchParty()
    {
        return _autoSearchParty;
    }

    public void enableAutoSearchParty()
    {
        _autoSearchParty = true;
        PartySubstituteManager.getInstance().addWaitingPlayer(this);
        sendPacket(ExWaitWaitingSubStituteInfo.OPEN);
    }

    public void disablePartySearch(boolean disableFlag)
    {
        if(_autoSearchParty)
        {
            PartySubstituteManager.getInstance().removeWaitingPlayer(this);
            sendPacket(ExWaitWaitingSubStituteInfo.CLOSE);
            _autoSearchParty = !disableFlag;
        }
    }

    public boolean refreshPartySearchStatus(boolean sendMsg)
    {
        if(!mayPartySearch(false,sendMsg))
        {
            disablePartySearch(false);
            return false;
        }

        if(isAutoSearchParty())
        {
            enableAutoSearchParty();
            return true;
        }
        return false;
    }

    public boolean mayPartySearch(boolean first, boolean msg)
    {
        if(getParty() != null)
            return false;

        if(isPK())
        {
            if(msg)
            {
                if(first)
                    sendPacket(SystemMsg.WAITING_LIST_REGISTRATION_IS_NOT_ALLOWED_WHILE_THE_CURSED_SWORD_IS_BEING_USED_OR_THE_STATUS_IS_IN_A_CHAOTIC_STATE);
                else
                    sendPacket(SystemMsg.WAITING_LIST_REGISTRATION_IS_CANCELLED_BECAUSE_THE_CURSED_SWORD_IS_BEING_USED_OR_THE_STATUS_IS_IN_A_CHAOTIC_STATE);
            }
            return false;
        }

        if(isInDuel() && getTeam() != TeamType.NONE)
        {
            if(msg)
            {
                if(first)
                    sendPacket(SystemMsg.YOU_CANNOT_REGISTER_IN_THE_WAITING_LIST_DURING_A_DUEL);
                else
                    sendPacket(SystemMsg.WAITING_LIST_REGISTRATION_IS_CANCELLED_BECAUSE_YOU_ARE_IN_A_DUEL);
            }
            return false;
        }

        if(isInOlympiadMode())
        {
            if(msg)
            {
                if(first)
                    sendPacket(SystemMsg.YOU_CANNOT_REGISTER_IN_THE_WAITING_LIST_WHILE_PARTICIPATING_IN_OLYMPIAD);
                else
                    sendPacket(SystemMsg.WAITING_LIST_REGISTRATION_IS_CANCELLED_BECAUSE_YOU_ARE_CURRENTLY_PARTICIPATING_IN_OLYMPIAD);
            }
            return false;
        }

        if(isInSiegeZone())
        {
            if(msg && first)
                sendPacket(SystemMsg.YOU_CANNOT_REGISTER_IN_THE_WAITING_LIST_WHILE_BEING_INSIDE_OF_A_BATTLEGROUND_CASTLE_SIEGEFORTRESS_SIEGETERRITORY_WAR);

            return false;
        }

        if(isInZoneBattle() || getReflectionId() != 0)
        {
            if(msg && first)
                sendPacket(SystemMsg.YOU_CANNOT_REGISTER_IN_THE_WAITING_LIST_WHILE_PARTICIPATING_IN_BLOCK_CHECKERCOLISEUMKRATEIS_CUBE);

            return false;
        }

        if(isInZone(ZoneType.no_escape) || isInZone(ZoneType.epic))
            return false;

        if(!Config.ENABLE_PARTY_SEARCH)
            return false;
        return true;
    }

    public void startSubstituteTask()
    {
        if(!isPartySubstituteStarted())
        {
            _substituteTask = PartySubstituteManager.getInstance().SubstituteSearchTask(this);
            sendUserInfo();
            if(isInParty())
                getParty().getPartyLeader().sendPacket(new PartySmallWindowUpdatePacket(this));
        }
    }

    public void stopSubstituteTask()
    {
        if(isPartySubstituteStarted())
        {
            PartySubstituteManager.getInstance().removePartyMember(this);
            _substituteTask.cancel(true);
            sendUserInfo();
            if(isInParty())
                getParty().getPartyLeader().sendPacket(new PartySmallWindowUpdatePacket(this));
        }
    }

    public boolean isPartySubstituteStarted()
    {
        return getParty() != null && _substituteTask != null && !_substituteTask.isDone() && !_substituteTask.isCancelled();
    }

    @Override
    public int getSkillLevel(int skillId)
    {
        switch(skillId)
        {
            case 1566:	// Смена Класса
            case 1567:	// Смена Класса
            case 1568:	// Смена Класса
            case 1569:	// Смена Класса
            case 17192:	// Отображение Головного Убора
                return 1;

        }
        return super.getSkillLevel(skillId);
    }

    public SymbolInstance getSymbol()
    {
        return _symbol;
    }

    public void setSymbol(SymbolInstance symbol)
    {
        _symbol = symbol;
    }

    public void setRegisteredInEvent(boolean inEvent)
    {
        _registeredInEvent = inEvent;
    }

    public boolean isRegisteredInEvent()
    {
        return _registeredInEvent;
    }

    private boolean checkActiveToggleEffects()
    {
        boolean dispelled = false;
        for(Abnormal effect : getAbnormalList())
        {
            Skill skill = effect.getSkill();
            if(skill == null)
                continue;

            if(!skill.isToggle())
                continue;

            if(getAllSkills().contains(skill))
                continue;

            effect.exit();
        }
        return dispelled;
    }

    @Override
    public Servitor getServitorForTransfereDamage(double transferDamage)
    {
        SummonInstance summon = getSummon();
        if(summon == null || summon.isDead() || summon.getCurrentHp() < transferDamage)
            return null;

        if(summon.isInRangeZ(this, 1200))
            return summon;

        return null;
    }

    @Override
    public double getDamageForTransferToServitor(double damage)
    {
        final double transferToSummonDam = calcStat(Stats.TRANSFER_TO_SUMMON_DAMAGE_PERCENT, 0.);
        if(transferToSummonDam > 0)
            return (damage * transferToSummonDam) * .01;
        return 0.;
    }

    public boolean canFixedRessurect()
    {
        if(getPlayerAccess().ResurectFixed)
            return true;

        if(!isInSiegeZone())
        {
            if(getInventory().getCountOf(10649) > 0)
                return true;
            if(getInventory().getCountOf(13300) > 0)
                return true;
        }
        else
        {
            int level = getLevel();
            if(level <= 19 && getInventory().getCountOf(8515) > 0)
                return true;

            if(level <= 39 && getInventory().getCountOf(8516) > 0)
                return true;

            if(level <= 51 && getInventory().getCountOf(8517) > 0)
                return true;

            if(level <= 60 && getInventory().getCountOf(8518) > 0)
                return true;

            if(level <= 75 && getInventory().getCountOf(8519) > 0)
                return true;

            if(level <= 84 && getInventory().getCountOf(8520) > 0)
                return true;
        }

        return false;
    }

    @Override
    public double getLevelBonus()
    {
        if(getTransform() != null && getTransform().getLevelBonus(getLevel()) > 0)
            return getTransform().getLevelBonus(getLevel());

        return super.getLevelBonus();
    }

    @Override
    public PlayerBaseStats getBaseStats()
    {
        if(_baseStats == null)
            _baseStats = new PlayerBaseStats(this);
        return (PlayerBaseStats) _baseStats;
    }

    @Override
    public PlayerFlags getFlags()
    {
        if(_statuses == null)
            _statuses = new PlayerFlags(this);

        return (PlayerFlags) _statuses;
    }

    public final String getVisibleName(Player receiver)
    {
        String name;
        for(Event event : getEvents())
        {
            name = event.getVisibleName(this, receiver);
            if(name != null)
                return name;
        }

        return getName();
    }

    public final String getVisibleTitle(Player receiver)
    {
        if(getPrivateStoreType() != Player.STORE_PRIVATE_NONE)
        {
            if(getReflection() == ReflectionManager.GIRAN_HARBOR)
                return "";

            if(getReflection() == ReflectionManager.PARNASSUS)
                return "";
        }

        String title;
        for(Event event : getEvents())
        {
            title = event.getVisibleTitle(this, receiver);
            if(title != null)
                return title;
        }

        return getTitle();
    }

    public final int getVisibleNameColor(Player receiver) {
        Integer color;
        for(Event event : getEvents())
        {
            color = event.getVisibleNameColor(this, receiver);
            if(color != null)
                return color.intValue();
        }

        int premiumNameColor = getPremiumAccount().getProperties().getNameColor();
        if(premiumNameColor != -1)
            return premiumNameColor;

        return getNameColor();
    }

    public final int getVisibleTitleColor(Player receiver)
    {

        Integer color;
        for(Event event : getEvents())
        {
            color = event.getVisibleTitleColor(this, receiver);
            if(color != null)
                return color.intValue();
        }

        int premiumTitleColor = getPremiumAccount().getProperties().getTitleColor();
        if(premiumTitleColor != -1)
            return premiumTitleColor;

        return getTitleColor();
    }

    public final boolean isPledgeVisible(Player receiver)
    {
        if(getPrivateStoreType() != Player.STORE_PRIVATE_NONE)
        {
            if(getReflection() == ReflectionManager.GIRAN_HARBOR)
                return false;

            if(getReflection() == ReflectionManager.PARNASSUS)
                return false;
        }

        for(Event event : getEvents())
        {
            if(!event.isPledgeVisible(this, receiver))
                return false;
        }

        return true;
    }

    public void checkAndDeleteOlympiadItems()
    {
        int rank = Olympiad.getRank(this);
        if(rank != 2 && rank != 3)
            ItemFunctions.deleteItemsEverywhere(this, ItemTemplate.ITEM_ID_FAME_CLOAK);

        if(!isHero())
        {
            ItemFunctions.deleteItemsEverywhere(this, ItemTemplate.ITEM_ID_HERO_WING);
            ItemFunctions.deleteItemsEverywhere(this, ItemTemplate.ITEM_ID_HERO_CLOAK);
            for(int itemId : ItemTemplate.HERO_WEAPON_IDS)
                ItemFunctions.deleteItemsEverywhere(this, itemId);
        }
    }

    public double getEnchantChanceModifier()
    {
        return calcStat(Stats.ENCHANT_CHANCE_MODIFIER);
    }

    @Override
    public boolean isSpecialAbnormal(Skill skill)
    {
        if(getClan() != null && getClan().isSpecialAbnormal(skill))
            return true;

        if(skill.isNecessaryToggle())
            return true;

        int skillId = skill.getId();

        if(skillId == 7008 || skillId == 6038 || skillId == 6039 || skillId == 6040 || skillId == 6055 || skillId == 6056 || skillId == 6057 || skillId == 6058)
            return true;

        return false;
    }

    @Override
    public void removeAllSkills()
    {
        _dontRewardSkills = true;

        super.removeAllSkills();

        _dontRewardSkills = false;
    }

    public void setLastMultisellBuyTime(long val)
    {
        _lastMultisellBuyTime = val;
    }

    public long getLastMultisellBuyTime()
    {
        return _lastMultisellBuyTime;
    }

    public void setLastEnchantItemTime(long val)
    {
        _lastEnchantItemTime = val;
    }

    public long getLastEnchantItemTime()
    {
        return _lastEnchantItemTime;
    }

    public void setLastAttributeItemTime(long val)
    {
        _lastAttributeItemTime = val;
    }

    public long getLastAttributeItemTime()
    {
        return _lastAttributeItemTime;
    }

    public void checkLevelUpReward(boolean onRestore)
    {
        int lastRewarded = getVarInt(LVL_UP_REWARD_VAR);
        int lastRewardedByClass = getVarInt(LVL_UP_REWARD_VAR + "_" + getActiveSubClass().getIndex());
        int playerLvl = getLevel();
        boolean rewarded = false;
        int clanPoints = 0;
        if(playerLvl > lastRewarded)
        {
            for(int i = playerLvl; i > lastRewarded; i--)
            {
                IntLongMap items = LevelUpRewardHolder.getInstance().getRewardData(i);
                if(items != null)
                {
                    for (IntLongPair pair : items.entrySet()) {
                        getPremiumItemList().add(new PremiumItem(pair.getKey(), pair.getValue(), ""));
                        rewarded = true;
                    }
                }
            }
            setVar(LVL_UP_REWARD_VAR, playerLvl);
        }

        if(playerLvl > lastRewardedByClass)
        {
            for(int i = playerLvl; i > lastRewardedByClass; i--)
            {
                if(getClan() != null && getClan().getLevel() >= 3)
                {
                    int earnedPoints = 0;
                    switch(i)
                    {
                        case 20:
                            earnedPoints = 2;
                            break;
                        case 21:
                            earnedPoints = 2;
                            break;
                        case 22:
                            earnedPoints = 2;
                            break;
                        case 23:
                            earnedPoints = 2;
                            break;
                        case 24:
                            earnedPoints = 2;
                            break;
                        case 25:
                            earnedPoints = 2;
                            break;
                        case 26:
                            earnedPoints = 4;
                            break;
                        case 27:
                            earnedPoints = 4;
                            break;
                        case 28:
                            earnedPoints = 4;
                            break;
                        case 29:
                            earnedPoints = 4;
                            break;
                        case 30:
                            earnedPoints = 4;
                            break;
                        case 31:
                            earnedPoints = 6;
                            break;
                        case 32:
                            earnedPoints = 6;
                            break;
                        case 33:
                            earnedPoints = 6;
                            break;
                        case 34:
                            earnedPoints = 6;
                            break;
                        case 35:
                            earnedPoints = 6;
                            break;
                        case 36:
                            earnedPoints = 8;
                            break;
                        case 37:
                            earnedPoints = 8;
                            break;
                        case 38:
                            earnedPoints = 8;
                            break;
                        case 39:
                            earnedPoints = 8;
                            break;
                        case 40:
                            earnedPoints = 8;
                            break;
                        case 41:
                            earnedPoints = 10;
                            break;
                        case 42:
                            earnedPoints = 10;
                            break;
                        case 43:
                            earnedPoints = 10;
                            break;
                        case 44:
                            earnedPoints = 10;
                            break;
                        case 45:
                            earnedPoints = 10;
                            break;
                        case 46:
                            earnedPoints = 12;
                            break;
                        case 47:
                            earnedPoints = 12;
                            break;
                        case 48:
                            earnedPoints = 12;
                            break;
                        case 49:
                            earnedPoints = 12;
                            break;
                        case 50:
                            earnedPoints = 12;
                            break;
                        case 51:
                            earnedPoints = 14;
                            break;
                        case 52:
                            earnedPoints = 14;
                            break;
                        case 53:
                            earnedPoints = 14;
                            break;
                        case 54:
                            earnedPoints = 14;
                            break;
                        case 55:
                            earnedPoints = 14;
                            break;
                        case 56:
                            earnedPoints = 16;
                            break;
                        case 57:
                            earnedPoints = 16;
                            break;
                        case 58:
                            earnedPoints = 16;
                            break;
                        case 59:
                            earnedPoints = 16;
                            break;
                        case 60:
                            earnedPoints = 16;
                            break;
                        case 61:
                            earnedPoints = 18;
                            break;
                        case 62:
                            earnedPoints = 18;
                            break;
                        case 63:
                            earnedPoints = 18;
                            break;
                        case 64:
                            earnedPoints = 18;
                            break;
                        case 65:
                            earnedPoints = 18;
                            break;
                        case 66:
                            earnedPoints = 21;
                            break;
                        case 67:
                            earnedPoints = 21;
                            break;
                        case 68:
                            earnedPoints = 21;
                            break;
                        case 69:
                            earnedPoints = 21;
                            break;
                        case 70:
                            earnedPoints = 21;
                            break;
                        case 71:
                            earnedPoints = 25;
                            break;
                        case 72:
                            earnedPoints = 25;
                            break;
                        case 73:
                            earnedPoints = 25;
                            break;
                        case 74:
                            earnedPoints = 25;
                            break;
                        case 75:
                            earnedPoints = 25;
                            break;
                    }

                    if(earnedPoints > 0)
                        clanPoints += earnedPoints;
                }
            }

            setVar(LVL_UP_REWARD_VAR + "_" + getActiveSubClass().getIndex(), playerLvl);
        }

        if(rewarded)
            sendPacket(ExNotifyPremiumItem.STATIC);

        if(clanPoints > 0)
            getClan().incReputation(clanPoints, true, "ClanMemberLvlUp");
    }

    public void checkHeroSkills()
    {
        final boolean hero = isHero() && isBaseClassActive();
        for(SkillLearn sl : SkillAcquireHolder.getInstance().getAvailableMaxLvlSkills(hero ? this : null, AcquireType.HERO))
        {
            SkillEntry skillEntry = SkillHolder.getInstance().getSkillEntry(sl.getId(), sl.getLevel());
            if(skillEntry == null)
                continue;

            if(hero)
            {
                if(getSkillLevel(skillEntry.getId()) < skillEntry.getLevel())
                    addSkill(skillEntry, true);
            }
            else
                removeSkill(skillEntry, true);
        }
    }

    public void activateHeroSkills(boolean activate)
    {
        for(SkillLearn sl : SkillAcquireHolder.getInstance().getAvailableMaxLvlSkills(null, AcquireType.HERO))
        {
            Skill skill = SkillHolder.getInstance().getSkill(sl.getId(), sl.getLevel());
            if(skill == null)
                continue;

            if(!activate)
                addUnActiveSkill(skill);
            else
                removeUnActiveSkill(skill);
        }
    }

    public void giveGMSkills()
    {
        if(!isGM())
            return;

        for(SkillLearn sl : SkillAcquireHolder.getInstance().getAvailableMaxLvlSkills(this, AcquireType.GM))
        {
            SkillEntry skillEntry = SkillHolder.getInstance().getSkillEntry(sl.getId(), sl.getLevel());
            if(skillEntry == null)
                continue;

            if(getSkillLevel(skillEntry.getId()) < skillEntry.getLevel())
                addSkill(skillEntry, true);
        }
    }

    private long _blockUntilTime = 0;

    public void setblockUntilTime(long time)
    {
        _blockUntilTime = time;
    }

    public long getblockUntilTime()
    {
        return _blockUntilTime;
    }

    public int getWorldChatPoints() {
        var serverSettings  = getSettings(ServerSettings.class);
        if(hasPremiumAccount())
            return Math.max(0, serverSettings.premiumWorldChatPointsPerDay() - _usedWorldChatPoints);

        return Math.max(0, serverSettings.worldChatPointsPerDay() - _usedWorldChatPoints);
    }

    public int getUsedWorldChatPoints()
    {
        return _usedWorldChatPoints;
    }

    public void setUsedWorldChatPoints(int value)
    {
        _usedWorldChatPoints = value;
    }

    public int getArmorSetEnchant()
    {
        return _armorSetEnchant;
    }

    public void setArmorSetEnchant(int value)
    {
        _armorSetEnchant = value;
    }

    public boolean hideHeadAccessories()
    {
        return _hideHeadAccessories;
    }

    public void setHideHeadAccessories(boolean value)
    {
        _hideHeadAccessories = value;
    }

    public ItemInstance getSynthesisItem1()
    {
        return _synthesisItem1;
    }

    public void setSynthesisItem1(ItemInstance value)
    {
        _synthesisItem1 = value;
    }

    public ItemInstance getSynthesisItem2()
    {
        return _synthesisItem2;
    }

    public void setSynthesisItem2(ItemInstance value)
    {
        _synthesisItem2 = value;
    }

    public String getHWID()
    {
        return getNetConnection().getHWID();
    }

    public double getMPCostDiff(Skill.SkillMagicType type)
    {
        double value = 0;
        switch(type)
        {
            case PHYSIC:
            {
                value = (calcStat(Stats.MP_PHYSICAL_SKILL_CONSUME, 10000) / 10000 * 100) - 100;
                break;
            }
            case MAGIC:
            {
                value = (calcStat(Stats.MP_MAGIC_SKILL_CONSUME, 10000) / 10000 * 100) - 100;
                break;
            }
            case MUSIC:
            {
                value = (calcStat(Stats.MP_DANCE_SKILL_CONSUME, 10000) / 10000 * 100) - 100;
                break;
            }
        }

        return value;
    }

    public int getExpertiseIndex()
    {
        return getSkillLevel(239, 0);
    }

    private final ConcurrentHashMap<ListenerHookType, CopyOnWriteArraySet<ListenerHook>> scriptHookTypeList = new ConcurrentHashMap<ListenerHookType, CopyOnWriteArraySet<ListenerHook>>();

    public void addListenerHook(ListenerHookType type, ListenerHook hook)
    {
        if(!scriptHookTypeList.containsKey(type))
        {
            CopyOnWriteArraySet<ListenerHook> hooks = new CopyOnWriteArraySet<ListenerHook>();
            hooks.add(hook);
            scriptHookTypeList.put(type, hooks);
        }
        else
        {
            CopyOnWriteArraySet<ListenerHook> hooks = scriptHookTypeList.get(type);
            hooks.add(hook);
        }
    }

    public void removeListenerHookType(ListenerHookType type, ListenerHook hook)
    {
        if(scriptHookTypeList.containsKey(type))
        {
            Set<ListenerHook> hooks = scriptHookTypeList.get(type);
            hooks.remove(hook);
        }
    }

    public Set<ListenerHook> getListenerHooks(ListenerHookType type)
    {
        Set<ListenerHook> hooks = scriptHookTypeList.get(type);
        if(hooks == null)
            hooks = Collections.emptySet();
        return hooks;
    }

    @Override
    public boolean isFakePlayer()
    {
        return getAI() != null && getAI().isFake();
    }

    public OptionDataTemplate addOptionData(OptionDataTemplate optionData)
    {
        if(optionData == null)
            return null;

        OptionDataTemplate oldOptionData = _options.get(optionData.getId());
        if(optionData == oldOptionData)
            return oldOptionData;

        _options.put(optionData.getId(), optionData);
        addTriggers(optionData);
        addStatFuncs(optionData.getStatFuncs(optionData));

        for(SkillEntry skillEntry : optionData.getSkills())
            addSkill(skillEntry);

        return oldOptionData;
    }

    public OptionDataTemplate removeOptionData(int id)
    {
        OptionDataTemplate oldOptionData = _options.remove(id);
        if(oldOptionData != null)
        {
            removeTriggers(oldOptionData);
            removeStatsOwner(oldOptionData);
            for(SkillEntry skillEntry : oldOptionData.getSkills())
                removeSkill(skillEntry);
        }
        return oldOptionData;
    }

    public long getReceivedExp()
    {
        return _receivedExp;
    }

    @Override
    protected void onSpawn()
    {
        super.onSpawn();
        getAI().notifyEvent(CtrlEvent.EVT_SPAWN);
    }

    @Override
    protected void onDespawn()
    {
        getAI().notifyEvent(CtrlEvent.EVT_DESPAWN);
        super.onDespawn();
    }

    public void setQuestZoneId(int id)
    {
        _questZoneId = id;
    }

    public int getQuestZoneId()
    {
        return _questZoneId;
    }

    @Override
    protected void onAddSkill(SkillEntry skillEntry)
    {
        Skill skill = skillEntry.getTemplate();
        if(skill.isNecessaryToggle())
        {
            if(skill.isToggleGrouped() && skill.getToggleGroupId() > 0)
            {
                for(Abnormal abnormal : getAbnormalList())
                {
                    if(abnormal.getSkill().isToggleGrouped() && abnormal.getSkill().getToggleGroupId() == skill.getToggleGroupId())
                        return;
                }
            }
            forceUseSkill(skill, this);
        }
    }

    public void setSelectedMultiClassId(ClassId classId)
    {
        _selectedMultiClassId = classId;
    }

    public ClassId getSelectedMultiClassId()
    {
        return _selectedMultiClassId;
    }

    @Override
    public int getPAtk(Creature target)
    {
        return (int) (super.getPAtk(target) * Config.PLAYER_P_ATK_MODIFIER);
    }

    @Override
    public int getMAtk(Creature target, Skill skill)
    {
        return (int) (super.getMAtk(target, skill) * Config.PLAYER_M_ATK_MODIFIER);
    }

    @Override
    public void onZoneEnter(Zone zone)
    {
        if(zone.getType() == ZoneType.SIEGE)
        {
            for(CastleSiegeEvent siegeEvent : zone.getEvents(CastleSiegeEvent.class))
            {
                if(containsEvent(siegeEvent))
                    siegeEvent.addVisitedParticipant(this);
            }
        }

        if(zone.getEnteringMessageId() != 0)
            sendPacket(new SystemMessage(zone.getEnteringMessageId()));

        if(zone.getTemplate().getBlockedActions() != null)
            blockActions(zone.getTemplate().getBlockedActions());

        if(zone.getType() == ZoneType.peace_zone)
        {
            DuelEvent duel = getEvent(DuelEvent.class);
            if(duel != null)
                duel.abortDuel(this);
        }
    }

    @Override
    public void onZoneLeave(Zone zone)
    {
        if(zone.getLeavingMessageId() != 0 && isPlayer())
            sendPacket(new SystemMessage(zone.getLeavingMessageId()));

        if(zone.getTemplate().getBlockedActions() != null)
            unblockActions(zone.getTemplate().getBlockedActions());
    }

    @Override
    public boolean hasBasicPropertyResist()
    {
        return false;
    }
}