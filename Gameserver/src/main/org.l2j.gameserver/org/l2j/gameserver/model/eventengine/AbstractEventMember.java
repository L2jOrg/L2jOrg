package org.l2j.gameserver.model.eventengine;

import org.l2j.gameserver.model.L2World;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @param <T>
 * @author UnAfraid
 */
public abstract class AbstractEventMember<T extends AbstractEvent<?>> {
    private final int _objectId;
    private final T _event;
    private final AtomicInteger _score = new AtomicInteger();

    public AbstractEventMember(L2PcInstance player, T event) {
        _objectId = player.getObjectId();
        _event = event;
    }

    public final int getObjectId() {
        return _objectId;
    }

    public L2PcInstance getPlayer() {
        return L2World.getInstance().getPlayer(_objectId);
    }

    public void sendPacket(IClientOutgoingPacket... packets) {
        final L2PcInstance player = getPlayer();
        if (player != null) {
            for (IClientOutgoingPacket packet : packets) {
                player.sendPacket(packet);
            }
        }
    }

    public int getClassId() {
        final L2PcInstance player = getPlayer();
        if (player != null) {
            return player.getClassId().getId();
        }
        return 0;
    }

    public int getScore() {
        return _score.get();
    }

    public void setScore(int score) {
        _score.set(score);
    }

    public int incrementScore() {
        return _score.incrementAndGet();
    }

    public int decrementScore() {
        return _score.decrementAndGet();
    }

    public int addScore(int score) {
        return _score.addAndGet(score);
    }

    public final T getEvent() {
        return _event;
    }
}
