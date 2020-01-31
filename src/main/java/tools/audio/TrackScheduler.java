package tools.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.dv8tion.jda.api.entities.Guild;
import tools.AudioTool;
import tools.TimeTool;

import java.util.concurrent.*;

public class TrackScheduler extends AudioEventAdapter {
    private final AudioPlayer player;
    private final BlockingQueue<AudioTrack> queue;
    private String lastQueuedTitle;
    private String lastQueuedType;
    private Guild g;
    boolean hasPlayedSince = false;

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

        TimeTool.printTime();
        System.out.println("Adding new song\n");
        hasPlayedSince = true;
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
            TimeTool.printTime();
            System.out.println("Playing next track");
            hasPlayedSince = true;
            System.out.println(nt.getInfo().title);
            System.out.println(nt.getDuration() + "\n");
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
            TimeTool.printTime();
            System.out.println("No song playing and none left in queue. Checking again in 10 seconds\n");
            hasPlayedSince = false;

            // perform another check after 10 minutes
            ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
            exec.schedule(new Runnable() {
                public void run() {
                    TimeTool.printTime();
                    System.out.println("Song has been played in last 10 seconds: " + hasPlayedSince);
                    System.out.println("Song is currently playing: " + (player.getPlayingTrack().getInfo().title));
                    System.out.println("Songs left in queue: " + queue.size() + "\n");
                    if (hasPlayedSince == false && player.getPlayingTrack() == null && queue.size() == 0) {
                        System.out.println("10 mins without playing anything. Disconnecting.\n");
                        AudioTool.disconnectFromVC(g);
                    }
                }
            }, 10, TimeUnit.SECONDS);

        } else if (endReason.mayStartNext) {
            nextTrack();
        }
    }
}
