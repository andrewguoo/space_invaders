package invaders.engine;

import java.util.List;
import java.util.ArrayList;

import invaders.entities.EntityViewImpl;
import invaders.entities.Player;
import invaders.entities.SpaceBackground;
import invaders.factory.EnemyProjectile;
import invaders.gameobject.Enemy;
import invaders.memento.Caretaker;
import invaders.observer.ScoreObserver;
import invaders.observer.TimeObserver;
import invaders.strategy.FastProjectileStrategy;
import invaders.strategy.SlowProjectileStrategy;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

import invaders.entities.EntityView;
import invaders.rendering.Renderable;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

public class GameWindow implements ScoreObserver, TimeObserver {
	private final int width;
    private final int height;
	private Scene scene;
    private Pane pane;
    private GameEngine model;
    private List<EntityView> entityViews =  new ArrayList<EntityView>();
    private Renderable background;
    private double xViewportOffset = 0.0;
    private double yViewportOffset = 0.0;
    // private static final double VIEWPORT_MARGIN = 280.0;
    private Label scoreLabel, timeLabel, gameEndLabel;
    private int score = 0;
    private int time = 0;
    private int timeMinutes, timeSeconds;
    private boolean gameWon = false;
    private boolean gameLost = false;
    private int enemyCount;
    private KeyboardInputHandler keyboardInputHandler;
    private Caretaker caretaker = new Caretaker();

	public GameWindow(GameEngine model){
        this.model = model;
		this.width =  model.getGameWidth();
        this.height = model.getGameHeight();

        pane = new Pane();
        scene = new Scene(pane, width, height);
        this.background = new SpaceBackground(model, pane);

        // displaying score
        scoreLabel = new Label("Score: " + this.score);
        scoreLabel.setFont(Font.font("Comic Sans MS", 20));
        scoreLabel.setTextFill(Color.WHITE);
        scoreLabel.setLayoutX(width - 104);
        scoreLabel.setLayoutY(15);
        pane.getChildren().add(scoreLabel);

        // displaying time
        timeMinutes = time / 60;
        timeSeconds = time % 60;
        String formattedSeconds = String.format("%02d", time % 60);
        timeLabel = new Label("Time: " + this.timeMinutes + ":" + formattedSeconds);
        timeLabel.setFont(Font.font("Comic Sans MS", 20));
        timeLabel.setTextFill(Color.WHITE);
        timeLabel.setLayoutX(15);
        timeLabel.setLayoutY(15);
        pane.getChildren().add(timeLabel);

        // displaying game end
        gameEndLabel = new Label("");
        gameEndLabel.setFont(Font.font("Comic Sans MS", 20));
        gameEndLabel.setTextFill(Color.WHITE);
        gameEndLabel.setLayoutX(width / 2 - 90);
        gameEndLabel.setLayoutY(height / 2 - 14);
        pane.getChildren().add(gameEndLabel);

        keyboardInputHandler = new KeyboardInputHandler(this.model);
        keyboardInputHandler.setCaretaker(caretaker);

        scene.setOnKeyPressed(keyboardInputHandler::handlePressed);
        scene.setOnKeyReleased(keyboardInputHandler::handleReleased);

    }

	public void run() {
         Timeline timeline = new Timeline(new KeyFrame(Duration.millis(17), t -> this.draw()));

         timeline.setCycleCount(Timeline.INDEFINITE);
         timeline.play();
    }


