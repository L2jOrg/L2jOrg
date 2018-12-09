package org.l2j.gameserver.network.l2.components;

import org.l2j.commons.util.Util;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.cache.ImagesCache;
import org.l2j.gameserver.data.htm.HtmCache;
import org.l2j.gameserver.model.GameObjectsStorage;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.instances.NpcInstance;
import org.l2j.gameserver.network.l2.s2c.*;
import org.l2j.gameserver.utils.BypassStorage.BypassType;
import org.l2j.gameserver.utils.HtmlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;

import static org.l2j.commons.util.Util.*;

/**
 * Класс обработки HTML диалогов перед отправкой клиенту.
 *
 * @author G1ta0
 */
public class HtmlMessage implements IBroadcastPacket {

    private static final Logger _log = LoggerFactory.getLogger(HtmlMessage.class);
    private static final int MAX_HTML_SIZE = 4599;
    private static final String HTML_IS_TOO_LONG = "<html><body>Text is too long!</body></html>";

    private String _filename;
	private String _html;
	private Map<String, String> _variables;
	private Map<String, String> _replaces;
	private NpcInstance _npc;
	private int _npcObjId;
    private int _itemId;
	private int _questId;
    private boolean _playVoice;

	public HtmlMessage(NpcInstance npc, String filename) {
		_npc = npc;
		_npcObjId = npc.getObjectId();
		_filename = filename;
	}

	public HtmlMessage(NpcInstance npc)
	{
		this(npc, null);
	}

	public HtmlMessage(int npcObjId) {
        _npc = GameObjectsStorage.getNpc(npcObjId);
		_npcObjId = npcObjId;
	}

	public HtmlMessage setHtml(String text) {
		_html = text;
		return this;
	}

	public final HtmlMessage setFile(String file) {
		_filename = file;
		return this;
	}

    public final HtmlMessage setItemId(int itemId) {
        _itemId = itemId;
        return this;
    }

    public final HtmlMessage setQuestId(int questId) {
		_questId = questId;
		return this;
	}

    public final HtmlMessage setPlayVoice(boolean playVoice) {
        _playVoice = playVoice;
        return this;
    }

	public HtmlMessage addVar(String name, Object value) {
		if(name == null)
			throw new IllegalArgumentException("Name can't be null!");
		if(value == null)
			throw new IllegalArgumentException("Value can't be null!");
		if(name.startsWith("${"))
			throw new IllegalArgumentException("Incorrect name: " + name);
		if(_variables == null)
			_variables = new HashMap<>(2);
		_variables.put(name, String.valueOf(value));
		return this;
	}

	public HtmlMessage replace(String name, String value) {
		if(name == null)
			throw new IllegalArgumentException("Name can't be null!");
		if(value == null)
			throw new IllegalArgumentException("Value can't be null!");
		if(!(name.startsWith("%") && name.endsWith("%") || name.startsWith("<?") && name.endsWith("?>")))
			throw new IllegalArgumentException("Incorrect name: " + name);
		if(_replaces == null)
			_replaces = new LinkedHashMap<>(2);
		_replaces.put(name, value);
		return this;
	}

	public HtmlMessage replace(String name, NpcString npcString) {
		return replace(name, HtmlUtils.htmlNpcString(npcString, OBJECT_ARRAY_EMPTY));
	}

	public HtmlMessage replace(String name, NpcString npcString, Object... arg) {
		if(npcString == null)
			throw new IllegalArgumentException("NpcString can't be null!");
		return replace(name, HtmlUtils.htmlNpcString(npcString, arg));
	}

	@Override
	public L2GameServerPacket packet(Player player) {
		CharSequence content = null;

		if(!isNullOrEmpty(_html)) {
            content = make(player, _html);
        } else if(!isNullOrEmpty(_filename)) {
			if(player.isGM())
				player.sendMessage("HTML: " + _filename);

			String htmCache = HtmCache.getInstance().getHtml(_filename, player);
			content = make(player, htmCache);
		} else {
            _log.warn("HtmlMessage: empty dialog" + (_npc == null ? "!" : " npc id : " + _npc.getNpcId() + "!"), new Exception());
        }

        if(_itemId == 0)
        {
            if(_npc != null) {
                player.setLastNpc(_npc);
            }
            player.getBypassStorage().parseHtml(content, BypassType.DEFAULT);
        } else {
            player.getBypassStorage().parseHtml(content, BypassType.ITEM);
        }

		if(isNullOrEmpty(content))
			return ActionFailPacket.STATIC;
		else if(_questId == 0)
            return new NpcHtmlMessagePacket(_npcObjId, _itemId, _playVoice, content);
		else
			return new ExNpcQuestHtmlMessage(_npcObjId, content, _questId);
	}

	private CharSequence make(Player player, String content) {
		if(content == null)
			return STRING_EMPTY;

        StringBuilder sb = new StringBuilder(content);

		if(_replaces != null)
		{
			for(Map.Entry<String, String> e : _replaces.entrySet())
				Util.replaceAll(sb, e.getKey(), e.getValue());
		}

        Matcher m = ImagesCache.HTML_PATTERN.matcher(content);
        while(m.find())
        {
            String imageName = m.group(1);
            int imageId = ImagesCache.getInstance().getImageId(imageName);

            Util.replaceAll(sb, "%image:" + imageName + "%", "Crest.pledge_crest_" + Config.REQUEST_ID + "_" + imageId);

            byte[] image = ImagesCache.getInstance().getImage(imageId);
            if(image != null)
                player.sendPacket(new PledgeCrestPacket(imageId, image));
        }

        Util.replaceAll(sb,"%playername%", player.getName());
		if(_npcObjId != 0)
		{
			Util.replaceAll(sb,"%objectId%", String.valueOf(_npcObjId));
			if(_npc != null)
				Util.replaceAll(sb,"%npcId%", String.valueOf(_npc.getNpcId()));
		}

		content = HtmlUtils.evaluate(sb.toString(), _variables);

		if(content.length() > MAX_HTML_SIZE) {
		    content = HTML_IS_TOO_LONG;
        }

		return content;
	}
}
