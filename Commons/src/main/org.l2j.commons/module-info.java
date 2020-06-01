import org.l2j.commons.database.handler.TypeHandler;

module org.l2j.commons {
    requires java.sql;
    requires org.slf4j;
    requires com.zaxxer.hikari;
    requires java.management;
    requires java.compiler;
    requires cache.api;
    requires java.desktop;
    requires io.github.joealisson.primitive;

    exports org.l2j.commons.util;
    exports org.l2j.commons.util.collection;
    exports org.l2j.commons.xml;
    exports org.l2j.commons.crypt;
    exports org.l2j.commons.database;
    exports org.l2j.commons.database.annotation;
    exports org.l2j.commons.configuration;
    exports org.l2j.commons.threading;
    exports org.l2j.commons.cache;
    exports org.l2j.commons.network;
    exports org.l2j.commons.util.filter;

    uses TypeHandler;
    provides TypeHandler
        with org.l2j.commons.database.handler.ByteHandler,
             org.l2j.commons.database.handler.ByteArrayHandler,
             org.l2j.commons.database.handler.IntegerHandler,
             org.l2j.commons.database.handler.IntMapHandler,
             org.l2j.commons.database.handler.IntSetHandler,
             org.l2j.commons.database.handler.ConcurrentIntMapHandler,
             org.l2j.commons.database.handler.ListHandler,
             org.l2j.commons.database.handler.LongHandler,
             org.l2j.commons.database.handler.VoidHandler,
             org.l2j.commons.database.handler.ShortHandler,
             org.l2j.commons.database.handler.StringHandler,
             org.l2j.commons.database.handler.DoubleHandler,
             org.l2j.commons.database.handler.BooleanHandler,
             org.l2j.commons.database.handler.DateHandler,
             org.l2j.commons.database.handler.DateTimeHandler,
             org.l2j.commons.database.handler.EnumHandler,
             org.l2j.commons.database.handler.EntityHandler;
}