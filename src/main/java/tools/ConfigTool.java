package tools;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.util.ArrayList;

public final class ConfigTool {
    private static ArrayList<ServerConfig> serverConfigs = new ArrayList<>();

    // Constructor reads the file upon creation
    public ConfigTool() throws Exception {

        try {
            Object obj = new JSONParser().parse(new FileReader("src/main/resources/config/configData.json"));
            JSONArray ja = (JSONArray) obj;

            // Iterate through JSON and parse every info of each server
            for (Object serv : ja) {
                JSONObject server = (JSONObject) serv;

                ArrayList<String> botChannels = new ArrayList<>();
                for (Object ch : (JSONArray) server.get("botChannels")) {
                    botChannels.add((String) ch);
                }
                ArrayList<String> botAdminIDs = new ArrayList<>();
                for (Object ad : (JSONArray) server.get("botAdminIDs")) {
                    botAdminIDs.add((String) ad);
                }

                serverConfigs.add(new ServerConfig((String) server.get("serverName"),
                                                (String) server.get("serverID"),
                                                (String) server.get("botPrefix"),
                                                botChannels,
                                                botAdminIDs));

            }
            System.out.println("Parsed config JSON.");
        } catch (Exception e) {
            System.out.println("Error parsing config file");
        }


        for (ServerConfig sc : serverConfigs) {
            System.out.println(sc);
        }
    }

    // Writes ServerConfig data to json
    public static void writeJson() {
        try {
            JSONArray serverList = new JSONArray();

            for (ServerConfig sc : serverConfigs) {
                JSONObject serverJSON = new JSONObject();
                serverJSON.put("serverName", sc.getServerName());
                serverJSON.put("serverID", sc.getServerID());
                serverJSON.put("botPrefix", sc.getBotPrefix());

                JSONArray botChannels = new JSONArray();
                for (String ch : sc.getBotchannels()) {
                    botChannels.add(ch);
                }
                serverJSON.put("botChannels", botChannels);

                JSONArray botAdminIDs = new JSONArray();
                for (String ad : sc.getBotAdminIDs()) {
                    botAdminIDs.add(ad);
                }
                serverJSON.put("botAdminIDs", botAdminIDs);

                serverList.add(serverJSON);

                FileWriter file = new FileWriter("src/main/resources/config/configData.json");

                file.write(serverList.toJSONString());
                file.flush();
            }
        } catch (Exception e) {
            System.out.println("Error writing to config file.");
        }
    }

    // Adds a new server to ServerConfig
    public static void addServer(String serverName, String serverID, String ownerID) throws Exception{
        serverConfigs.add(new ServerConfig(serverName, serverID, ownerID));
        System.out.println("Added new server");
        writeJson();
    }

    // removes a server from ServerConfig if exists
    public static void removeServer(String serverID) throws Exception{
        int removeIndex = -1;
        for (int i = 0; i < serverConfigs.size(); i++) {
            if (serverConfigs.get(i).getServerID().equals(serverID)) {
                removeIndex = i;
            }
        }
        if (removeIndex > -1) {
            serverConfigs.remove(removeIndex);
        }
    }

    // Returns a ServerConfig by server ID
    public static ServerConfig getServerConfigByID(String id) {
        ServerConfig out = null;

        for (ServerConfig sc : serverConfigs) {
            if (sc.getServerID().equals(id)) {
                out = sc;
            }
        }

        return out;
    }

    // Sets a Server's Prefix by server ID
    public static void setServerPrefixByID(String id, String prefix) {
        getServerConfigByID(id).setBotPrefix(prefix);
    }

    // Gets info for all servers as a string
    public static String getStringAll() {
        String out = "";
        for (ServerConfig sc : serverConfigs) {
            out += sc.toString();
            out += "\n";
        }

        return out;
    }

    // Gets info for single server as a string
    public static String getStringByID(String id) {
        String out = "";
        for (ServerConfig sc : serverConfigs) {
            if (sc.getServerID().equals(id)) {
                out = sc.toString();
            }
        }

        return out;
    }
}

// Object to contain server data
class ServerConfig {
    private String serverName;
    private String serverID;
    private String botPrefix;
    private ArrayList<String> botChannels;
    private ArrayList<String> botAdminIDs;

    // For adding a new server config
    public ServerConfig(String serverName, String serverID, String ownerID) {
        this.serverName = serverName;
        this.serverID = serverID;
        this.botPrefix = "^";
        this.botChannels = new ArrayList<String>();
        this.botAdminIDs = new ArrayList<String>();
        botAdminIDs.add(ownerID);
    }

    // For parsing an existing server from json
    public ServerConfig(String serverName, String serverID, String botPrefix,
                        ArrayList<String> botChannels, ArrayList<String> botAdminIDs) {
        this.serverName = serverName;
        this.serverID = serverID;
        this.botPrefix = botPrefix;
        this.botChannels = botChannels;
        this.botAdminIDs = botAdminIDs;
    }

    public String getServerName() {
        return serverName;
    }
    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getServerID() {
        return serverID;
    }

    public String getBotPrefix() {
        return botPrefix;
    }
    public void setBotPrefix(String botPrefix) {
        this.botPrefix = botPrefix;
    }

    public ArrayList<String> getBotchannels() {
        return botChannels;
    }
    public void addBotChannel(String channelID) {
        this.botChannels.add(channelID);
    }
    public void removeBotChannel(String channelID) {
        this.botChannels.remove(channelID);
    }

    public ArrayList<String> getBotAdminIDs() { return botAdminIDs; }
    public void addBotAdminID(String botAdminID) {
        this.botAdminIDs.add(botAdminID);
    }
    public void removeBotAdminID(String botAdminID) {
        this.botAdminIDs.remove(botAdminID);
    }

    public String toString()    {
        return "Server Name: " + serverName + "\n" +
                "Server ID: " + serverID + "\n" +
                "Bot Prefix: " + botPrefix + "\n" +
                "Bot Channels: " + botChannels + "\n" +
                "Bot Admins: " + botAdminIDs + "\n";
    }
}