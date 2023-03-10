package model;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WhiteboardTest {
    private Whiteboard board;

    @BeforeEach
    public void setup() {
        board = new Whiteboard(32, 20);
    }

    @Test
    public void testWhiteboard() {
        assertEquals(0, board.getNumTextLinesOnBoard());
        assertEquals(32, board.getWidth());
        assertEquals(20, board.getHeight());
        EventTest.assertLastEventDescriptionEquals("Whiteboard created with size 32x20");
    }

    @Test
    public void test1By1Whiteboard() {
        Whiteboard oneByOne = new Whiteboard(1, 1);
        assertEquals(0, oneByOne.getNumTextLinesOnBoard());
        assertEquals(1, oneByOne.getWidth());
        assertEquals(1, oneByOne.getHeight());
        EventTest.assertLastEventDescriptionEquals("Whiteboard created with size 1x1");
    }

    @Test
    public void testAddSingleText() {
        board.addText("Hello, world!", 0, 13);
        EventTest.assertSecondLastEventDescriptionEquals("Adding new text \"Hello, world!\" to whiteboard at (0, 13)");

        Text line = board.getTextAtIndex(0);

        assertEquals("Hello, world!", line.getText());
        assertEquals(0, line.getXcoord());
        assertEquals(13, line.getYcoord());
    }
    @Test
    public void testMultipleText() {
        // Test after repeated additions, including corners
        board.addText("Hello, world!", 0, 13);
        EventTest.assertSecondLastEventDescriptionEquals("Adding new text \"Hello, world!\" to whiteboard at (0, 13)");
        board.addText("", 6, 20);
        EventTest.assertSecondLastEventDescriptionEquals("Adding new text \"\" to whiteboard at (6, 20)");
        board.addText("foo", 0, 0);
        EventTest.assertSecondLastEventDescriptionEquals("Adding new text \"foo\" to whiteboard at (0, 0)");
        board.addText("bar", 32, 20);
        EventTest.assertSecondLastEventDescriptionEquals("Adding new text \"bar\" to whiteboard at (32, 20)");

        Text line = board.getTextAtIndex(0);
        Text secondLine = board.getTextAtIndex(1);
        Text foo = board.getTextAtIndex(2);
        Text bar = board.getTextAtIndex(3);

        assertEquals("Hello, world!", line.getText());
        assertEquals(0, line.getXcoord());
        assertEquals(13, line.getYcoord());

        assertEquals("", secondLine.getText());
        assertEquals(6, secondLine.getXcoord());
        assertEquals(20, secondLine.getYcoord());

        assertEquals("foo", foo.getText());
        assertEquals(0, foo.getXcoord());
        assertEquals(0, foo.getYcoord());

        assertEquals("bar", bar.getText());
        assertEquals(32, bar.getXcoord());
        assertEquals(20, bar.getYcoord());
    }

    @Test
    public void testRemoveSingleText() {
        board.addText("foo", 1, 13);
        Text foo = board.getTextAtIndex(0);
        board.removeText(foo);
        EventTest.assertLastEventDescriptionEquals("Text \"foo\" removed from whiteboard");
        assertEquals(0, board.getNumTextLinesOnBoard());
    }

    @Test
    public void testRemoveSingleDuplicateText() {
        board.addText("foo", 1, 13);
        board.addText("foo", 4, 4);
        Text foo0 = board.getTextAtIndex(0);
        Text foo1 = board.getTextAtIndex(1);
        board.removeText(foo0);
        EventTest.assertLastEventDescriptionEquals("Text \"foo\" removed from whiteboard");

        assertEquals(1, board.getNumTextLinesOnBoard());
        Text remaining = board.getTextAtIndex(0);
        assertEquals("foo", remaining.getText());
        assertEquals(4, remaining.getXcoord());
        assertEquals(4, remaining.getYcoord());
    }

    @Test
    public void testRemoveSingleTextFromMultiple() {
        board.addText("foo", 1, 13);
        board.addText("bar", 6, 7);
        board.addText("baz", 13, 5);
        Text bar = board.getTextAtIndex(1);
        // Remove middle entry
        board.removeText(bar);
        EventTest.assertLastEventDescriptionEquals("Text \"bar\" removed from whiteboard");
        assertEquals(2, board.getNumTextLinesOnBoard());
        Text firstLine = board.getTextAtIndex(0);
        Text secondLine = board.getTextAtIndex(1);

        assertEquals("foo", firstLine.getText());
        assertEquals(1, firstLine.getXcoord());
        assertEquals(13, firstLine.getYcoord());

        assertEquals("baz", secondLine.getText());
        assertEquals(13, secondLine.getXcoord());
        assertEquals(5, secondLine.getYcoord());
    }

    @Test
    public void testRemoveAllTextFromMultiple() {
        board.addText("foo", 1, 13);
        board.addText("bar", 6, 7);
        board.addText("baz", 13, 5);

        Text foo = board.getTextAtIndex(0);
        Text bar = board.getTextAtIndex(1);
        Text baz = board.getTextAtIndex(2);

        board.removeText(foo);
        EventTest.assertLastEventDescriptionEquals("Text \"foo\" removed from whiteboard");
        board.removeText(bar);
        EventTest.assertLastEventDescriptionEquals("Text \"bar\" removed from whiteboard");
        board.removeText(baz);
        EventTest.assertLastEventDescriptionEquals("Text \"baz\" removed from whiteboard");

        assertEquals(0, board.getNumTextLinesOnBoard());
    }

    @Test
    public void testMoveText() {
        board.addText("foo", 1, 13);
        Text foo = board.getTextAtIndex(0);
        board.moveText(foo, 5, 6);
        EventTest.assertLastEventDescriptionEquals("Text \"foo\" moved to (5, 6)");

        assertEquals(5, foo.getXcoord());
        assertEquals(6, foo.getYcoord());
    }

    @Test
    public void testMoveTextMultipleTimes() {
        board.addText("foo", 1, 13);
        Text foo = board.getTextAtIndex(0);
        board.moveText(foo, 5, 6);
        EventTest.assertLastEventDescriptionEquals("Text \"foo\" moved to (5, 6)");
        assertEquals(5, foo.getXcoord());
        assertEquals(6, foo.getYcoord());

        board.moveText(foo, 0, 0);
        EventTest.assertLastEventDescriptionEquals("Text \"foo\" moved to (0, 0)");
        assertEquals(0, foo.getXcoord());
        assertEquals(0, foo.getYcoord());

        board.moveText(foo, 31, 19);
        EventTest.assertLastEventDescriptionEquals("Text \"foo\" moved to (31, 19)");
        assertEquals(31, foo.getXcoord());
        assertEquals(19, foo.getYcoord());
    }

    @Test
    public void testMoveDuplicateText() {
        board.addText("foo", 1, 13);
        board.addText("foo", 4, 3);
        Text foo0 = board.getTextAtIndex(0);
        Text foo1 = board.getTextAtIndex(1);
        board.moveText(foo0, 5, 6);
        EventTest.assertLastEventDescriptionEquals("Text \"foo\" moved to (5, 6)");

        assertEquals(5, foo0.getXcoord());
        assertEquals(6, foo0.getYcoord());

        assertEquals(4, foo1.getXcoord());
        assertEquals(3, foo1.getYcoord());
    }

    @Test
    public void testMoveSingleTextFromMultiple() {
        board.addText("foo", 1, 13);
        board.addText("bar", 4, 3);
        board.addText("baz", 5, 6);
        Text foo = board.getTextAtIndex(0);
        Text bar = board.getTextAtIndex(1);
        Text baz = board.getTextAtIndex(1);
        board.moveText(bar, 5, 6);
        EventTest.assertLastEventDescriptionEquals("Text \"bar\" moved to (5, 6)");

        assertEquals(1, foo.getXcoord());
        assertEquals(13, foo.getYcoord());

        assertEquals(5, bar.getXcoord());
        assertEquals(6, bar.getYcoord());

        assertEquals(5, baz.getXcoord());
        assertEquals(6, baz.getYcoord());
    }

    @Test
    public void testSetWidth() {
        board.setWidth(40);
        assertEquals(40, board.getWidth());
        EventTest.assertLastEventDescriptionEquals("Whiteboard width changed from 32 to 40");
    }

    @Test
    public void testSetHeight() {
        board.setHeight(50);
        assertEquals(50, board.getHeight());
        EventTest.assertLastEventDescriptionEquals("Whiteboard height changed from 20 to 50");
    }

    @Test
    public void testTextDoesntMoveOnSetGreaterWidth() {
        board.addText("Testing 1, 2, 3!", 30, 18);
        Text text = board.getTextAtIndex(0);

        board.setWidth(40);
        assertEquals(30, text.getXcoord());
    }

    @Test
    public void testTextDoesntMoveOnSetGreaterHeight() {
        board.addText("Testing 1, 2, 3!", 30, 18);
        Text text = board.getTextAtIndex(0);

        board.setHeight(50);
        assertEquals(18, text.getYcoord());
    }

    @Test
    public void testTextMovesOnBoardResizeEqualHeight() {
        board.addText("Testing 1, 2, 3!", 30, 18);
        Text text = board.getTextAtIndex(0);

        board.setHeight(18);
        assertEquals(1, board.getNumTextLinesOnBoard());
        assertEquals(30, text.getXcoord());
        assertEquals(17, text.getYcoord());
    }

    @Test
    public void testTextMovesOnBoardResizeEqualWidth() {
        board.addText("Testing 1, 2, 3!", 30, 18);
        Text text = board.getTextAtIndex(0);

        board.setWidth(30);
        assertEquals(1, board.getNumTextLinesOnBoard());
        assertEquals(29, text.getXcoord());
        assertEquals(18, text.getYcoord());
    }

    @Test
    public void testTextMovesOnBoardResizeGreaterHeight() {
        board.addText("Testing 1, 2, 3!", 30, 18);
        Text text = board.getTextAtIndex(0);

        board.setHeight(10);
        assertEquals(30, text.getXcoord());
        assertEquals(9, text.getYcoord());
    }

    @Test
    public void testTextMovesOnBoardResizeGreaterWidth() {
        board.addText("Testing 1, 2, 3!", 30, 18);
        Text text = board.getTextAtIndex(0);

        board.setWidth(10);
        assertEquals(9, text.getXcoord());
        assertEquals(18, text.getYcoord());
    }

    @Test
    public void testTextMovesOnBoardResizeMinimumHeight() {
        board.addText("Testing 1, 2, 3!", 30, 18);
        Text text = board.getTextAtIndex(0);

        board.setHeight(1);
        assertEquals(30, text.getXcoord());
        assertEquals(0, text.getYcoord());
    }

    @Test
    public void testTextMovesOnBoardResizeMinimumWidth() {
        board.addText("Testing 1, 2, 3!", 30, 18);
        Text text = board.getTextAtIndex(0);

        board.setWidth(1);
        assertEquals(0, text.getXcoord());
        assertEquals(18, text.getYcoord());
    }

    @Test
    public void testToJsonEmpty() {
        JSONObject expected = new JSONObject();
        expected.put("height", 20);
        expected.put("width", 32);
        expected.put("textLines", new JSONArray());
        assertTrue(expected.similar(board.toJson()));
    }

    @Test
    public void testToJsonWithText() {
        board.addText("Hello", 3, 4);
        board.addText("hi", 0, 0);

        JSONObject actual = board.toJson();
        assertEquals(32, actual.getNumber("width"));
        assertEquals(20, actual.getNumber("height"));

        JSONObject text1 = new JSONObject().put("text", "Hello").put("x", 3).put("y", 4);
        JSONObject text2 = new JSONObject().put("text", "hi").put("x", 0).put("y", 0);

        JSONArray array = actual.getJSONArray("textLines");
        assertTrue(array.getJSONObject(0).similar(text1));
        assertTrue(array.getJSONObject(1).similar(text2));
    }
}