/*
 * This file is part of the L2J Mobius project.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.mobius.gameserver.datatables;

import com.l2jmobius.Config;
import com.l2jmobius.commons.database.DatabaseFactory;
import com.l2jmobius.gameserver.model.holders.BuffSkillHolder;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * This class loads available skills and stores players' buff schemes into _schemesTable.
 */
public class SchemeBufferTable
{
	private static final Logger LOGGER = Logger.getLogger(SchemeBufferTable.class.getName());
	
	private static final String LOAD_SCHEMES = "SELECT * FROM buffer_schemes";
	private static final String DELETE_SCHEMES = "TRUNCATE TABLE buffer_schemes";
	private static final String INSERT_SCHEME = "INSERT INTO buffer_schemes (object_id, scheme_name, skills) VALUES (?,?,?)";
	
	private final Map<Integer, HashMap<String, ArrayList<Integer>>> _schemesTable = new ConcurrentHashMap<>();
	private final Map<Integer, BuffSkillHolder> _availableBuffs = new LinkedHashMap<>();
	
	public SchemeBufferTable()
	{
		int count = 0;
		
		try (Connection con = DatabaseFactory.getConnection())
		{
			PreparedStatement st = con.prepareStatement(LOAD_SCHEMES);
			ResultSet rs = st.executeQuery();
			
			while (rs.next())
			{
				final int objectId = rs.getInt("object_id");
				
				final String schemeName = rs.getString("scheme_name");
				final String[] skills = rs.getString("skills").split(",");
				
				ArrayList<Integer> schemeList = new ArrayList<>();
				
				for (String skill : skills)
				{
					// Don't feed the skills list if the list is empty.
					if (skill.isEmpty())
					{
						break;
					}
					
					schemeList.add(Integer.valueOf(skill));
				}
				
				setScheme(objectId, schemeName, schemeList);
				count++;
			}
			
			rs.close();
			st.close();
		}
		catch (Exception e)
		{
			LOGGER.warning("SchemeBufferTable: Failed to load buff schemes : " + e);
		}
		
		try
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(new File("./data/SchemeBufferSkills.xml"));
			
			final Node n = doc.getFirstChild();
			
			for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling())
			{
				if (!d.getNodeName().equalsIgnoreCase("category"))
				{
					continue;
				}
				
				final String category = d.getAttributes().getNamedItem("type").getNodeValue();
				
				for (Node c = d.getFirstChild(); c != null; c = c.getNextSibling())
				{
					if (!c.getNodeName().equalsIgnoreCase("buff"))
					{
						continue;
					}
					
					final NamedNodeMap attrs = c.getAttributes();
					final int skillId = Integer.parseInt(attrs.getNamedItem("id").getNodeValue());
					
					_availableBuffs.put(skillId, new BuffSkillHolder(skillId, Integer.parseInt(attrs.getNamedItem("price").getNodeValue()), category, attrs.getNamedItem("desc").getNodeValue()));
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.warning("SchemeBufferTable: Failed to load buff info : " + e);
		}
		LOGGER.info("SchemeBufferTable: Loaded " + count + " players schemes and " + _availableBuffs.size() + " available buffs.");
	}
	
	public void saveSchemes()
	{
		try (Connection con = DatabaseFactory.getConnection())
		{
			// Delete all entries from database.
			PreparedStatement st = con.prepareStatement(DELETE_SCHEMES);
			st.execute();
			st.close();
			
			st = con.prepareStatement(INSERT_SCHEME);
			
			// Save _schemesTable content.
			for (Map.Entry<Integer, HashMap<String, ArrayList<Integer>>> player : _schemesTable.entrySet())
			{
				for (Map.Entry<String, ArrayList<Integer>> scheme : player.getValue().entrySet())
				{
					// Build a String composed of skill ids seperated by a ",".
					final StringBuilder sb = new StringBuilder();
					for (int skillId : scheme.getValue())
					{
						sb.append(skillId + ",");
					}
					
					// Delete the last "," : must be called only if there is something to delete !
					if (sb.length() > 0)
					{
						sb.setLength(sb.length() - 1);
					}
					
					st.setInt(1, player.getKey());
					st.setString(2, scheme.getKey());
					st.setString(3, sb.toString());
					st.addBatch();
				}
			}
			st.executeBatch();
			st.close();
		}
		catch (Exception e)
		{
			LOGGER.warning("BufferTableScheme: Error while saving schemes : " + e);
		}
	}
	
	public void setScheme(int playerId, String schemeName, ArrayList<Integer> list)
	{
		if (!_schemesTable.containsKey(playerId))
		{
			_schemesTable.put(playerId, new HashMap<>());
		}
		else if (_schemesTable.get(playerId).size() >= Config.BUFFER_MAX_SCHEMES)
		{
			return;
		}
		
		_schemesTable.get(playerId).put(schemeName, list);
	}
	
	/**
	 * @param playerId : The player objectId to check.
	 * @return the list of schemes for a given player.
	 */
	public Map<String, ArrayList<Integer>> getPlayerSchemes(int playerId)
	{
		return _schemesTable.get(playerId);
	}
	
	/**
	 * @param playerId : The player objectId to check.
	 * @param schemeName : The scheme name to check.
	 * @return the List holding skills for the given scheme name and player, or null (if scheme or player isn't registered).
	 */
	public List<Integer> getScheme(int playerId, String schemeName)
	{
		if ((_schemesTable.get(playerId) == null) || (_schemesTable.get(playerId).get(schemeName) == null))
		{
			return Collections.emptyList();
		}
		
		return _schemesTable.get(playerId).get(schemeName);
	}
	
	/**
	 * @param playerId : The player objectId to check.
	 * @param schemeName : The scheme name to check.
	 * @param skillId : The skill id to check.
	 * @return true if the skill is already registered on the scheme, or false otherwise.
	 */
	public boolean getSchemeContainsSkill(int playerId, String schemeName, int skillId)
	{
		final List<Integer> skills = getScheme(playerId, schemeName);
		if (skills.isEmpty())
		{
			return false;
		}
		
		for (int id : skills)
		{
			if (id == skillId)
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * @param groupType : The type of skills to return.
	 * @return a list of skills ids based on the given groupType.
	 */
	public List<Integer> getSkillsIdsByType(String groupType)
	{
		List<Integer> skills = new ArrayList<>();
		for (BuffSkillHolder skill : _availableBuffs.values())
		{
			if (skill.getType().equalsIgnoreCase(groupType))
			{
				skills.add(skill.getId());
			}
		}
		return skills;
	}
	
	/**
	 * @return a list of all buff types available.
	 */
	public List<String> getSkillTypes()
	{
		List<String> skillTypes = new ArrayList<>();
		for (BuffSkillHolder skill : _availableBuffs.values())
		{
			if (!skillTypes.contains(skill.getType()))
			{
				skillTypes.add(skill.getType());
			}
		}
		return skillTypes;
	}
	
	public BuffSkillHolder getAvailableBuff(int skillId)
	{
		return _availableBuffs.get(skillId);
	}
	
	public static SchemeBufferTable getInstance()
	{
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder
	{
		protected static final SchemeBufferTable INSTANCE = new SchemeBufferTable();
	}
}