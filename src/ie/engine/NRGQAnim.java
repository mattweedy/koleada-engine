package ie.engine;

import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.*;

import ie.engine.implementations.AudioEventLL;
import ie.engine.implementations.PenroseLSystem;
import ie.engine.implementations.Synchronization;
import ie.engine.interaction.*;
import ie.engine.loading.SongInfo;
import ie.engine.maths.*;
import ie.engine.testing.AidanTesting;
import ie.engine.testing.BenTesting;
import ie.engine.testing.KamilaTesting;
import ie.engine.testing.TweedyNS;


public class NRGQAnim extends Scene {

    enum GameState {
        SPLASH,
        MENU,
        RUNNING,
        EXIT
    }

    Menu menu;
    GameState state;

    public void setupMenu() {
        // button coords
        Coordinate buttonSize = new Coordinate((2f * width) / 3f, height / 6f);
        Coordinate startBtnCoord = new Coordinate(width / 6f, height / 16f);
        Coordinate quitBtnCoord = new Coordinate(width / 6f, buttonSize.y + startBtnCoord.y + height / 6f);
        Coordinate creditBtnCoord = new Coordinate(width / 6f, buttonSize.y + quitBtnCoord.y + height / 6f);

        menu = new Menu(this);
        menu.createMenuObject(Menu.MenuChoice.START, "Start Game", startBtnCoord, buttonSize);
        menu.createMenuObject(Menu.MenuChoice.QUIT, "Quit Game", quitBtnCoord, buttonSize);
        menu.createMenuObject(Menu.MenuChoice.CREDITS, "Credits", creditBtnCoord, buttonSize);

        benCode = new BenTesting();
        tweedyCode = new TweedyNS();
        aidanCode = new AidanTesting();
        kamilaCode = new KamilaTesting();
        benCode.g = g;
        tweedyCode.g = g;
        aidanCode.g = g;
        kamilaCode.g = g;
        benCode.setSurface(surface);
        tweedyCode.setSurface(surface);
        aidanCode.setSurface(surface);
        kamilaCode.setSurface(surface);
        aidanCode.platform = platform;

    }
    BenTesting benCode;
    TweedyNS tweedyCode;
    AidanTesting aidanCode;
    KamilaTesting kamilaCode;
    boolean benRan;
    boolean tweedyRan;
    boolean kamilaRan;
    boolean aidanRan;

    @Override
    public void setup() {
        super.setup();

        state = GameState.MENU;
    }

    // TODO check all things in here are being created and recycled before creation
    // ALL USES
    boolean menuCreated = false;
    boolean doCleanGame = false;
    boolean wasBeat = false;
    public void startAnim(){
        String songName = "assets/audio/songs/nrgq.wav";
        audioSync = new AudioSync(this, songName);
        audioSync.play();
        songInfo = new SongInfo(songName);
        lastEvent = audioSync.songInfo.eventList.blank;
    }
    @Override
    public void draw() {
        super.draw();
        clear();
        switch (state) {
            case SPLASH:
                break;
            case MENU:
                if (doCleanGame) {
                    doCleanGame = false;
                } else {
                    if (menuCreated) {
                        menu.draw();
                        switch (menu.output) {
                            case START:
                                state = GameState.RUNNING;
                                menuCreated = false;
                                menu = null;
                                startAnim();
                                break;
                            case QUIT:
                                state = GameState.EXIT;
                                menuCreated = false;
                                break;
                            case CREDITS:
                                break;
                            case UNSELECTED:
                                break;
                        }
                    } else {
                        setupMenu();
                        menuCreated = true;
                    }
                }
                break;

            case RUNNING:
                if(currentFrame < AudioSync.songParts.CHORUS1.get()){
                    if(!aidanRan){
                        aidanRan = true;

                    }
                    
                    pushMatrix();
                    pushStyle();
                    colorMode(RGB);
                    aidanCode.draw();
                    popStyle();
                    popMatrix();
                } else if (currentFrame < AudioSync.songParts.BRIDGE1.get()){
                    pushMatrix();
                    pushStyle();
                    colorMode(HSB);
                    benCode.draw();
                    popStyle();
                    popMatrix();
                } else if(currentFrame < AudioSync.songParts.BRIDGE2.get()){
                    pushMatrix();
                    pushStyle();
                    kamilaCode.draw();
                    popStyle();

                    popMatrix();
                } else {
                    pushMatrix();
                    pushStyle();

                    tweedyCode.draw();
                    popStyle();

                    popMatrix();
                }
                
                break;
            case EXIT:
                exit();
                break;
        }

    }
}
