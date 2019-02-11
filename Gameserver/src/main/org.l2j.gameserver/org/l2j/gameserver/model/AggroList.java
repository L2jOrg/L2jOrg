package org.l2j.gameserver.model;

import io.github.joealisson.primitive.maps.IntObjectMap;
import io.github.joealisson.primitive.maps.impl.CHashIntObjectMap;
import org.l2j.commons.collections.CollectionUtils;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.ThreadPoolManager;
import org.l2j.gameserver.ai.CtrlEvent;
import org.l2j.gameserver.model.instances.NpcInstance;
import io.github.joealisson.primitive.pair.IntObjectPair;
import io.github.joealisson.primitive.maps.impl.HashIntObjectMap;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AggroList
{
    private abstract static class DamageHate
    {
        public int hate;
        public int damage;
    }

    public static class HateInfo extends DamageHate
    {
        public final Creature attacker;

        HateInfo(Creature attacker, AggroInfo ai)
        {
            this.attacker = attacker;
            this.hate = ai.hate;
            this.damage = ai.damage;
        }
    }

    public static class AggroInfo extends DamageHate
    {
        public final int attackerId;

        AggroInfo(Creature attacker)
        {
            this.attackerId = attacker.getObjectId();
        }
    }

    public static class PartyDamage extends DamageHate
    {
        public final Party party;

        PartyDamage(Party party)
        {
            this.party = party;
        }
    }

    public static class DamageComparator implements Comparator<DamageHate>
    {
        private static Comparator<DamageHate> instance = new DamageComparator();

        public static Comparator<DamageHate> getInstance()
        {
            return instance;
        }

        @Override
        public int compare(DamageHate o1, DamageHate o2)
        {
            return Integer.compare(o2.damage, o1.damage);
        }
    }

    public static class HateComparator implements Comparator<DamageHate>
    {
        private static Comparator<DamageHate> instance = new HateComparator();

        public static Comparator<DamageHate> getInstance()
        {
            return instance;
        }

        @Override
        public int compare(DamageHate o1, DamageHate o2)
        {
            if(o2.hate == o1.hate)
                return Integer.compare(o2.damage, o1.damage);
            return Integer.compare(o2.hate, o1.hate);
        }
    }

    private final NpcInstance _npc;
    private final IntObjectMap<AggroInfo> _hateList = new CHashIntObjectMap<>();
    private final Map<Party, PartyDamage> _partyDamageMap = new HashMap<Party, PartyDamage>();
    /** Блокировка для чтения/записи объектов списка */
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock readLock = lock.readLock();
    private final Lock writeLock = lock.writeLock();

    private AggroInfo _mostHated = null;

    public AggroList(NpcInstance npc)
    {
        _npc = npc;
    }

    public void addDamageHate(Creature attacker, int damage, int aggro)
    {
        damage = Math.max(damage, 0);

        if(damage == 0 && aggro == 0)
            return;

        if(attacker.isConfused())
            return;

        writeLock.lock();
        try
        {
            AggroInfo ai;

            if((ai = _hateList.get(attacker.getObjectId())) == null)
                _hateList.put(attacker.getObjectId(), ai = new AggroInfo(attacker));

            if(attacker.getPlayer() != null)
            {
                Party party = attacker.getPlayer().getParty();
                if(party != null)
                {
                    PartyDamage pd = _partyDamageMap.get(party);
                    if(pd == null)
                    {
                        pd = new PartyDamage(party);
                        _partyDamageMap.put(party, pd);
                    }
                    pd.damage += damage;
                }
            }

            ai.damage += damage;
            ai.hate += aggro;
            ai.damage = Math.max(ai.damage, 0);
            ai.hate = Math.max(ai.hate, 0);

            if(aggro > 0 && (_mostHated == null || (_mostHated != ai && ai.hate > _mostHated.hate)))
            {
                _mostHated = ai;
                ThreadPoolManager.getInstance().execute(new GameObjectTasks.NotifyAITask(_npc, CtrlEvent.EVT_MOST_HATED_CHANGED));
            }
        }
        finally
        {
            writeLock.unlock();
        }
    }

    public void reduceHate(Creature target, int hate)
    {
        writeLock.lock();
        try
        {
            AggroInfo ai = _hateList.get(target.getObjectId());
            if(ai != null)
            {
                ai.hate -= hate;
                ai.hate = Math.max(ai.hate, 0);

                if(ai == _mostHated)
                {
                    Creature mostHated = getMostHated(-1);
                    if(mostHated != null)
                    {
                        AggroInfo mostHatedInfo = get(mostHated);
                        if(mostHatedInfo != null && mostHatedInfo != ai && mostHatedInfo.hate > ai.hate)
                        {
                            _mostHated = mostHatedInfo;
                            ThreadPoolManager.getInstance().execute(new GameObjectTasks.NotifyAITask(_npc, CtrlEvent.EVT_MOST_HATED_CHANGED));
                            return;
                        }
                    }

                    if(ai.hate <= 0)
                        ThreadPoolManager.getInstance().execute(new GameObjectTasks.NotifyAITask(_npc, CtrlEvent.EVT_MOST_HATED_CHANGED));
                }
            }
        }
        finally
        {
            writeLock.unlock();
        }
    }

    public int getHate(Creature target)
    {
        int hate = 0;

        writeLock.lock();
        try
        {
            AggroInfo ai = _hateList.get(target.getObjectId());
            if(ai != null)
                hate = ai.hate;
        }
        finally
        {
            writeLock.unlock();
        }
        return hate;
    }

    public AggroInfo get(Creature attacker)
    {
        readLock.lock();
        try
        {
            return _hateList.get(attacker.getObjectId());
        }
        finally
        {
            readLock.unlock();
        }
    }

    private void remove(int objectId, boolean onlyHate)
    {
        writeLock.lock();
        try
        {
            if(!onlyHate)
            {
                _hateList.remove(objectId);
                return;
            }

            AggroInfo ai = _hateList.get(objectId);
            if(ai != null)
            {
                if(ai.damage == 0)
                    _hateList.remove(objectId);
                else
                    ai.hate = 0;
            }
        }
        finally
        {
            writeLock.unlock();
        }
    }

    public void remove(Creature attacker, boolean onlyHate)
    {
        remove(attacker.getObjectId(), onlyHate);
    }

    public void clear()
    {
        clear(false);
    }

    public void clear(boolean onlyHate)
    {
        writeLock.lock();
        try
        {
            if(_hateList.isEmpty())
                return;

            if(!onlyHate)
            {
                _hateList.clear();
                return;
            }

            AggroInfo ai;

            for (IntObjectPair<AggroInfo> pair : _hateList.entrySet()) {
                ai = pair.getValue();
                ai.hate = 0;
                if(ai.damage == 0)
                    _hateList.remove(pair.getKey());
            }
        }
        finally
        {
            writeLock.unlock();
        }
    }

    public boolean isEmpty()
    {
        readLock.lock();
        try
        {
            return _hateList.isEmpty();
        }
        finally
        {
            readLock.unlock();
        }
    }

    private Creature getOrRemoveHated(int objectId)
    {
        GameObject object = GameObjectsStorage.findObject(objectId);
        if(object == null || !object.isCreature())
        {
            remove(objectId, true);
            return null;
        }

        Creature cha = (Creature) object;
        if(cha.isPlayable() && ((Playable) cha).isInNonAggroTime())
        {
            remove(objectId, true);
            return null;
        }

        if(cha.isPlayer() && !((Player) cha).isOnline())
        {
            remove(objectId, true);
            return null;
        }
        return cha;
    }

    public List<Creature> getHateList(int radius)
    {
        List<AggroInfo> hated = new ArrayList<AggroInfo>();

        readLock.lock();
        try
        {
            hated.addAll(_hateList.values());
        }
        finally
        {
            readLock.unlock();
        }

        if(hated.isEmpty())
            return Collections.emptyList();

        Collections.sort(hated, HateComparator.getInstance());

        if(hated.get(0).hate == 0)
            return Collections.emptyList();

        List<Creature> tempList = new ArrayList<>();
        for (AggroInfo ai : hated)
        {
            if(ai.hate == 0)
                continue;

            Creature cha = getOrRemoveHated(ai.attackerId);
            if(cha == null)
                continue;

            if(radius == -1 || cha.isInRangeZ(_npc.getLoc(), radius))
            {
                tempList.add(cha);
                break;
            }
        }

        return tempList;
    }

    public Creature getMostHated(int radius)
    {
        List<AggroInfo> hated = new ArrayList<AggroInfo>();

        readLock.lock();
        try
        {
            hated.addAll(_hateList.values());
        }
        finally
        {
            readLock.unlock();
        }

        if(hated.isEmpty())
            return null;

        Collections.sort(hated, HateComparator.getInstance());
        if(hated.get(0).hate == 0)
            return null;

        for(AggroInfo ai : hated)
        {
            if(ai.hate == 0)
                continue;

            Creature cha = getOrRemoveHated(ai.attackerId);
            if(cha == null)
                continue;

            if(radius == -1 || cha.isInRangeZ(_npc.getLoc(), radius))
            {
                if(cha.isDead())
                    continue;
                return cha;
            }
        }

        return null;
    }

    public Creature getRandomHated(int radius)
    {
        List<AggroInfo> hated = new ArrayList<AggroInfo>();

        readLock.lock();
        try
        {
            hated.addAll(_hateList.values());
        }
        finally
        {
            readLock.unlock();
        }

        if(hated.isEmpty())
            return null;

        Collections.sort(hated, HateComparator.getInstance());
        if(hated.get(0).hate == 0)
            return null;

        List<Creature> randomHated = CollectionUtils.pooledList();

        Creature mostHated;
        for(AggroInfo ai : hated)
        {
            if(ai.hate == 0)
                continue;

            Creature cha = getOrRemoveHated(ai.attackerId);
            if(cha == null)
                continue;

            if(radius == -1 || cha.isInRangeZ(_npc.getLoc(), radius))
            {
                if(cha.isDead())
                    continue;
                randomHated.add(cha);
                break;
            }
        }

        if(randomHated.isEmpty())
            mostHated = null;
        else
            mostHated = randomHated.get(Rnd.get(randomHated.size()));

        CollectionUtils.recycle(randomHated);

        return mostHated;
    }

    public Creature getTopDamager(Creature defaultDamager)
    {
        List<AggroInfo> hated = new ArrayList<AggroInfo>();

        readLock.lock();
        try
        {
            hated.addAll(_hateList.values());
        }
        finally
        {
            readLock.unlock();
        }

        if(hated.isEmpty())
            return defaultDamager;

        Collections.sort(hated, DamageComparator.getInstance());
        if(hated.get(0).damage == 0)
            return defaultDamager;

        Creature topDamager = defaultDamager;
        int topDamage = 0;

        List<Creature> chars = World.getAroundCharacters(_npc);
        loop: for(AggroInfo ai : hated)
        {
            for(Creature cha : chars)
            {
                if(cha.getObjectId() == ai.attackerId)
                {
                    topDamager = cha;
                    topDamage = ai.damage;
                    break loop;
                }
            }
        }

        PartyDamage[] partyDmg;
        readLock.lock();
        try
        {
            if(_partyDamageMap.isEmpty())
                return topDamager;

            partyDmg = _partyDamageMap.values().toArray(new PartyDamage[_partyDamageMap.size()]);
        }
        finally
        {
            readLock.unlock();
        }

        Arrays.sort(partyDmg, DamageComparator.getInstance());
        for(PartyDamage pd : partyDmg)
        {
            if(pd.damage > topDamage)
            {
                for(AggroInfo ai : hated)
                {
                    for(Player player : pd.party.getPartyMembers())
                    {
                        if(player.getObjectId() == ai.attackerId && chars.contains(player))
                            return player;
                    }
                }
            }
        }
        return topDamager;
    }

    public Map<Creature, HateInfo> getCharMap()
    {
        if(isEmpty())
            return Collections.emptyMap();

        Map<Creature, HateInfo> aggroMap = new HashMap<Creature, HateInfo>();
        List<Creature> chars = World.getAroundCharacters(_npc);
        readLock.lock();
        try
        {
            for (AggroInfo ai : _hateList.values()) {
                if(ai.damage == 0 && ai.hate == 0)
                    continue;
                for(Creature attacker : chars)
                {
                    if(attacker.getObjectId() == ai.attackerId)
                    {
                        aggroMap.put(attacker, new HateInfo(attacker, ai));
                        break;
                    }
                }
            }
        }
        finally
        {
            readLock.unlock();
        }

        return aggroMap;
    }

    public Map<Playable, HateInfo> getPlayableMap()
    {
        if(isEmpty())
            return Collections.emptyMap();

        Map<Playable, HateInfo> aggroMap = new HashMap<Playable, HateInfo>();
        List<Playable> chars = World.getAroundPlayables(_npc);
        readLock.lock();
        try
        {
            for (AggroInfo aggroInfo : _hateList.values()) {
                if(aggroInfo.damage == 0 && aggroInfo.hate == 0)
                    continue;
                for(Playable attacker : chars)
                {
                    if(attacker.getObjectId() == aggroInfo.attackerId)
                    {
                        aggroMap.put(attacker, new HateInfo(attacker, aggroInfo));
                        break;
                    }
                }
            }
        }
        finally
        {
            readLock.unlock();
        }

        return aggroMap;
    }

    public Collection<AggroInfo> getAggroInfos()
    {
        Collection<AggroInfo> infos;
        readLock.lock();
        try
        {
            infos = _hateList.values();
        }
        finally
        {
            readLock.unlock();
        }
        return infos;
    }

    public Collection<PartyDamage> getPartyDamages()
    {
        Collection<PartyDamage> damages;
        readLock.lock();
        try
        {
            damages = _partyDamageMap.values();
        }
        finally
        {
            readLock.unlock();
        }
        return damages;
    }

    public void copy(AggroList aggroList)
    {
        writeLock.lock();
        try
        {
            Collection<AggroInfo> aggroInfos = aggroList.getAggroInfos();
            for(AggroInfo aggroInfo : aggroInfos)
                _hateList.put(aggroInfo.attackerId, aggroInfo);

            Collection<PartyDamage> partyDamages = aggroList.getPartyDamages();
            for(PartyDamage partyDamage : partyDamages)
                _partyDamageMap.put(partyDamage.party, partyDamage);
        }
        finally
        {
            writeLock.unlock();
        }
    }
}