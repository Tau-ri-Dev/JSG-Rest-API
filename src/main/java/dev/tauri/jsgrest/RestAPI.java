package dev.tauri.jsgrest;


import com.sun.net.httpserver.HttpServer;
import dev.tauri.jsgrest.sites.IHttpSite;
import dev.tauri.jsgrest.sites.Network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RestAPI {
    protected static HttpServer server = null;

    public static final IHttpSite[] sitesArray = {
            new Network()
    };

    public static Map<String, String> queryToMap(String query) {
        if (query == null) {
            return null;
        }
        Map<String, String> result = new HashMap<>();
        for (String param : query.split("&")) {
            try {
                String[] entry = param.split("=");
                if (entry.length > 1) {
                    result.put(
                            entry[0],
                            entry[1]
                    );
                } else {
                    result.put(
                            entry[0],
                            ""
                    );
                }
            } catch (Exception ignored) {
            }
        }
        return result;
    }

    public static void start() {
        if (server != null) server.stop(0);
        try {
            var port = RestConfig.General.port.get();
            var authToken = RestConfig.General.token.get();
            server = HttpServer.create(new InetSocketAddress(port), 0);
            for (var site : sitesArray) {
                server.createContext(site.getPath(), exchange -> {
                    var query = queryToMap(exchange.getRequestURI().getQuery());
                    try {
                        if (!Objects.equals(authToken, "")) {
                            var tokenGot = query.get("authToken");
                            if (tokenGot == null) {
                                exchange.sendResponseHeaders(403, 0);
                                return;
                            }
                            if (!Objects.equals(tokenGot, authToken)) {
                                exchange.sendResponseHeaders(403, 0);
                                return;
                            }
                        }
                        site.handle(exchange);
                    } catch (Exception ignored) {
                    }
                });
            }
            server.setExecutor(null);
            server.start();
            JSGRest.logger.info("Listening on port: " + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void stop() {
        if (server == null) return;
        server.stop(0);
        server = null;
        JSGRest.logger.info("Stopping HTTP Server");
    }
}
