package invaders.engine;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import invaders.ConfigReader;
import invaders.builder.BunkerBuilder;
import invaders.builder.Director;
import invaders.builder.EnemyBuilder;
import invaders.factory.EnemyProjectile;
import invaders.factory.PlayerProjectile;
import invaders.factory.Projectile;
import invaders.gameobject.Bunker;
import invaders.gameobject.Enemy;
import invaders.gameobject.GameObject;
import invaders.entities.Player;
import invaders.memento.Memento;
import invaders.observer.ScoreSubject;
import invaders.observer.TimeSubject;
import invaders.rendering.Renderable;
import invaders.strategy.FastProjectileStrategy;
import invaders.strategy.SlowProjectileStrategy;
import org.json.simple.JSONObject;

/**
 * This class manages the main loop and logic of the game
 */
public class GameEngine {
	private List<GameObject> gameObjects = new ArrayList<>(); // A list of game objects that gets updated each frame
	private List<GameObject> pendingToAddGameObject = new ArrayList<>();
	private List<GameObject> pendingToRemoveGameObject = new ArrayList<>();
	private List<Renderable> pendingToAddRenderable = new ArrayList<>();
	private List<Renderable> pendingToRemoveRenderable = new ArrayList<>();

	private List<Renderable> renderables =  new ArrayList<>();
	private Player player;
	private boolean left;
	private boolean right;
	private int gameWidth;
	private int gameHeight;
	private int timer = 45;
	private int timeCount = 0;
	private int score = 0;
	private ScoreSubject scoreSubject;
	private TimeSubject timeSubject;
	private boolean once = false;
	private boolean deleteAll = false;
	private boolean deleteSlowProjectile = false;
	private boolean deleteFastProjectile = false;
	private boolean deleteSlowAlien = false;
	private boolean deleteFastAlien = false;
	private boolean onceSlowProjectile = false;
	private boolean onceFastProjectile = false;
	private boolean onceSlowAlien = false;
	private boolean onceFastAlien = false;


	public GameEngine(String config){

		// Read the config here
		ConfigReader.parse(config);

		// Get game width and height
		gameWidth = ((Long)((JSONObject) ConfigReader.getGameInfo().get("size")).get("x")).intValue();
		gameHeight = ((Long)((JSONObject) ConfigReader.getGameInfo().get("size")).get("y")).intValue();

		//Get player info
		this.player = new Player(ConfigReader.getPlayerInfo());
		renderables.add(player);


		Director director = new Director();
		BunkerBuilder bunkerBuilder = new BunkerBuilder();
		//Get Bunkers info
		for(Object eachBunkerInfo:ConfigReader.getBunkersInfo()){
			Bunker bunker = director.constructBunker(bunkerBuilder, (JSONObject) eachBunkerInfo);
			gameObjects.add(bunker);
			renderables.add(bunker);
		}


		EnemyBuilder enemyBuilder = new EnemyBuilder();
		//Get Enemy info
		for(Object eachEnemyInfo:ConfigReader.getEnemiesInfo()){
			Enemy enemy = director.constructEnemy(this,enemyBuilder,(JSONObject)eachEnemyInfo);
			gameObjects.add(enemy);
			renderables.add(enemy);
		}

	}

	/**
	 * Updates the game/simulation
	 */
	public void update(){

		timer+=1;
		timeCount+=1;

		if (timeCount % 120 == 0){
			timeSubject.setTime(timeCount/120);
		}

		movePlayer();

		for(GameObject go: gameObjects){
			go.update(this);
		}

		for (int i = 0; i < renderables.size(); i++) {
			Renderable renderableA = renderables.get(i);
			for (int j = i+1; j < renderables.size(); j++) {
				Renderable renderableB = renderables.get(j);

				if((renderableA.getRenderableObjectName().equals("Enemy") && renderableB.getRenderableObjectName().equals("EnemyProjectile"))
						||(renderableA.getRenderableObjectName().equals("EnemyProjectile") && renderableB.getRenderableObjectName().equals("Enemy"))||
						(renderableA.getRenderableObjectName().equals("EnemyProjectile") && renderableB.getRenderableObjectName().equals("EnemyProjectile"))){
				}else{
					if(renderableA.isColliding(renderableB) && (renderableA.getHealth()>0 && renderableB.getHealth()>0)) {
						renderableA.takeDamage(1);
						renderableB.takeDamage(1);
						// if an alien is destroyed, update score accordingly
						if (renderableA instanceof Enemy && renderableB instanceof PlayerProjectile){
							if (((Enemy) renderableA).getProjectileStrategy() instanceof FastProjectileStrategy){
								score += 4;
								scoreSubject.updateScore(score);
							} else if (((Enemy) renderableA).getProjectileStrategy() instanceof SlowProjectileStrategy) {
								score += 3;
								scoreSubject.updateScore(score);
							}
						}
						// if a projectile is destroyed, update score accordingly
						if (renderableA instanceof EnemyProjectile && renderableB instanceof PlayerProjectile){
							if (((EnemyProjectile) renderableA).getStrategy() instanceof FastProjectileStrategy){
								score += 2;
								scoreSubject.updateScore(score);
							} else if (((EnemyProjectile) renderableA).getStrategy() instanceof SlowProjectileStrategy) {
								score += 1;
								scoreSubject.updateScore(score);
							}
						}
					}
				}
			}
		}


		// ensure that renderable foreground objects don't go off-screen
		int offset = 1;
		for(Renderable ro: renderables){
			if(!ro.getLayer().equals(Renderable.Layer.FOREGROUND)){
				continue;
			}
			if(ro.getPosition().getX() + ro.getWidth() >= gameWidth) {
				ro.getPosition().setX((gameWidth - offset) -ro.getWidth());
			}

			if(ro.getPosition().getX() <= 0) {
				ro.getPosition().setX(offset);
			}

			if(ro.getPosition().getY() + ro.getHeight() >= gameHeight) {
				ro.getPosition().setY((gameHeight - offset) -ro.getHeight());
			}

			if(ro.getPosition().getY() <= 0) {
				ro.getPosition().setY(offset);
			}
		}

	}

