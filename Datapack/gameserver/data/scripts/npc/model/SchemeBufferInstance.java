package npc.model;

import org.l2j.commons.collections.MultiValueSet;
import org.l2j.commons.database.L2DatabaseFactory;
import org.l2j.commons.dbutils.DbUtils;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.data.xml.holder.SkillHolder;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.Servitor;
import org.l2j.gameserver.model.actor.instances.player.Cubic;
import org.l2j.gameserver.model.entity.olympiad.Olympiad;
import org.l2j.gameserver.model.instances.NpcInstance;
import org.l2j.gameserver.network.l2.components.HtmlMessage;
import org.l2j.gameserver.network.l2.components.SystemMsg;
import org.l2j.gameserver.network.l2.s2c.*;
import org.l2j.gameserver.templates.npc.NpcTemplate;
import org.l2j.gameserver.utils.ItemFunctions;
import org.l2j.gameserver.utils.Util;
import io.github.joealisson.primitive.lists.IntList;
import io.github.joealisson.primitive.lists.impl.ArrayIntList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SchemeBufferInstance extends NpcInstance
{
	private static final long serialVersionUID = 1L;
	
	private static final Logger _log = LoggerFactory.getLogger(SchemeBufferInstance.class);
	
	private static final boolean DEBUG = false;
	private static final String TITLE_NAME = "Battle For Power Buffer";
    private static final boolean ENABLE_VIP_BUFFER = Config.NpcBuffer_VIP;
    private static final int VIP_ACCESS_LEVEL = Config.NpcBuffer_VIP_ALV;
    private static final boolean ENABLE_BUFF_SECTION = Config.NpcBuffer_EnableBuff;
    private static final boolean ENABLE_SCHEME_SYSTEM = Config.NpcBuffer_EnableScheme;
    private static final boolean ENABLE_HEAL = Config.NpcBuffer_EnableHeal;
    private static final boolean ENABLE_BUFFS = Config.NpcBuffer_EnableBuffs;
    private static final boolean ENABLE_RESIST = Config.NpcBuffer_EnableResist;
    private static final boolean ENABLE_SONGS = Config.NpcBuffer_EnableSong;
    private static final boolean ENABLE_DANCES = Config.NpcBuffer_EnableDance;
    private static final boolean ENABLE_CHANTS = Config.NpcBuffer_EnableChant;
    private static final boolean ENABLE_OTHERS = Config.NpcBuffer_EnableOther;
    private static final boolean ENABLE_SPECIAL = Config.NpcBuffer_EnableSpecial;
    private static final boolean ENABLE_CUBIC = Config.NpcBuffer_EnableCubic;
    private static final boolean ENABLE_BUFF_REMOVE = Config.NpcBuffer_EnableCancel;
    private static final boolean ENABLE_BUFF_SET = Config.NpcBuffer_EnableBuffSet;
    private static final boolean BUFF_WITH_KARMA = Config.NpcBuffer_EnableBuffPK;
    private static final boolean FREE_BUFFS = Config.NpcBuffer_EnableFreeBuffs;
    private static final boolean TIME_OUT = Config.NpcBuffer_EnableTimeOut;
    private static final int TIME_OUT_TIME = Config.NpcBuffer_TimeOutTime;
    private static final int MIN_LEVEL = Config.NpcBuffer_MinLevel;
    private static final int BUFF_REMOVE_PRICE = Config.NpcBuffer_PriceCancel;
    private static final int HEAL_PRICE = Config.NpcBuffer_PriceHeal;
    private static final int BUFF_PRICE = Config.NpcBuffer_PriceBuffs;
    private static final int RESIST_PRICE = Config.NpcBuffer_PriceResist;
    private static final int SONG_PRICE = Config.NpcBuffer_PriceSong;
    private static final int DANCE_PRICE = Config.NpcBuffer_PriceDance;
    private static final int CHANT_PRICE = Config.NpcBuffer_PriceChant;
    private static final int OTHERS_PRICE = Config.NpcBuffer_PriceOther;
    private static final int SPECIAL_PRICE = Config.NpcBuffer_PriceSpecial;
    private static final int CUBIC_PRICE = Config.NpcBuffer_PriceCubic;
    private static final int BUFF_SET_PRICE = Config.NpcBuffer_PriceSet;
    private static final int SCHEME_BUFF_PRICE = Config.NpcBuffer_PriceScheme;
    private static final int SCHEMES_PER_PLAYER = Config.NpcBuffer_MaxScheme;
    private static final int MAX_SCHEME_BUFFS = Config.ALT_BUFF_LIMIT;
    private static final int MAX_SCHEME_DANCES = Config.ALT_MUSIC_LIMIT;
    private int PET_BUFF = 0;
    
    private static final int CONSUMABLE_ID = 57;
    
    private static final String SET_FIGHTER = "Fighter";
    private static final String SET_MAGE = "Mage";
    private static final String SET_ALL = "All";
    private static final String SET_NONE = "None";
	
	public SchemeBufferInstance(int objectId, NpcTemplate template, MultiValueSet<String> set)
	{
		super(objectId, template, set);
	}
	
	private static void print(Exception e)
	{
		_log.warn(">>>" + e.toString() + "<<<");
		if (DEBUG)
			e.printStackTrace();
	}
	
	public void setPetBuff(String eventParam1)
	{
		PET_BUFF = Integer.parseInt(eventParam1);
	}
	
	public boolean isPetBuff()
	{
		return PET_BUFF != 0;
	}
	
	@Override
	public void showChatWindow(Player player, int val, boolean firstTalk, Object... arg)
    {
        if (this != player.getTarget())
        {
            player.setTarget(this);
            player.sendPacket(new MyTargetSelectedPacket(getObjectId(), 0));
            player.sendPacket(new ValidateLocationPacket(this));
        }
        else if (!isInRange(player, 200L))
        {
            player.getAI().setIntention(CtrlIntention.AI_INTENTION_INTERACT, this);
        }
        else
        {
        	HtmlMessage msg = new HtmlMessage(this).setPlayVoice(firstTalk);
        	
        	if ((int)(System.currentTimeMillis()/1000) > player.getblockUntilTime())
            {
                if (!ENABLE_VIP_BUFFER || ENABLE_VIP_BUFFER && player.getAccessLevel() == VIP_ACCESS_LEVEL)
                {
                    if (!BUFF_WITH_KARMA && player.isPK())
                    {
                    	msg.setHtml(showText(player, "Info", "You have too much <font color=FF0000>karma!</font><br>Come back,<br>when you don't have any karma!", false, "Return", "main"));
                    }
                    else if(Olympiad.isRegistered(player))
                    {
                    	msg.setHtml(showText(player, "Info", "You can not buff while you are in <font color=FF0000>Olympiad!</font><br>Come back,<br>when you are out of the Olympiad.", false, "Return", "main"));
                    }
                    else if (player.getLevel() < MIN_LEVEL)
                    {	
                    	msg.setHtml(showText(player, "Info", "Your level is too low!<br>You have to be at least level <font color=LEVEL>" + MIN_LEVEL + "</font>,<br>to use my services!", false, "Return", "main"));
                    }
                    else if (player.getPvpFlag() > 0)
                    {
                    	msg.setHtml(showText(player, "Info", "You can't buff while you are <font color=800080>flagged!</font><br>Wait some time and try again!", false, "Return", "main"));
                    }
                    else if (player.isInCombat())
                    {
                    	msg.setHtml(showText(player, "Info", "You can't buff while you are attacking!<br>Stop your fight and try again!", false, "Return", "main"));
                    }
                    else
                    {
                    	msg.setHtml(main(player));
                    }
                }
                else
                {
                	msg.setHtml(showText(player, "Sorry", "This buffer is only for VIP's!<br>Contact the administrator for more info!", false, "Return", "main"));
                }         
            }
            else
            {
            	msg.setHtml(showText(player, "Sorry", "You have to wait a while!<br>if you wish to use my services!", false, "Return", "main"));
            }
        	
        	//msg.replace("%objectId%", String.valueOf(getObjectId()));
    		player.sendPacket(msg);
        }
    }
	
	private String main(final Player player)
	{
		String MAIN_HTML_MESSAGE = "<html><body><head>" + TITLE_NAME + "</head><center><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32>";
        //int td=0;
       // String[] TRS = {"<table width=100% border=0 cellspacing=0 cellpadding=1 bgcolor=444444><tr><td height=25>", "</td>", "<td height=25>", "</td></tr></table>"};

        final String bottonA, bottonB, bottonC;
        if (isPetBuff())
        {
            bottonA = "Auto Buff Pet"; bottonB = "Heal My Pet"; bottonC = "Remove Pet Buffs";
            MAIN_HTML_MESSAGE += "<button value=\"Pet Options\" action=\"bypass -h npc_%objectId%_buffpet 0 0 0\" width=130 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">";
        }
        else
        {
            bottonA = "Auto Buff"; bottonB = "Heal"; bottonC = "Remove Buffs";
            MAIN_HTML_MESSAGE += "<button value=\"Char Options\" action=\"bypass -h npc_%objectId%_buffpet 1 0 0\" width=130 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"><br>";
        }
        
        if (ENABLE_BUFF_SECTION)
        {
            if (ENABLE_BUFFS)
            {
                MAIN_HTML_MESSAGE += "<button value=\"Buffs\" action=\"bypass -h npc_%objectId%_redirect view_buffs 0 0\" width=130 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"><br>";
            }
            if (ENABLE_RESIST)
            {
                MAIN_HTML_MESSAGE += "<button value=\"Resist\" action=\"bypass -h npc_%objectId%_redirect view_resists 0 0\" width=130 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"><br>";
            }
            if (ENABLE_SONGS)
            {
                MAIN_HTML_MESSAGE += "<button value=\"Songs\" action=\"bypass -h npc_%objectId%_redirect view_songs 0 0\" width=130 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"><br>";
            }
            if (ENABLE_DANCES)
            {
                MAIN_HTML_MESSAGE += "<button value=\"Dances\" action=\"bypass -h npc_%objectId%_redirect view_dances 0 0\" width=130 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"><br>";
            }
            if (ENABLE_CHANTS)
            {
                MAIN_HTML_MESSAGE += "<button value=\"Chants\" action=\"bypass -h npc_%objectId%_redirect view_chants 0 0\" width=130 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"><br>";
            }
            if (ENABLE_SPECIAL)
            {
                MAIN_HTML_MESSAGE += "<button value=\"Special\" action=\"bypass -h npc_%objectId%_redirect view_special 0 0\" width=130 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"><br>";
            }
            if (ENABLE_OTHERS)
            {
                MAIN_HTML_MESSAGE += "<button value=\"Others\" action=\"bypass -h npc_%objectId%_redirect view_others 0 0\" width=130 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"><br>";
            }
        }

        if (ENABLE_CUBIC)
        {
            MAIN_HTML_MESSAGE += "<button value=\"Cubics\" action=\"bypass -h npc_%objectId%_redirect view_cubic 0 0\" width=130 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"><br>";
        }
        
       /* if (MESSAGE.length() > 0)
        {
            MAIN_HTML_MESSAGE += "<BR1><table width=100% border=0 cellspacing=0 cellpadding=1 bgcolor=444444><tr>"
                              +  "<td><font color=00FFFF>Buffs:</font></td><td align=right>...</td></tr></table>"
                              +  "<BR1><table cellspacing=0 cellpadding=0>" + MESSAGE + "</table>";
            MESSAGE = "";
            td = 0;
        }
		*/

        if (ENABLE_BUFF_SET)
        {
            MAIN_HTML_MESSAGE += "<button value=\"" + bottonA + "\" action=\"bypass -h npc_%objectId%_castBuffSet 0 0 0\" width=130 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"><br>";
        }
        
        if (ENABLE_HEAL)
        {
            MAIN_HTML_MESSAGE += "<button value=\"" + bottonB + "\" action=\"bypass -h npc_%objectId%_heal 0 0 0\" width=130 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"><br>";
        }
        
        if (ENABLE_BUFF_REMOVE)
        {
            MAIN_HTML_MESSAGE += "<button value=\"" + bottonC + "\" action=\"bypass -h npc_%objectId%_removeBuffs 0 0 0\" width=130 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"><br>";
        }
        
      /*  if (MESSAGE.length() > 0)
        {
            MAIN_HTML_MESSAGE += "<BR1><table width=100% border=0 cellspacing=0 cellpadding=1 bgcolor=444444><tr>"
                              +  "<td><font color=00FFFF>Preset:</font></td><td align=right><font color=LEVEL>" + Util.formatAdena(BUFF_SET_PRICE) + "</font> adena</td></tr></table>"
                              +  "<BR1><table cellspacing=0 cellpadding=0>" + MESSAGE + "</table>";
            MESSAGE = "";
            td = 0;
        }
*/
        if (ENABLE_SCHEME_SYSTEM)
        {
            MAIN_HTML_MESSAGE += generateScheme(player);
        }
        
        if (player.isGM())
        {
            MAIN_HTML_MESSAGE += "<br><button value=\"GM Manage Buffs\" action=\"bypass -h npc_%objectId%_redirect manage_buffs 0 0\" width=130 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">";
        }
            MAIN_HTML_MESSAGE += "<br><font color=303030>" + TITLE_NAME + "</font>" +  "</center></body></html>";
        return MAIN_HTML_MESSAGE;
	}
	
	private void addTimeout(Player player, SetupGaugePacket.Colors gaugeColor, int amount, int offset)
    {
        int endtime = (int) ((System.currentTimeMillis() + amount * 1000) / 1000);
        player.setblockUntilTime(endtime);
        player.sendPacket(new SetupGaugePacket(player, gaugeColor, amount * 1000 + offset));
    }
	
	private String viewAllSchemeBuffs(String scheme, String page, String action)
    {
		List<String> buffList = new ArrayList<String>();
        String HTML_MESSAGE = "<head>" + TITLE_NAME + "</head><body><center><br>";
        String[] eventSplit = viewAllSchemeBuffs$getBuffCount(scheme).split(" ");
        int TOTAL_BUFF = Integer.parseInt(eventSplit[0]);
        int BUFF_COUNT = Integer.parseInt(eventSplit[1]);
        int DANCE_SONG = Integer.parseInt(eventSplit[2]);
        Connection con = null;
        PreparedStatement getBuffCount = null;
        ResultSet rss = null;
        
        try
        {
        	con = L2DatabaseFactory.getInstance().getConnection();
            if (action.equals("add"))
            {
                HTML_MESSAGE += "You can add <font color=LEVEL>" + (MAX_SCHEME_BUFFS - BUFF_COUNT) + "</font> Buffs and <font color=LEVEL>" + (MAX_SCHEME_DANCES - DANCE_SONG) + "</font> Dances more!";
                String QUERY = "SELECT * FROM npcbuffer_buff_list WHERE buffType IN (" + generateQuery(BUFF_COUNT, DANCE_SONG) + ") AND canUse=1 ORDER BY Buff_Class ASC, id";
                getBuffCount = con.prepareStatement(QUERY);
                rss = getBuffCount.executeQuery();
                while (rss.next())
                {
                    String name = SkillHolder.getInstance().getSkill(rss.getInt("buffId"), rss.getInt("buffLevel")).getName();
                    name = name.replace(" ", "+");
                    buffList.add(name + "_" + rss.getInt("buffId") + "_" + rss.getInt("buffLevel"));
                }
            }
            else if (action.equals("remove"))
            {
                HTML_MESSAGE += "You have <font color=LEVEL>" + BUFF_COUNT + "</font> Buffs and <font color=LEVEL>" + DANCE_SONG + "</font> Dances";
                String QUERY = "SELECT * FROM npcbuffer_scheme_contents WHERE scheme_id=? ORDER BY Buff_Class ASC, id";
                getBuffCount = con.prepareStatement(QUERY);
                getBuffCount.setString(1, scheme);
                rss = getBuffCount.executeQuery();
                while (rss.next())
                {
                    String name = SkillHolder.getInstance().getSkill(rss.getInt("skill_id"), rss.getInt("skill_level")).getName();
                    name = name.replace(" ", "+");
                    buffList.add(name + "_" + rss.getInt("skill_id") + "_" + rss.getInt("skill_level"));
                }
            }
            else if (DEBUG)
            {
            	throw new RuntimeException();
            }
        }
        catch (SQLException e)
        {
        	print(e);
        }
        finally
		{
			DbUtils.closeQuietly(con, getBuffCount, rss);
		}
        HTML_MESSAGE += "<BR1><table border=0><tr>";
        final int buffsPerPage = 20;
        final String width, pageName;
        int pc = (buffList.size() - 1) / buffsPerPage + 1;
        if (pc > 5)
        {
            width = "25";
            pageName = "P";
        }
        else
        {
            width = "50";
            pageName = "Page ";
        }
        for (int ii = 1; ii <= pc; ++ii)
            if (ii == Integer.parseInt(page))
            {
            	HTML_MESSAGE += "<td width=" + width + " align=center><font color=LEVEL>" + pageName + ii + "</font></td>";
            }
            else if (action.equals("add"))
            {
            	HTML_MESSAGE += "<td width=" + width + ">" + "<button value=\"" + pageName + ii + "\" action=\"bypass -h npc_%objectId%_manage_scheme_1 " + scheme + " " + ii + " x\" width=" + width + " height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>";
            }
            else if (action.equals("remove"))
            {
            	HTML_MESSAGE += "<td width=" + width + ">" + "<button value=\"" + pageName + ii + "\" action=\"bypass -h npc_%objectId%_manage_scheme_2 " + scheme + " " + ii + " x\" width=" + width + " height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>";
            }
            else if (DEBUG)
            {
            	throw new RuntimeException();
            }
        HTML_MESSAGE += "</tr></table>";

        int limit = buffsPerPage * Integer.parseInt(page);
        int start = limit - buffsPerPage;
        int end = Math.min(limit, buffList.size());
        int k=0;
        for (int i = start; i < end; ++i)
        {
            String value = buffList.get(i);
            value = value.replace("_", " ");
            String[] extr = value.split(" ");
            String name = extr[0];
            name = name.replace("+", " ");
            int id = Integer.parseInt(extr[1]);
            int level = Integer.parseInt(extr[2]);
            /*--String page = extr[3];--*/
            if (action.equals("add"))
            {
                if (!isUsed(scheme, id, level))
                {
                    if (k % 2 != 0) HTML_MESSAGE += "<BR1><table border=0 bgcolor=333333>";
                    else            HTML_MESSAGE += "<BR1><table border=0 bgcolor=292929>";
                    HTML_MESSAGE += "<tr><td width=35>" + getSkillIconHtml(id, level) + "</td><td fixwidth=170>" + name + "</td><td><button value=\"Add\" action=\"bypass -h npc_%objectId%_add_buff " + scheme + "_" + id + "_" + level + " " + page + " " + TOTAL_BUFF + "\" width=65 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>"
                                 +  "</tr></table>"; k+=1;
                }
            }
            else if (action.equals("remove"))
            {
                if (k % 2 != 0) HTML_MESSAGE += "<BR1><table border=0 bgcolor=333333>";
                else            HTML_MESSAGE += "<BR1><table border=0 bgcolor=292929>";
                HTML_MESSAGE += "<tr><td width=35>" + getSkillIconHtml(id, level) + "</td><td fixwidth=170>" + name + "</td><td><button value=\"Remove\" action=\"bypass -h npc_%objectId%_remove_buff " + scheme + "_" + id + "_" + level + " " + page + " " + TOTAL_BUFF + "\" width=65 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>"
                             +  "</table>"; k+=1;
            }
        }
        HTML_MESSAGE += "<br><br><button value=\"Back\" action=\"bypass -h npc_%objectId%_manage_scheme_select " + scheme + " x x\" width=100 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">"
                     +  "<button value=\"Home\" action=\"bypass -h npc_%objectId%_redirect main 0 0\" width=100 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">"
                     +  "<br><font color=303030>" + TITLE_NAME + "</font></center>";
        return HTML_MESSAGE;
    } //viewAllSchemeBuffs
	
	private void heal(Player player, boolean isPet)
    {
        if (!isPet)
        {
        	player.setCurrentHp(player.getMaxHp(), false);
        	player.setCurrentMp(player.getMaxMp());
        	player.setCurrentCp(player.getMaxCp());
        }
		for(Servitor servitor : player.getServitors())
		{
			servitor.setCurrentHp(servitor.getMaxHp(), false);
			servitor.setCurrentMp(servitor.getMaxMp());
			servitor.setCurrentCp(servitor.getMaxCp());
		}
    }
	
	private String getSkillIconHtml(int id, int level)
    {
        String iconNumber = getSkillIconNumber(id, level);
        return "<button action=\"bypass -h npc_%objectId%_description " + id + " " + level + " x\" width=32 height=32 back=\"Icon.skill" + iconNumber + "\" fore=\"Icon.skill" + iconNumber + "\">";
    }
	
	private String getSkillIconNumber(int id, int level)
    {
        String formato;
        if (id == 4) formato = "0004";
        else if (id > 9 && id < 100) formato = "00" + id;
        else if (id > 99 && id < 1000) formato = "0" + id;
        else if (id == 1517) formato = "1536";
        else if (id == 1518) formato = "1537";
        else if (id == 1547) formato = "0065";
        else if (id == 2076) formato = "0195";
        else if (id > 4550 && id < 4555) formato = "5739";
        else if (id > 4698 && id < 4701) formato = "1331";
        else if (id > 4701 && id < 4704) formato = "1332";
        else if (id == 6049) formato = "0094";
        else formato = String.valueOf(id);
        return formato;
    }
	
	private String deleteScheme(Player player)
    {
        String HTML = "<head>" + TITLE_NAME + "</head><body><center><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br>Available schemes:<br><br>";
        Connection con = null;
        PreparedStatement rss = null;
        ResultSet action = null;
        
        try
        {
        	con = L2DatabaseFactory.getInstance().getConnection();
            rss = con.prepareStatement("SELECT * FROM npcbuffer_scheme_list WHERE player_id=?");
            rss.setInt(1, player.getObjectId());
            action = rss.executeQuery();
            while (action.next())
                HTML += "<button value=\"" + action.getString("scheme_name") + "\" action=\"bypass -h npc_%objectId%_delete_c " + action.getString("id") + " " + action.getString("scheme_name") + " x\" width=200 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">";
        }
        catch (SQLException e)
        {
        	print(e);
        }
        finally
		{
			DbUtils.closeQuietly(con, rss, action);
		}
        HTML += "<br><button value=\"Back\" action=\"bypass -h npc_%objectId%_redirect main 0 0\" width=100 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">"
             +  "<br><font color=303030>" + TITLE_NAME + "</font></center>";
        return HTML;
    }
	
	private String getItemNameHtml(Player st, int itemval)
    {
        return "&#" + itemval + ";";
    }
	
	private String showText(Player st, String type, String text, boolean buttonEnabled, String buttonName, String location)
    {
        String MESSAGE = "<html><head>" + TITLE_NAME + "</head><body><center><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br>";
        MESSAGE += "<font color=LEVEL>" + type + "</font><br>" + text + "<br>";
        if (buttonEnabled)
            MESSAGE += "<button value=\"" + buttonName + "\" action=\"bypass -h npc_%objectId%_redirect " + location + " 0 0\" width=100 height=22 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">";
        MESSAGE += "<font color=303030>" + TITLE_NAME + "</font></center></body></html>";
        st.sendPacket(new PlaySoundPacket("ItemSound3.sys_shortage"));
        return MESSAGE;
    }
	
	private String buildHtml(String buffType)
    {
        String HTML_MESSAGE = "<head>" + TITLE_NAME + "</head><body><center><br>";

        List<String> availableBuffs = new ArrayList<String>();
        Connection con = null;
        PreparedStatement getList = null;
        ResultSet rs = null;
        
        try
        {
        	con = L2DatabaseFactory.getInstance().getConnection();
            getList = con.prepareStatement("SELECT buffId,buffLevel FROM npcbuffer_buff_list WHERE buffType=\"" + buffType + "\" AND canUse=1  ORDER BY Buff_Class ASC, id");
            rs = getList.executeQuery();
            while (rs.next()) {
          //try :
                int bId = rs.getInt("buffId");
                int bLevel = rs.getInt("buffLevel");
                String bName = SkillHolder.getInstance().getSkill(bId, bLevel).getName();
                bName = bName.replace(" ", "+");
                availableBuffs.add(bName + "_" + bId + "_" + bLevel);
          //except: HTML_MESSAGE += "Error loading buff list...<br>"
            }
        }
        catch (SQLException e)
        {
        	print(e);
        }
        finally
		{
			DbUtils.closeQuietly(con, getList, rs);
		}
        if (availableBuffs.size() == 0)
        {
            HTML_MESSAGE += "No buffs are available at this moment!";
        }
        else
        {
            if (FREE_BUFFS)
            {
                HTML_MESSAGE += "All buffs are for <font color=LEVEL>free</font>!";
            }
            else
            {
                int price = 0;
                if (buffType.equalsIgnoreCase("buff"))
                	price = BUFF_PRICE;
                else if (buffType.equalsIgnoreCase("resist"))
                	price = RESIST_PRICE;
                else if (buffType.equalsIgnoreCase("song"))
                	price = SONG_PRICE;
                else if (buffType.equalsIgnoreCase("dance"))
                	price = DANCE_PRICE;
                else if (buffType.equalsIgnoreCase("chant"))
                	price = CHANT_PRICE;
                else if (buffType.equalsIgnoreCase("others"))
                	price = OTHERS_PRICE;
                else if (buffType.equalsIgnoreCase("special"))
                	price = SPECIAL_PRICE;
                else if (buffType.equalsIgnoreCase("cubic"))
                	price = CUBIC_PRICE;
                else if (DEBUG)
                {
                	throw new RuntimeException();
                }
                HTML_MESSAGE += "All special buffs cost <font color=LEVEL>" + Util.formatAdena(price) + "</font> adena!";
            }
            HTML_MESSAGE += "<BR1><table>";
            for (String buff : availableBuffs)
            {
                buff = buff.replace("_", " ");
                String[] buffSplit = buff.split(" ");
                String name = buffSplit[0];
                int id = Integer.parseInt(buffSplit[1]);
                int level = Integer.parseInt(buffSplit[2]);
                name = name.replace("+", " ");
                HTML_MESSAGE += "<tr><td>" + getSkillIconHtml(id, level) + "</td><td><button value=\"" + name + "\" action=\"bypass -h npc_%objectId%_giveBuffs " + id + " " + level + " " + buffType + "\" width=190 height=32 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>";
            }
            HTML_MESSAGE += "</table>";
        }

        HTML_MESSAGE += "<br><button value=\"Back\" action=\"bypass -h npc_%objectId%_redirect main 0 0\" width=100 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">"
                     +  "<br><font color=303030>" + TITLE_NAME + "</font></center>";
        return HTML_MESSAGE;
    }
	
	private String editScheme(Player player)
    {
        String HTML = "<head>" + TITLE_NAME + "</head><body><center><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br>Select a scheme that you would like to manage:<br><br>";
        Connection con = null;
        PreparedStatement rss = null;
        ResultSet action = null;
        
        try
        {
        	con = L2DatabaseFactory.getInstance().getConnection();
            rss = con.prepareStatement("SELECT * FROM npcbuffer_scheme_list WHERE player_id=?");
            rss.setInt(1, player.getObjectId());
            action = rss.executeQuery();
            while (action.next()) {
                String name = action.getString("scheme_name");
                String id = action.getString("id");
                HTML += "<button value=\"" + name + "\" action=\"bypass -h npc_%objectId%_manage_scheme_select " + id + " x x\" width=200 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">";
            }
        }
        catch (SQLException e)
        {
        	print(e);
        }
        finally
		{
			DbUtils.closeQuietly(con, rss, action);
		}
        HTML += "<br><button value=\"Back\" action=\"bypass -h npc_%objectId%_redirect main 0 0\" width=100 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">"
             +  "<br><font color=303030>" + TITLE_NAME + "</font></center>";
        return HTML;
    }
	
	private String viewAllSchemeBuffs$getBuffCount(String scheme)
    {
        int count = 0; int D_S_Count = 0; int B_Count = 0;
        Connection con = null;
        PreparedStatement rss = null;
        ResultSet action = null;
        
        try
        {
        	con = L2DatabaseFactory.getInstance().getConnection();
            rss = con.prepareStatement("SELECT buff_class FROM npcbuffer_scheme_contents WHERE scheme_id=?");
            rss.setString(1, scheme);
            action = rss.executeQuery();
            while (action.next())
            {
                ++count;
                int val = action.getInt("buff_class");
                if (val == 1 || val == 2) ++D_S_Count;
                else ++B_Count;
            }
        }
        catch (SQLException e)
        {
        	print(e);
        }
        finally
		{
			DbUtils.closeQuietly(con, rss, action);
		}
        String res = count + " " + B_Count + " " + D_S_Count;
        return res;
    } //viewAllSchemeBuffs$getBuffCount
	
	private int getBuffCount(String scheme)
    {
        int count = 0;
        Connection con = null;
        PreparedStatement rss = null;
        ResultSet action = null;
        
        try
        {
        	con = L2DatabaseFactory.getInstance().getConnection();
            rss = con.prepareStatement("SELECT buff_class FROM npcbuffer_scheme_contents WHERE scheme_id=?");
            rss.setString(1, scheme);
            action = rss.executeQuery();
            while (action.next())
                ++count;
        }
        catch (SQLException e)
        {
        	print(e);
        }
        finally
		{
			DbUtils.closeQuietly(con, rss, action);
		}
        return count;
    }
	
	private String getOptionList(String scheme)
    {
        int bcount = getBuffCount(scheme);
        String HTML = "<head>" + TITLE_NAME + "</head><body><center><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br>There are <font color=LEVEL>" + bcount + "</font> buffs in current scheme!<br><br>";
        if (bcount < MAX_SCHEME_BUFFS + MAX_SCHEME_DANCES)
            HTML += "<button value=\"Add buffs\" action=\"bypass -h npc_%objectId%_manage_scheme_1 " + scheme + " 1 x\" width=200 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">";
        if (bcount > 0)
            HTML += "<button value=\"Remove buffs\" action=\"bypass -h npc_%objectId%_manage_scheme_2 " + scheme + " 1 x\" width=200 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">";
        HTML += "<br><button value=\"Back\" action=\"bypass -h npc_%objectId%_edit_1 0 0 0\" width=100 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">"
             +  "<button value=\"Home\" action=\"bypass -h npc_%objectId%_redirect main 0 0\" width=100 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">"
             +  "<br><font color=303030>" + TITLE_NAME + "</font></center>";
        return HTML;
    }
	
	private String viewAllBuffTypes()
    {
        String HTML_MESSAGE = "<head>" + TITLE_NAME + "</head><body><center><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br>";
        HTML_MESSAGE += "<font color=LEVEL>[Buff management]</font><br>";
        if (ENABLE_BUFFS)
        {
        	HTML_MESSAGE += "<button value=\"Buffs\" action=\"bypass -h npc_%objectId%_edit_buff_list buff Buffs 1\" width=200 height=22 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">";
        }
        if (ENABLE_RESIST)
        {
        	HTML_MESSAGE += "<button value=\"Resist Buffs\" action=\"bypass -h npc_%objectId%_edit_buff_list resist Resists 1\" width=200 height=22 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">";
        }
        if (ENABLE_SONGS)
        {
        	HTML_MESSAGE += "<button value=\"Songs\" action=\"bypass -h npc_%objectId%_edit_buff_list song Songs 1\" width=200 height=22 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">";
        }
        if (ENABLE_DANCES)
        {
        	HTML_MESSAGE += "<button value=\"Dances\" action=\"bypass -h npc_%objectId%_edit_buff_list dance Dances 1\" width=200 height=22 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">";
        }
        if (ENABLE_CHANTS)
        {
        	HTML_MESSAGE += "<button value=\"Chants\" action=\"bypass -h npc_%objectId%_edit_buff_list chant Chants 1\" width=200 height=22 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">";
        }
        if (ENABLE_SPECIAL)
        {
        	HTML_MESSAGE += "<button value=\"Special Buffs\" action=\"bypass -h npc_%objectId%_edit_buff_list special Special_Buffs 1\" width=200 height=22 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">";
        }
        if (ENABLE_OTHERS)
        {
        	HTML_MESSAGE += "<button value=\"Others Buffs\" action=\"bypass -h npc_%objectId%_edit_buff_list others Others_Buffs 1\" width=200 height=22 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">";
        }
        if (ENABLE_CUBIC)
        {
        	HTML_MESSAGE += "<button value=\"Cubics\" action=\"bypass -h npc_%objectId%_edit_buff_list cubic cubic_Buffs 1\" width=200 height=22 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">";
        }
        if (ENABLE_BUFF_SET)
        {
        	HTML_MESSAGE += "<button value=\"Buff Sets\" action=\"bypass -h npc_%objectId%_edit_buff_list set Buff_Sets 1\" width=200 height=22 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"><br>";
        }
        HTML_MESSAGE += "<button value=\"Back\" action=\"bypass -h npc_%objectId%_redirect main 0 0\" width=100 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">"
                     +  "<br><font color=303030>" + TITLE_NAME + "</font></center>";
        return HTML_MESSAGE;
    }
	
	private String createScheme()
    {
        return "<head>" + TITLE_NAME + "</head><body><center><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br><br>You MUST seprerate new words with a dot (.)<br><br>Scheme name: <edit var=\"name\" width=100><br><br>"
             +  "<button value=\"Create Scheme\" action=\"bypass -h npc_%objectId%_create $name no_name x x\" width=200 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">"
             +  "<br><button value=\"Back\" action=\"bypass -h npc_%objectId%_redirect main 0 0\" width=100 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">"
             +  "<br><font color=303030>" + TITLE_NAME + "</font></center>";
    }
	
	private String generateQuery(int case1, int case2)
    {
        StringBuilder qry = new StringBuilder();
        if (ENABLE_BUFFS) if (case1 < MAX_SCHEME_BUFFS) qry.append(",\"buff\"");
        if (ENABLE_RESIST) if (case1 < MAX_SCHEME_BUFFS) qry.append(",\"resist\"");
        if (ENABLE_SONGS) if (case2 < MAX_SCHEME_DANCES) qry.append(",\"song\"");
        if (ENABLE_DANCES) if (case2 < MAX_SCHEME_DANCES) qry.append(",\"dance\"");
        if (ENABLE_CHANTS) if (case1 < MAX_SCHEME_BUFFS) qry.append(",\"chant\"");
        if (ENABLE_OTHERS) if (case1 < MAX_SCHEME_BUFFS) qry.append(",\"others\"");
        if (ENABLE_SPECIAL) if (case1 < MAX_SCHEME_BUFFS) qry.append(",\"special\"");
        if (qry.length() > 0) qry.deleteCharAt(0);
        return qry.toString();
    }
	
	private String generateScheme(Player st)
	{
        List<String> schemeName = new ArrayList<String>();
        List<String> schemeId = new ArrayList<String>();
        String HTML = "";
        Connection con = null;
        PreparedStatement rss = null;
        ResultSet action = null;
        
        try
        {
        	con = L2DatabaseFactory.getInstance().getConnection();
            rss = con.prepareStatement("SELECT * FROM npcbuffer_scheme_list WHERE player_id=?");
            rss.setInt(1, st.getPlayer().getObjectId());
            action = rss.executeQuery();
            while (action.next())
            {
                schemeName.add(action.getString("scheme_name"));
                schemeId.add(action.getString("id"));
            }
        }
        catch (SQLException e)
        {
        	print(e);
        }
        finally
		{
			DbUtils.closeQuietly(con, rss, action);
		}
        
        HTML += "<BR1><font color=LEVEL>" + Util.formatAdena(SCHEME_BUFF_PRICE) + "</font> adena!";
        if (schemeName.size() > 0)
        {
            for (int i = 0; i < schemeName.size(); ++i) {
                HTML += "<button value=\"" + schemeName.get(i) + "\" action=\"bypass -h npc_%objectId%_cast " + schemeId.get(i) + " x x\" width=130 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"><br>";
         }
	}
        
        if (schemeName.size() < SCHEMES_PER_PLAYER)
        {
            HTML += "<button value=\"Create\" action=\"bypass -h npc_%objectId%_create_1 x x x\" width=85 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">";
        }
            else
            {
            HTML += "<BR1>";
            }
        
        if (schemeName.size() > 0)
        {
            HTML += "<button value=\"Edit\" action=\"bypass -h npc_%objectId%_edit_1 x x x\" width=85 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>" + "<td><button value=\"Delete\" action=\"bypass -h npc_%objectId%_delete_1 x x x\" width=85 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">";
        }
        return HTML;
    }
	
	private String manageSelectedSet(String id, String newVal, String opt3)
    {
        String[] bpid = id.split("_");
        String bId = bpid[0];
        String bLvl = bpid[1];
        Connection con = null;
        PreparedStatement upd = null;
        
        try
        {
        	con = L2DatabaseFactory.getInstance().getConnection();
            upd = con.prepareStatement("UPDATE npcbuffer_buff_list SET forClass=? WHERE buffId=? AND bufflevel=?");
            upd.setString(1, newVal);
            upd.setString(2, bId);
            upd.setString(3, bLvl);
            upd.executeUpdate();
            upd.close();
        }
        catch (SQLException e)
        {
        	print(e);
        }
        finally
		{
			DbUtils.closeQuietly(con, upd);
		}
        return viewAllBuffs("set", "Buff Sets", opt3);
    }
	
	private void manageSelectedBuff(String buffPosId, String canUseBuff)
    {
        String[] bpid = buffPosId.split("_");
        String bId = bpid[0];
        String bLvl = bpid[1];
        Connection con = null;
        PreparedStatement upd = null;
        
        try
        {
        	con = L2DatabaseFactory.getInstance().getConnection();
            upd = con.prepareStatement("UPDATE npcbuffer_buff_list SET canUse=? WHERE buffId=? AND buffLevel=? LIMIT 1");
            upd.setString(1, canUseBuff);
            upd.setString(2, bId);
            upd.setString(3, bLvl);
            upd.executeUpdate();
            upd.close();
        }
        catch (SQLException e)
        {
        	print(e);
        }
        finally
		{
			DbUtils.closeQuietly(con, upd);
		}
    }
	
	private int getBuffType(int id)
    {
        String val = "none";
        Connection con = null;
        PreparedStatement act = null;
        ResultSet rs = null;
        
        try
        {
        	con = L2DatabaseFactory.getInstance().getConnection();
            act = con.prepareStatement("SELECT buffType FROM npcbuffer_buff_list WHERE buffId=? LIMIT 1");
            act.setInt(1, id);
            rs = act.executeQuery();
            if (rs.next())
                val = rs.getString("buffType");
        }
        catch (SQLException e)
        {
        	print(e);
        }
        finally
		{
			DbUtils.closeQuietly(con, act, rs);
		}
		if(val.equalsIgnoreCase("buff"))
			return 0;
		if(val.equalsIgnoreCase("resist"))
			return 1;
		if(val.equalsIgnoreCase("song"))
			return 2;
		if(val.equalsIgnoreCase("dance"))
			return 3;
		if(val.equalsIgnoreCase("chant"))
			return 4;
		if(val.equalsIgnoreCase("others"))
			return 5;
		if(val.equalsIgnoreCase("special"))
			return 6;			
        return -1;
    }
	
    private boolean isEnabled(int id, int level)
    {
        boolean val = false;
        Connection con = null;
        PreparedStatement act = null;
        ResultSet rs = null;
        
        try
        {
        	con = L2DatabaseFactory.getInstance().getConnection();
            act = con.prepareStatement("SELECT canUse FROM npcbuffer_buff_list WHERE buffId=? AND buffLevel=? LIMIT 1");
            act.setInt(1, id);
            act.setInt(2, level);
            rs = act.executeQuery();
            if (rs.next())
                if ("1".equals(rs.getString("canUse"))) val = true;
        }
        catch (SQLException e)
        {
        	print(e);
        }
        finally
		{
			DbUtils.closeQuietly(con, act, rs);
		}
        return val;
    }
    
    private boolean isUsed(String scheme, int id, int level)
    {
        boolean used = false;
        Connection con = null;
        PreparedStatement rss = null;
        ResultSet action = null;
        
        try
        {
        	con = L2DatabaseFactory.getInstance().getConnection();
            rss = con.prepareStatement("SELECT id FROM npcbuffer_scheme_contents WHERE scheme_id=? AND skill_id=? AND skill_level=? LIMIT 1");
            rss.setString(1, scheme);
            rss.setInt(2, id);
            rss.setInt(3, level);
            action = rss.executeQuery();
            if (action.next())
                used = true;
        }
        catch (SQLException e)
        {
        	print(e);
        }
        finally
		{
			DbUtils.closeQuietly(con, rss, action);
		}
        return used;
    }
    
    private int getClassBuff(String id)
    {
        int val = 0;
        Connection con = null;
        PreparedStatement getTipo = null;
        ResultSet gt = null;
        
        try
        {
        	con = L2DatabaseFactory.getInstance().getConnection();
            getTipo = con.prepareStatement("SELECT buff_class FROM npcbuffer_buff_list WHERE buffId=?");
            getTipo.setString(1, id);
            gt = getTipo.executeQuery();
            if (gt.next())
                val = gt.getInt("buff_class");
        }
        catch (SQLException e)
        {
        	print(e);
        }
        finally
		{
			DbUtils.closeQuietly(con, getTipo, gt);
		}
        return val;
    }
	
    private String viewAllBuffs(String type, String typeName, String page)
    {
        List<String> buffList = new ArrayList<String>();
        String HTML_MESSAGE = "<head>" + TITLE_NAME + "</head><body><center><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br>";
        typeName = typeName.replace("_", " ");
        Connection con = null;
        PreparedStatement getBuffCount = null;
        ResultSet rss = null;
        
        try
        {
        	con = L2DatabaseFactory.getInstance().getConnection();
            if (type.equals("set"))
            {
                getBuffCount = con.prepareStatement("SELECT * FROM npcbuffer_buff_list WHERE buffType IN (" + generateQuery(0, 0) + ") AND canUse=1");
            }
            else
            {
                getBuffCount = con.prepareStatement("SELECT * FROM npcbuffer_buff_list WHERE buffType=?");
                getBuffCount.setString(1, type);
            }
            rss = getBuffCount.executeQuery();
            while (rss.next())
            {
                String name = SkillHolder.getInstance().getSkill(rss.getInt("buffId"), rss.getInt("buffLevel")).getName();
                name = name.replace(" ", "+");
                String usable = rss.getString("canUse");
                String forClass = rss.getString("forClass");
                String skill_id = rss.getString("buffId");
                String skill_level = rss.getString("buffLevel");
                buffList.add(name + "_" + forClass + "_" + page + "_" + usable + "_" + skill_id + "_" + skill_level);
            }
        }
        catch (SQLException e)
        {
        	print(e);
        }
        finally
		{
			DbUtils.closeQuietly(con, getBuffCount, rss);
		}
        Collections.sort(buffList);
        HTML_MESSAGE += "<font color=LEVEL>[Buff management - " + typeName + " - Page " + page + "]</font><br><table border=0><tr>";
        final int buffsPerPage;
        if (type.equals("set")) buffsPerPage = 12;
        else buffsPerPage = 20;
        final String width, pageName;
        int pc = (buffList.size() - 1) / buffsPerPage + 1;
        if (pc > 5)
        {
            width = "25";
            pageName = "P";
        }
        else
        {
            width = "50";
            pageName = "Page ";
        }
        typeName = typeName.replace(" ", "_");
        for (int ii = 1; ii <= pc; ++ii)
            if (ii == Integer.parseInt(page))
            {
            	HTML_MESSAGE += "<td width=" + width + " align=center><font color=LEVEL>" + pageName + ii + "</font></td>";
            }
            else
            {
            	HTML_MESSAGE += "<td width=" + width + "><button value=\"" + pageName + "" + ii + "\" action=\"bypass -h npc_%objectId%_edit_buff_list " + type + " " + typeName + " " + ii + "\" width=" + width + " height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td>";
            }
        HTML_MESSAGE += "</tr></table><br>";

        int limit = buffsPerPage * Integer.parseInt(page);
        int start = limit - buffsPerPage;
        int end = Math.min(limit, buffList.size());
        for (int i = start; i < end; ++i)
        {
            String value = buffList.get(i);
            value = value.replace("_", " ");
            String[] extr = value.split(" ");
            String name = extr[0];
            name = name.replace("+", " ");
            int forClass = Integer.parseInt(extr[1]);
            /*page = extr[2];*/
            int usable = Integer.parseInt(extr[3]);
            String skillPos = extr[4] + "_" + extr[5];
            if (i % 2 != 0)
            {
            	HTML_MESSAGE += "<BR1><table border=0 bgcolor=333333>";
            }
            else
            {
            	HTML_MESSAGE += "<BR1><table border=0 bgcolor=292929>";
            }
            if (type.equals("set"))
            {
                String listOrder = null;
                if (forClass == 0)
                {
                	listOrder = "List=\"" + SET_FIGHTER + ";" + SET_MAGE + ";" + SET_ALL + ";" + SET_NONE + ";\"";
                }
                else if (forClass == 1)
                {
                	listOrder = "List=\"" + SET_MAGE + ";" + SET_FIGHTER + ";" + SET_ALL + ";" + SET_NONE + ";\"";
                }
                else if (forClass == 2)
                {
                	listOrder = "List=\"" + SET_ALL + ";" + SET_FIGHTER + ";" + SET_MAGE + ";" + SET_NONE + ";\"";
                }
                else if (forClass == 3)
                {
                	listOrder = "List=\"" + SET_NONE + ";" + SET_FIGHTER + ";" + SET_MAGE + ";" + SET_ALL + ";\"";
                }
                HTML_MESSAGE += "<tr><td fixwidth=145>" + name + "</td><td width=70><combobox var=\"newSet" + i + "\" width=70 " + listOrder + "></td>"
                             +  "<td width=50><button value=\"Update\" action=\"bypass -h npc_%objectId%_changeBuffSet " + skillPos + " $newSet" + i + " " + page + "\" width=50 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>";
            }
            else
            {
                HTML_MESSAGE += "<tr><td fixwidth=170>" + name + "</td><td width=80>";
                if (usable == 1)
                {
                	HTML_MESSAGE += "<button value=\"Disable\" action=\"bypass -h npc_%objectId%_editSelectedBuff " + skillPos + " 0-" + page + " " + type + "\" width=80 height=22 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>";
                }
                else if (usable == 0)
                {
                	HTML_MESSAGE += "<button value=\"Enable\" action=\"bypass -h npc_%objectId%_editSelectedBuff " + skillPos + " 1-" + page + " " + type + "\" width=80 height=22 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\"></td></tr>";
                }
            }
            HTML_MESSAGE += "</table>";
        }
        HTML_MESSAGE += "<br><br><button value=\"Back\" action=\"bypass -h npc_%objectId%_redirect manage_buffs 0 0\" width=100 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">"
                     +  "<button value=\"Home\" action=\"bypass -h npc_%objectId%_redirect main 0 0\" width=100 height=20 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">"
                     +  "<br><font color=303030>" + TITLE_NAME + "</font></center>";
        return HTML_MESSAGE;
    }
	
	@Override
	public void onBypassFeedback(Player player, String command)
	{
		HtmlMessage msg = new HtmlMessage(this);
		
        String[] eventSplit = command.split(" ", 4);
        String eventParam0 = eventSplit[0];
        String eventParam1 = eventSplit[1];
        String eventParam2 = eventSplit[2];
        String eventParam3 = eventSplit[3];
        
        if (!FREE_BUFFS)
        {
        	if (player.getAdena() < SCHEME_BUFF_PRICE)
        	{
        		HtmlMessage msgNoAdena = new HtmlMessage(this);
        		msgNoAdena.setHtml(showText(player, "Sorry", "You don't have the enough adena, you need at least " + SCHEME_BUFF_PRICE + "!", false, "0", "0"));
        		//msgNoAdena.replace("%objectId%", String.valueOf(getObjectId()));
        		player.sendPacket(msgNoAdena);
        		return;
        	}
        }
			
		if (eventParam0.equalsIgnoreCase("buffpet"))
		{			
			if (System.currentTimeMillis()/1000 > player.getblockUntilTime())
			{
				setPetBuff(eventParam1);
				if (TIME_OUT) addTimeout(player, SetupGaugePacket.Colors.GREEN, TIME_OUT_TIME / 2, 600);
			}
			
			msg.setHtml(main(player));
		}
		else if (eventParam0.equals("redirect"))
		{
			if (eventParam1.equals("main"))
				msg.setHtml(main(player));
        	else if (eventParam1.equals("manage_buffs"))
        		msg.setHtml(viewAllBuffTypes());
        	else if (eventParam1.equals("view_buffs"))
        		msg.setHtml(buildHtml("buff"));
        	else if (eventParam1.equals("view_resists"))
        		msg.setHtml(buildHtml("resist"));
        	else if (eventParam1.equals("view_songs"))
        		msg.setHtml(buildHtml("song"));
        	else if (eventParam1.equals("view_dances"))
        		msg.setHtml(buildHtml("dance"));
        	else if (eventParam1.equals("view_chants"))
        		msg.setHtml(buildHtml("chant"));
        	else if (eventParam1.equals("view_others"))
        		msg.setHtml(buildHtml("others"));
        	else if (eventParam1.equals("view_special"))
        		msg.setHtml(buildHtml("special"));
        	else if (eventParam1.equals("view_cubic"))
        		msg.setHtml(buildHtml("cubic"));
        	else if (DEBUG)
        	{
        		throw new RuntimeException();
        	}
		}
		else if (eventParam0.equalsIgnoreCase("edit_buff_list"))
        {
			msg.setHtml(viewAllBuffs(eventParam1, eventParam2, eventParam3));
        }
		else if (eventParam0.equalsIgnoreCase("changeBuffSet"))
        {
        	if (eventParam2.equals(SET_FIGHTER)) eventParam2 = "0";
            else if (eventParam2.equals(SET_MAGE)) eventParam2 = "1";
            else if (eventParam2.equals(SET_ALL)) eventParam2 = "2";
            else if (eventParam2.equals(SET_NONE)) eventParam2 = "3";
            else if (DEBUG) throw new RuntimeException();
        	msg.setHtml(manageSelectedSet(eventParam1, eventParam2, eventParam3));
        }
		else if (eventParam0.equalsIgnoreCase("editSelectedBuff"))
        {
        	eventParam2 = eventParam2.replace("-", " ");
            String[] split = eventParam2.split(" ");
            String action = split[0];
            String page = split[1];
            manageSelectedBuff(eventParam1, action);
            String typeName = "";
            
            if (eventParam3.equalsIgnoreCase("buff"))
            	typeName = "Buffs";
        	else if (eventParam3.equalsIgnoreCase("resist"))
        		typeName = "Resists";
        	else if (eventParam3.equalsIgnoreCase("song"))
        		typeName = "Songs";
        	else if (eventParam3.equalsIgnoreCase("dance"))
        		typeName = "Dances";
        	else if (eventParam3.equalsIgnoreCase("chant"))
        		typeName = "Chants";
        	else if (eventParam3.equalsIgnoreCase("others"))
        		typeName = "Others_Buffs";
        	else if (eventParam3.equalsIgnoreCase("special"))
        		typeName = "Special_Buffs";
        	else if (eventParam3.equalsIgnoreCase("cubic"))
        		typeName = "Cubics";
        	else if (DEBUG)
        	{
        		throw new RuntimeException();
        	}
            
            msg.setHtml(viewAllBuffs(eventParam3, typeName, page));
        }
		else if (eventParam0.equalsIgnoreCase("giveBuffs"))
        {
        	int cost = 0;
            if (eventParam3.equalsIgnoreCase("buff"))
            	cost = BUFF_PRICE;
            else if (eventParam3.equalsIgnoreCase("resist"))
            	cost = RESIST_PRICE;
            else if (eventParam3.equalsIgnoreCase("song"))
            	cost = SONG_PRICE;
            else if (eventParam3.equalsIgnoreCase("dance"))
            	cost = DANCE_PRICE;
            else if (eventParam3.equalsIgnoreCase("chant"))
            	cost = CHANT_PRICE;
            else if (eventParam3.equalsIgnoreCase("others"))
            	cost = OTHERS_PRICE;
            else if (eventParam3.equalsIgnoreCase("special"))
            	cost = SPECIAL_PRICE;
            else if (eventParam3.equalsIgnoreCase("cubic"))
            	cost = CUBIC_PRICE;
            else if (DEBUG)
            {
            	throw new RuntimeException();
            }
            if ((int)(System.currentTimeMillis()/1000) > player.getblockUntilTime())
            {
                if(!FREE_BUFFS)
                {
                	if(!ItemFunctions.haveItem(player, CONSUMABLE_ID, cost))
                	{
                		msg.setHtml(showText(player, "Sorry", "You don't have the enough items:<br>You need: <font color=LEVEL>" + cost + " " + getItemNameHtml(player, CONSUMABLE_ID) + "!", false, "0", "0"));
                		return;
                	}
                }
                final boolean getpetbuff = isPetBuff();
                if (!getpetbuff)
                {
                    if (eventParam3.equals("cubic"))
                    {
                        if (player.getCubics() != null)
                        {
                        	for (Cubic cubic : player.getCubics())
                            	cubic.delete();
                        }
                        player.onMagicUseTimer(player, SkillHolder.getInstance().getSkill(Integer.parseInt(eventParam1), Integer.parseInt(eventParam2)), false);
                    }
                    else
                    {
                    	SkillHolder.getInstance().getSkill(Integer.parseInt(eventParam1), Integer.parseInt(eventParam2)).getEffects(player, player);
                    }
                }
                else
                {
                    if (eventParam3.equals("cubic"))
                    {
                        if (player.getCubics() != null)
                        {
                        	for (Cubic cubic : player.getCubics())
                            	cubic.delete();
                        }
                        player.onMagicUseTimer(player, SkillHolder.getInstance().getSkill(Integer.parseInt(eventParam1), Integer.parseInt(eventParam2)), false);
                    }
                    else
                    {
						if(player.hasServitor())
						{
							for(Servitor servitor : player.getServitors())
							{
								SkillHolder.getInstance().getSkill(Integer.parseInt(eventParam1), Integer.parseInt(eventParam2)).getEffects(servitor, servitor);
							}	
						}					
                        else
                        {
                        	msg.setHtml(showText(player, "Info", "You can't use the Pet's options.<br>Summon your pet first!", false, "Return", "main"));
                        }
                    }
                }
                ItemFunctions.deleteItem(player, CONSUMABLE_ID, cost);
                if (TIME_OUT)
                {
                	addTimeout(player, SetupGaugePacket.Colors.GREEN, TIME_OUT_TIME / 10, 600);
                }
            }
            
            msg.setHtml(buildHtml(eventParam3));
        }
		else if (eventParam0.equalsIgnoreCase("castBuffSet"))
        {
        	if ((int)(System.currentTimeMillis()/1000) > player.getblockUntilTime())
            {
                if (!FREE_BUFFS)
                {
                	if(!ItemFunctions.haveItem(player, CONSUMABLE_ID, BUFF_SET_PRICE))
                    {
                		msg.setHtml(showText(player, "Sorry", "You don't have the enough items:<br>You need: <font color=LEVEL>" + BUFF_SET_PRICE + " " + getItemNameHtml(player, CONSUMABLE_ID) + "!", false, "0", "0"));
                		return;
                    }
                }
                List<int[]> buff_sets = new ArrayList<int[]>();
                final int player_class;
                if (player.isMageClass()) player_class = 1;
                else player_class = 0;
                final boolean getpetbuff = isPetBuff();
                if (!getpetbuff)
                {
                	Connection con5 = null;
                    PreparedStatement getSimilarNameCount = null;
                    ResultSet rss = null;
                    
                    try
                    {
                    	con5 = L2DatabaseFactory.getInstance().getConnection();
                        getSimilarNameCount = con5.prepareStatement("SELECT buffId,buffLevel FROM npcbuffer_buff_list WHERE forClass IN (?,?) ORDER BY id ASC");
                        getSimilarNameCount.setInt(1, player_class);
                        getSimilarNameCount.setString(2, "2");
                        rss = getSimilarNameCount.executeQuery();
                        while (rss.next())
                        {
                            int id = rss.getInt("buffId");
                            int lvl = rss.getInt("buffLevel");
                            buff_sets.add(new int[]{id, lvl});
                        }
                    }
                    catch (SQLException e)
                    {
                    	print(e);
                    }
                    finally
            		{
            			DbUtils.closeQuietly(con5, getSimilarNameCount, rss);
            		}
                    for (int[] i : buff_sets)
                        SkillHolder.getInstance().getSkill(i[0], i[1]).getEffects(player, player);
                }
                else
                {
					if(player.hasServitor())
					{
						for(Servitor servitor : player.getServitors())
						{
							Connection con6 = null;
							PreparedStatement getSimilarNameCount = null;
							ResultSet rss = null;
                        
							try
							{
								con6 = L2DatabaseFactory.getInstance().getConnection();
								getSimilarNameCount = con6.prepareStatement("SELECT buffId,buffLevel FROM npcbuffer_buff_list WHERE forClass IN (?,?) ORDER BY id ASC");
								getSimilarNameCount.setString(1, "0");
								getSimilarNameCount.setString(2, "2");
								rss = getSimilarNameCount.executeQuery();
								while (rss.next())
								{
									int id = rss.getInt("buffId");
									int lvl = rss.getInt("buffLevel");
									buff_sets.add(new int[]{id, lvl});
								}
							}
							catch (SQLException e)
							{
								print(e);
							}
							finally
							{
								DbUtils.closeQuietly(con6, getSimilarNameCount, rss);
							}
							for (int[] i : buff_sets)
								SkillHolder.getInstance().getSkill(i[0], i[1]).getEffects(servitor, servitor);
						}	
					}					
                    else
                    {
                    	msg.setHtml(showText(player, "Info", "You can't use the Pet's options.<br>Summon your pet first!", false, "Return", "main"));
                    }
                }
                ItemFunctions.deleteItem(player, CONSUMABLE_ID, BUFF_SET_PRICE);
                if (TIME_OUT)
                {
                	addTimeout(player, SetupGaugePacket.Colors.GREEN, TIME_OUT_TIME, 600);
                }
            }
        	
        	msg.setHtml(main(player));
        }
		else if (eventParam0.equalsIgnoreCase("heal"))
        {
        	if ((int)(System.currentTimeMillis()/1000) > player.getblockUntilTime()) {
        		if(!ItemFunctions.haveItem(player, CONSUMABLE_ID, HEAL_PRICE))
        		{
        			msg.setHtml(showText(player, "Sorry", "You don't have the enough items:<br>You need: <font color=LEVEL>" + HEAL_PRICE + " " + getItemNameHtml(player, CONSUMABLE_ID) + "!", false, "0", "0"));
            		return;
        		}
				final boolean getpetbuff = isPetBuff();
				if (getpetbuff)
				{
					if(player.hasServitor())
					{
						for(Servitor servitor : player.getServitors())
						{
							heal(player, getpetbuff);
						}
					}				
				    else
				    {
				    	msg.setHtml(showText(player, "Info", "You can't use the Pet's options.<br>Summon your pet first!", false, "Return", "main"));
				    }
				}
				else
				{
					heal(player, getpetbuff);
				}
				ItemFunctions.deleteItem(player, CONSUMABLE_ID, HEAL_PRICE);
				if (TIME_OUT)
				{
					addTimeout(player, SetupGaugePacket.Colors.BLUE, TIME_OUT_TIME / 2, 600);
				}
            }
        	
        	msg.setHtml(main(player));
        }
		else if (eventParam0.equalsIgnoreCase("removeBuffs"))
        {
        	if ((int)(System.currentTimeMillis()/1000) > player.getblockUntilTime())
            {
        		if(!ItemFunctions.haveItem(player, CONSUMABLE_ID, BUFF_REMOVE_PRICE))
                {
                	msg.setHtml(showText(player, "Sorry", "You don't have the enough items:<br>You need: <font color=LEVEL>" + BUFF_REMOVE_PRICE + " " + getItemNameHtml(player, CONSUMABLE_ID) + "!", false, "0", "0"));
                	return;
                }
				final boolean getpetbuff = isPetBuff();
				if (getpetbuff)
				{
					if(player.hasServitor())
					{
						for(Servitor servitor : player.getServitors())
						{
							servitor.getAbnormalList().stopAll();
						}
					}					
				    else
				    {
				    	msg.setHtml(showText(player, "Info", "You can't use the Pet's options.<br>Summon your pet first!", false, "Return", "main"));
				    }
				}
				else
				{
				    player.getAbnormalList().stopAll();
					player.deleteCubics();
				}
				ItemFunctions.deleteItem(player, CONSUMABLE_ID, BUFF_REMOVE_PRICE);
				if (TIME_OUT)
				{
					addTimeout(player, SetupGaugePacket.Colors.BLUE, TIME_OUT_TIME / 2, 600);
				}
            }
        	
        	msg.setHtml(main(player));
        }
		else if (eventParam0.equalsIgnoreCase("cast"))
        {
			if ((int)(System.currentTimeMillis()/1000) > player.getblockUntilTime())
			{
			    IntList buffs = new ArrayIntList();
			    IntList levels = new ArrayIntList();
			    Connection con = null;
			    PreparedStatement rss = null;
			    ResultSet action = null;
			    
			    try
			    {
			    	con = L2DatabaseFactory.getInstance().getConnection();
			        rss = con.prepareStatement("SELECT * FROM npcbuffer_scheme_contents WHERE scheme_id=? ORDER BY id");
			        rss.setString(1, eventParam1);
			        action = rss.executeQuery();
			        while (action.next())
			        {
			          //enabled = 1;
			            int id = Integer.parseInt(action.getString("skill_id"));
			            int level = Integer.parseInt(action.getString("skill_level"));
			            switch (getBuffType(id))
			            {
			            case 0:
			                if (ENABLE_BUFFS)
			                    if (isEnabled(id, level))
			                    {
			                        buffs.add(id);
			                        levels.add(level);
			                    }
			                break;
			            case 1:
			                if (ENABLE_RESIST)
			                    if (isEnabled(id, level))
			                    {
			                        buffs.add(id);
			                        levels.add(level);
			                    }
			                break;
			            case 2:
			                if (ENABLE_SONGS)
			                    if (isEnabled(id, level))
			                    {
			                        buffs.add(id);
			                        levels.add(level);
			                    }
			                break;
			            case 3:
			                if (ENABLE_DANCES)
			                    if (isEnabled(id, level))
			                    {
			                        buffs.add(id);
			                        levels.add(level);
			                    }
			                break;
			            case 4:
			                if (ENABLE_CHANTS)
			                    if (isEnabled(id, level))
			                    {
			                        buffs.add(id);
			                        levels.add(level);
			                    }
			                break;
			            case 5:
			                if (ENABLE_OTHERS)
			                    if (isEnabled(id, level))
			                    {
			                        buffs.add(id);
			                        levels.add(level);
			                    }
			                break;
			            case 6:
			                if (ENABLE_SPECIAL)
			                    if (isEnabled(id, level))
			                    {
			                        buffs.add(id);
			                        levels.add(level);
			                    }
			                break;
			            default:
			                if (DEBUG)
			                {
			                	throw new RuntimeException();
			                }
			            } //switch getBuffType(id)
			        } //while action.next()
			    } //con
			    catch (SQLException e)
			    {
			    	print(e);
			    }
			    if (!FREE_BUFFS)
				{
			    	if(!ItemFunctions.haveItem(player, CONSUMABLE_ID, SCHEME_BUFF_PRICE))
			    	{
			    		msg.setHtml(showText(player, "Sorry", "You don't have the enough items:<br>You need: <font color=LEVEL>" + SCHEME_BUFF_PRICE + " " + getItemNameHtml(player, CONSUMABLE_ID) + "!", false, "0", "0"));
			    		return;
			    	}
				}
							
			final boolean getpetbuff = isPetBuff();
			for (int i = 0; i < buffs.size(); ++i)
			{
				if (buffs.size() == 0)
			    {
			    	msg.setHtml(viewAllSchemeBuffs(eventParam1, "1", "add"));
			    }
				else if (!getpetbuff)
			    {
			    	SkillHolder.getInstance().getSkill(buffs.get(i), levels.get(i)).getEffects(player, player);
			    }
			    else
			    {
					if(player.hasServitor())
					{
						for(Servitor servitor : player.getServitors())
						{
							SkillHolder.getInstance().getSkill(buffs.get(i), levels.get(i)).getEffects(servitor, servitor);
						}
					}				
			        else
			        	msg.setHtml(showText(player, "Info", "You can't use the Pet's options.<br>Summon your pet first!", false, "Return", "main"));
			    }
			}
			ItemFunctions.deleteItem(player, CONSUMABLE_ID, SCHEME_BUFF_PRICE);
			if (TIME_OUT) addTimeout(player, SetupGaugePacket.Colors.GREEN, TIME_OUT_TIME, 600);
			}
			
			msg.setHtml(main(player));
        }
		else if (eventParam0.equalsIgnoreCase("manage_scheme_1"))
        {
			msg.setHtml(viewAllSchemeBuffs(eventParam1, eventParam2, "add"));
        }
		else if (eventParam0.equalsIgnoreCase("manage_scheme_2"))
        {
			msg.setHtml(viewAllSchemeBuffs(eventParam1, eventParam2, "remove"));
        }
		else if (eventParam0.equalsIgnoreCase("remove_buff"))
        {
        	String[] split = eventParam1.split("_");
            String scheme = split[0];
            String skill = split[1];
            String level = split[2];
            Connection con2 = null;
            PreparedStatement rem2 = null;
            
            try
            {
            	con2 = L2DatabaseFactory.getInstance().getConnection();
            	rem2 = con2.prepareStatement("DELETE FROM npcbuffer_scheme_contents WHERE scheme_id=? AND skill_id=? AND skill_level=? LIMIT 1");
            	rem2.setString(1, scheme);
            	rem2.setString(2, skill);
            	rem2.setString(3, level);
            	rem2.executeUpdate();
            }
            catch (SQLException e)
            {
            	print(e);
            }
            finally
    		{
    			DbUtils.closeQuietly(con2, rem2);
    		}
            int temp = Integer.parseInt(eventParam3) - 1;
            final String HTML;
            if (temp <= 0) HTML = getOptionList(scheme);
            else HTML = viewAllSchemeBuffs(scheme, eventParam2, "remove");
            msg.setHtml(HTML);
        }
        else if (eventParam0.equalsIgnoreCase("add_buff"))
        {
        	String[] split = eventParam1.split("_");
            String scheme = split[0];
            String skill = split[1];
            String level = split[2];
            int idbuffclass = getClassBuff(skill);
            Connection con3 = null;
            PreparedStatement ins = null;
            
            try
            {
            	con3 = L2DatabaseFactory.getInstance().getConnection();
                ins = con3.prepareStatement("INSERT INTO npcbuffer_scheme_contents (scheme_id,skill_id,skill_level,buff_class) VALUES (?,?,?,?)");
                ins.setString(1, scheme);
                ins.setString(2, skill);
                ins.setString(3, level);
                ins.setInt(4, idbuffclass);
                ins.executeUpdate();
                ins.close();
            }
            catch (SQLException e)
            {
            	print(e);
            }
            finally
    		{
    			DbUtils.closeQuietly(con3, ins);
    		}
            int temp = Integer.parseInt(eventParam3) + 1;
            final String HTML;
            if (temp >= MAX_SCHEME_BUFFS + MAX_SCHEME_DANCES) HTML = getOptionList(scheme);
            else HTML = viewAllSchemeBuffs(scheme, eventParam2, "add");
            msg.setHtml(HTML);
        }
        else if (eventParam0.equalsIgnoreCase("create"))
        {
        	String param = eventParam1.replaceAll("[ !"+"\""+"#$%&'()*+,/:;<=>?@"+"\\["+"\\\\"+"\\]"+"\\^"+"`{|}~]", "");	//JOJO
            if (param.length() == 0 || param.equals("no_name"))
            {
                player.sendPacket(new SystemMessage(SystemMsg.INCORRECT_NAME));
                msg.setHtml(showText(player, "Info", "Please, enter the scheme name!", true, "Return", "main"));
            }
            Connection con = null;
            PreparedStatement ins = null;
            
            try
            {
            	con = L2DatabaseFactory.getInstance().getConnection();
			    ins = con.prepareStatement("INSERT INTO npcbuffer_scheme_list (player_id,scheme_name) VALUES (?,?)");
			    ins.setInt(1, player.getObjectId());
			    ins.setString(2, param);
			    ins.executeUpdate();
			    ins.close();
			}
			catch (SQLException e)
			{
				print(e);
			}
            finally
    		{
    			DbUtils.closeQuietly(con, ins);
    		}
            
            msg.setHtml(main(player));
        }
        else if (eventParam0.equalsIgnoreCase("delete"))
        {
        	Connection con = null;
            PreparedStatement rem = null;
            
            try
            {
            	con = L2DatabaseFactory.getInstance().getConnection();
                rem = con.prepareStatement("DELETE FROM npcbuffer_scheme_list WHERE id=? LIMIT 1");
                rem.setString(1, eventParam1);
                rem.executeUpdate();
                rem.close();
                rem = con.prepareStatement("DELETE FROM npcbuffer_scheme_contents WHERE scheme_id=?");
                rem.setString(1, eventParam1);
                rem.executeUpdate();
                rem.close();
            }
            catch (SQLException e)
            {
            	print(e);
            }
            finally
    		{
    			DbUtils.closeQuietly(con, rem);
    		}
            
            msg.setHtml(main(player));
        }
        else if (eventParam0.equalsIgnoreCase("delete_c"))
        {
        	msg.setHtml("<html><head>" + TITLE_NAME + "</head><body><center><img src=\"L2UI_CH3.herotower_deco\" width=256 height=32><br>Do you really want to delete '" + eventParam2 + "' scheme?<br><br>"
                    +  "<button value=\"Yes\" action=\"bypass -h npc_%objectId%_delete " + eventParam1 + " x x\" width=50 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">"
                    +  "<button value=\"No\" action=\"bypass -h npc_%objectId%_delete_1 x x x\" width=50 height=25 back=\"L2UI_ct1.button_df\" fore=\"L2UI_ct1.button_df\">"
                    +  "<br><font color=303030>" + TITLE_NAME + "</font></center></body></html>");
        }
        else if (eventParam0.equalsIgnoreCase("create_1"))
        {
        	msg.setHtml(createScheme());
        }
        else if (eventParam0.equalsIgnoreCase("edit_1"))
        {
        	msg.setHtml(editScheme(player));
        }
        else if (eventParam0.equalsIgnoreCase("delete_1"))
        {
        	msg.setHtml(deleteScheme(player));
        }
        else if (eventParam0.equalsIgnoreCase("manage_scheme_select"))
        {
        	msg.setHtml(getOptionList(eventParam1));
        }
		
		//msg.replace("%objectId%", String.valueOf(getObjectId()));
		player.sendPacket(msg);
	}
}
