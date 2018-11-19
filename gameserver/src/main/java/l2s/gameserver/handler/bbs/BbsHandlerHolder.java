package l2s.gameserver.handler.bbs;

import java.util.HashMap;
import java.util.Map;

import l2s.commons.data.xml.AbstractHolder;
import l2s.gameserver.Config;
import l2s.gameserver.templates.StatsSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BbsHandlerHolder extends AbstractHolder
{
    private static final Logger _log = LoggerFactory.getLogger(BbsHandlerHolder.class);
    private static final BbsHandlerHolder _instance = new BbsHandlerHolder();

    private final Map<String, IBbsHandler> _handlers = new HashMap<String, IBbsHandler>();
    private final StatsSet _properties = new StatsSet();

    public static BbsHandlerHolder getInstance()
    {
        return _instance;
    }

    private BbsHandlerHolder()
    {
        //
    }

    public void registerHandler(IBbsHandler commHandler)
    {
        for(String bypass : commHandler.getBypassCommands())
        {
            if(_handlers.containsKey(bypass))
                _log.warn("CommunityBoard: dublicate bypass registered! First handler: " + _handlers.get(bypass).getClass().getSimpleName() + " second: " + commHandler.getClass().getSimpleName());

            _handlers.put(bypass, commHandler);
        }
    }

    public void removeHandler(IBbsHandler handler)
    {
        for(String bypass : handler.getBypassCommands())
            _handlers.remove(bypass);
        _log.info("CommunityBoard: " + handler.getClass().getSimpleName() + " unloaded.");
    }

    public IBbsHandler getCommunityHandler(String bypass)
    {
        if(!Config.BBS_ENABLED || _handlers.isEmpty())
            return null;

        for(Map.Entry<String, IBbsHandler> entry : _handlers.entrySet())
            if(bypass.toLowerCase().startsWith(entry.getKey().toLowerCase()))
                return entry.getValue();

        return null;
    }

    public void setProperty(String name, String val)
    {
        _properties.set(name, val);
    }

    public void setProperty(String name, int val)
    {
        _properties.set(name, val);
    }

    public int getIntProperty(String name)
    {
        return _properties.getInteger(name, 0);
    }

    @Override
    public int size()
    {
        return _handlers.size();
    }

    @Override
    public void clear()
    {
        _handlers.clear();
    }
}