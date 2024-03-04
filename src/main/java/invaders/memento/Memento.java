package invaders.memento;

import invaders.engine.GameEngine;
import invaders.entities.Player;
import invaders.factory.EnemyProjectile;
import invaders.factory.EnemyProjectileFactory;
import invaders.factory.PlayerProjectile;
import invaders.factory.Projectile;
import invaders.gameobject.Bunker;
import invaders.gameobject.Enemy;
import invaders.gameobject.GameObject;
import invaders.observer.ScoreSubject;
import invaders.observer.TimeSubject;
import invaders.physics.Vector2D;
import invaders.rendering.Renderable;
import invaders.state.BunkerState;
import invaders.state.GreenState;
import invaders.state.RedState;
import invaders.state.YellowState;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

public class Memento {
    private List<GameObject> gameObjects;
    private List<Renderable> renderables;
    private int timer, timeCount, score;
    private double playerX, playerY;

    /**
     * Constructor for momento, to save previous game states
     * @param gameObjects List of game objects
     * @param renderables List of renderables
     * @param timer The current timer in the game (controls rate of fire)
     * @param timeCount The current time count in the game (time is shown at the top)
     * @param player The player
     * @param score The current score in the game (score is shown at the top)
     */
    public Memento(List<GameObject> gameObjects, List<Renderable> renderables, int timer, int timeCount, Player player, int score) {

        this.gameObjects = new ArrayList<GameObject>();
        this.renderables = new ArrayList<Renderable>();

        for (GameObject go : gameObjects){
            if (go instanceof Bunker){
                Bunker bunker = copyBunker((Bunker) go);
                this.gameObjects.add(bunker);
                this.renderables.add(bunker);
            } else if (go instanceof Enemy){
                Enemy enemy = copyEnemy((Enemy) go);
                for (Projectile ep : enemy.getEnemyProjectile()){
                    this.gameObjects.add(ep);
                    this.renderables.add(ep);
                }
                this.gameObjects.add(enemy);
                this.renderables.add(enemy);
            } else if (go instanceof PlayerProjectile){
                PlayerProjectile playerProjectile = copyPlayerProjectile((PlayerProjectile) go);
                this.gameObjects.add(playerProjectile);
                this.renderables.add(playerProjectile);
            }
        }

        this.renderables.add(player);
        this.timer = timer;
        this.timeCount = timeCount;
        this.playerX = player.getPosition().getX();
        this.playerY = player.getPosition().getY();
        this.score = score;
    }

    /**
     * Copies a bunker for saving state
     * @param bunker The bunker to copy
     * @return A new bunker with no dependencies on the old bunker
     */
    public Bunker copyBunker(Bunker bunker){

        // creating new bunker to return
        Bunker newBunker = new Bunker();

        // creating position to set to bunker
        Vector2D position = new Vector2D(bunker.getPosition().getX(),bunker.getPosition().getY());

        // recording state of bunker
        newBunker.setPosition(position);
        newBunker.setWidth(bunker.getWidth());
        newBunker.setHeight(bunker.getHeight());
        newBunker.setLives(bunker.getLives());
        newBunker.setImage(bunker.getImage());
        if (bunker.getHealth() == 3){
            BunkerState state = new GreenState(newBunker);
            newBunker.setState(state);
        } else if (bunker.getHealth() == 2){
            BunkerState state = new YellowState(newBunker);
            newBunker.setState(state);
        } else if (bunker.getHealth() == 1){
            BunkerState state = new RedState(newBunker);
            newBunker.setState(state);
        }

        return newBunker;

    }

    /**
     * Copies a enemy for saving state
     * @param enemy The enemy to copy
     * @return A new enemy with no dependencies on the old enemy
     */
    public Enemy copyEnemy(Enemy enemy) {

        // initialising a position to remove dependency on old enemy
        Vector2D position = new Vector2D(enemy.getPosition().getX(),enemy.getPosition().getY());

        // creating new enemy to return
        Enemy newEnemy = new Enemy(position);

        // copying enemy state
        newEnemy.setLives(enemy.getLives());
        ArrayList<Projectile> enemyProjectile = new ArrayList<Projectile>();
        for (Projectile ep : enemy.getEnemyProjectile()){
            enemyProjectile.add(copyEnemyProjectile((EnemyProjectile) ep));
        }
        newEnemy.setEnemyProjectile(enemyProjectile);
        newEnemy.setImage(enemy.getImage());
        newEnemy.setProjectileStrategy(enemy.getProjectileStrategy());
        newEnemy.setProjectileImage(enemy.getProjectileImage());
        newEnemy.setXVel(enemy.getXVel());

        return newEnemy;

    }

    /**
     * Copies an enemy projectile for saving state
     * @param enemyProjectile The enemy projectile to copy
     * @return A new enemy projectile with no dependencies on the old one
     */
    public EnemyProjectile copyEnemyProjectile(EnemyProjectile enemyProjectile){

        Vector2D position = new Vector2D(enemyProjectile.getPosition().getX(),enemyProjectile.getPosition().getY());
        return new EnemyProjectile(position, enemyProjectile.getStrategy(), enemyProjectile.getImage());

    }

    /**
     * Copies a player projectile for saving state
     * @param playerProjectile The player projectile to copy
     * @return A new player projectile with no dependencies on the old one
     */
    public PlayerProjectile copyPlayerProjectile(PlayerProjectile playerProjectile){

        Vector2D position = new Vector2D(playerProjectile.getPosition().getX(),playerProjectile.getPosition().getY());
        return new PlayerProjectile(position, playerProjectile.getStrategy());

    }

    public List<GameObject> getGameObjects() {
        return gameObjects;
    }

    public List<Renderable> getRenderables() {
        return renderables;
    }

    public int getTimer() {
        return timer;
    }

    public int getTimeCount() {
        return timeCount;
    }

    public double getPlayerX() {
        return playerX;
    }

    public double getPlayerY() {
        return playerY;
    }

    public int getScore() {
        return score;
    }
}
