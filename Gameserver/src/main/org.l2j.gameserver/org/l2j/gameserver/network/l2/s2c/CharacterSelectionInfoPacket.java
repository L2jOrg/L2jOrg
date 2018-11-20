package org.l2j.gameserver.network.l2.s2c;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.l2j.commons.dbutils.DbUtils;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.dao.CharacterDAO;
import org.l2j.gameserver.database.DatabaseFactory;
import org.l2j.gameserver.model.CharSelectInfoPackage;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.base.Experience;
import org.l2j.gameserver.model.base.Race;
import org.l2j.gameserver.model.base.SubClassType;
import org.l2j.gameserver.model.items.Inventory;
import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.utils.AutoBan;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CharacterSelectionInfoPacket extends L2GameServerPacket
{
	private static final Logger _log = LoggerFactory.getLogger(CharacterSelectionInfoPacket.class);

	private final String _loginName;
	private final int _sessionId;
	private final CharSelectInfoPackage[] _characterPackages;
	private final boolean _hasPremiumAccount;

	public CharacterSelectionInfoPacket(GameClient client)
	{
		_loginName = client.getLogin();
		_sessionId = client.getSessionKey().playOkID1;
		_characterPackages = loadCharacterSelectInfo(_loginName);
		_hasPremiumAccount = client.getPremiumAccountType() > 0 && client.getPremiumAccountExpire() > System.currentTimeMillis() / 1000L;
	}

	public CharSelectInfoPackage[] getCharInfo()
	{
		return _characterPackages;
	}

	@Override
	protected final void writeImpl()
	{
		int size = _characterPackages != null ? _characterPackages.length : 0;

		writeInt(size);
		writeInt(0x07); // Максимальное количество персонажей на сервере
		writeByte(0x00); // 0x00 - Разрешить, 0x01 - запретить. Разрешает или запрещает создание игроков
		writeByte(0x00);
		writeInt(0x02); // 0x01 - Выводит окно, что нужно купить игру, что создавать более 2х чаров. 0х02 - обычное лобби.
		writeByte(0x00); // 0x01 - Предлогает купить ПА.

		long lastAccess = -1L;
		int lastUsed = -1;
		for(int i = 0; i < size; i++)
			if(lastAccess < _characterPackages[i].getLastAccess())
			{
				lastAccess = _characterPackages[i].getLastAccess();
				lastUsed = i;
			}

		for(int i = 0; i < size; i++)
		{
			CharSelectInfoPackage charInfoPackage = _characterPackages[i];

			writeString(charInfoPackage.getName());
			writeInt(charInfoPackage.getCharId()); // ?
			writeString(_loginName);
			writeInt(_sessionId);
			writeInt(charInfoPackage.getClanId());
			writeInt(0x00); // ??

			writeInt(charInfoPackage.getSex());
			writeInt(charInfoPackage.getRace());
			writeInt(charInfoPackage.getBaseClassId());

			writeInt(Config.REQUEST_ID); // active ??

			writeInt(charInfoPackage.getX());
			writeInt(charInfoPackage.getY());
			writeInt(charInfoPackage.getZ());

			writeF(charInfoPackage.getCurrentHp());
			writeF(charInfoPackage.getCurrentMp());

			writeLong(charInfoPackage.getSp());
			writeLong(charInfoPackage.getExp());
			int lvl = Experience.getLevel(charInfoPackage.getExp());
			writeF(Experience.getExpPercent(lvl, charInfoPackage.getExp()));
			writeInt(lvl);

			writeInt(charInfoPackage.getKarma());
			writeInt(charInfoPackage.getPk());
			writeInt(charInfoPackage.getPvP());

			writeInt(0x00);
			writeInt(0x00);
			writeInt(0x00);
			writeInt(0x00);
			writeInt(0x00);
			writeInt(0x00);
			writeInt(0x00);

			writeInt(0x00); // unk Ertheia
			writeInt(0x00); // unk Ertheia

			for(int PAPERDOLL_ID : Inventory.PAPERDOLL_ORDER)
				writeInt(charInfoPackage.getPaperdollItemId(PAPERDOLL_ID));

			writeInt(charInfoPackage.getPaperdollVisualId(Inventory.PAPERDOLL_RHAND)); //Внешний вид оружия (ИД Итема).
			writeInt(charInfoPackage.getPaperdollVisualId(Inventory.PAPERDOLL_LHAND)); //Внешний вид щита (ИД Итема).
			writeInt(charInfoPackage.getPaperdollVisualId(Inventory.PAPERDOLL_GLOVES)); //Внешний вид перчаток (ИД Итема).
			writeInt(charInfoPackage.getPaperdollVisualId(Inventory.PAPERDOLL_CHEST)); //Внешний вид верха (ИД Итема).
			writeInt(charInfoPackage.getPaperdollVisualId(Inventory.PAPERDOLL_LEGS)); //Внешний вид низа (ИД Итема).
			writeInt(charInfoPackage.getPaperdollVisualId(Inventory.PAPERDOLL_FEET)); //Внешний вид ботинок (ИД Итема).
			writeInt(charInfoPackage.getPaperdollVisualId(Inventory.PAPERDOLL_LRHAND));
			writeInt(charInfoPackage.getPaperdollVisualId(Inventory.PAPERDOLL_HAIR)); //Внешний вид шляпы (ИД итема).
			writeInt(charInfoPackage.getPaperdollVisualId(Inventory.PAPERDOLL_DHAIR)); //Внешний вид маски (ИД итема).

			writeShort(charInfoPackage.getPaperdollEnchantEffect(Inventory.PAPERDOLL_CHEST)); // unk Episodion
			writeShort(charInfoPackage.getPaperdollEnchantEffect(Inventory.PAPERDOLL_LEGS)); // unk Episodion
			writeShort(charInfoPackage.getPaperdollEnchantEffect(Inventory.PAPERDOLL_HEAD)); // unk Episodion
			writeShort(charInfoPackage.getPaperdollEnchantEffect(Inventory.PAPERDOLL_GLOVES)); // unk Episodion
			writeShort(charInfoPackage.getPaperdollEnchantEffect(Inventory.PAPERDOLL_FEET)); // unk Episodion

			writeInt(charInfoPackage.getPaperdollItemId(Inventory.PAPERDOLL_HAIR) > 0 ? charInfoPackage.getSex() : charInfoPackage.getHairStyle());
			writeInt(charInfoPackage.getHairColor());
			writeInt(charInfoPackage.getFace());

			writeF(charInfoPackage.getMaxHp()); // hp max
			writeF(charInfoPackage.getMaxMp()); // mp max

			writeInt(charInfoPackage.getAccessLevel() > -100 ? charInfoPackage.getDeleteTimer() : -1);
			writeInt(charInfoPackage.getClassId());
			writeInt(i == lastUsed ? 1 : 0);

			writeByte(Math.min(charInfoPackage.getPaperdollEnchantEffect(Inventory.PAPERDOLL_RHAND), 127));
			writeInt(charInfoPackage.getPaperdollVariation1Id(Inventory.PAPERDOLL_RHAND));
			writeInt(charInfoPackage.getPaperdollVariation2Id(Inventory.PAPERDOLL_RHAND));

			writeInt(0x00);

			//TODO: Pet info?
			writeInt(0x00);
			writeInt(0x00);
			writeInt(0x00);
			writeInt(0x00);
			writeF(0x00);
			writeF(0x00);

			writeInt(0x00);
			writeInt(0x00);
			writeInt(0x00);

			writeInt(charInfoPackage.isAvailable());
			writeByte(0x00); // UNK
			writeByte(charInfoPackage.isHero()); // hero glow
			writeByte(charInfoPackage.isHairAccessoryEnabled() ? 0x01 : 0x00); // show hair accessory if enabled
		}
	}

	public static CharSelectInfoPackage[] loadCharacterSelectInfo(String loginName)
	{
		CharSelectInfoPackage charInfopackage;
		List<CharSelectInfoPackage> characterList = new ArrayList<CharSelectInfoPackage>();

		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT * FROM characters AS c LEFT JOIN character_subclasses AS cs ON (c.obj_Id=cs.char_obj_id AND cs.active=1) WHERE account_name=? LIMIT 7");
			statement.setString(1, loginName);
			rset = statement.executeQuery();
			while(rset.next()) // fills the package
			{
				charInfopackage = restoreChar(rset);
				if(charInfopackage != null)
					characterList.add(charInfopackage);
			}
		}
		catch(Exception e)
		{
			_log.error("could not restore charinfo:", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}

		return characterList.toArray(new CharSelectInfoPackage[characterList.size()]);
	}

	private static int restoreBaseClassId(int objId)
	{
		int classId = 0;

		Connection con = null;
		PreparedStatement statement = null;
		ResultSet rset = null;
		try
		{
			con = DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT class_id FROM character_subclasses WHERE char_obj_id=? AND type=" + SubClassType.BASE_CLASS.ordinal());
			statement.setInt(1, objId);
			rset = statement.executeQuery();
			while(rset.next())
			{
				classId = rset.getInt("class_id");
			}
		}
		catch(Exception e)
		{
			_log.error("could not restore base class id:", e);
		}
		finally
		{
			DbUtils.closeQuietly(con, statement, rset);
		}

		return classId;
	}

	private static CharSelectInfoPackage restoreChar(ResultSet chardata)
	{
		CharSelectInfoPackage charInfopackage = null;
		try
		{
			int objectId = chardata.getInt("obj_Id");
			int classid = chardata.getInt("class_id");
			int baseClassId = classid;
			boolean useBaseClass = chardata.getInt("type") == SubClassType.BASE_CLASS.ordinal();
			if(!useBaseClass)
				baseClassId = restoreBaseClassId(objectId);

			Race race = ClassId.VALUES[baseClassId].getRace();
			if(race == null)
			{
				_log.warn(CharacterSelectionInfoPacket.class.getSimpleName() + ": Race was not found for the class id: " + baseClassId);
				return null;
			}

			String name = chardata.getString("char_name");
			charInfopackage = new CharSelectInfoPackage(objectId, name);
			charInfopackage.setMaxHp(chardata.getInt("maxHp"));
			charInfopackage.setCurrentHp(chardata.getDouble("curHp"));
			charInfopackage.setMaxMp(chardata.getInt("maxMp"));
			charInfopackage.setCurrentMp(chardata.getDouble("curMp"));

			charInfopackage.setX(chardata.getInt("x"));
			charInfopackage.setY(chardata.getInt("y"));
			charInfopackage.setZ(chardata.getInt("z"));
			charInfopackage.setPk(chardata.getInt("pkkills"));
			charInfopackage.setPvP(chardata.getInt("pvpkills"));

			int face = chardata.getInt("beautyFace");
			charInfopackage.setFace(face > 0 ? face : chardata.getInt("face"));

			int hairstyle = chardata.getInt("beautyHairstyle");
			charInfopackage.setHairStyle(hairstyle > 0 ? hairstyle : chardata.getInt("hairstyle"));

			int haircolor = chardata.getInt("beautyHaircolor");
			charInfopackage.setHairColor(haircolor > 0 ? haircolor : chardata.getInt("haircolor"));

			charInfopackage.setSex(chardata.getInt("sex"));

			charInfopackage.setExp(chardata.getLong("exp"));
			charInfopackage.setSp(chardata.getLong("sp"));
			charInfopackage.setClanId(chardata.getInt("clanid"));

			charInfopackage.setKarma(chardata.getInt("karma"));
			charInfopackage.setRace(race.ordinal());
			charInfopackage.setClassId(classid);
			charInfopackage.setBaseClassId(baseClassId);
			long deletetime = chardata.getLong("deletetime");
			int deletehours = 0;
			if(Config.CHARACTER_DELETE_AFTER_HOURS > 0)
				if(deletetime > 0)
				{
					deletetime = (int) (System.currentTimeMillis() / 1000 - deletetime);
					deletehours = (int) (deletetime / 3600);
					if(deletehours >= Config.CHARACTER_DELETE_AFTER_HOURS)
					{
						CharacterDAO.getInstance().deleteCharByObjId(objectId);
						return null;
					}
					deletetime = Config.CHARACTER_DELETE_AFTER_HOURS * 3600 - deletetime;
				}
				else
					deletetime = 0;
			charInfopackage.setDeleteTimer((int) deletetime);
			charInfopackage.setLastAccess(chardata.getLong("lastAccess") * 1000L);
			charInfopackage.setAccessLevel(chardata.getInt("accesslevel"));

			charInfopackage.setHairAccessoryEnabled(chardata.getInt("hide_head_accessories") == 0);

			if(charInfopackage.getAccessLevel() < 0 && !AutoBan.isBanned(objectId))
				charInfopackage.setAccessLevel(0);
		}
		catch(Exception e)
		{
			_log.error("", e);
		}

		return charInfopackage;
	}
}