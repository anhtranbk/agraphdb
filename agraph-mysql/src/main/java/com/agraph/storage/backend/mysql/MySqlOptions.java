package com.agraph.storage.backend.mysql;

import com.agraph.common.util.Strings;
import com.agraph.config.Config;
import com.agraph.config.ConfigDescriptor;
import com.agraph.storage.backend.BackendOptions;
import com.agraph.storage.backend.ConnectionUri;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true, fluent = true)
public @Data class MySqlOptions extends BackendOptions {

    @Getter(AccessLevel.NONE)
    @ConfigDescriptor(name = "mysql.uri")
    private String uri;

    @ConfigDescriptor(name = "mysql.host", defaultValue = "localhost")
    private String host;

    @ConfigDescriptor(name = "mysql.port", defaultValue = "3306")
    private int port;

    @ConfigDescriptor(name = "mysql.database")
    private String database;

    @ConfigDescriptor(name = "mysql.username")
    private String username;

    @ConfigDescriptor(name = "mysql.password")
    private String password;

    public MySqlOptions() {
        super();
    }

    public MySqlOptions(Config conf) {
        super(conf);
    }

    public String uri() {
        return uri(false);
    }

    public String uri(boolean withCredentials) {
        if (Strings.isNonEmpty(this.uri)) {
            return this.uri;
        }
        ConnectionUri connectionUri = new ConnectionUri()
                .scheme("jdbc:mysql")
                .host(this.host)
                .port(this.port)
                .username(this.username)
                .password(this.password)
                .database(this.database)
                .addParameters("charset", "utf8mb4", "autoReconnect", "true", "useUnicode", "yes");
        return connectionUri.asString(withCredentials);
    }
}
