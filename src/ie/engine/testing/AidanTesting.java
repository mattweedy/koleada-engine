package ie.engine.testing;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PShapeSVG.Font;
import ie.engine.Scene;
import ie.engine.implementations.LyricEventLL;
import ie.engine.implementations.ParticleSystem;
import ie.engine.implementations.LyricEventLL.LyricEvent;
import ie.engine.interaction.AudioSync;
import ie.engine.interaction.LyricSync;
import ie.engine.loading.SongInfo;
import ie.engine.maths.Animation;
import ie.engine.maths.Coordinate;
import ie.engine.maths.KeyFrame;
import ie.engine.objects.Waves;

public class AidanTesting extends Scene {
    PFont myFont;
    public Waves wavey;

    public void setup() {
        super.setup();
        String songName = "assets/audio/songs/nrgq.wav";
        audioSync = new AudioSync(this, songName);
        songInfo = new SongInfo(songName);

        lastEvent = audioSync.songInfo.eventList.blank;

        KeyFrame[] frames = new KeyFrame[4];
        frames[0] = new KeyFrame(0, new Coordinate(0, 0, 0));
        frames[1] = new KeyFrame(100000, new Coordinate(15, 15, 15));
        frames[2] = new KeyFrame(600000, new Coordinate(10, 10, 10));
        frames[3] = new KeyFrame(1000000, new Coordinate(20, 20, 20));

        lyricSync = new LyricSync(this, audioSync);

        testAnimation = new Animation(4, frames);
        ps = new ParticleSystem(new Coordinate(width / 4f, height / 2f), this);
        ps2 = new ParticleSystem(new Coordinate((3 * width) / 4f, height / 2f), this);
        wavey = new Waves(this);
        audioSync.play();
        lyricSync.listOfEvents.clear();
        myFont = createFont("assets/fonts/Manrope-Regular.ttf", 100, true);
        textFont(myFont);
        debugDictionary.put("bp", new DebugObject<>("bp.process()", 0, ""));
        debugDictionary.put("bp2", new DebugObject<>("bp.processSin()", 0, ""));
        // jumpTo(3214854);

    }

    ParticleSystem ps;
    ParticleSystem ps2;

    LyricSync lyricSync;
    LyricEvent tempLyricEvent;
    float lerpValue;
    Animation testAnimation;
    float smoothBackground;
    float smoothTubes;
    boolean wasLyric;

    public void pre() {
        lyricSync.lyricLL.updateScale();
        if (w != width || h != height) {
            w = width;
            h = height;
        }
    }

