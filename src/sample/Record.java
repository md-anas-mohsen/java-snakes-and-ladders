package sample;

public class Record {
    String playerName;
    int rolls;

    public Record(String playerName, int rolls) {
        this.playerName =  playerName;
        this.rolls = rolls;
    }

    public String toString() {
        return playerName+" won with "+rolls+" rolls";
    }
}
