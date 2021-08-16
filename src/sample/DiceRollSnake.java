package sample;

import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

import java.awt.*;
import java.io.*;
import java.util.*;

public class DiceRollSnake extends Application {

    public int rand;
    public Label randResult;

    public Label status;
    public Label snakeOrLadder;

    public Snake snakePos[][] = new Snake[10][10];
    public Ladder ladderPos[][] = new Ladder[10][10];

    public static final int Tile_Size = 65;
    public static final int Width = 10;
    public static final int Height = 10;

    public Circle player1;
    public Circle player2;

    public int playerPosition1 = 1;
    public int playerPosition2 = 1;

    public boolean player1Turn = false;
    public boolean player2Turn = false;

    public boolean ladderTouched = false;
    public boolean snakeTouched = false;

    public static double player1XPos = Tile_Size/2.0;
    public static double player1YPos = (Tile_Size*10)-Tile_Size/2.0;

    public static double player2XPos = Tile_Size/2.0;
    public static double player2YPos = (Tile_Size*10)-Tile_Size/2.0;

    int player1RelX = 0;
    int player1RelY = 0;

    int player2RelX = 0;
    int player2RelY = 0;

    public static int posCir1 = 1;
    public static int posCir2 = 1;

    int player1Rolls = 0;
    int player2Rolls = 0;
    int winnerRolls = 0;

    String player1Name = "Player 1";
    String player2Name = "Player 2";
    String winnerName;

    int num = 1;
    HashMap<Tile, Integer> tileNumbers = new HashMap<>();

    LinkedList<Record> recordList = new LinkedList<>();

    int b = 100;
    int c = 0;

    public boolean gameStart = false;

    Button gameButton = new Button("Start Game");

    private Group tileGroup = new Group();