    @Override
    public void draw() {
        pre();
        super.draw();
        // clear();
        camera();
        if (currentFrame > 4288737) {
            background(map(currentFrame, 4288737, AudioSync.songParts.CHORUS1.get(), 0, 255));
        } else {
            background(0, 0, smoothBackground);
        }
        tempLyricEvent = lyricSync.isLyric();
        wasLyric = lyricSync.wasLyric;
        tempEvent = audioSync.isBeat();
        float sinLoop = bp.processSin();
        float regLoop = bp.process();

        wasBeat = audioSync.wasBeat;
        wavey.draw();
        noStroke();
        angle += 0.002f;

        smoothTubes = lerp(smoothTubes, (tempEvent.volume * 1000) + 5, 0.005f);
        // intro
        if (currentFrame < AudioSync.songParts.VERSE1.get()) {
            lightning(width / 2f, height / 2f, 10, map(currentFrame, 0, AudioSync.songParts.VERSE1.get(), 0, 100),
                    cos(degrees(angle)), true);
            storm(width / 4f, height / 2, 15, map(currentFrame, 0, AudioSync.songParts.VERSE1.get(), 0, 100),
                    sin(degrees(angle)), true, 1, false);
            storm((width * 3) / 4f, height / 2, 15, map(currentFrame, 0, AudioSync.songParts.VERSE1.get(), 0, 100),
                    sin(degrees(angle)), true, 0, true);

            // verse
        } else if (currentFrame < AudioSync.songParts.BRIDGE1.get()) {
            lightning(width / 2f, height / 2f, 10, width / 2f * sin(bp.process()), cos(degrees(angle)), true);
            storm(width / 4f, height / 2f, 15, map(currentFrame, 0, AudioSync.songParts.VERSE1.get(), 0, 100),
                    sin(degrees(angle)), true, smoothTubes, false);
            storm((width * 3) / 4f, height / 2f, 15, map(currentFrame, 0, AudioSync.songParts.VERSE1.get(), 0, 100),
                    sin(degrees(angle)), true, smoothTubes, true);
            // bridge
        } else if (currentFrame < AudioSync.songParts.CHORUS1.get()) {
            pushMatrix();
            stroke(255);
            float circleMap = map(currentFrame, AudioSync.songParts.BRIDGE1.get(), AudioSync.songParts.CHORUS1.get(), 0,
                    width);
            if (currentFrame > 4288737) {
                circleMap = map(currentFrame, 4288737, AudioSync.songParts.CHORUS1.get(), width, 0);
            }
            for (int i = 1; i < (int) (circleMap / 10); i++) {
                fill((circleMap) % 255, (circleMap * i) % 255, (circleMap * (i + 1)) % 255);
                circle(width / 2f, height / 2f, (circleMap / i) + (regLoop * i));

            }
            popMatrix();
            // chorus
        } else if (currentFrame < AudioSync.songParts.VERSE2.get()) {

        }
        if (wasBeat) {
            lastEvent = tempEvent;

            // for(int i = 0; i < 4; i++){
            // // ps.addParticle(new Coordinate(0, 10f * i, 0), new Coordinate(0.5f, 0, 5f),
            // new Coordinate(5, 5, 5));
            // // ps2.addParticle(new Coordinate(0, 10f * i, 0), new Coordinate(-0.5f, 0,
            // 5f), new Coordinate(-5, 5, 5));

            // }
        }
        wavey.update(wasBeat);
        ps.run();
        ps2.run();
        camera();
        hint(DISABLE_DEPTH_TEST);
        noLights();
        // backwards traversal to allow active removal
        for (int i = lyricSync.listOfEvents.size() - 1; i >= 0; i--) {
            LyricEvent lEvent = lyricSync.listOfEvents.get(i);
            // println(lEvent + " dying at: " + lEvent.timeToDie);
            // check if the frame has died yet
            if (currentFrame > lEvent.timeToDie) {
                lyricSync.listOfEvents.remove(lEvent);
            } else {

                if (currentFrame > 1283965) {

                    lEvent.draw(sinLoop);
                } else {
                    lEvent.draw(1);
                }
            }
        }

        if (debugger.doDebug) {
            debugDictionary.get("frametime").updateValue(debugger.getFrameTime());
            debugDictionary.get("framerate").updateValue(frameRate);
            debugDictionary.get("avgbeattime")
                    .updateValue((((float) audioSync.averageBeatTime) / (float) (songInfo.sampleRate)));
            debugDictionary.get("currentframe").updateValue(audioSync.song.positionFrame());
            debugDictionary.get("lastframe").updateValue(lastEvent.frame);
            debugDictionary.get("nextframe").updateValue(audioSync.peekNext().frame);
            debugDictionary.get("bp").updateValue(regLoop);
            debugDictionary.get("bp2").updateValue(sinLoop);
            // System.out.println(sinLoop);

            debugger.draw();
        }
    }

    float angle = 0;

    public void lightning(float originX, float originY, int n, float posX, float posY, boolean firstLayer) {
        for (int i = 0; i < n; i++) {
            pushMatrix();
            if (firstLayer) {
                translate(originX, originY);
                scale(bp.process() / 5);
            } else {
                translate(posX, posY, 0);
            }
            fill(map(i, 0, n, 0, 255), 255, 255);
            rotateY(TAU * i / n);
            rotateZ(PI / 12);
            box(10, 100, 10);
            if (i == 1)
                lightning(originX, originY, n - 1, posX, posY, false);
            popMatrix();
        }
    }

    public void storm(float originX, float originY, int n, float posX, float posY, boolean firstLayer, float volume,
            boolean flip) {
        if (firstLayer) {
            pushMatrix();
            translate(originX, originY);
            rotateX(-45);
            if (flip) {
                rotateY(180);
            }
        }
        for (int i = 0; i < n; i++) {
            pushMatrix();
            fill(255, map(i, 0, n, 0, 255), 255);
            rotateY(TAU * i / n);
            // (flip ? 1 :-1 ) *
            rotateZ(PI / 12);
            translate(posX, 100 + volume, volume);
            box(10, 100, 10);
            if (i == 1)
                storm(originX, originY, n - 1, posX, posY, false, (volume * 9f) / 10f, flip);
            popMatrix();
        }
        if (firstLayer) {
            popMatrix();
        }
    }

    public void keyPressed() {
        if (key == 'n') {
            debugger.doDebug = !debugger.doDebug;
        }
        if (key == 'p') {
            jumpTo(lyricSync.lyricLL.peek().frame - 100);
        }
    }

    public void jumpTo(int positionFrame) {
        audioSync.song.jumpFrame(positionFrame);
        while (audioSync.song.positionFrame() < audioSync.peekNext().frame) {
            tempEvent = songInfo.eventList.pop();
        }
        smoothBackground = 0;
    }

    static String[] gameArgs = { "Main" };

    public static void main(String[] args) {
        PApplet.runSketch(gameArgs, new AidanTesting());
    }
}
