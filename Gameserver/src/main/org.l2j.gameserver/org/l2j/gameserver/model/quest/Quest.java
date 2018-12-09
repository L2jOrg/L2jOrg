package org.l2j.gameserver.model.quest;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.l2j.commons.database.L2DatabaseFactory;
import org.l2j.commons.dbutils.DbUtils;
import org.l2j.commons.lang.ArrayUtils;
import org.l2j.commons.logging.LogUtils;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.QuestHolder;
import org.l2j.gameserver.data.htm.HtmCache;
import org.l2j.gameserver.data.xml.holder.ItemHolder;
import org.l2j.gameserver.data.xml.holder.NpcHolder;
import org.l2j.gameserver.instancemanager.ReflectionManager;
import org.l2j.gameserver.listener.script.OnInitScriptListener;
import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.Skill;
import org.l2j.gameserver.model.base.*;
import org.l2j.gameserver.model.entity.Reflection;
import org.l2j.gameserver.model.entity.olympiad.OlympiadGame;
import org.l2j.gameserver.model.instances.NpcInstance;
import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.model.quest.startcondition.ICheckStartCondition;
import org.l2j.gameserver.model.quest.startcondition.impl.*;
import org.l2j.gameserver.network.l2.components.HtmlMessage;
import org.l2j.gameserver.network.l2.s2c.ExQuestNpcLogList;
import org.l2j.gameserver.network.l2.s2c.TutorialShowHtmlPacket;
import org.l2j.gameserver.templates.item.ItemTemplate;
import org.l2j.gameserver.templates.npc.NpcTemplate;
import org.l2j.gameserver.utils.HtmlUtils;
import org.l2j.gameserver.utils.Location;
import org.l2j.gameserver.utils.NpcUtils;
import org.l2j.gameserver.utils.ReflectionUtils;
import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.CHashIntObjectMap;
import org.napile.primitive.sets.IntSet;
import org.napile.primitive.sets.impl.HashIntSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

import static org.l2j.commons.util.Util.OBJECT_ARRAY_EMPTY;

public class Quest implements OnInitScriptListener
{
    private static class ConditionMessage
    {
        private final int _npcId;
        private final String _message;

        public ConditionMessage(int npcId, String message)
        {
            _npcId = npcId;
            _message = message;
        }

        public int getNpcId()
        {
            return _npcId;
        }

        public String getMessage()
        {
            return _message;
        }
    }

	private static final Logger _log = LoggerFactory.getLogger(Quest.class);

	public static final String SOUND_ITEMGET = "ItemSound.quest_itemget";
	public static final String SOUND_ACCEPT = "ItemSound.quest_accept";
	public static final String SOUND_MIDDLE = "ItemSound.quest_middle";
	public static final String SOUND_FINISH = "ItemSound.quest_finish";
	public static final String SOUND_GIVEUP = "ItemSound.quest_giveup";
	public static final String SOUND_TUTORIAL = "ItemSound.quest_tutorial";
	public static final String SOUND_JACKPOT = "ItemSound.quest_jackpot";
	public static final String SOUND_HORROR2 = "SkillSound5.horror_02";
	public static final String SOUND_BEFORE_BATTLE = "Itemsound.quest_before_battle";
	public static final String SOUND_FANFARE_MIDDLE = "ItemSound.quest_fanfare_middle";
	public static final String SOUND_FANFARE2 = "ItemSound.quest_fanfare_2";
	public static final String SOUND_BROKEN_KEY = "ItemSound2.broken_key";
	public static final String SOUND_ENCHANT_SUCESS = "ItemSound3.sys_enchant_sucess";
	public static final String SOUND_ENCHANT_FAILED = "ItemSound3.sys_enchant_failed";
	public static final String SOUND_ED_CHIMES05 = "AmdSound.ed_chimes_05";
	public static final String SOUND_ARMOR_WOOD_3 = "ItemSound.armor_wood_3";
	public static final String SOUND_ITEM_DROP_EQUIP_ARMOR_CLOTH = "ItemSound.item_drop_equip_armor_cloth";

	public static final String NO_QUEST_DIALOG = "no-quest";
    public static final String COMPLETED_DIALOG = "completed";

	private static final String FONT_QUEST_AVAILABLE = "<font color=\"bbaa88\">";
	private static final String FONT_QUEST_DONE = "<font color=\"787878\">";
	private static final String FONT_QUEST_IN_PROGRESS = "<font color=\"ffdd66\">";

	protected static final String TODO_FIND_HTML = "<font color=\"6699ff\">TODO:<br>Find this dialog";

    public static final String ACCEPT_QUEST_EVENT = "quest_accept";

	public static final int ADENA_ID = ItemTemplate.ITEM_ID_ADENA;

    public static final QuestPartyType PARTY_NONE = QuestPartyType.PARTY_NONE;
    public static final QuestPartyType PARTY_ONE = QuestPartyType.PARTY_ONE;
    public static final QuestPartyType PARTY_ALL = QuestPartyType.PARTY_ALL;
    public static final QuestPartyType COMMAND_CHANNEL = QuestPartyType.COMMAND_CHANNEL;

    public static final QuestRepeatType ONETIME = QuestRepeatType.ONETIME;
    public static final QuestRepeatType REPEATABLE = QuestRepeatType.REPEATABLE;
    public static final QuestRepeatType DAILY = QuestRepeatType.DAILY;

	//карта с приостановленными квестовыми таймерами для каждого игрока
    private IntObjectMap<Map<String, QuestTimer>> _pausedQuestTimers = new CHashIntObjectMap<Map<String, QuestTimer>>();