    private Parent createMenu(Stage primaryStage) {
        Pane root = new Pane();
        root.setPrefSize(Width*Tile_Size, (Height*Tile_Size)+80);

        Button start = new Button("Play");
        Button exit = new Button("Exit");
        Button fastestWinners = new Button("Fastest Winners");

        Image menuBg = new Image("sample/images/menu-bg2.png");
        ImageView bgImage = new ImageView();
        bgImage.setImage(menuBg);

        start.setStyle("-fx-font-size:24px; padding: 10px 80px");
        exit.setStyle("-fx-font-size:24px; padding: 10px 80px");
        fastestWinners.setStyle("-fx-font-size:24px; padding: 10px 80px");

        start.setTranslateX(265);
        start.setTranslateY(300);

        fastestWinners.setTranslateX(200);
        fastestWinners.setTranslateY(400);

        exit.setTranslateX(265);
        exit.setTranslateY(500);

        start.setId("button-two");
        fastestWinners.setId("button-two");
        exit.setId("button-two");

        start.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Scene gameBoard = new Scene(createContent());
                gameBoard.getStylesheets().add("sample/styles.css");
                primaryStage.setScene(gameBoard);
            }
        });

        fastestWinners.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Scene fastestWins = new Scene(showFastestWinners(primaryStage));
                fastestWins.getStylesheets().add("sample/styles.css");
                primaryStage.setScene(fastestWins);
            }
        });

        exit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.exit(0);
            }
        });

        root.getChildren().addAll(bgImage, start, exit, fastestWinners);

        return root;
    }

    private Parent showFastestWinners(Stage primaryStage) {
        VBox winners = new VBox();
        Label heading = new Label("Fastest Winners");

        heading.setStyle("-fx-font-size: 24px; -fx-font-family: Langar;");
        winners.getChildren().add(heading);
        winners.getChildren().add(new Label());

        int num = 1;

        for(Record r:recordList) {
            Label rec = new Label(num+". "+r.toString());
            rec.setId("winnerRecord");
            num++;
            rec.setStyle("-fx-font-size: 24px;");
            winners.getChildren().add(rec);
            winners.getChildren().add(new Label("-------------------------------------------------------------------------------------"));
        }
        Button back = new Button("Back");
        back.setId("button-two");
        back.setStyle("-fx-font-size: 24px;");
        back.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Scene menu = new Scene(createMenu(primaryStage));
                menu.getStylesheets().add("sample/styles.css");
                primaryStage.setScene(menu);
            }
        });

        winners.getChildren().add(back);
        return winners;
    }

    private Parent createContent() {
        Pane root = new Pane();
        root.setPrefSize(Width*Tile_Size, (Height*Tile_Size)+80);
        root.getChildren().add(tileGroup);

        for(int i = 0; i < Height; i++) {
            for(int j = 0; j < Width; j++) {
                Tile tile = new Tile(Tile_Size, Tile_Size);
                tile.setTranslateX(j * Tile_Size);
                tile.setTranslateY(i * Tile_Size);

                tile.i = i;
                tile.j = j;

                if(i%2 == 0) {
                    System.out.print(((10-i-1)*10+10-j)+" ");
                    tileNumbers.put(tile, ((10-i-1)*10+10-j));
                }
                else {
                    System.out.print(((10-i-1)*10)+(1+j)+" ");
                    tileNumbers.put(tile, ((10-i-1)*10)+(1+j));
                }
                tileGroup.getChildren().add(tile);
                
            }
            System.out.println();
        }

        Circle player1 = new Circle(32.5);
        player1.setId("player1");
        player1.getStyleClass().add("sample/style.css");
        player1.setTranslateX(player1XPos);
        player1.setTranslateY(player1YPos);
        Image player1Skin = new Image("sample/images/token1.png");
        player1.setFill(new ImagePattern(player1Skin));

        Circle player2 = new Circle(32.5);
        player2.setId("player2");
        player2.getStyleClass().add("sample/style.css");
        player2.setTranslateX(player2XPos);
        player2.setTranslateY(player2YPos);
        Image player2Skin = new Image("sample/images/token2.png");
        player2.setFill(new ImagePattern(player2Skin));

        Button rollPlayer1 = new Button("Move");
        Button rollPlayer2 = new Button("Move");

        rollPlayer1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(gameStart) {
                    if(player1Turn) {
                        getDiceRoll();
                        snakeOrLadder.setText("");
                        randResult.setText("Dice Roll: "+String.valueOf(rand));

                        movePlayer1();

                        player1RelX = (int)(player1XPos/Tile_Size - 0.5);
                        player1RelY = (int)(player1YPos/Tile_Size - 0.5);
                        translatePlayer(player1XPos, player1YPos, player1);
                        player1Rolls++;

                        if((player1RelY < 0 || player1RelX < 0) || (player1RelY == 0 && player1RelX == 0)) {
                            translatePlayer(32.5, 32.5, player1);
                            status.setText(player1Name+" Wins!!!");
                            status.setTextFill(Color.GREEN);
                            gameStart = false;
                            winnerRolls = player1Rolls;
                            playMusic("C:\\Users\\scs\\Documents\\IdeaProjects\\snakeAndLadders\\src\\sample\\sound\\victory.wav");
                            try {
                                showPopup(player1Name);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            return;
                        }

                        int currTileNum = getTileNumber(player1RelY, player1RelX);
                        Tile currTile = getTileXY(currTileNum);
                        System.out.println(currTile.i+" "+currTile.j);

                        Ladder l = ladderPos[currTile.i][currTile.j];
                        Snake s = snakePos[currTile.i][currTile.j];

                        if(l!=null) {
                            System.out.println(player1Name+" got ladder at "+currTileNum);
                            snakeOrLadder.setText(player1Name+" got ladder at tile "+currTileNum);
                            snakeOrLadder.setTextFill(Color.GREEN);
                            translatePlayer(l.getX(), l.getY(), player1);
                            posCir1 = (int)(10-l.y);
                            player1XPos = l.getX();
                            player1YPos = l.getY();
                        }
                        if(s!=null) {
                            System.out.println(player1Name+" got bit at "+currTileNum);
                            snakeOrLadder.setText(player1Name+" got bit at tile "+currTileNum);
                            snakeOrLadder.setTextFill(Color.CRIMSON);
                            translatePlayer(s.getX(), s.getY(), player1);
                            posCir1 = (int)(10-s.y);
                            player1XPos = s.getX();
                            player1YPos = s.getY();
                        }

                        player1Turn = false;
                        player2Turn = true;
                        status.setText("Turn: "+player2Name+" (RED)");
                        status.setStyle("-fx-text-fill:red;");

                        int tNum = getTileNumber(player1RelY, player1RelX);
                        System.out.println(player1Name+": ("+player1RelX+", "+player1RelY+") Abs:("+player1XPos+", "+player1YPos+") posCir1: "+posCir1+" tile: "+tNum);
                    }
                    else {
                        outOfTurn(player1Name);
                    }
                }
            }
        });

        rollPlayer2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(player2Turn) {
                    getDiceRoll();
                    snakeOrLadder.setText("");
                    randResult.setText("Dice Roll: "+String.valueOf(rand));
                    movePlayer2();
                    player2RelX = (int)(player2XPos/Tile_Size - 0.5);
                    player2RelY = (int)(player2YPos/Tile_Size - 0.5);
                    translatePlayer(player2XPos, player2YPos, player2);
                    player2Rolls++;

                    if((player2RelY < 0 || player2RelX < 0) || (player2RelY == 0 && player2RelX == 0)) {
                        translatePlayer(32.5, 32.5, player2);
                        status.setText(player2Name+" Wins!!!");
                        status.setTextFill(Color.GREEN);
                        gameStart = false;
                        winnerRolls = player2Rolls;
                        playMusic("C:\\Users\\scs\\Documents\\IdeaProjects\\snakeAndLadders\\src\\sample\\sound\\victory.wav");
                        try {
                            showPopup(player2Name);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return;
                    }

                    int currTileNum = getTileNumber(player2RelY, player2RelX);
                    Tile currTile = getTileXY(currTileNum);
                    System.out.println(currTile.i+" "+currTile.j);

                    Ladder l = ladderPos[currTile.i][currTile.j];
                    Snake s = snakePos[currTile.i][currTile.j];

                    if(l!=null) {
                        System.out.println(player2Name+" got ladder at tile "+currTileNum);
                        snakeOrLadder.setText(player2Name+" got ladder at tile "+currTileNum);
                        snakeOrLadder.setTextFill(Color.GREEN);
                        translatePlayer(l.getX(), l.getY(), player2);
                        posCir2 = (int)(10-l.y);
                        player2XPos = l.getX();
                        player2YPos = l.getY();
                        player2RelX = (int) l.x;
                        player2RelY = (int) l.y;

                    }
                    if(s!=null) {
                        System.out.println(player2Name+" got bit at tile "+currTileNum);
                        snakeOrLadder.setText(player2Name+" got bit at tile "+currTileNum);
                        snakeOrLadder.setTextFill(Color.CRIMSON);
                        translatePlayer(s.getX(), s.getY(), player2);
                        posCir2 = (int)(10-s.y);
                        player2XPos = s.getX();
                        player2YPos = s.getY();
                        player2RelX = (int) s.x;
                        player2RelY = (int) s.y;
                    }

//                    if(ladderTouched) {
//                        translatePlayer(player2XPos, player2YPos, player2);
//                        posCir2 = 10-player2RelY;
//                        System.out.println(player2Name+" got ladder at tile "+getTileNumber(player2RelY, player2RelX));
//                        ladderTouched = false;
//                    }
//                    if(snakeTouched) {
//                        translatePlayer(player2XPos, player2YPos, player2);
//                        posCir2 = 10-player2RelY;
//                        System.out.println(player2Name+" got bit");
//                        snakeTouched = false;
//                    }

                    player2Turn = false;
                    player1Turn = true;
                    status.setText("Turn: "+player1Name+" (BLUE)");
                    status.setStyle("-fx-text-fill:blue;");


                    int tNum = getTileNumber(player2RelY, player2RelX);
                    System.out.println(player2Name+": ("+player2RelX+", "+player2RelY+") Abs:("+player2XPos+", "+player2YPos+") posCir2: "+posCir2+" tile: "+tNum);
                }
                else {
                    outOfTurn(player2Name);
                }
            }
        });

        Button rollDice = new Button("Roll");
        rollDice.setId("button-one");
        rollDice.setTranslateX(1500);
        rollDice.setTranslateY(6750);

        rollPlayer1.setTranslateX(1500);
        rollPlayer1.setTranslateY(6750);

        rollPlayer2.setTranslateX(1500);
        rollPlayer2.setTranslateY(6750);

        rollPlayer1.setId("button-blue");
        rollPlayer2.setId("button-three");

        gameButton.setTranslateY(660);
        gameButton.setTranslateX(230);
        gameButton.setId("button-one");
        gameButton.setStyle("-fx-font-size: 24px;");
        gameButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                player1XPos = Tile_Size/2.0;
                player1YPos = (Tile_Size*10)-Tile_Size/2.0;

                player2XPos = Tile_Size/2.0;
                player2YPos = (Tile_Size*10)-Tile_Size/2.0;

                player1.setTranslateX(player1XPos);
                player1.setTranslateY(player1YPos);
                player2.setTranslateX(player2XPos);
                player2.setTranslateY(player2YPos);

                randResult.setText("Dice Roll: ");

                gameButton.setTranslateX(2000);
                gameButton.setTranslateY(6750);
                gameStart = true;

                rollDice.setTranslateX(275);
                rollDice.setTranslateY(670);

                rollPlayer1.setTranslateX(130);
                rollPlayer1.setTranslateY(670);

                rollPlayer2.setTranslateX(500);
                rollPlayer2.setTranslateY(670);

                getPlayer1Name();
                getPlayer2Name();

                insertLadders();
                insertSnakes();
                toss();
            }
        });

        randResult = new Label("");
        randResult.setTranslateX(32.5);
        randResult.setTranslateY(675);
        randResult.setId("diceRoll");

        status = new Label("");
        status.setTranslateX(290);
        status.setTranslateY(670);
        status.setId("status");

        snakeOrLadder = new Label("");
        snakeOrLadder.setTranslateX(290);
        snakeOrLadder.setTranslateY(695);
        snakeOrLadder.setId("diceRoll");

        Image img = new Image("sample/images/board.jpg");
        ImageView bgImage = new ImageView();
        bgImage.setImage(img);
        bgImage.setFitHeight(670);
        bgImage.setFitWidth(670);

        bgImage.setTranslateX(-14.5);
        bgImage.setTranslateY(-11);
        tileGroup.getChildren().addAll(bgImage, player1, player2, rollPlayer1, rollPlayer2, gameButton, randResult, status, snakeOrLadder);

        return root;
    }

    public void getDiceRoll() {
        rand = (int) (Math.random()*6+1);
    }

    public void insertLadders() {
        ladderPos[9][1] = new Ladder(2, 7);
        ladderPos[9][5] = new Ladder(4, 5);
        ladderPos[8][0] = new Ladder(1, 4);
        ladderPos[4][3] = new Ladder(4, 0);
        ladderPos[4][8] = new Ladder(8, 2);
        ladderPos[2][9] = new Ladder(8, 0);
    }

    public void insertSnakes() {
        snakePos[5][2] = new Snake(3, 8);
        snakePos[5][9] = new Snake(4, 9);
        snakePos[4][4] = new Snake(7, 9);
        snakePos[2][7] = new Snake(5, 8);
        snakePos[1][3] = new Snake(2, 4);
        snakePos[1][6] = new Snake(8, 5);
        snakePos[0][2] = new Snake(0, 6);
    }

    public void toss() {
        double randomInt = Math.random();
        if(randomInt < 0.5) {
            player1Turn = true;
            status.setText("Turn: "+player1Name+" (BLUE)");
            status.setTextFill(Color.BLUE);
        }
        else {
            player2Turn = true;
            status.setText("Turn: "+player2Name+" (RED)");
            status.setTextFill(Color.RED);
        }
    }

    public void movePlayer1() {
        for(int i = 0; i < rand; i++) {
            if(posCir1 % 2 == 1) {
                player1XPos+=Tile_Size;
            }
            if(posCir1 % 2 == 0) {
                player1XPos-=Tile_Size;
            }
            if(player1XPos > (Tile_Size*10 - Tile_Size/2)) {
                player1YPos-=Tile_Size;
                player1XPos-=Tile_Size;
                posCir1++;
            }
            if(player1XPos < Tile_Size/2) {
                player1YPos-=Tile_Size;
                player1XPos+=Tile_Size;
                posCir1++;
            }
        }
    }

    public void movePlayer2() {
        for(int i = 0; i < rand; i++) {
            if(posCir2 % 2 == 1) {
                player2XPos+=Tile_Size;
            }
            if(posCir2 % 2 == 0) {
                player2XPos-=Tile_Size;
            }
            if(player2XPos > (Tile_Size*10 - Tile_Size/2)) {
                player2YPos-=Tile_Size;
                player2XPos-=Tile_Size;
                posCir2++;
            }
            if(player2XPos < Tile_Size/2) {
                player2YPos-=Tile_Size;
                player2XPos+=Tile_Size;
                posCir2++;
            }

        }
    }

    public int getTileNumber(int i, int j) {
        Iterator tIterator = tileNumbers.entrySet().iterator();

        while (tIterator.hasNext()) {
            Map.Entry mapElement = (Map.Entry)tIterator.next();
            Tile tile = (Tile) mapElement.getKey();
            if(tile.i == i && tile.j == j)
                return tileNumbers.get(tile);
        }
        return -1;
    }

    public Tile getTileXY(int number) {
        Iterator tIterator = tileNumbers.entrySet().iterator();

        while (tIterator.hasNext()) {
            Map.Entry mapElement = (Map.Entry)tIterator.next();
            int tileNum = (int) mapElement.getValue();
            Tile tile = (Tile) mapElement.getKey();
            if(tileNum == number)
                return tile;
        }
        return null;
    }

    public void translatePlayer(double x, double y, Circle C) {
        TranslateTransition animate = new TranslateTransition(Duration.millis(250), C);
        animate.setToX(x);
        animate.setToY(y);
        animate.setAutoReverse(false);
        animate.play();
    }


    public void outOfTurn(String whoMoves) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Out of turn");
        alert.setHeaderText("Its not your turn");
        if(whoMoves.equals(player1Name))
            alert.setContentText("This is currently "+player2Name+"'s turn, RED's turn");
        else if(whoMoves.equals(player2Name))
            alert.setContentText("This is currently "+player1Name+"'s turn, BLUE's turn");
        alert.showAndWait();
    }

    public void playMusic(String filePath) {
        InputStream music;

        try{
            music = new FileInputStream(filePath);
            AudioStream audios = new AudioStream(music);
            AudioPlayer.player.start(audios);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void showPopup(String whoWon) throws IOException {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Congratulations!");
        alert.setHeaderText(null);
        alert.setContentText(whoWon+" won in "+winnerRolls+" dice rolls");

        winnerName = whoWon;
        insertRecord(new Record(winnerName, winnerRolls));
        sortRecords();
        updateRecords();

        alert.showAndWait();
    }

    public void getPlayer1Name() {
        TextInputDialog textInput = new TextInputDialog();
        textInput.setTitle("Player 1 please enter your name");
        textInput.setHeaderText("Player 1 (BLUE)");
        textInput.getDialogPane().setContentText("Player 1 enter your name ");
        Optional<String> result = textInput.showAndWait();
        if(result.isPresent())
            player1Name = result.toString().substring(result.toString().indexOf("[")+1, result.toString().indexOf("]"));
        else
            player1Name = "Player 1";
        TextField input = textInput.getEditor();
    }

    public void getPlayer2Name() {
        TextInputDialog textInput = new TextInputDialog();
        textInput.setTitle("Player 2 please enter your name");
        textInput.setHeaderText("Player 2 (RED)");
        textInput.getDialogPane().setContentText("Player 2 enter your name ");
        Optional<String> result = textInput.showAndWait();
        if(result.isPresent())
            player2Name = result.toString().substring(result.toString().indexOf("[")+1, result.toString().indexOf("]"));
        else
            player2Name = "Player 2";
        TextField input = textInput.getEditor();
    }

    public void createRecords() throws IOException {
        File records = new File("src//sample//records.txt");
        if(records.createNewFile()) {
            System.out.println("records file created");
        }
        else {
            System.out.println("records file already exists");
        }
    }

    public void updateRecords() throws IOException {
        BufferedWriter recWriter = new BufferedWriter(new FileWriter("src//sample//records.txt"));
        recWriter.write("");
        for(Record record:recordList) {
            recWriter.write(record.playerName+"-"+record.rolls);
            recWriter.newLine();
        }
        recWriter.close();
    }

    public void readRecords() throws FileNotFoundException {
        File records = new File("src//sample//records.txt");
        Scanner recReader = new Scanner(records);
        while (recReader.hasNextLine()) {
            String line = recReader.nextLine();
            HashMap<String, Integer> playerRecord = new HashMap<>();

            playerRecord.put(line.substring(0, line.indexOf("-")), Integer.parseInt(line.substring(line.indexOf("-")+1)));

            String playerName = (String) playerRecord.keySet().toArray()[0];
            int playerRolls = (int) playerRecord.values().toArray()[0];

            Record record = new Record(playerName, playerRolls);
            recordList.add(record);
        }
        sortRecords();
        recReader.close();
    }

    public void sortRecords() {
        Collections.sort(recordList, new Comparator<Record>() {
            @Override
            public int compare(Record o1, Record o2) {
                return o1.rolls-o2.rolls;
            }
        });
    }

    public void insertRecord(Record record) {
        if(recordList.size() >= 10) {
            recordList.add(record);
            sortRecords();
            recordList.removeLast();
        }
        else {
            recordList.add(record);
            sortRecords();
        }
    }

    public void showRecords() {
        for(Record record:recordList) {
            System.out.println(record);
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
//        Scene scene = new Scene(createContent());
        createRecords();
        readRecords();
        showRecords();
        Scene scene = new Scene(createMenu(primaryStage));
        primaryStage.setTitle("Snake and Ladders");
        scene.getStylesheets().add("sample/styles.css");
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) throws IOException, FontFormatException {
        launch(args);
    }
}
