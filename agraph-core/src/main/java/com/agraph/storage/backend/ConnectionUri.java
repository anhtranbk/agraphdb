package com.agraph.storage.backend;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="https://github.com/tjeubaoit">tjeubaoit</a>
 */
@Accessors(chain = true, fluent = true)
public @Data class ConnectionUri {

    private String scheme;
    private String host;
    private int port;
    private String username;
    private String password;
    private String database;
    @Getter(AccessLevel.MODULE)
    private Map<String, String> parameters = new HashMap<>();

    @Override
    public String toString() {
        return this.asString(false);
    }

    public ConnectionUri addParameters(String... params) {
        Preconditions.checkArgument(params.length % 2 == 0,
                "Invalid number parameters, must be an even number");
        for (int i = 0; i < params.length; i += 2) {
            this.parameters.put(params[i], params[i+1]);
        }
        return this;
    }

    public String asString(boolean withCredentials) {
        StringBuilder sb = new StringBuilder();
        sb.append(scheme).append("://");
        if (username != null && password != null && withCredentials) {
            sb.append(username).append(":").append(password).append("@");
        }
        sb.append(host);
        if (port != 0) {
            sb.append(":").append(port);
        }
        sb.append("/").append(database);
        if (!parameters.isEmpty()) {
            int i = 0;
            for (Map.Entry<String, String> e : parameters.entrySet()) {
                String separator = i++ == 0 ? "?" : "&";
                sb.append(separator);
                sb.append(e.getKey()).append("=").append(e.getValue());
            }
        }
        return sb.toString();
    }

    public static ConnectionUri parseFromString(String connectionString) {
        try {
            ConnectionUri uri = new ConnectionUri();

            String[] p1 = connectionString.split("://");
            uri.scheme(p1[0]);
            String[] p2 = p1[1].split("@");

            String tmp;
            if (p2.length > 1) {
                String[] p3 = p2[0].split(":");
                uri.username(p3[0]);
                uri.password(p3[1]);
                tmp = p2[1];
            } else {
                tmp = p2[0];
            }

            String[] p4 = tmp.split("/");
            String[] p5 = p4[0].split(":");
            if (p5.length > 1) {
                uri.port(Integer.parseInt(p5[1]));
            }
            uri.host(p5[0]);

            String[] p6 = p4[1].split("\\?");
            uri.database(p6[0]);

            String[] p7 = p6[1].split("&");
            Map<String, String> m = new HashMap<>(p7.length);
            for (String q : p7) {
                String[] p8 = q.split("=");
                m.put(p8[0], p8[1]);
            }
            uri.parameters(m);

            return uri;
        } catch (Exception e) {
            throw new IllegalArgumentException("Parse ConnectionUri failed", e);
        }
    }
}