    private IntSet _startNpcs = new HashIntSet();
    private IntSet _questItems = new HashIntSet();
    private TIntObjectMap<List<QuestNpcLogInfo>> _npcLogList = new TIntObjectHashMap<List<QuestNpcLogInfo>>(5);
    private TIntObjectMap<List<QuestNpcLogInfo>> _itemsLogList = new TIntObjectHashMap<List<QuestNpcLogInfo>>(5);
    private TIntObjectMap<List<QuestNpcLogInfo>> _customLogList = new TIntObjectHashMap<List<QuestNpcLogInfo>>(5);
    private Map<ICheckStartCondition, ConditionMessage> _startConditions = new HashMap<ICheckStartCondition, ConditionMessage>();

	private final double _rewardRate;

	/**
	 * Этот метод для регистрации квестовых вещей, которые будут удалены
	 * при прекращении квеста, независимо от того, был он закончен или
	 * прерван. <strong>Добавлять сюда награды нельзя</strong>.
	 */
    public void addQuestItem(int... ids)
	{
		for(int id : ids)
			if(id != 0)
			{
                ItemTemplate i = ItemHolder.getInstance().getTemplate(id);

				//For debug (Iqman), that's a really fast way to sync quest items. uncommit only for debug stuff
                if (i == null)
                {
	                _log.warn("Item ID[" + id + "] is null in quest drop in " + getName());
	                continue;
                }

                /*if(!i.isQuest())
					logger.warn("Item " + i + " multiple is not quest type, but appears in quests - fix ");*/

			    _questItems.add(id);
			}
	}

	public void addQuestItemWithLog(int cond, int npcStringId, int max, int... ids)
	{
		if(ids.length == 0)
			throw new IllegalArgumentException("Items list cant be empty!");

		addQuestItem(ids);

		List<QuestNpcLogInfo> vars = _itemsLogList.get(cond);
		if(vars == null)
			_itemsLogList.put(cond, (vars = new ArrayList<QuestNpcLogInfo>(5)));

		vars.add(new QuestNpcLogInfo(ids, null, max, npcStringId));
	}

	public void updateItems(ItemInstance item, QuestState st)
	{
		Player player = st.getPlayer();
		if(player == null)
			return;

		List<QuestNpcLogInfo> vars = getItemsLogList(st.getCond());
		if(vars == null)
			return;

		for(QuestNpcLogInfo info : vars)
		{
            if(ArrayUtils.contains(info.getNpcIds(), item.getItemId()))
            {
                player.sendPacket(new ExQuestNpcLogList(st));
                break;
            }
		}
	}

	public int[] getItems()
	{
		return _questItems.toArray();
	}

	public boolean isQuestItem(int id)
	{
		return _questItems.contains(id);
	}

    public void addCustomLog(int cond, String varName, int npcStringId, int max)
    {
        List<QuestNpcLogInfo> vars = _customLogList.get(cond);
        if(vars == null)
            _customLogList.put(cond, (vars = new ArrayList<QuestNpcLogInfo>(5)));

        vars.add(new QuestNpcLogInfo(null, varName, max, npcStringId));
    }

