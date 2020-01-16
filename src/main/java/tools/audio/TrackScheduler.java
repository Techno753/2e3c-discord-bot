package tools.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import tools.YTTool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class TrackScheduler extends AudioEventAdapter {
    private final AudioPlayer player;
    private final BlockingQueue<AudioTrack> queue;
    private String lastQueuedTitle;

    // constructor
    public TrackScheduler(AudioPlayer player) {
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
        this.lastQueuedTitle = "NOT SET";
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
        player.startTrack(nt, false);
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
        if (endReason.mayStartNext) {
            nextTrack();
        }
    }
}
