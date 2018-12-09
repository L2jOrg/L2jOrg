package org.l2j.gameserver.network.l2.components;

import java.util.ArrayList;
import java.util.List;

import org.l2j.gameserver.data.string.StringsHolder;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.network.l2.s2c.L2GameServerPacket;
import org.l2j.gameserver.network.l2.s2c.SystemMessage;
import org.l2j.gameserver.utils.Language;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.l2j.commons.util.Util.*;

/**
 * Даный класс является обработчиком серверных интернациональных сообщений.
 */
public class CustomMessage implements IBroadcastPacket
{
	private static final Logger _log = LoggerFactory.getLogger(CustomMessage.class);

	private static final String[] PARAMS = {"{0}", "{1}", "{2}", "{3}", "{4}", "{5}", "{6}", "{7}", "{8}", "{9}"};

	private String _address;
	private List<Object> _args;

	public CustomMessage(String address)
	{
		_address = address;
	}

	public CustomMessage addString(String arg)
	{
		if(_args == null)
			_args = new ArrayList<Object>();
		_args.add(arg);
		return this;
	}

	public CustomMessage addNumber(int i)
	{
		return addString(String.valueOf(i));
	}

	public CustomMessage addNumber(long l)
	{
		return addString(String.valueOf(l));
	}

    public CustomMessage addCustomMessage(CustomMessage msg)
    {
        if(_args == null)
            _args = new ArrayList<Object>();
        _args.add(msg);
        return this;
    }
	
	public String toString(Player player)
	{
		return toString(player.getLanguage());
	}
	
	public String toString(Language lang)
	{
		StringBuilder msg = null;

		String text = StringsHolder.getInstance().getString(_address, lang);
		if(text != null)
		{
			msg = new StringBuilder(text);

			if(_args != null)
			{
				for(int i = 0; i < _args.size(); i++)
                {
                    Object arg = _args.get(i);
                    if(arg instanceof CustomMessage)
                    	replaceFirst(msg, PARAMS[i], ((CustomMessage) arg).toString(lang));
                    else
                        replaceFirst(msg, PARAMS[i], String.valueOf(arg));
                }
			}
		}

		if(isNullOrEmpty(msg)) {
			_log.warn("CustomMessage: string: {} not found for lang: {}", _address, lang);
			return STRING_EMPTY;
		}

		return msg.toString();
	}

	@Override
	public L2GameServerPacket packet(Player player)
	{
		return new SystemMessage(SystemMsg.S1).addString(toString(player));
	}
}