	/**
	 * Insert in the database the quest for the player.
	 * @param qs : QuestState pointing out the state of the quest
	 * @param var : String designating the name of the variable for the quest
	 * @param value : String designating the value of the variable for the quest
	 */
	public static void updateQuestVarInDb(QuestState qs, String var, String value)
	{
		Player player = qs.getPlayer();
		if(player == null)
			return;

		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("REPLACE INTO character_quests (char_id,id,var,value) VALUES (?,?,?,?)");
			statement.setInt(1, qs.getPlayer().getObjectId());
            statement.setInt(2, qs.getQuest().getId());
			statement.setString(3, var);
			statement.setString(4, value);
			statement.executeUpdate();
		}
		catch(Exception e)
		{
			_log.error("could not insert char quest:", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	/**
	 * Delete the player's quest from database.
	 * @param qs : QuestState pointing out the player's quest
	 */
	public static void deleteQuestInDb(QuestState qs)
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("DELETE FROM character_quests WHERE char_id=? AND id=?");
			statement.setInt(1, qs.getPlayer().getObjectId());
            statement.setInt(2, qs.getQuest().getId());
			statement.executeUpdate();
		}
		catch(Exception e)
		{
			_log.error("could not delete char quest:", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	/**
	 * Delete a variable of player's quest from the database.
	 * @param qs : object QuestState pointing out the player's quest
	 * @param var : String designating the variable characterizing the quest
	 */
	public static void deleteQuestVarInDb(QuestState qs, String var)
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("DELETE FROM character_quests WHERE char_id=? AND id=? AND var=?");
			statement.setInt(1, qs.getPlayer().getObjectId());
            statement.setInt(2, qs.getQuest().getId());
			statement.setString(3, var);
			statement.executeUpdate();
		}
		catch(Exception e)
		{
			_log.error("could not delete char quest:", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	/**
	 * Add quests to the L2Player.<BR><BR>
	 * <U><I>Action : </U></I><BR>
	 * Add state of quests, drops and variables for quests in the HashMap _quest of L2Player
	 * @param player : Player who is entering the world
	 */
	public static void restoreQuestStates(Player player)
	{
        IntSet questsToDelete = null;
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
            questsToDelete = new HashIntSet();
			con = L2DatabaseFactory.getInstance().getConnection();
            statement = con.prepareStatement("SELECT id,var,value FROM character_quests WHERE char_id=?");
			statement.setInt(1, player.getObjectId());
			rset = statement.executeQuery();
			while(rset.next())
			{
                int questId = rset.getInt("id");
                String var = rset.getString("var");
                String value = rset.getString("value");

				// Get the QuestState saved in the loop before
                QuestState qs = player.getQuestState(questId);
                if(qs == null)
                {
                    // Search quest associated with the ID
                    Quest q = QuestHolder.getInstance().getQuest(questId);
                    if(q == null)
                    {
                        if(!Config.DONTLOADQUEST && !questsToDelete.contains(questId))
                        {
                            questsToDelete.add(questId);
                            _log.warn("Unknown quest " + questId + " for player " + player.getName());
                        }
	                    continue;
                    }

                    // Create a new QuestState for the player that will be added to the player's list of quests
                    qs = new QuestState(q, player);
                }

				// Add parameter to the quest
                qs.set(var, value, false);
			}

            if(!questsToDelete.isEmpty())
            {
                DbUtils.close(statement);

                statement = con.prepareStatement("DELETE FROM character_quests WHERE char_id=? AND id=?");
                for (int questId : questsToDelete.toArray())
                {
                    statement.setInt(1, player.getObjectId());
                    statement.setInt(2, questId);
                    statement.addBatch();
                }

                statement.executeBatch();
            }
        }
		catch(Exception e)
		{
			_log.error("could not insert char quest:", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}
	}

    private final String _name;
    private final int _id;
    private final QuestPartyType _partyType;
    private final QuestRepeatType _repeatType;
    private final boolean _abortable;

    /**
     * 0 - по ластхиту, 1 - случайно по пати, 2 - всей пати.
     */
    public Quest(QuestPartyType partyType, QuestRepeatType repeatType, boolean abortable)
    {
        _name = getClass().getSimpleName();
        _id = Integer.parseInt(_name.split("_")[1]);
        _partyType = partyType;
        _repeatType = repeatType;
        _abortable = abortable;

        if(!Config.EX_USE_QUEST_REWARD_PENALTY_PER || !Config.EX_F2P_QUEST_REWARD_PENALTY_QUESTS.contains(_id))
            _rewardRate = 1.;
        else
            _rewardRate = Config.EX_F2P_QUEST_REWARD_PENALTY_PER * 0.01;
    }

    public Quest(QuestPartyType partyType, QuestRepeatType repeatType)
    {
        this(partyType, repeatType, true);
    }

    public QuestRepeatType getRepeatType()
    {
        return _repeatType;
    }

    public boolean isAbortable()
    {
        return _abortable;
    }

	public List<QuestNpcLogInfo> getNpcLogList(int cond)
	{
		return _npcLogList.get(cond);
	}

	public List<QuestNpcLogInfo> getItemsLogList(int cond)
	{
		return _itemsLogList.get(cond);
	}

    public List<QuestNpcLogInfo> getCustomLogList(int cond)
    {
        return _customLogList.get(cond);
    }

	/**
	 * Add this quest to the list of quests that the passed mob will respond to
	 * for Attack Events.<BR>
	 * <BR>
	 *
	 * @param attackIds
	 */
	public void addAttackId(int... attackIds)
	{
		for(int attackId : attackIds)
			addEventId(attackId, QuestEventType.ATTACKED_WITH_QUEST);
	}

	/**
	 * Add this quest to the list of quests that the passed mob will respond to
	 * for the specified Event type.<BR>
	 * <BR>
	 *
	 * @param npcId : id of the NPC to register
	 * @param eventType : type of event being registered
	 * @return int : npcId
	 */
	public NpcTemplate addEventId(int npcId, QuestEventType eventType)
	{
		try
		{
			NpcTemplate t = NpcHolder.getInstance().getTemplate(npcId);
			if(t != null)
				t.addQuestEvent(eventType, this);
			return t;
		}
		catch(Exception e)
		{
			_log.error("", e);
			return null;
		}
	}

	/**
	 * Add this quest to the list of quests that the passed mob will respond to
	 * for Kill Events.<BR>
	 * <BR>
	 *
	 * @param killIds
	 * @return int : killId
	 */
	public void addKillId(int... killIds)
	{
		for(int killid : killIds)
			addEventId(killid, QuestEventType.MOB_KILLED_WITH_QUEST);
	}

	/**
	 * Добавляет нпц масив для слушателя при их убийстве, и обновлении пакетом {@link org.l2j.gameserver.network.l2.s2c.ExQuestNpcLogList}
	 * @param cond
	 * @param varName
	 * @param killIds
	 */
	public void addKillNpcWithLog(int cond, int npcStringId, String varName, int max, int... killIds)
	{
		if(killIds.length == 0)
			throw new IllegalArgumentException("Npc list cant be empty!");

		addKillId(killIds);

		List<QuestNpcLogInfo> vars = _npcLogList.get(cond);
		if(vars == null)
			_npcLogList.put(cond, (vars = new ArrayList<QuestNpcLogInfo>(5)));

		/*int npcStringId = 0;
		if(useNpcString)
		{
			npcStringId = getId();
			if(npcStringId >= 10000)
				npcStringId -= 5000;

			npcStringId = npcStringId * 100 + 10 + cond;
		}*/

		vars.add(new QuestNpcLogInfo(killIds, varName, max, npcStringId));
	}

	public void addKillNpcWithLog(int cond, String varName, int max, int... killIds)
	{
		addKillNpcWithLog(cond, 0, varName, max, killIds);
	}

	public boolean updateKill(NpcInstance npc, QuestState st)
	{
		Player player = st.getPlayer();
		if(player == null)
			return false;
		List<QuestNpcLogInfo> vars = getNpcLogList(st.getCond());
		if(vars == null)
			return false;
		boolean done = true;
		boolean find = false;
        boolean update = false;
		for(QuestNpcLogInfo info : vars)
		{
			int count = st.getInt(info.getVarName());
			if(!find && ArrayUtils.contains(info.getNpcIds(), npc.getNpcId()))
			{
				find = true;
				if(count < info.getMaxCount())
				{
					st.set(info.getVarName(), ++count);
                    update = true;
				}
			}

			if(count != info.getMaxCount())
				done = false;
		}

        if(update)
            player.sendPacket(new ExQuestNpcLogList(st));

        return done;
	}

    public boolean updateCustomLog(String var, QuestState st)
    {
        Player player = st.getPlayer();
        if(player == null)
            return false;
        List<QuestNpcLogInfo> vars = getCustomLogList(st.getCond());
        if(vars == null)
            return false;
        boolean done = true;
        boolean update = false;
        for(QuestNpcLogInfo info : vars)
        {
            if(!var.equalsIgnoreCase(info.getVarName()))
                continue;

            int count = st.getInt(var);
            if(count < info.getMaxCount())
            {
                st.set(info.getVarName(), ++count);
                update = true;
            }

            if(count != info.getMaxCount())
                done = false;
        }

        if(update)
            player.sendPacket(new ExQuestNpcLogList(st));

        return done;
    }

	public void addKillId(Collection<Integer> killIds)
	{
		for(int killid : killIds)
			addKillId(killid);
	}

	/**
	 * Add this quest to the list of quests that the passed npc will respond to
	 * for Skill-Use Events.<BR>
	 * <BR>
	 *
	 * @param npcId : ID of the NPC
	 * @return int : ID of the NPC
	 */
	public NpcTemplate addSkillUseId(int npcId)
	{
		return addEventId(npcId, QuestEventType.MOB_TARGETED_BY_SKILL);
	}

	public void addStartNpc(int... npcIds)
	{
		for(int talkId : npcIds)
			addStartNpc(talkId);
	}

	/**
	 * Add the quest to the NPC's startQuest
	 * Вызывает addTalkId
	 *
	 * @param npcId
	 * @return L2NpcTemplate : Start NPC
	 */
	public NpcTemplate addStartNpc(int npcId)
	{
        _startNpcs.add(npcId);
		addTalkId(npcId);
		return addEventId(npcId, QuestEventType.QUEST_START);
	}

	/**
	 * Add the quest to the NPC's first-talk (default action dialog)
	 *
	 * @param npcIds
	 * @return L2NpcTemplate : Start NPC
	 */
	public void addFirstTalkId(int... npcIds)
	{
		for(int npcId : npcIds)
			addEventId(npcId, QuestEventType.NPC_FIRST_TALK);
	}

	/**
	 * Add this quest to the list of quests that the passed npc will respond to
	 * for Talk Events.<BR>
	 * <BR>
	 *
	 * @param talkIds : ID of the NPC
	 * @return int : ID of the NPC
	 */
	public void addTalkId(int... talkIds)
	{
		for(int talkId : talkIds)
			addEventId(talkId, QuestEventType.QUEST_TALK);
	}

	public void addTalkId(Collection<Integer> talkIds)
	{
		for(int talkId : talkIds)
			addTalkId(talkId);
	}

	/**
	 * Добавляет проверку уровня при старте квеста
	 *
	 * @param min - минимальный уровень
	 */
    public void addLevelCheck(int npcId, String message, int min)
    {
        addLevelCheck(message, min, Experience.getMaxLevel());
    }

    public void addLevelCheck(String message, int min)
    {
        addLevelCheck(-1, message, min);
    }

    /**
	 * Добавляет проверку уровня при старте квеста
	 *
	 * @param min - минимальный уровень
	 * @param max - максимальный уровень
	 */
    public void addLevelCheck(int npcId, String message, int min, int max)
    {
        _startConditions.put(new PlayerLevelCondition(min, max), new ConditionMessage(npcId, message));
    }

	public void addLevelCheck(String message, int min, int max)
    {
        addLevelCheck(-1, message, min, max);
    }

    public void addRaceCheck(int npcId, String message, boolean classRace, Race... races)
    {
        _startConditions.put(new PlayerRaceCondition(classRace, races), new ConditionMessage(npcId, message));
    }

    public void addRaceCheck(String message, boolean classRace, Race... races)
    {
        addRaceCheck(-1, message, classRace, races);
    }

    public void addRaceCheck(int npcId, String message, Race... races)
    {
        addRaceCheck(npcId, message, false, races);
    }

    public void addRaceCheck(String message, Race... races)
    {
        addRaceCheck(-1, message, false, races);
    }

	/**
	 * Добавляет проверку выполненного квеста
	 *
	 * @param questId - ид квеста
	 */
    public void addQuestCompletedCheck(int npcId, String message, int questId)
    {
        _startConditions.put(new QuestCompletedCondition(questId), new ConditionMessage(npcId, message));
    }

    public void addQuestCompletedCheck(String message, int questId)
    {
        addQuestCompletedCheck(-1, message, questId);
    }

	/**
	 * Добавляет проверку уровня профессии
	 *
	 * @param classLevels - список уровней классов, для которых доступен квест
	 */
    public void addClassLevelCheck(int npcId, String message, ClassLevel... classLevels)
    {
        _startConditions.put(new ClassLevelCondition(classLevels), new ConditionMessage(npcId, message));
    }

    public void addClassLevelCheck(String message, ClassLevel... classLevels)
    {
        addClassLevelCheck(-1, message, classLevels);
    }

    public void addClassIdCheck(int npcId, String message, int... classIds)
    {
        _startConditions.put(new ClassIdCondition(classIds), new ConditionMessage(npcId, message));
    }

    public void addClassIdCheck(String message, int... classIds)
    {
        addClassIdCheck(-1, message, classIds);
    }

    public void addClassIdCheck(int npcId, String message, ClassId... classIds)
    {
        _startConditions.put(new ClassIdCondition(classIds), new ConditionMessage(npcId, message));
    }

    public void addClassIdCheck(String message, ClassId... classIds)
    {
        addClassIdCheck(-1, message, classIds);
    }

    public void addItemHaveCheck(int npcId, String message, int itemId, long count)
    {
        _startConditions.put(new ItemHaveCondition(itemId, count), new ConditionMessage(npcId, message));
    }

    public void addItemHaveCheck(String message, int itemId, long count)
    {
        addItemHaveCheck(-1, message, itemId, count);
    }

    public void addClassTypeCheck(int npcId, String message, ClassType type)
    {
        _startConditions.put(new ClassTypeCondition(type), new ConditionMessage(npcId, message));
    }

    public void addClassTypeCheck(String message, ClassType type)
    {
        addClassTypeCheck(-1, message, type);
    }

	/**
	 * Возвращает название квеста (Берется с npcstring-*.dat)
	 * state 1 = ""
	 * state 2 = "In Progress"
	 * state 3 = "Done"
	 * state 4 = ""
	 */
	public String getDescr(NpcInstance npc, Player player, boolean isStartNpc)
	{
		if(!isVisible(player))
			return null;

		int state = getDescrState(npc, player, isStartNpc);
		String font = FONT_QUEST_AVAILABLE;
		switch(state)
		{
			case 2:
				font = FONT_QUEST_IN_PROGRESS;
				break;
			case 3:
				font = FONT_QUEST_DONE;
				break;
		}

        return font.concat(HtmlUtils.htmlNpcString(getDescriprionId(state))).concat("</font>");
    }

    public int getDescriprionId(int state)
    {
        int fStringId = getId();
        if(fStringId > 11000)
            fStringId -= 10000;
        else if(fStringId >= 10000)
			fStringId -= 5000;

		fStringId = fStringId * 100 + state;
        return fStringId;
	}

	/**
	 * Только для отображения квестов в списке либо сравнения квестов!
	 *
	 * @return - текущее состояние квеста для конкретного игрока
	 */
	public final int getDescrState(NpcInstance npc, Player player, boolean isStartNpc)
	{
        QuestState qs = player.getQuestState(this);
        int state = 1;
        if(qs != null && qs.isStarted())
			state = 2;
		else if(qs != null && qs.isCompleted())
			state = 3;
		return state;
	}

	/**
	 * Return name of the quest
	 * @return String
	 */
	public String getName()
	{
		return _name;
	}

	/**
	 * Return ID of the quest
	 * @return int
	 */
    public int getId()
    {
        return _id;
    }

	/**
	 * Return party state of quest
	 * @return String
	 */
    public QuestPartyType getPartyType()
    {
        return _partyType;
    }

	/**
	 * Add a new QuestState to the database and return it.
	 * @param player
	 * @return QuestState : QuestState created
	 */
    public QuestState newQuestState(Player player)
    {
        return new QuestState(this, player);
    }

	public void notifyAttack(NpcInstance npc, QuestState qs)
	{
        boolean showQuestInfo = canShowQuestInfo(qs.getPlayer());

		String res = null;
		try
		{
			res = onAttack(npc, qs);
		}
		catch(Exception e)
		{
			showError(qs.getPlayer(), e);
			return;
		}

        showQuestInfo = showQuestInfo && canShowQuestInfo(qs.getPlayer());

        showResult(npc, qs.getPlayer(), res, showQuestInfo);
	}

	public void notifyDeath(Creature killer, Creature victim, QuestState qs)
	{
        boolean showQuestInfo = canShowQuestInfo(qs.getPlayer());

		String res = null;
		try
		{
			res = onDeath(killer, victim, qs);
		}
		catch(Exception e)
		{
			showError(qs.getPlayer(), e);
			return;
		}

        showQuestInfo = showQuestInfo && canShowQuestInfo(qs.getPlayer());

        showResult(null, qs.getPlayer(), res, showQuestInfo);
	}

	public void notifyEvent(String event, QuestState qs, NpcInstance npc)
	{
        boolean showQuestInfo = canShowQuestInfo(qs.getPlayer());

		String res = null;
		try
		{
            if(event.equalsIgnoreCase(ACCEPT_QUEST_EVENT))
                res = onAcceptQuest(qs, npc);
            if(res == null)
                res = onEvent(event, qs, npc);
		}
        catch(Exception e)
        {
            showError(qs.getPlayer(), e);
            return;
        }

        showQuestInfo = showQuestInfo && canShowQuestInfo(qs.getPlayer());

        showResult(npc, qs.getPlayer(), res, showQuestInfo);
	}

    public void notifyMenuSelect(int reply, QuestState qs, NpcInstance npc)
    {
        boolean showQuestInfo = canShowQuestInfo(qs.getPlayer());

        String res = null;
        try
        {
            res = onMenuSelect(reply, qs, npc);
        }
        catch(Exception e)
        {
            showError(qs.getPlayer(), e);
            return;
        }

        showQuestInfo = showQuestInfo && canShowQuestInfo(qs.getPlayer());

        showResult(npc, qs.getPlayer(), res, showQuestInfo);
    }

	public void notifyKill(NpcInstance npc, QuestState qs)
	{
        boolean showQuestInfo = canShowQuestInfo(qs.getPlayer());

		String res = null;
		try
		{
			res = onKill(npc, qs);
		}
		catch(Exception e)
		{
			showError(qs.getPlayer(), e);
			return;
		}

        showQuestInfo = showQuestInfo && canShowQuestInfo(qs.getPlayer());

        showResult(npc, qs.getPlayer(), res, showQuestInfo);
	}

	public void notifyKill(Player target, QuestState qs)
	{
        boolean showQuestInfo = canShowQuestInfo(qs.getPlayer());

		String res = null;
		try
		{
			res = onKill(target, qs);
		}
		catch(Exception e)
		{
			showError(qs.getPlayer(), e);
			return;
		}

        showQuestInfo = showQuestInfo && canShowQuestInfo(qs.getPlayer());

        showResult(null, qs.getPlayer(), res, showQuestInfo);
	}

	/**
	 * Override the default NPC dialogs when a quest defines this for the given NPC
	 */
	public final boolean notifyFirstTalk(NpcInstance npc, Player player)
	{
		String res = null;
		try
		{
			res = onFirstTalk(npc, player);
		}
		catch(Exception e)
		{
			showError(player, e);
			return true;
		}
		// if the quest returns text to display, display it. Otherwise, use the default npc text.
		return showResult(npc, player, res, true, false);
	}

	public boolean notifyTalk(NpcInstance npc, QuestState qs)
	{
        boolean showQuestInfo = canShowQuestInfo(qs.getPlayer());

		String res = null;
		try
		{
            if(qs.isNotAccepted())
            {
                Set<Quest> quests = npc.getTemplate().getEventQuests(QuestEventType.QUEST_START);
                if(quests != null && quests.contains(this))
                    res = checkStartCondition(npc, qs.getPlayer());
            }
            if(res == null || res.isEmpty())
                res = onTalk(npc, qs);
		}
		catch(Exception e)
		{
			showError(qs.getPlayer(), e);
			return true;
		}

        showQuestInfo = showQuestInfo && canShowQuestInfo(qs.getPlayer());

        return showResult(npc, qs.getPlayer(), res, showQuestInfo);
	}

    public boolean notifyCompleted(NpcInstance npc, QuestState qs)
    {
        boolean showQuestInfo = canShowQuestInfo(qs.getPlayer());

        String res = null;
        try
        {
            res = onCompleted(npc, qs);
        }
        catch(Exception e)
        {
            showError(qs.getPlayer(), e);
            return true;
        }

        showQuestInfo = showQuestInfo && canShowQuestInfo(qs.getPlayer());

        return showResult(npc, qs.getPlayer(), res, showQuestInfo);
    }

	public boolean notifySkillUse(NpcInstance npc, Skill skill, QuestState qs)
	{
        boolean showQuestInfo = canShowQuestInfo(qs.getPlayer());

		String res = null;
		try
		{
            res = onSkillUse(npc, skill, qs);
		}
		catch(Exception e)
		{
			showError(qs.getPlayer(), e);
			return true;
		}

        showQuestInfo = showQuestInfo && canShowQuestInfo(qs.getPlayer());

        return showResult(npc, qs.getPlayer(), res, showQuestInfo);
	}

	public void notifySocialActionUse(QuestState qs, int actionId)
	{
		try
		{
			onSocialActionUse(qs, actionId);
		}
		catch(Exception e)
		{
			showError(qs.getPlayer(), e);
		}
	}

	public void notifyUpdateItem(ItemInstance item, QuestState qs)
	{
		try
		{
			updateItems(item, qs);
		}
		catch(Exception e)
		{
			showError(qs.getPlayer(), e);
			return;
		}
	}

    public void notifyTutorialEvent(String event, boolean quest, String value, QuestState qs)
    {
        String res = null;
        try
        {
            res = onTutorialEvent(event, quest, value, qs);
        }
        catch(Exception e)
        {
            showError(qs.getPlayer(), e);
            return;
        }
        showTutorialResult(qs.getPlayer(), res);
    }

	public void onSocialActionUse(QuestState qs, int actionId)
	{}

    public void onRestore(QuestState qs)
    {}

    public void onAccept(QuestState qs)
    {}

    public void onExit(QuestState qs)
    {}

    public void onAbort(QuestState qs)
    {}

    public void onFinish(QuestState qs)
    {}

	public String onAttack(NpcInstance npc, QuestState qs)
	{
		return null;
	}

	public String onDeath(Creature killer, Creature victim, QuestState qs)
	{
		return null;
	}

	public String onEvent(String event, QuestState qs, NpcInstance npc)
	{
		return null;
	}

    public String onAcceptQuest(QuestState qs, NpcInstance npc)
    {
        return null;
    }

    public String onMenuSelect(long reply, QuestState qs, NpcInstance npc)
    {
        return null;
    }

	public String onKill(NpcInstance npc, QuestState qs)
	{
		return null;
	}

	public String onKill(Player killed, QuestState st)
	{
		return null;
	}

	public String onFirstTalk(NpcInstance npc, Player player)
	{
		return null;
	}

	public String onTalk(NpcInstance npc, QuestState qs)
	{
		return null;
	}

    public String onCompleted(NpcInstance npc, QuestState qs)
    {
        return COMPLETED_DIALOG;
    }

	public String onSkillUse(NpcInstance npc, Skill skill, QuestState qs)
	{
		return null;
	}

	public void onOlympiadEnd(OlympiadGame og, QuestState qs)
	{
		//
	}

    public String onTutorialEvent(String event, boolean quest, String value, QuestState qs)
    {
        return null;
    }

	public boolean canAbortByPacket()
	{
        return isAbortable();
	}

	/**
	 * Show message error to player who has an access level greater than 0
	 * @param player : L2Player
	 * @param t : Throwable
	 */
	private void showError(Player player, Throwable t)
	{
		_log.error("", t);
		if(player != null && player.isGM())
		{
			String res = "<html><body><title>Script error</title>" + LogUtils.dumpStack(t).replace("\n", "<br>") + "</body></html>";
			showResult(null, player, res, false);
		}
	}

	protected void showHtmlFile(Player player, NpcInstance npc, String fileName, boolean showQuestInfo)
	{
		showHtmlFile(player, npc, fileName, showQuestInfo, OBJECT_ARRAY_EMPTY);
	}

	protected void showHtmlFile(Player player, NpcInstance npc, String fileName, boolean showQuestInfo, Object... arg)
	{
		if(player == null)
			return;

        HtmlMessage npcReply = new HtmlMessage(npc == null ? 5 : npc.getObjectId());
        if(showQuestInfo)
            npcReply.setQuestId(getId());
		npcReply.setFile("quests/" + getClass().getSimpleName() + "/" + fileName);
        npcReply.replace("<?quest_id?>", String.valueOf(getId()));

		if(arg.length % 2 == 0)
			for(int i = 0; i < arg.length; i += 2)
				npcReply.replace(String.valueOf(arg[i]), String.valueOf(arg[i + 1]));

		player.sendPacket(npcReply);
	}

	protected void showSimpleHtmFile(Player player, String fileName)
	{
		if(player == null)
			return;

        HtmlMessage npcReply = new HtmlMessage(5);
		npcReply.setFile(fileName);
		player.sendPacket(npcReply);
	}

    protected void showTutorialHtmlFile(Player player, String fileName, Object... arg)
    {
        if(player == null)
            return;

        String text = HtmCache.getInstance().getHtml("quests/" + getClass().getSimpleName() + "/tutorial/" + fileName, player);

        if(arg.length % 2 == 0)
            for(int i = 0; i < arg.length; i += 2)
                text.replace(String.valueOf(arg[i]), String.valueOf(arg[i + 1]));

        player.sendPacket(new TutorialShowHtmlPacket(TutorialShowHtmlPacket.NORMAL_WINDOW, text));
    }

	/**
	 * Show a message to player.<BR><BR>
	 * <U><I>Concept : </I></U><BR>
	 * 3 cases are managed according to the value of the parameter "res" :<BR>
	 * <LI><U>"res" ends with string ".html" :</U> an HTML is opened in order to be shown in a dialog box</LI>
	 * <LI><U>"res" starts with tag "html" :</U> the message hold in "res" is shown in a dialog box</LI>
	 * <LI><U>"res" is null :</U> do not show any message</LI>
	 * <LI><U>"res" is empty string :</U> show default message</LI>
	 * <LI><U>otherwise :</U> the message hold in "res" is shown in chat box</LI>
	 * @param npc
	 * @param player
	 * @param res : String pointing out the message to show at the player
	 */
    private boolean showResult(NpcInstance npc, Player player, String res, boolean showQuestInfo)
    {
        return showResult(npc, player, res, false, showQuestInfo);
    }

	private boolean showResult(NpcInstance npc, Player player, String res, boolean isFirstTalk, boolean showQuestInfo)
	{
		if(res == null) // do not show message
			return true;
		if(res.isEmpty()) // show default npc message
			return false;
		if(res.startsWith("no_quest") || res.equalsIgnoreCase("noquest") || res.equalsIgnoreCase(NO_QUEST_DIALOG))
			showSimpleHtmFile(player, "no-quest.htm");
		else if(res.equalsIgnoreCase(COMPLETED_DIALOG))
			showSimpleHtmFile(player, "completed-quest.htm");
		else if(res.endsWith(".htm"))
			showHtmlFile(player, npc, res, showQuestInfo);
		else
		{
            HtmlMessage npcReply = new HtmlMessage(npc == null ? 5 : npc.getObjectId()).setPlayVoice(isFirstTalk);
            npcReply.setHtml(res);
            if(showQuestInfo)
                npcReply.setQuestId(getId());
            npcReply.replace("<?quest_id?>", String.valueOf(getId()));
            player.sendPacket(npcReply);
		}
		return true;
	}

    private void showTutorialResult(Player player, String res)
    {
        if(res == null || res.isEmpty())
            return;

        if(res.endsWith(".htm"))
            showTutorialHtmlFile(player, res);
        else
            player.sendPacket(new TutorialShowHtmlPacket(TutorialShowHtmlPacket.NORMAL_WINDOW, res));
    }

	// Проверяем, показывать ли информацию о квесте в диалоге.
    private boolean canShowQuestInfo(Player player)
    {
        QuestState qs = player.getQuestState(this);
        if(isVisible(player) && (qs == null || qs.isNotAccepted()))
            return true;
        return false;
    }

	// Останавливаем и сохраняем таймеры (при выходе из игры)
	void pauseQuestTimers(QuestState qs)
	{
		if(qs.getTimers().isEmpty())
			return;

		for(QuestTimer timer : qs.getTimers().values())
		{
			timer.setQuestState(null);
			timer.pause();
		}

		_pausedQuestTimers.put(qs.getPlayer().getObjectId(), qs.getTimers());
	}

	// Восстанавливаем таймеры (при входе в игру)
	void resumeQuestTimers(QuestState qs)
	{
		Map<String, QuestTimer> timers = _pausedQuestTimers.remove(qs.getPlayer().getObjectId());
		if(timers == null)
			return;

		qs.getTimers().putAll(timers);

		for(QuestTimer timer : qs.getTimers().values())
		{
			timer.setQuestState(qs);
			timer.start();
		}
	}

	protected String str(long i)
	{
		return String.valueOf(i);
	}

	// =========================================================
	//  QUEST SPAWNS
	// =========================================================

    public NpcInstance addSpawn(int npcId, int x, int y, int z, int heading, int randomOffset, int despawnDelay)
	{
		return addSpawn(npcId, new Location(x, y, z, heading), randomOffset, despawnDelay);
	}

	public NpcInstance addSpawn(int npcId, Location loc, int randomOffset, int despawnDelay)
	{
        return NpcUtils.spawnSingle(npcId, randomOffset > 50 ? Location.findPointToStay(loc, 50, randomOffset, ReflectionManager.MAIN.getGeoIndex()) : loc, despawnDelay);
    }

	/**
	 * Добавляет спаун с числовым значением разброса - от 50 до randomOffset.
	 * Если randomOffset указан мене 50, то координаты не меняются.
	 */
	public static NpcInstance addSpawnToInstance(int npcId, int x, int y, int z, int heading, int randomOffset, int refId)
	{
		return addSpawnToInstance(npcId, new Location(x, y, z, heading), randomOffset, refId);
	}

	public static NpcInstance addSpawnToInstance(int npcId, Location loc, int randomOffset, int refId)
	{
		try
		{
			NpcTemplate template = NpcHolder.getInstance().getTemplate(npcId);
			if(template != null)
			{
				NpcInstance npc = NpcHolder.getInstance().getTemplate(npcId).getNewInstance();
				npc.setReflection(refId);
				npc.setSpawnedLoc(randomOffset > 50 ? Location.findPointToStay(loc, 50, randomOffset, npc.getGeoIndex()) : loc);
				npc.spawnMe(npc.getSpawnedLoc());
				return npc;
			}
		}
		catch(Exception e1)
		{
			_log.warn("Could not spawn Npc " + npcId);
		}
		return null;
	}

	public boolean isVisible(Player player)
	{
		return true;
	}

    public final boolean enterInstance(QuestState st, Reflection reflection, int instancedZoneId, Object... args)
	{
		Player player = st.getPlayer();
		if(player == null)
            return false;

		Reflection activeReflection = player.getActiveReflection();
		if(activeReflection != null)
		{
			if(player.canReenterInstance(instancedZoneId))
			{
				player.teleToLocation(activeReflection.getTeleportLoc(), activeReflection);
				onReenterInstance(st, activeReflection, args);
                return true;
			}
		}
		else if(player.canEnterInstance(instancedZoneId))
		{
			Reflection newReflection = ReflectionUtils.enterReflection(player, reflection, instancedZoneId);
            if(newReflection != null)
            {
                onEnterInstance(st, newReflection, args);
                return true;
            }
		}
        return false;
	}

    public final boolean enterInstance(QuestState st, int instancedZoneId, Object... args)
    {
        return enterInstance(st, new Reflection(), instancedZoneId, args);
    }

	public void onEnterInstance(QuestState st, Reflection reflection, Object[] args)
	{
		//
	}

	public void onReenterInstance(QuestState st, Reflection reflection, Object[] args)
	{
		//
	}

	/**
	 * Метод для проверки квеста на доступность данному персонажу.
	 * Используется при показе списка квестов,
	 * Доступные квесты выделяются синим, недоступные - красным
	 * Также используется для показа соответствующих диалогов
	 *
	 * @param player - игрок, для которого проверяем условия квеста
	 * @return true, если игрок соответствует требованиям квеста, иначе - false
	*/
    public String checkStartCondition(int npcId, Player player)
    {
        for(Map.Entry<ICheckStartCondition, ConditionMessage> entry : _startConditions.entrySet())
        {
            ConditionMessage condMsg = entry.getValue();
            if(condMsg.getNpcId() == -1 || condMsg.getNpcId() == npcId)
            {
                if(!entry.getKey().checkCondition(player))
                    return condMsg.getMessage();
            }
        }
        return null;
    }

    public String checkStartCondition(NpcInstance npc, Player player)
    {
        if(npc != null)
            return checkStartCondition(npc.getNpcId(), player);

        String msg = checkStartCondition(-1, player);
        if(msg == null)
        {
            for(int npcId : _startNpcs.toArray())
            {
                msg = checkStartCondition(npcId, player);
                if(msg == null)
                    return null;
            }
            return msg;
        }
        return msg;
    }

	public boolean checkStartNpc(NpcInstance npc, Player player)
	{
		return true;
	}

	public boolean checkMaxLevelCondition(Player player)
	{
		for(ICheckStartCondition startCondition : _startConditions.keySet())
		{
			if(!(startCondition instanceof PlayerLevelCondition))
				continue;
			if(!startCondition.checkCondition(player))
				return false;
		}
		return true;
	}

	public boolean checkTalkNpc(NpcInstance npc, QuestState st)
	{
		return true;
	}

	public double getRewardRate()
	{
		return _rewardRate;
	}

	public void onHaosBattleEnd(Player player, boolean isWinner)	
	{}

	@Override
    public void onInit()
    {
        if(!Config.DONTLOADQUEST)
            QuestHolder.getInstance().addQuest(this);
    }
}