    private void draw(){
        model.update();

        List<Renderable> renderables = model.getRenderables();
        for (Renderable entity : renderables) {
            boolean notFound = true;
            for (EntityView view : entityViews) {
                if (view.matchesEntity(entity)) {
                    notFound = false;
                    view.update(xViewportOffset, yViewportOffset);
                    break;
                }
            }
            if (notFound) {
                EntityView entityView = new EntityViewImpl(entity);
                entityViews.add(entityView);
                pane.getChildren().add(entityView.getNode());
            }
        }

        // checking game end conditions
        enemyCount = 0;
        for (Renderable entity : renderables){
            if (!entity.isAlive()){
                for (EntityView entityView : entityViews){
                    if (entityView.matchesEntity(entity)){
                        entityView.markForDelete();
                    }
                }
            }
            // if player has no lives, end game
            if (entity instanceof Player){
                if (entity.getHealth() == 0){
                    gameLost = true;
                }
            }
            // counting alive enemies
            if (entity instanceof Enemy){
                if (entity.getHealth() > 0){
                    enemyCount += 1;
                }
            }
        }

        // if no enemies left, end game
        if (enemyCount == 0){
            gameWon = true;
        }

        // if game is over, delete everything
        if (gameWon || gameLost){
            for (EntityView entityView : entityViews){
                entityView.markForDelete();
            }
        }

        // if memento is restored, delete everything
        if (model.getDeleteAll()){
            for (EntityView entityView : entityViews){
                entityView.markForDelete();
            }
            model.setDeleteAll();
        }

        // deleting all slow projectiles on button 'A' press
        if (model.getDeleteSlowProjectile()){
            for (EntityView entityView : entityViews){
                Renderable ev = ((EntityViewImpl)entityView).getEntity();
                if (ev instanceof EnemyProjectile) {
                    if (((EnemyProjectile)ev).getStrategy() instanceof SlowProjectileStrategy){
                        entityView.markForDelete();
                    }
                }
            }
            model.setDeleteSlowProjectile();
        }

        // deleting all fast projectiles on button 'S' press
        if (model.getDeleteFastProjectile()){
            for (EntityView entityView : entityViews){
                Renderable ev = ((EntityViewImpl)entityView).getEntity();
                if (ev instanceof EnemyProjectile) {
                    if (((EnemyProjectile)ev).getStrategy() instanceof FastProjectileStrategy){
                        entityView.markForDelete();
                    }
                }
            }
            model.setDeleteFastProjectile();
        }

        // deleting all slow aliens  on button 'D' press
        if (model.getDeleteSlowAlien()){
            for (EntityView entityView : entityViews){
                Renderable ev = ((EntityViewImpl)entityView).getEntity();
                if (ev instanceof Enemy) {
                    if (((Enemy)ev).getProjectileStrategy() instanceof SlowProjectileStrategy){
                        entityView.markForDelete();
                    }
                }
            }
            model.setDeleteSlowAlien();
        }

        // deleting all fast aliens  on button 'F' press
        if (model.getDeleteFastAlien()){
            for (EntityView entityView : entityViews){
                Renderable ev = ((EntityViewImpl)entityView).getEntity();
                if (ev instanceof Enemy) {
                    if (((Enemy)ev).getProjectileStrategy() instanceof FastProjectileStrategy){
                        entityView.markForDelete();
                    }
                }
            }
            model.setDeleteFastAlien();
        }


        for (EntityView entityView : entityViews) {
            if (entityView.isMarkedForDelete()) {
                pane.getChildren().remove(entityView.getNode());
            }
        }

        // if game is over, show appropriate message
        if (gameLost){
            showGameEnd("lost");
        }
        if (gameWon){
            showGameEnd("won");
        }

        model.getGameObjects().removeAll(model.getPendingToRemoveGameObject());
        model.getGameObjects().addAll(model.getPendingToAddGameObject());
        model.getRenderables().removeAll(model.getPendingToRemoveRenderable());
        model.getRenderables().addAll(model.getPendingToAddRenderable());

        model.getPendingToAddGameObject().clear();
        model.getPendingToRemoveGameObject().clear();
        model.getPendingToAddRenderable().clear();
        model.getPendingToRemoveRenderable().clear();

        entityViews.removeIf(EntityView::isMarkedForDelete);

    }

	public Scene getScene() {
        return scene;
    }

    @Override
    public void updateScore(int totalScore) {
        score = totalScore;
        scoreLabel.setText("Score: " + score);
    }
    @Override
    public void updateTime(int newTime) {

        if (gameWon || gameLost){
            return;
        }

        time = newTime;
        timeMinutes = time / 60;
        timeSeconds = time % 60;
        String formattedSeconds = String.format("%02d", timeSeconds);
        timeLabel.setText("Time: " + this.timeMinutes + ":" + formattedSeconds);
    }

    /**
     * Shows a message at the end of the game to signal game end
     * @param end The context in which the game ended
     */
    public void showGameEnd(String end){
        if (end.equals("won")){
            gameEndLabel.setText("Game over, you win!");
        } else {
            gameEndLabel.setText("Game over, you lost");
        }
    }

}
