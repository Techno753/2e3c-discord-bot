package tools.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.entities.Guild;
import tools.AudioTool;

import java.util.concurrent.*;

public class TrackScheduler extends AudioEventAdapter {
    private final AudioPlayer player;
    private final BlockingQueue<AudioTrack> queue;
    private String lastQueuedTitle;
    private Guild g;

    // constructor
    public TrackScheduler(AudioPlayer player) {
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
        this.lastQueuedTitle = "NOT SET";
    }

    public void setGuild(Guild g) {
        this.g = g;
    }

    // If nothing is playing then play track
    // else add track to queue
    public void queue(AudioTrack track) {
        if (!player.startTrack(track, true)) {
            queue.offer(track);
        }
    }

    public void setLastQueuedTitle(String s){
        lastQueuedTitle = s;
    }

    public String getLastQueuedTitle() {
        return lastQueuedTitle;
    }

    public void nextTrack() {
        AudioTrack nt = queue.poll();
        if (nt != null) {
            player.startTrack(nt, false);
        }
    }

    public void skip() {
        try {
            queue.poll(3, TimeUnit.SECONDS);
        } catch (Exception e) {
            System.out.println("Error skipping song(s)");
        }
    }

    public void clear() {
        player.stopTrack();
        queue.clear();
    }

    public BlockingQueue<AudioTrack> getQueue() {
        return queue;
    }

    // If the track ended normally then play next track
    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        // if song ended and none left in queue
        if (queue.size() == 0) {

            // perform another check after 10 minutes
            ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);

            exec.schedule(new Runnable() {
                public void run() {
                    if (queue.size() == 0) {
                        System.out.println("5 seconds without playing anything. Disconnecting.");
                        AudioTool.disconnectFromVC(g);
                    }
                }
            }, 10, TimeUnit.MINUTES);
        } else if (endReason.mayStartNext) {
            System.out.println("???");
            nextTrack();
        }
    }
}
