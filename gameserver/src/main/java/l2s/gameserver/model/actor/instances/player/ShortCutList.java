package l2s.gameserver.model.actor.instances.player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import l2s.commons.dbutils.DbUtils;
import l2s.gameserver.database.DatabaseFactory;
import l2s.gameserver.model.Player;
import l2s.gameserver.network.l2.components.SystemMsg;
import l2s.gameserver.network.l2.s2c.ShortCutInitPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShortCutList
{
	private static final Logger _log = LoggerFactory.getLogger(ShortCutList.class);

	private final Player player;
	private Map<Integer, ShortCut> _shortCuts = new ConcurrentHashMap<Integer, ShortCut>();

	public ShortCutList(Player owner)
	{
		player = owner;
	}

	public Collection<ShortCut> getAllShortCuts()
	{
		return _shortCuts.values();
	}

	public void validate()
	{
		// Проверка ярлыков
		for(ShortCut sc : _shortCuts.values())
			// Удаляем ярлыки на предметы, которых нету в инвентаре
			if(sc.getType() == ShortCut.TYPE_ITEM)
				if(player.getInventory().getItemByObjectId(sc.getId()) == null)
					deleteShortCut(sc.getSlot(), sc.getPage());
	}

	public ShortCut getShortCut(int slot, int page)
	{
		ShortCut sc = _shortCuts.get(slot + page * 12);
		// verify shortcut
		if(sc != null && sc.getType() == ShortCut.TYPE_ITEM)
			if(player.getInventory().getItemByObjectId(sc.getId()) == null)
			{
				player.sendPacket(SystemMsg.THERE_ARE_NO_MORE_ITEMS_IN_THE_SHORTCUT);
				deleteShortCut(sc.getSlot(), sc.getPage());
				sc = null;
			}
		return sc;
	}

	public void registerShortCut(ShortCut shortcut)
	{
		ShortCut oldShortCut = _shortCuts.put(shortcut.getSlot() + 12 * shortcut.getPage(), shortcut);
		registerShortCutInDb(shortcut, oldShortCut);
	}

	private synchronized void registerShortCutInDb(ShortCut shortcut, ShortCut oldShortCut)
	{
		if(oldShortCut != null)
			deleteShortCutFromDb(oldShortCut);

		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("REPLACE INTO character_shortcuts SET object_id=?,slot=?,page=?,type=?,shortcut_id=?,level=?,character_type=?,class_index=?");
			statement.setInt(1, player.getObjectId());
			statement.setInt(2, shortcut.getSlot());
			statement.setInt(3, shortcut.getPage());
			statement.setInt(4, shortcut.getType());
			statement.setInt(5, shortcut.getId());
			statement.setInt(6, shortcut.getLevel());
			statement.setInt(7, shortcut.getCharacterType());
			statement.setInt(8, player.getActiveClassId());
			statement.execute();
		}
		catch(Exception e)
		{
			_log.error("could not store shortcuts:", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	/**
	 * @param shortcut
	 */
	private void deleteShortCutFromDb(ShortCut shortcut)
	{
		Connection con = null;
		PreparedStatement statement = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("DELETE FROM character_shortcuts WHERE object_id=? AND slot=? AND page=? AND class_index=?");
			statement.setInt(1, player.getObjectId());
			statement.setInt(2, shortcut.getSlot());
			statement.setInt(3, shortcut.getPage());
			statement.setInt(4, player.getActiveClassId());
			statement.execute();
		}
		catch(Exception e)
		{
			_log.error("could not delete shortcuts:", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement);
		}
	}

	/**
	 * Удаляет ярлык с пользовательской панели по номеру страницы и слота.
	 * @param slot
	 * @param page
	 */
	public void deleteShortCut(int slot, int page)
	{
		ShortCut old = _shortCuts.remove(slot + page * 12);
		if(old == null)
			return;
		deleteShortCutFromDb(old);
		// При удалении с панели скила, на оффе шлется полный инит ярлыков
		// Обработка удаления предметных ярлыков - клиент сайд.
		if(old.getType() == ShortCut.TYPE_SKILL)
		{
			player.sendPacket(new ShortCutInitPacket(player));
			player.sendActiveAutoShots();
		}
	}

	/**
	 * Удаляет ярлык предмета с пользовательской панели.
	 * @param objectId
	 */
	public void deleteShortCutByObjectId(int objectId)
	{
		for(ShortCut shortcut : _shortCuts.values())
			if(shortcut != null && shortcut.getType() == ShortCut.TYPE_ITEM && shortcut.getId() == objectId)
				deleteShortCut(shortcut.getSlot(), shortcut.getPage());
	}

	/**
	 * Удаляет ярлык скила с пользовательской панели.
	 * @param skillId
	 */
	public void deleteShortCutBySkillId(int skillId)
	{
		for(ShortCut shortcut : _shortCuts.values())
			if(shortcut != null && shortcut.getType() == ShortCut.TYPE_SKILL && shortcut.getId() == skillId)
				deleteShortCut(shortcut.getSlot(), shortcut.getPage());
	}

	public void restore()
	{
		_shortCuts.clear();
		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT character_type, slot, page, type, shortcut_id, level FROM character_shortcuts WHERE object_id=? AND class_index=?");
			statement.setInt(1, player.getObjectId());
			statement.setInt(2, player.getActiveClassId());
			rset = statement.executeQuery();
			while(rset.next())
			{
				int slot = rset.getInt("slot");
				int page = rset.getInt("page");
				int type = rset.getInt("type");
				int id = rset.getInt("shortcut_id");
				int level = rset.getInt("level");
				int character_type = rset.getInt("character_type");

				_shortCuts.put(slot + page * 12, new ShortCut(slot, page, type, id, level, character_type));
			}
		}
		catch(Exception e)
		{
			_log.error("could not store shortcuts:", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}
	}
}