	public List<Renderable> getRenderables(){
		return renderables;
	}

	public List<GameObject> getGameObjects() {
		return gameObjects;
	}
	public List<GameObject> getPendingToAddGameObject() {
		return pendingToAddGameObject;
	}

	public List<GameObject> getPendingToRemoveGameObject() {
		return pendingToRemoveGameObject;
	}

	public List<Renderable> getPendingToAddRenderable() {
		return pendingToAddRenderable;
	}

	public List<Renderable> getPendingToRemoveRenderable() {
		return pendingToRemoveRenderable;
	}


	public void leftReleased() {
		this.left = false;
	}

	public void rightReleased(){
		this.right = false;
	}

	public void leftPressed() {
		this.left = true;
	}

	public void rightPressed(){
		this.right = true;
	}

	public boolean shootPressed(){
		if(timer>45 && player.isAlive()){
			Projectile projectile = player.shoot();
			gameObjects.add(projectile);
			renderables.add(projectile);
			timer=0;
			return true;
		}
		return false;
	}

	private void movePlayer(){
		if(left){
			player.left();
		}

		if(right){
			player.right();
		}
	}

	public int getGameWidth() {
		return gameWidth;
	}

	public int getGameHeight() {
		return gameHeight;
	}

	public Player getPlayer() {
		return player;
	}

	/**
	 * Sets the local score subject to which the GameEngine will send updates to
	 * @param scoreSubject The score subject
	 */
	public void setScoreSubject(ScoreSubject scoreSubject){
		this.scoreSubject = scoreSubject;
	}

	/**
	 * Sets the local time subject to which the GameEngine will send updates to
	 * @param timeSubject The time subject
	 */
	public void setTimeSubject(TimeSubject timeSubject){
		this.timeSubject = timeSubject;
	}

	/**
	 * Creates a memento to return to, triggered by pressing 'Z'
	 * @return The memento saving the game state
	 */
	public Memento setMemento() {
		once = true;
		return new Memento(gameObjects, renderables, timer, timeCount, player, score);
	}

	public boolean getDeleteAll() {
		return deleteAll;
	}

	public void setDeleteAll(){
		this.deleteAll = false;
	}

	/**
	 * Restores the game state to a previous state stored, triggered upon pressing 'X'
	 * @param memento The state to set the game too
	 */
	public void restoreMemento(Memento memento) {

		// only do it once per save
		if (!once){
			return;
		}

		// if no memento, don't do it
		if (memento == null){
			return;
		}

		// removing all current objects
		pendingToRemoveGameObject.addAll(this.gameObjects);
		pendingToRemoveRenderable.addAll(this.renderables);
		deleteAll = true;

		// updating important variables such as time, score etc.
		this.timer = memento.getTimer();
		this.timeCount = memento.getTimeCount();
		timeSubject.setTime(timeCount / 120);
		this.score = memento.getScore();
		scoreSubject.updateScore(score);

		// adding all previous objects
		pendingToAddGameObject.addAll(memento.getGameObjects());
		pendingToAddRenderable.addAll(memento.getRenderables());

		// resetting player
		player.getPosition().setX(memento.getPlayerX());
		player.getPosition().setY(memento.getPlayerY());

		// can only undo once per save
		once = false;

	}

