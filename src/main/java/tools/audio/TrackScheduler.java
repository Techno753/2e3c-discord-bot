package tools.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.entities.Guild;
import tools.AudioTool;
import tools.TimeTool;

import java.sql.Time;
import java.time.ZonedDateTime;
import java.util.concurrent.*;

public class TrackScheduler extends AudioEventAdapter {
    private final AudioPlayer player;
    private final BlockingQueue<AudioTrack> queue;
    private String lastQueuedTitle;
    private String lastQueuedType;

    private Guild g;
    boolean hasPlayedSince = false;
    ZonedDateTime lastPlayedTime = null;

    // constructor
    public TrackScheduler(AudioPlayer player) {
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
        this.lastQueuedTitle = "NOT SET";
        this.lastQueuedType = "NOT SET";
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
        hasPlayedSince = true;
        lastPlayedTime = TimeTool.getTime(); //time.now
    }

    public void setQueuedTitle(String s){
        lastQueuedTitle = s;
    }

    public String getQueuedTitle() {
        return lastQueuedTitle;
    }

    public void setQueuedType(String s){
        lastQueuedType = s;
    }

    public String getQueuedType(){
        return lastQueuedType;
    }

    public void nextTrack() {
        AudioTrack nt = queue.poll();
        if (nt != null) {
            player.startTrack(nt, false);

            hasPlayedSince = true;
            lastPlayedTime = TimeTool.getTime();
        }
    }

    public void skip() {
        try {
            queue.poll();
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
        if (queue.size() == 0 && player.getPlayingTrack() == null) {
            hasPlayedSince = false;

            // perform another check after 10 minutes
            ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
            exec.schedule(new Runnable() {
                public void run() {
//                    if (hasPlayedSince == false && player.getPlayingTrack() == null && queue.size() == 0 &&
//                            TimeTool.getTime().compareTo(lastPlayedTime.plusMinutes(5)) >= 0) {
                    if (hasPlayedSince == false && TimeTool.getTime().compareTo(lastPlayedTime.plusMinutes(10)) >= 0) {
                        AudioTool.disconnectFromVC(g);
                    }
                }
            }, 10, TimeUnit.MINUTES);

        } else if (endReason.mayStartNext) {
            nextTrack();
        }
    }
}
