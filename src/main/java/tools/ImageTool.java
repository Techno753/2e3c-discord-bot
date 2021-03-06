package tools;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.io.File;

/**
 * Manages the download and sending of images for the bot
 */
public final class ImageTool {

    // Download image by Message Received Event
    public static int downloadImageFromMessage(GuildMessageReceivedEvent gmre, String tagTag) {
        Message.Attachment img = gmre.getMessage().getAttachments().get(0);
        if (img.getSize() < 8388120) {
            String imgSaveDir = "src/main/resources/tagData/" + tagTag + "." + img.getFileExtension();
            if (FileTool.downloadFileByURL(img.getUrl(), imgSaveDir) == 1) {
                return 1;
            }
        }
        return -1;
    }

    // Uploads an image to channel/server by Message Received Event
    public static int uploadImageAsReply(GuildMessageReceivedEvent gmre, String src, String type) {
        // get file
        File image = FileTool.getLocalFile(src);

        // check for null image
        if (image.exists()) {
            gmre.getChannel().sendFile(image, image.getName()).queue();
            System.out.println("Image uploaded");
            return 1;
        }
        System.out.println("Image doesn't exist");
        return -1;  // Image doesn't exist
    }
}
