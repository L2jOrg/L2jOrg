module org.l2j.commons {
    requires java.sql;
    requires org.slf4j;
    requires com.zaxxer.hikari;
    requires transitive java.xml.bind;
    requires java.management;
    requires java.compiler;
    requires dom4j;
    requires ecj;
    requires cache.api;
    requires java.desktop;
    requires primitive;

    exports org.l2j.commons.util;
    exports org.l2j.commons.xml;
    exports org.l2j.commons.crypt;
    exports org.l2j.commons.database;
    exports org.l2j.commons.database.annotation;
    exports org.l2j.commons.configuration;
    exports org.l2j.commons.settings;
    exports org.l2j.commons.geometry;
    exports org.l2j.commons.collections;
    exports org.l2j.commons.lang;
    exports org.l2j.commons.listener;
    exports org.l2j.commons.threading;
    exports org.l2j.commons.dbutils;
    exports org.l2j.commons.lang.reference;
    exports org.l2j.commons.util.concurrent.atomic;
    exports org.l2j.commons.dao;
    exports org.l2j.commons.math;
    exports org.l2j.commons.time.cron;
    exports org.l2j.commons.string;
    exports org.l2j.commons.math.random;
    exports org.l2j.commons.logging;
    exports org.l2j.commons.compiler;
    exports org.l2j.commons.annotations;
    exports org.l2j.commons.data.xml;
    exports org.l2j.commons.text;
    exports org.l2j.commons.formats.dds;
    exports org.l2j.commons.cache;
    exports org.l2j.commons.database.handler;

    uses org.l2j.commons.database.handler.TypeHandler;
    provides org.l2j.commons.database.handler.TypeHandler
        with org.l2j.commons.database.handler.IntegerHandler,
             org.l2j.commons.database.handler.VoidHandler,
             org.l2j.commons.database.handler.ListHandler,
             org.l2j.commons.database.handler.IntSetHandler,
             org.l2j.commons.database.handler.EntityHandler;
}