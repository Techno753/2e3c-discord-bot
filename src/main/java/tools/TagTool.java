package tools;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

public final class TagTool {
    private static ArrayList<Tag> tagArray = new ArrayList<>();

    /**
     * Reads and parses tags from tags json
     * @return 1 - JSON is found and read correctly
     *          -1 - Error: Tag JSON not found
     *          -2 - Error: Tag JSON found but failed to parse
     */
    public static int readTags() {
        Object obj;

        try {
            obj = new JSONParser().parse(new FileReader("src/main/resources/tagData/tagData.json"));
        } catch (Exception e) {
            System.out.println("Tag json not found.");
            return -1;  // Tag json file not found
        }

        try {
            JSONArray ja = (JSONArray) obj;

            // Iterate through JSON and parse every info of every tag
            for (Object ta : ja) {
                JSONObject tag = (JSONObject) ta;

                tagArray.add(new Tag((String) tag.get("Tag"),
                        (String) tag.get("Reply"),
                        (String) tag.get("Type"),
                        (String) tag.get("Creator")));
            }
            System.out.println("Parsed tags JSON and found " + tagArray.size() + " tag(s).\n");
            return 1;   // tag json successfully parsed
        } catch (Exception e) {
            System.out.println("Error parsing tags JSON file.");
            return -2; // Error parsing tag json file
        }
    }

    /**
     * Writes the currently stored tags in tagArray to the tags JSON
     *
     * @return 1 - JSON formed and written to file
     * -1 - Should never be returned
     * -2 - Error: Failed to form JSON
     * -3 - Error: Formed JSON but failed to write to file
     */
    public static int writeTags() {
        JSONArray tagList = new JSONArray();

        try {

            for (Tag tag : tagArray) {
                JSONObject tagJSON = new JSONObject();
                tagJSON.put("Tag", tag.getTag());
                tagJSON.put("Reply", tag.getReply());
                tagJSON.put("Type", tag.getType());
                tagJSON.put("Creator", tag.getCreator());

                tagList.add(tagJSON);
            }
        } catch (Exception e) {
            System.out.println("Error forming JSON from tagArray.");
            return -2;  // Error forming JSON from tagArray
        }

        try {
            FileWriter file = new FileWriter("src/main/resources/tagData/tagData.json");
            file.write(tagList.toJSONString());
            file.flush();

        } catch (Exception e) {
            System.out.println("Error writing to config file.");
            return -3;  // Error writing to tag file.
        }

        return -1; // This should never return -1.
    }

    public static int addTextTag(GuildMessageReceivedEvent gmre, String cmdString) {
        // Get terms from command msg
        ArrayList<String> terms = RegexTool.getGroups("^(?is)att (\\w+)[\\s\\n](.+)$", cmdString);
        String tag = terms.get(0);
        String reply = terms.get(1);
        String type = "text";
        String userID = gmre.getAuthor().getId();

        if (TagTool.getTagByTag(tag) == null) {
            if (tagArray.add(new Tag(tag, reply, type, userID))) {
                writeTags();
                return 1;   // Successfully added new tag
            }
            return -1;  // Error adding tag
        }
        return -2;  // Tag already exists
    }

    public static int addImageTag(GuildMessageReceivedEvent gmre, String cmdString) {
        // check message has image
        if (gmre.getMessage().getAttachments().size() != 1 || !gmre.getMessage().getAttachments().get(0).isImage()) {
            return -1;  // Message doesn't contain image or contains multiple images.
        }

        // Get terms from message
        ArrayList<String> terms = RegexTool.getGroups("^(?is)ait (\\w+)$", cmdString);
        String tag = terms.get(0);
        String ext = gmre.getMessage().getAttachments().get(0).getFileExtension();
        String reply = "src/main/resources/tagData/" + tag + "." + ext;
        String userID = gmre.getAuthor().getId();
        String type = "image";

        // check if tag exists
        if (TagTool.getTagByTag(tag) == null) {
            if (tagArray.add(new Tag(tag, reply, type, userID))) {
                int result = ImageTool.downloadImageFromMessage(gmre, tag);
                if (result == 1) {
                    writeTags();
                    return 1;   // Successfully added new tag
                } else if (result == -1) {
                    System.out.println("Error downloading image");
                    return -4;
                }
            }
            return -2;  // Error adding tag
        }
        return -3;  // Tag already exists
    }

    public static int removeTag(String tagTag) {
//        if (FileTool.deleteFile(TagTool.getReplyByTag(tagTag)) == 1) {
        // If image tag remove image tag

        // Remove tag json information
        if (tagArray.remove(getTagByTag(tagTag))) {
            writeTags();
            return 1;   // Tag successfully removed
        }
        return -2;  // Tag not found
    }


    // TODO
    public static int updateTag(String tagTag) {
        return -1;
    }

    public static String getReplyByTag(String tagTag) {
        return getTagByTag(tagTag).getReply();
    }

    public static String getTypeByTag(String tagTag) {
        return getTagByTag(tagTag).getType();
    }

    private static Tag getTagByTag(String tagTag) {
        for (Tag tag : tagArray) {
            if (tag.getTag().equals(tagTag)) {
                return tag;
            }
        }
        return null;
    }
}



class Tag {
    private String tag;
    private String reply;
    private String type;
    private String creator;

    // For creating new tag
    public Tag(String tag, String reply, String type, String creator) {
        this.tag = tag;
        this.reply = reply;
        this.type = type;
        this.creator = creator;
    }

    public String getTag() {
        return tag;
    }

    public String getReply() {
        return reply;
    }
    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getType() {
        return type;
    }

    public String getCreator() {
        return creator;
    }

}