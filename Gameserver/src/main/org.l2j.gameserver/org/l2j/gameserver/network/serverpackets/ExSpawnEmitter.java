package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author KenM
 * @author JoeAlisson
 */
public class ExSpawnEmitter extends ServerPacket {
    private final int attackerId;
    private final int targetId;
    private final SpawnEmitterType type;

    private ExSpawnEmitter(int attackerId, int targetId, SpawnEmitterType type) {
        this.attackerId = attackerId;
        this.targetId = targetId;
        this.type = type;

    }

    public ExSpawnEmitter(Creature attacker, Creature target, SpawnEmitterType type) {
        this(attacker.getObjectId(), target.getObjectId(), type);
    }

    public ExSpawnEmitter(Creature creature, SpawnEmitterType type) {
        this(creature.getObjectId(), creature.getObjectId(), type);
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_SPAWN_EMITTER);

        writeInt(targetId);
        writeInt(attackerId);
        writeInt(type.ordinal());
    }

    public enum SpawnEmitterType {
        BLUE_SOUL_EATEN,
        YELLOW_UNK,
        WHITE_SOUL,
        BLACK_SOUL,
    }

}
