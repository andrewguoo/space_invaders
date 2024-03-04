package invaders;

import invaders.observer.ScoreSubject;
import invaders.observer.TimeSubject;
import invaders.singleton.DifficultySingleton;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import invaders.engine.GameEngine;
import invaders.engine.GameWindow;

public class App extends Application {
    private DifficultySingleton difficultySingleton = DifficultySingleton.getInstance();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        // creating and showing difficulty select screen
        Stage difficultyStage = new Stage();
        Scene difficultyScene = getDifficultyScene(difficultyStage);
        difficultyStage.setScene(difficultyScene);
        difficultyStage.setTitle("Space Invaders");
        difficultyStage.showAndWait();

        // if none is selected (i.e. they close the window), then abort the game
        if (difficultySingleton.getCurrentDifficulty().equals("none")){
            return;
        }

        // setting score subjects and subscribing the game window
        ScoreSubject scoreSubject = new ScoreSubject();
        TimeSubject timeSubject = new TimeSubject();
        GameEngine model = new GameEngine("src/main/resources/config_" + difficultySingleton.getCurrentDifficulty() + ".json");
        model.setScoreSubject(scoreSubject);
        model.setTimeSubject(timeSubject);
        GameWindow window = new GameWindow(model);
        scoreSubject.attach(window);
        timeSubject.attach(window);
        window.run();

        primaryStage.setTitle("Space Invaders");
        primaryStage.setScene(window.getScene());
        primaryStage.show();

        window.run();
    }

    /**
     * This method creates the first window for the user in which they will select the difficulty
     * @param stage The stage the scene will be built on
     * @return The scene built for user interaction
     */
    private Scene getDifficultyScene(Stage stage) {

        // default difficulty is none, game will not launch
        difficultySingleton.setDifficulty("none");

        // using vbox to stack the buttons on top of each other
        VBox vBox = new VBox();
        vBox.setStyle("-fx-background-color: grey;");
        Scene scene = new Scene(vBox, 300, 300);

        // creating funky select difficulty text lol
        Label difficultySelectText = new Label("Please select a difficulty :");
        difficultySelectText.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 20));
        Stop[] stops = new Stop[] {
                new Stop(0, javafx.scene.paint.Color.RED),
                new Stop(0.2, javafx.scene.paint.Color.ORANGE),
                new Stop(0.4, javafx.scene.paint.Color.YELLOW),
                new Stop(0.6, javafx.scene.paint.Color.GREEN),
                new Stop(0.8, javafx.scene.paint.Color.BLUE),
                new Stop(1, javafx.scene.paint.Color.PURPLE)
        };
        difficultySelectText.setTextFill(new LinearGradient(0, 0, 1, 0, true, null, stops));

        // creating funky pink buttons
        Button easyButton = new Button("Easy");
        easyButton.setStyle("-fx-text-fill: pink; -fx-background-color: grey; -fx-border-color: hotpink; -fx-border-width: 1px; -fx-font-family: 'Comic Sans MS';");
        Button mediumButton = new Button("Medium");
        mediumButton.setStyle("-fx-text-fill: pink; -fx-background-color: grey; -fx-border-color: hotpink; -fx-border-width: 1px; -fx-font-family: 'Comic Sans MS';");
        Button hardButton = new Button("Hard");
        hardButton.setStyle("-fx-text-fill: pink; -fx-background-color: grey; -fx-border-color: hotpink; -fx-border-width: 1px; -fx-font-family: 'Comic Sans MS';");

        // defining button behaviour, every button sets the difficulty and closes the window
        easyButton.setOnAction(e -> {
            difficultySingleton.setDifficulty("easy");
            stage.close();
        });
        mediumButton.setOnAction(e -> {
            difficultySingleton.setDifficulty("medium");
            stage.close();
        });
        hardButton.setOnAction(e -> {
            difficultySingleton.setDifficulty("hard");
            stage.close();
        });

        // finalising the vbox
        vBox.getChildren().addAll(difficultySelectText, easyButton, mediumButton, hardButton);
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(20);

        // returning created scene
        return scene;
    }

}
