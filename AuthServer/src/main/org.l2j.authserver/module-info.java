module org.l2j.authserver {
    requires org.l2j.commons;
    requires org.slf4j;
    requires io.github.joealisson.mmocore;
    requires io.github.joealisson.primitive;
    requires java.xml;
    requires jdk.unsupported;

    exports org.l2j.authserver;
    opens org.l2j.authserver.settings to org.l2j.commons;
    opens org.l2j.authserver.data.database to org.l2j.commons;
}