	/**
	 * Deletes all slow projectiles from the game, when 'A' is pressed
	 */
	public void deleteAllSlowProjectiles(){

		// only done once per game
		if(onceSlowProjectile){
			return;
		}

		// making array lists to mark what to delete
		ArrayList<GameObject> goToDelete = new ArrayList<>();
		ArrayList<Renderable> roToDelete = new ArrayList<>();

		// adding the objects to be deleted and clearing all slow alien projectiles
		for (GameObject go : gameObjects){
			if (go instanceof EnemyProjectile){
				if (((EnemyProjectile)go).getStrategy() instanceof SlowProjectileStrategy){
					goToDelete.add(go);
					roToDelete.add((Renderable) go);
					score += 1;
				}
			}
			if (go instanceof Enemy){
				if (((Enemy)go).getProjectileStrategy() instanceof SlowProjectileStrategy){
					((Enemy) go).getEnemyProjectile().clear();
				}
			}
		}

		// updating score
		scoreSubject.updateScore(score);

		// adding all objects to be removed
		pendingToRemoveGameObject.addAll(goToDelete);
		pendingToRemoveRenderable.addAll(roToDelete);

		// only done one per game
		deleteSlowProjectile = true;
		onceSlowProjectile = true;

	}

	/**
	 * Deletes all fast projectiles from the game, when 'S' is pressed
	 */
	public void deleteAllFastProjectiles(){

		// only done once per game
		if(onceFastProjectile){
			return;
		}

		// making array lists to mark what to delete
		ArrayList<GameObject> goToDelete = new ArrayList<>();
		ArrayList<Renderable> roToDelete = new ArrayList<>();

		// adding the objects to be deleted and clearing all fast alien projectiles
		for (GameObject go : gameObjects){
			if (go instanceof EnemyProjectile){
				if (((EnemyProjectile)go).getStrategy() instanceof FastProjectileStrategy){
					goToDelete.add(go);
					roToDelete.add((Renderable) go);
					score += 2;
				}
			}
			if (go instanceof Enemy){
				if (((Enemy)go).getProjectileStrategy() instanceof FastProjectileStrategy){
					((Enemy) go).getEnemyProjectile().clear();
				}
			}
		}

		// updating score
		scoreSubject.updateScore(score);

		// adding all objects to be removed
		pendingToRemoveGameObject.addAll(goToDelete);
		pendingToRemoveRenderable.addAll(roToDelete);

		// only done one per game
		deleteFastProjectile = true;
		onceFastProjectile = true;

	}

	/**
	 * Deletes all slow aliens from the game, when 'D' is pressed
	 */
	public void deleteAllSlowAliens(){

		// only done once per game
		if(onceSlowAlien){
			return;
		}

		// making array lists to mark what to delete
		ArrayList<GameObject> goToDelete = new ArrayList<>();
		ArrayList<Renderable> roToDelete = new ArrayList<>();

		// adding the objects to be deleted and clearing all slow aliens
		for (GameObject go : gameObjects){
			if (go instanceof Enemy){
				if (((Enemy)go).getProjectileStrategy() instanceof SlowProjectileStrategy){
					goToDelete.add(go);
					roToDelete.add((Renderable) go);
					score += 3;
				}
			}
		}

		// updating score
		scoreSubject.updateScore(score);

		// adding all objects to be removed
		pendingToRemoveGameObject.addAll(goToDelete);
		pendingToRemoveRenderable.addAll(roToDelete);

		// only done one per game
		deleteSlowAlien = true;
		onceSlowAlien = true;

	}

	/**
	 * Deletes all fast aliens from the game, when 'F' is pressed
	 */
	public void deleteAllFastAliens(){

		// only done once per game
		if(onceFastAlien){
			return;
		}

		// making array lists to mark what to delete
		ArrayList<GameObject> goToDelete = new ArrayList<>();
		ArrayList<Renderable> roToDelete = new ArrayList<>();

		// adding the objects to be deleted and clearing all fast aliens
		for (GameObject go : gameObjects){
			if (go instanceof Enemy){
				if (((Enemy)go).getProjectileStrategy() instanceof FastProjectileStrategy){
					goToDelete.add(go);
					roToDelete.add((Renderable) go);
					score += 4;
				}
			}
		}

		// updating score
		scoreSubject.updateScore(score);

		// adding all objects to be removed
		pendingToRemoveGameObject.addAll(goToDelete);
		pendingToRemoveRenderable.addAll(roToDelete);

		// only done one per game
		deleteFastAlien = true;
		onceFastAlien = true;

	}

	public boolean getDeleteSlowProjectile() {
		return deleteSlowProjectile;
	}

	public boolean getDeleteFastProjectile() {
		return deleteFastProjectile;
	}

	public boolean getDeleteSlowAlien() {
		return deleteSlowAlien;
	}

	public boolean getDeleteFastAlien() {
		return deleteFastAlien;
	}

	public void setDeleteSlowProjectile() {
		this.deleteSlowProjectile = false;
	}

	public void setDeleteFastProjectile() {
		this.deleteFastProjectile = false;
	}

	public void setDeleteSlowAlien() {
		this.deleteSlowAlien = false;
	}

	public void setDeleteFastAlien() {
		this.deleteFastAlien = false;
	}
}