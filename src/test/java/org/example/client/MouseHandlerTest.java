package org.example.client;

import org.example.Pos;
import org.example.connection.Packet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.event.MouseEvent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * This test checks only flow of this class, does not check any implementation of abstract methods in GamePanel class.
 * <p></p>
 * In order to test whether this object behaves correctly, this test bases its results on how many times each abstract
 * method is called.
 */
public class MouseHandlerTest {

    private MouseHandler handler;
    private Dimension dim;
    private Pos offset;
    private int diameter;
    // used to check how many times each method is called
    // Since MouseHandler is abstract, most of purpose is to call abstract method based on various checks
    private int countSetFieldSelection = 0;
    private int countStartCheck = 0;
    private int countEndCheck = 0;
    private int countSend = 0;

    @BeforeEach
    public void setup() {
        diameter = 10;
        dim = new Dimension(60, 30);

        offset = new Pos(dim.width / 2, dim.height / 2);

        countEndCheck = 0;
        countSend = 0;
        countSetFieldSelection = 0;
        countStartCheck = 0;
        handler = new MouseHandler(dim, diameter, offset) {
            @Override
            protected void setFieldSelection(Pos pos, boolean selected) {
                countSetFieldSelection++;
            }

            @Override
            protected boolean startCheck(Pos pos) {
                countStartCheck++;
                return true;
            }

            @Override
            protected boolean endCheck(Pos pos) {
                countEndCheck++;
                return true;
            }

            @Override
            public void send(Packet packet) {
                countSend++;
            }
        };
    }

    @Test
    public void testFieldSelection() {
        MouseEvent e = mock(MouseEvent.class);

        /*
            mouse press sets field's selection iff mouse press is contained in a circle in the middle of
            a field, in this case it's a 60w by 30h field and a circle with 10 diameter (in pixels)
            so method setFieldSelection should not be called by handler
         */
        when(e.getPoint()).thenReturn(new Point(dim.width, dim.height));
        handler.mousePressed(e);
        assertEquals(1, countSetFieldSelection); //handler is offset by half of the field

        //field * 1.5 - offset = field -> setSelection should not be called
        when(e.getPoint()).thenReturn(new Point(dim.width * 3 / 2, dim.height * 3 / 2));
        handler.mousePressed(e);
        assertEquals(1, countSetFieldSelection);

        handler.mouseReleased(e);
        assertEquals(0, countSend); // send should not be called
    }

    @Test
    public void testMouseEvent() {
        MouseEvent e = mock(MouseEvent.class);
        //middle of field at (1,1)
        when(e.getPoint()).thenReturn(new Point(dim.width * 2, dim.height * 2));
        handler.mousePressed(e);
        assertEquals(1, countSetFieldSelection); //handler is offset by half of the field

        handler.mouseReleased(e);
        assertEquals(0, countSend); //same field, nothing else should happen
        assertEquals(2, countSetFieldSelection); // a field should be unselected nonetheless
        assertEquals(0, countEndCheck); // end check should not be called, as mouse did not move to another field
    }

    /**
     * check correct behaviour if user pressed on a field but not inside the circle
     */
    @Test
    public void testWrongEvent() {
        MouseEvent e = mock(MouseEvent.class);
        //position (2,2) of field at (1,1) // outside of circle
        when(e.getPoint()).thenReturn(new Point(dim.width * 3 / 2 + 2, dim.height * 3 / 2 + 2));
        handler.mousePressed(e);
        assertEquals(0, countSetFieldSelection); //outside of circle

        // middle of a field at (1,1) -> different absolute position, same field
        when(e.getPoint()).thenReturn(new Point(dim.width * 2, dim.height * 2));
        handler.mouseReleased(e);
        assertEquals(0, countSend); // same field, should not be called
        assertEquals(0, countSetFieldSelection); //press doesn't trigger, so release shouldn't too
    }

    @Test
    public void testMouseReleased() {
        MouseEvent e = mock(MouseEvent.class);
        //middle of field at (1,1)
        when(e.getPoint()).thenReturn(new Point(dim.width * 2, dim.height * 2));
        handler.mousePressed(e);

        //middle of the field at (3,3)
        when(e.getPoint()).thenReturn(new Point(dim.width * 4, dim.height * 4));
        handler.mouseReleased(e);
        assertEquals(1, countEndCheck);
        assertEquals(1, countStartCheck);
        assertEquals(1, countSend); // different field, should be called
        assertEquals(2, countSetFieldSelection); //press + release
    }
}
