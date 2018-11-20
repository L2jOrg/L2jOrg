package org.l2j.commons.database;

import org.l2j.commons.database.model.GameServer;
import org.springframework.data.repository.CrudRepository;

public interface GameserverRepository extends CrudRepository<GameServer, Integer> {
}
