import org.example.connection.Pos;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class PosTest {

    @Test
    public void testEquals() {
        var p1 = new Pos(5, 4);
        var p2 = new Pos(5, 4);
        assertEquals(p1, p2);
        assertEquals(p1.toString(), p2.toString());
    }

    @Test
    public void testNotEquals() {
        var p1 = new Pos(5, 3);
        var p2 = new Pos(5, 4);
        assertNotEquals(p1, p2);

        p1 = new Pos(5, 4);
        p2 = new Pos(6, 4);
        assertNotEquals(p1, p2);

        p1 = new Pos(5, 4);
        assertNotEquals(p1, null);
    }

    @Test
    public void testHashCode() {
        var p1 = new Pos(5, 3);
        var p2 = new Pos(5, 4);
        assertNotEquals(p1.hashCode(), p2.hashCode());

        p1 = new Pos(5, 4);
        p2 = new Pos(6, 4);
        assertNotEquals(p1.hashCode(), p2.hashCode());

        p1 = new Pos(5, 4);
        p2 = new Pos(4, 5);
        assertNotEquals(p1.hashCode(), p2.hashCode());

        p1 = new Pos(5, 4);
        p2 = new Pos(5, 4);
        assertEquals(p1.hashCode(), p2.hashCode());
    }
}
