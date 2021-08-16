package sample;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Tile extends Rectangle {
    int i, j = 0;
    public Tile(int x, int y) {
        setWidth(x);
        setHeight(x);

        setFill(Color.AZURE);
        setStroke(Color.BLACK);

    }
    public Tile(int x, int y, int z) {
        setWidth(x);
        setHeight(x);

        setFill(Color.FUCHSIA);
    }
}
