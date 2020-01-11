package tools;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.util.ArrayList;

/**
 * Performs all actions regarding server configs
 */
public final class ConfigTool {
    private static ArrayList<ServerConfig> serverConfigs = new ArrayList<>();

    /**
     * Reads the config JSON and stores information in an ArrayList serverConfigs
     * @return 1 - JSON is found and read correctly
     *          -1 - Error: Config JSON not found
     *          -2 - Error: Config JSON found but failed to parse
     */
    public static int readConfig() {
        Object obj;

        try {
            obj = new JSONParser().parse(new FileReader("src/main/resources/config/configData.json"));
        } catch (Exception e) {
            System.out.println("Config file not found.");
            return -1;  // Config file not found
        }

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
            System.out.println("Parsed config JSON and found " + serverConfigs.size() + " server(s).\n");
            return 1;   // Config successfully parsed
        } catch (Exception e) {
            System.out.println("Error parsing config file.");
            return -2; // Error parsing config file
        }
    }


    /**
     * Writes the currently stored server configs in serverConfigs to the config JSON
     * @return 1 - JSON formed and written to file
     *          -1 - Should never be returned
     *          -2 - Error: Failed to form JSON
     *          -3 - Error: Formed JSON but failed to write to file
     */
    public static int writeConfig() {
        boolean jsonFormed = false;
        JSONArray serverList = new JSONArray();

        try {

            for (ServerConfig sc : serverConfigs) {
                JSONObject serverJSON = new JSONObject();
                serverJSON.put("serverName", sc.getServerName());
                serverJSON.put("serverID", sc.getServerID());
                serverJSON.put("botPrefix", sc.getBotPrefix());

                JSONArray botChannels = new JSONArray();
                botChannels.addAll(sc.getBotchannels());
                serverJSON.put("botChannels", botChannels);

                JSONArray botAdminIDs = new JSONArray();
                botAdminIDs.addAll(sc.getBotAdminIDs());
                serverJSON.put("botAdminIDs", botAdminIDs);

                serverList.add(serverJSON);
            }
            jsonFormed = true;
        } catch (Exception e) {
            System.out.println("Error forming JSON from serverConfigs.");
            return -2;  // Error forming JSON from serverConfigs
        }

        try {
                FileWriter file = new FileWriter("src/main/resources/config/configData.json");
                file.write(serverList.toJSONString());
                file.flush();

        } catch (Exception e) {
            System.out.println("Error writing to config file.");
            return -3;  // Error writing to config file.
        }

        return -1; // This should never return -1.
    }

    /**
     * Prints data stored in serverConfigs to terminal
     * @param jda JDA to access server information
     */
    public static void printConfig(JDA jda) {
        System.out.println("Printing Config Info");
        System.out.print(getStringAll(jda));
    }


    /**
     * Adds a new server to serverConfigs
     * @param serverID ID of server to add
     * @param jda JDA to access server information
     * @return 1 - Server added successfully
     *          -1 - Error: Error adding server data
     *          -2 - Error: Server data already in serverConfigs
     */
    public static int addServer(String serverID, JDA jda){
        if (ConfigTool.getServerConfigByID(serverID) == null) {
            if (serverConfigs.add(new ServerConfig(jda.getGuildById(serverID).getName(), serverID, jda.getGuildById(serverID).getOwnerId()))) {
                writeConfig();
                return 1;   // Successfully added new server to config
            }
            return -1;  // Error adding server data
        }
        return -2;  // Server data already exists in config
    }

    /**
     * Removes a server from server configs
     * @param serverID ID of server to remove
     * @return 1 - Server removed successfully
     *          -1 - Server not in serverConfigs
     */
    public static int removeServer(String serverID) {
        if (serverConfigs.remove(getServerConfigByID(serverID))) {
            writeConfig();
            return 1;
        }
        return -1;
    }

    /**
     * Updates serverConfig by checking if servers were left or joined while offline.
     * Removes configs of servers left while offline.
     * Adds configs of servers joined while offline.
     * @param jda JDA to access server information
     * @return
     */
    public static int updateServers(JDA jda) {
        // Remove configs for any servers left while bot offline
        int out = -1;   // No changes
        ArrayList<String> serversLeft = new ArrayList<>();
        ArrayList<String> serversJoined = new ArrayList<>();

        ArrayList<String> serverIDsToRemove = new ArrayList<>();
        for (ServerConfig sc : serverConfigs) {
            boolean isInServer = false;
            for (Guild g : jda.getGuilds()) {
                if (g.getId().equals(sc.getServerID())) {
                    isInServer = true;
                }
            }
            // If config exists for server the bot is no longer in then remove it
            if (!isInServer) {
                serverIDsToRemove.add(sc.getServerID());
            }
        }
        for (String serverID : serverIDsToRemove) {
            serversLeft.add(ConfigTool.getServerNameByID(serverID));
            removeServer(serverID);
        }

        // Print removed servers
        if (serversLeft.size() > 0) {
            System.out.println("Removed configs for the following servers as the bot left while offline:");
            for (String serverName : serversLeft) {
                System.out.println(serverName);
            }
            System.out.println();
            out = 1;    // Servers removed
        }

        // Add configs for any servers joined while bot offline
        for (Guild g : jda.getGuilds()) {
            boolean hasConfig = false;
            for (ServerConfig sc : serverConfigs) {
                if (g.getId().equals(sc.getServerID())) {
                    hasConfig = true;
                }
            }

            // If in server but config doesn't exist then create one
            if (!hasConfig) {
                addServer(g.getId(), jda);
                serversJoined.add(g.getName());
            }
        }

        // Print added servers
        if (serversJoined.size() > 0) {
            System.out.println("Added the following server configs as the bot joined while offline:");
            for (String serverName : serversJoined) {
                System.out.println(serverName);
            }
            System.out.println();

            if (out == 1) {
                out = 3;    // Servers removed and joined
            } else {
                out = 2;    // Servers joined
            }
        }
        return out; // -1 no changes, 1 servers removed, 2 servers joined, 3 servers joined and removed
    }


    /**
     * Returns information of all servers as a string
     * @param jda JDA to access server information
     * @return String of information on all servers stored in serverConfigs
     */
    public static String getStringAll(JDA jda) {
        String out = "";
        for (ServerConfig sc : serverConfigs) {
            out += getStringByID(sc.getServerID(), jda) + "\n";
        }

        return out;
    }

    /**
     * Returns information of a single server determined by server ID parameter
     * @param serverID Server to return information of
     * @param jda JDA to access server information
     * @return String of information on single server
     */
    public static String getStringByID(String serverID, JDA jda) {
        String out = "";
        for (ServerConfig sc : serverConfigs) {
            if (sc.getServerID().equals(serverID)) {

                ArrayList<Object> bc = new ArrayList<>();
                for (String bcID : sc.getBotchannels()) {
                    try {
                        bc.add(jda.getTextChannelById(bcID).getName() + " (" + bcID +")");
                    } catch (NullPointerException npe) {
                        System.out.println("ERROR: Invalid Bot Channel ID.");
                    }
                }

                ArrayList<Object> ba = new ArrayList<>();
                for (String baID : sc.getBotAdminIDs()) {
                    try {
                        ba.add(jda.getUserById(baID).getName() + " (" + baID + ")");
                    } catch (NullPointerException npe) {
                        System.out.println("ERROR: Invalid Bot Admin ID.");
                    }
                }

                out = "Server Name: " + sc.getServerName() + " (" + sc.getServerID() + ")" + "\n" +
                        "Bot Prefix: " + sc.getBotPrefix() + "\n" +
                        "Bot Channels: " + bc + "\n" +
                        "Bot Admins: " + ba + "\n";
            }
        }

        return out;
    }


    /**
     * Returns server name by server ID
     * @param serverID Server to get information of
     * @return Server name
     */
    public static String getServerNameByID(String serverID) {
        ServerConfig sc = getServerConfigByID(serverID);
        if (sc != null) {
            return sc.getServerName();
        }
        return null;
    }

    /**
     * Returns bot prefix for a server by server ID
     * @param serverID Server to get information of
     * @return Bot prefix
     */
    public static String getBotPrefixByID(String serverID) {
        ServerConfig sc = getServerConfigByID(serverID);
        if (sc != null) {
            return sc.getBotPrefix();
        }
        return null;
    }

    /**
     * Sets the command prefix for a server
     * @param serverID Server to set command prefix
     * @param prefix Command prefix to update to
     * @return 1 - Server prefix updated successfully
     *          -1 - Error unable to find server
     */
    public static int setServerPrefixByID(String serverID, String prefix) {
        ServerConfig sc = getServerConfigByID(serverID);
        if (sc != null) {
            sc.setBotPrefix(prefix);
            return 1;
        } else {
            return -1;
        }
    }

    /**
     * Returns bot channels IDs of a server
     * @param serverID Server to get bot channels of
     * @return An ArrayList of bot channel IDs
     */
    public static ArrayList<String> getBotChannelsByID(String serverID) {
        ServerConfig sc = getServerConfigByID(serverID);
        if (sc != null) {
            return sc.getBotchannels();
        }
        return null;
    }

    /**
     * Returns bot admins IDs of a server
     * @param serverID Server to get bot admin IDs of
     * @return An ArrayList of bot admin IDs
     */
    public static ArrayList<String> getBotAdminsByID(String serverID) {
        ServerConfig sc = getServerConfigByID(serverID);
        if (sc != null) {
            return sc.getBotAdminIDs();
        }
        return null;
    }


    /**
     * Adds a bot admin to a server
     * @param serverID Server to add bot admin to
     * @param userID User to add as bot admin
     * @param jda JDA to access server information
     * @return 1 - Successfully added bot admin
     *          -1 - Server doesn't exist
     *          -2 - User not in server
     *          -3 - Bot admin already exists
     */
    public static int addBotAdminByID(String serverID, String userID, JDA jda) {
        ServerConfig sc = getServerConfigByID(serverID);
        if (sc != null) {
            if (VerifyInTool.verifyUserInServer(serverID, userID, jda) == 1) {
                if (!sc.getBotAdminIDs().contains(userID)) {
                    sc.addBotAdminID(userID);
                }
                return -3;  // Bot admin already exists
            }
            return -2;  // User not in server
        }
        return -1;  // Server doesn't exist
    }

    /**
     * Removes a bot admin from a server
     * @param serverID Server to remove bot admin from
     * @param userID User to remove from bot admin
     * @param jda JDA to access server information
     * @return 1 - Bot admin removed successfully
     *          -1 - Server not found
     *          -2 - User is not an existing bot admin
     */
    public static int removeBotAdminByID(String serverID, String userID, JDA jda) {
        ServerConfig sc = getServerConfigByID(serverID);
        if (sc != null) {
            if (sc.removeBotAdminID(userID)) {
                return 1;   // Bot admin successfully removed
            }
            return -2;  // User is not existing bot admin
        }
        return -1;  // Server not found
    }

    /**
     * Defines a channel in a server as a bot channel
     * @param serverID Server to define new bot channel
     * @param channelID Channel to define as bot channel
     * @param jda JDA to access server information
     * @return 1 - Successfully added bot channel
     *          -1 - Server doesn't exist
     *          -2 - Channel doesn't exist in server
     *          -3 - Channel is already a bot channel
     */
    public static int addBotChannelByID(String serverID, String channelID, JDA jda) {
        ServerConfig sc = getServerConfigByID(serverID);
        if (sc != null) {
            if (VerifyInTool.verifyChannelInServer(serverID, channelID, jda) == 1) {
                if (!sc.getBotchannels().contains(channelID)) {
                    return 1;
                }
                return -3; // Channel already bot channel
            }
            return -2; // Channel doesn't exist in server
        }
        return -1; // Server doesn't exist
    }

    /**
     * Removes a bot channel from a server
     * @param serverID Server to remove bot channel
     * @param channelID Channel to remove as bot channel
     * @param jda JDA to access server information
     * @return 1 - Removed bot channel successfully
     *          -1 - Server not found
     *          -2 - Channel is not an existing bot channel
     */
    public static int removeBotChannelByID(String serverID, String channelID, JDA jda) {
        ServerConfig sc = getServerConfigByID(serverID);
        if (sc != null) {
            if (sc.removeBotChannel(channelID)) {
                return 1;   // Bot channel successfully removed
            }
            return -2;  // Channel is not a bot channel
        }
        return -1;  // Server not found
    }

    /**
     * Returns a ServerConfig by a server ID
     * @param serverID Server to get ServerConfig of
     * @return ServerConfig of specified server
     */
    private static ServerConfig getServerConfigByID(String serverID) {
        for (ServerConfig sc : serverConfigs) {
            if (sc.getServerID().equals(serverID)) {
                return sc;
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
    private ArrayList<String> botChannelIDs;
    private ArrayList<String> botAdminIDs;

    // For adding a new server config
    public ServerConfig(String serverName, String serverID, String ownerID) {
        this.serverName = serverName;
        this.serverID = serverID;
        this.botPrefix = "^";
        this.botChannelIDs = new ArrayList<>();
        this.botAdminIDs = new ArrayList<>();
        botAdminIDs.add(ownerID);
    }

    // For parsing an existing server from json
    public ServerConfig(String serverName, String serverID, String botPrefix,
                        ArrayList<String> botChannelIDs, ArrayList<String> botAdminIDs) {
        this.serverName = serverName;
        this.serverID = serverID;
        this.botPrefix = botPrefix;
        this.botChannelIDs = botChannelIDs;
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
        return botChannelIDs;
    }
    public boolean addBotChannel(String channelID) {
        return this.botChannelIDs.add(channelID);
    }
    public boolean removeBotChannel(String channelID) {
        return this.botChannelIDs.remove(channelID);
    }

    public ArrayList<String> getBotAdminIDs() { return botAdminIDs; }
    public boolean addBotAdminID(String botAdminID) {
        return this.botAdminIDs.add(botAdminID);
    }
    public boolean removeBotAdminID(String botAdminID) {
        return this.botAdminIDs.remove(botAdminID);
    }
}