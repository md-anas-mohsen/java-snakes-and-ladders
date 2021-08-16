package sample;

public class Snake {
    double x = 0, y = 0;
    public Snake(double x, double y) {
        this.x = x;
        this.y = y;
    }
    public double getX() {
        return (this.x+0.5)*DiceRollSnake.Tile_Size;
    }
    public double getY() {
        return (this.y+0.5)*DiceRollSnake.Tile_Size;
    }
}
