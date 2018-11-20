package org.l2j.mmocore;

public interface ClientFactory<T extends Client<Connection<T>>> {

    T create(Connection<T> connection);
}
