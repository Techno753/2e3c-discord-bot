package tools.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;

public class MyAudioLoadResultHandler implements AudioLoadResultHandler {
    public TrackScheduler ts;

    public MyAudioLoadResultHandler(TrackScheduler ts) {
        this.ts = ts;
    }

    public void trackLoaded(AudioTrack audioTrack) {
        ts.setLastQueuedTitle(audioTrack.getInfo().title);
        ts.queue(audioTrack);
    }

    public void playlistLoaded(AudioPlaylist audioPlaylist) {
        for (AudioTrack track : audioPlaylist.getTracks()) {
            ts.queue(track);
        }
    }

    public void noMatches() {
        System.out.println("No such audio");
        ts.setLastQueuedTitle(null);
    }

    public void loadFailed(FriendlyException e) {
        System.out.println("Things went bad");
        ts.setLastQueuedTitle(null);
    }
}
