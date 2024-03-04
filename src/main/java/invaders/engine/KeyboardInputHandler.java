package invaders.engine;

import invaders.memento.Caretaker;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class KeyboardInputHandler {
    private final GameEngine model;
    private boolean left = false;
    private boolean right = false;
    private Set<KeyCode> pressedKeys = new HashSet<>();

    private Map<String, MediaPlayer> sounds = new HashMap<>();
    private Caretaker caretaker;

    KeyboardInputHandler(GameEngine model) {
        this.model = model;

        // TODO (longGoneUser): Is there a better place for this code?
        URL mediaUrl = getClass().getResource("/shoot.wav");
        String jumpURL = mediaUrl.toExternalForm();

        Media sound = new Media(jumpURL);
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        sounds.put("shoot", mediaPlayer);
    }

    void handlePressed(KeyEvent keyEvent) {
        if (pressedKeys.contains(keyEvent.getCode())) {
            return;
        }
        pressedKeys.add(keyEvent.getCode());

        if (keyEvent.getCode().equals(KeyCode.SPACE)) {
            if (model.shootPressed()) {
                MediaPlayer shoot = sounds.get("shoot");
                shoot.stop();
                shoot.play();
            }
        }

        if (keyEvent.getCode().equals(KeyCode.LEFT)) {
            left = true;
        }
        if (keyEvent.getCode().equals(KeyCode.RIGHT)) {
            right = true;
        }
        if (keyEvent.getCode().equals(KeyCode.Z)) {
            // 'Z' is to save state
            caretaker.setPreviousState(model.setMemento());
        }
        if (keyEvent.getCode().equals(KeyCode.X)) {
            // 'X' is to reload previous state
            model.restoreMemento(caretaker.getPreviousState());
        }
        if (keyEvent.getCode().equals(KeyCode.A)) {
            // 'A' is to delete all slow projectiles
            model.deleteAllSlowProjectiles();
        }
        if (keyEvent.getCode().equals(KeyCode.S)) {
            // 'S' is to delete all fast projectiles
            model.deleteAllFastProjectiles();
        }
        if (keyEvent.getCode().equals(KeyCode.D)) {
            // 'D' is to delete all slow aliens
            model.deleteAllSlowAliens();
        }
        if (keyEvent.getCode().equals(KeyCode.F)) {
            // 'F' is to delete all fast aliens
            model.deleteAllFastAliens();
        }

        if (left) {
            model.leftPressed();
        }

        if(right){
            model.rightPressed();
        }
    }

    void handleReleased(KeyEvent keyEvent) {
        pressedKeys.remove(keyEvent.getCode());

        if (keyEvent.getCode().equals(KeyCode.LEFT)) {
            left = false;
            model.leftReleased();
        }
        if (keyEvent.getCode().equals(KeyCode.RIGHT)) {
            model.rightReleased();
            right = false;
        }
    }

    /**
     * Sets the local caretaker within the context of this class, so that previous states can be handled on button press
     * @param caretaker The caretaker to be set
     */
    public void setCaretaker(Caretaker caretaker){
        this.caretaker = caretaker;
    }
}
