package test.map;

import static org.junit.jupiter.api.Assertions.*;

import main.map.Tile;
import org.junit.jupiter.api.Test;

public class TileTest {

    @Test
    void constructorShouldSetType() {
        Tile tile = new Tile(Tile.Type.GRASS);
        assertEquals(Tile.Type.GRASS, tile.getType());
    }

    @Test
    void setTypeShouldChangeTileType() {
        Tile tile = new Tile(Tile.Type.ROAD);
        tile.setType(Tile.Type.CASTLE_PLAYER);
        assertEquals(Tile.Type.CASTLE_PLAYER, tile.getType());
    }

    @Test
    void toStringShouldReturnCorrectSymbols() {
        assertEquals(".", new Tile(Tile.Type.GRASS).toString());
        assertEquals("=", new Tile(Tile.Type.ROAD).toString());
        assertEquals("#", new Tile(Tile.Type.OBSTACLE).toString());
        assertEquals("🏰", new Tile(Tile.Type.CASTLE_PLAYER).toString());
        assertEquals("🏯", new Tile(Tile.Type.CASTLE_COMP).toString());
        assertEquals("+", new Tile(Tile.Type.PLAYER_ZONE).toString());
        assertEquals("-", new Tile(Tile.Type.COMP_ZONE).toString());
    }
}
