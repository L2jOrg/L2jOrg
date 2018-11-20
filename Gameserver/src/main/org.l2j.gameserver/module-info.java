module org.l2j.gameserver {
    requires org.l2j.commons;
    requires org.l2j.mmocore;

    requires java.sql;
    requires java.desktop;
    requires org.slf4j;
    requires java.scripting;
    requires jython.standalone;
    requires spring.data.commons;
    requires spring.data.jdbc;
    requires spring.context;
    requires java.xml.bind;
    requires trove;
    requires commons.lang3;
    requires org.napile.primitive;
    requires commons.io;
    requires java.management;
    requires dom4j;
    requires ehcache.core;
    requires velocity;
    requires velocity.slf4j;
    requires mesp;

    exports org.l2j.gameserver;
}