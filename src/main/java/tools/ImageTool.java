package tools;

import net.dv8tion.jda.api.entities.EmbedType;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.RichPresence;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.utils.data.DataObject;

import java.io.File;
import java.util.List;

/**
 * Manages the download and sending of images for the bot
 */
public final class ImageTool {

    // Download image by Message Received Event
    public static int downloadImageFromMessage(GuildMessageReceivedEvent gmre, String tagTag) {
        for (Message.Attachment a : gmre.getMessage().getAttachments()) {
            if (gmre.getMessage().getAttachments().size() == 1) {
                if (a.isImage()) {
                    String imgSaveDir = "src/main/resources/tagData/" + tagTag + "." + a.getFileExtension();
                    FileTool.downloadImageByURL(a.getUrl(), imgSaveDir);
                    return 1;
                } return -3;    // Error downloading image
            }
            return -2;  // Message contains multiple attachments
        }
        return -1;  // Message doesn't have image attachment



//        List<Message.Attachment> attachs = gmre.getMessage().getAttachments();
//        DataObject image = gmre.getMessage().getEmbeds().get(0).toData();
//        Message.Attachment a = attachs.get(0);
//        a.
    }


    // Uploads an image to channel/server by Message Received Event
    public static int uploadImageAsReply(GuildMessageReceivedEvent gmre, String src, String type) {
        // get file
        File image = FileTool.getLocalImage(src);

        // check for null image
        gmre.getChannel().sendFile(image, image.getName()).queue();
        return 1;
    }
}
