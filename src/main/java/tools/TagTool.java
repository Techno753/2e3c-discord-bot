package tools;

import net.dv8tion.jda.api.JDA;
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

    public static int addTag(String tagTag, String tagReply, String tagType, String tagCreator, JDA jda) {
        if (TagTool.getTagByTag(tagTag) == null) {
            if (tagArray.add(new Tag(tagTag, tagReply, tagType, tagCreator))) {
                writeTags();
                return 1;   // Successfully added new server to config
            }
            return -1;  // Error adding server data
        }
        return -2;  // Server data already exists in config
    }

    public static int removeTag(String tagTag) {
        if (tagArray.remove(getTagByTag(tagTag))) {
            writeTags();
            return 1;
        }
        return -1;
    }

    // TODO
    public static int updateTag(String tagTag) {
        return -1;
    }

    public static String getReplyByTag(String tagTag) {
        for (Tag t : tagArray) {
            if (t.getTag().equals(tagTag)) {
                return t.getReply();
            }
        }
        return null;
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