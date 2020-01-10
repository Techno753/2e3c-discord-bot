package tools;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.Event;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.annotation.Nonnull;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public final class ConfigTool {
    private static ArrayList<ServerConfig> serverConfigs = new ArrayList<>();

    // Constructor reads the file upon creation
    public static void readConfig() {
        boolean fileExists = false;
        Object obj = null;

        try {
            obj = new JSONParser().parse(new FileReader("src/main/resources/config/configData.json"));
            fileExists = true;
        } catch (Exception e) {
            System.out.println("Config file not found.");
        }

        if (fileExists) {
            try {
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
                System.out.println("Parsed config JSON and found " + serverConfigs.size() + " server(s).");
            } catch (Exception e) {
                System.out.println("Error parsing config file.");
            }
        }
    }

    // Writes ServerConfig data to json
    public static void writeConfig() {
        try {
            JSONArray serverList = new JSONArray();

            for (ServerConfig sc : serverConfigs) {
                JSONObject serverJSON = new JSONObject();
                serverJSON.put("serverName", sc.getServerName());
                serverJSON.put("serverID", sc.getServerID());
                serverJSON.put("botPrefix", sc.getBotPrefix());

                JSONArray botChannels = new JSONArray();
//                for (String ch : sc.getBotchannels()) {
//                    botChannels.add(ch);
//                }
                botChannels.addAll(sc.getBotchannels());
                serverJSON.put("botChannels", botChannels);

                JSONArray botAdminIDs = new JSONArray();
//                for (String ad : sc.getBotAdminIDs()) {
//                    botAdminIDs.add(ad);
//                }
                botAdminIDs.addAll(sc.getBotAdminIDs());
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
    public static void addServer(String serverName, String serverID, String ownerID){
        serverConfigs.add(new ServerConfig(serverName, serverID, ownerID));
        System.out.println("Added new server");
        writeConfig();
    }

    // removes a server from ServerConfig if exists
    public static void removeServer(String serverID) {
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


    // Gets info for all servers as a string
    public static String getStringAll(Event e) {
        String out = "";
        for (ServerConfig sc : serverConfigs) {
            out += getStringByID(sc.getServerID(), e) + "\n";
        }

        return out;
    }

    // Gets info for single server as a string
    public static String getStringByID(String id, Event e) {
        String out = "";
        for (ServerConfig sc : serverConfigs) {
            if (sc.getServerID().equals(id)) {

                ArrayList<Object> botChannelNames = new ArrayList<>();

                for (String bcID : sc.getBotchannels()) {
                    try {
                        botChannelNames.add(e.getJDA().getTextChannelById(bcID).getName());
                    } catch (NullPointerException npe) {
                        System.out.println("ERROR: Invalid Bot Channel ID.");
                    }
                }

                ArrayList<Object> botAdminNames = new ArrayList<>();
                for (String baID : sc.getBotAdminIDs()) {
                    try {
                        botAdminNames.add(e.getJDA().getUserById(baID).getName());
                    } catch (NullPointerException npe) {
                        System.out.println("ERROR: Invalid Bot Admin ID.");
                    }
                }

                out = "Server Name: " + sc.getServerName() + "\n" +
                        "Server ID: " + sc.getServerID() + "\n" +
                        "Bot Prefix: " + sc.getBotPrefix() + "\n" +
                        "Bot Channels: " + botChannelNames + "\n" +
                        "Bot Channel IDs: " + sc.getBotchannels() + "\n" +
                        "Bot Admins: " + botAdminNames + "\n" +
                        "Bot Admin IDs: " + sc.getBotAdminIDs() + "\n";
            }
        }

        return out;
    }

    public static String getServerNameByID(String id) {
        for (ServerConfig sc : serverConfigs) {
            if (sc.getServerID().equals(id)) {
                return sc.getServerName();
            }
        }
        return null;
    }

    public static String getBotPrefixByID(String id) {
        for (ServerConfig sc : serverConfigs) {
            if (sc.getServerID().equals(id)) {
                return sc.getBotPrefix();
            }
        }
        return null;
    }

    public static void setServerPrefixByID(String id, String prefix) {
        for (ServerConfig sc : serverConfigs) {
            if (sc.getServerID().equals(id)) {
                sc.setBotPrefix(prefix);
                break;
            }
        }
    }

    public static ArrayList<String> getBotChannelsByID(String id) {
        for (ServerConfig sc : serverConfigs) {
            if (sc.getServerID().equals(id)) {
                return sc.getBotchannels();
            }
        }
        return null;
    }

    public static ArrayList<String> getBotAdminsByID(String id) {
        for (ServerConfig sc : serverConfigs) {
            if (sc.getServerID().equals(id)) {
                return sc.getBotAdminIDs();
            }
        }
        return null;
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
        this.botChannels = new ArrayList<>();
        this.botAdminIDs = new ArrayList<>();
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

//    public String toString()    {
//        ArrayList<String> botAdminNames = new ArrayList<>();
//        return "Server Name: " + serverName + "\n" +
//                "Server ID: " + serverID + "\n" +
//                "Bot Prefix: " + botPrefix + "\n" +
//                "Bot Channels: " + botChannels + "\n" +
//                "Bot Admins: " + botAdminIDs + "\n";
//    }
}