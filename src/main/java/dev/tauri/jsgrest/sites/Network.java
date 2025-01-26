package dev.tauri.jsgrest.sites;

import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import dev.tauri.jsg.stargate.network.StargateNetwork;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class Network implements IHttpSite {
    @Override
    public String getPath() {
        return "/sgnetwork";
    }

    @Override
    public void handle(HttpExchange exchange) {
        try {
            var network = StargateNetwork.INSTANCE.getAll();
            var map = new ArrayList<HashMap<String, Object>>();
            for (var gate : network.entrySet()) {
                var entry = new HashMap<String, Object>();
                entry.put("dim", gate.getKey().dimension.location().toString());
                entry.put("name", gate.getKey().getName());
                var posMapped = new HashMap<String, Integer>();
                posMapped.put("x", gate.getKey().gatePos.getX());
                posMapped.put("y", gate.getKey().gatePos.getY());
                posMapped.put("z", gate.getKey().gatePos.getZ());
                entry.put("pos", posMapped);
                entry.put("addresses", gate.getValue());
                map.add(entry);
            }

            var json = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(map);

            exchange.sendResponseHeaders(200, json.length());
            OutputStream os = exchange.getResponseBody();
            os.write(json.getBytes());
            os.close();
        } catch (Exception ignored) {
        }
    }
}
