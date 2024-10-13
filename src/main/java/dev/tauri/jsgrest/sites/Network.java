package dev.tauri.jsgrest.sites;

import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import dev.tauri.jsg.stargate.network.StargateNetwork;

import java.io.OutputStream;

public class Network implements IHttpSite {
    @Override
    public String getPath() {
        return "/sgnetwork";
    }

    @Override
    public void handle(HttpExchange exchange) {
        try {
            var network = StargateNetwork.INSTANCE.getAll();
            var json = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(network);

            exchange.sendResponseHeaders(200, json.length());
            OutputStream os = exchange.getResponseBody();
            os.write(json.getBytes());
            os.close();
        } catch (Exception ignored) {
        }
    }
}
