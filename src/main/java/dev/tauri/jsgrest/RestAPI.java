package dev.tauri.jsgrest;


import com.sun.net.httpserver.HttpServer;
import dev.tauri.jsgrest.sites.IHttpSite;
import dev.tauri.jsgrest.sites.Network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Objects;

public class RestAPI {
    protected static HttpServer server = null;

    public static final IHttpSite[] sitesArray = {
            new Network()
    };

    public static void start() {
        if (server != null) server.stop(0);
        try {
            var port = RestConfig.General.port.get();
            var authToken = RestConfig.General.token.get();
            server = HttpServer.create(new InetSocketAddress(port), 0);
            for (var site : sitesArray) {
                server.createContext(site.getPath(), exchange -> {
                    try {
                        if (!Objects.equals(authToken, "")) {
                            if (!exchange.getRequestHeaders().containsKey("auth")) {
                                exchange.sendResponseHeaders(403, 0);
                                return;
                            }
                            var tokenGot = exchange.getRequestHeaders().getFirst("auth");
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
