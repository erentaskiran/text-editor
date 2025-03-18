package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TextEditor implements InputProcessor {
    private final SpriteBatch batch;

    private BitmapFont activeFont;
    private final BitmapFont fontBold;
    private final BitmapFont fontItalic;
    private final BitmapFont fontBoldItalic;
    private final BitmapFont fontRegular;
    private final Texture boldButton;
    private final Texture italicButton;
    private final Texture underlineButton;

    ShapeRenderer shapeRenderer;

    boolean selecting;

    Vector2 touchPos;

    FitViewport viewport;

    GapBuffer gapBuffer;

    int startX;
    int startY;
    int endX;
    int endY;
    int selectedStartIndex;
    int selectedEndIndex;
    int colSize;
    int rowSize;
    Long startTime;
    int screenWidth;
    int screenHeight;
    int safeTextAreaX;
    int safeTextAreaY;
    int safeAreaTopRow;
    int cursorY;
    int cursorIndex;
    boolean isAnyKeyPressed;
    int cursorLine;

    List<List<Node>> lines;
    int topIndex;

    int contentHeigth;
    boolean underlined;
    boolean bold;
    boolean italic;
    private final ScrollBar scrollBar;

    public TextEditor(SpriteBatch batch) {
        this.batch = batch;
        FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("HackNerdFontMono-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParameter.borderWidth = 0.1f;
        fontParameter.borderColor = Color.BLACK;
        fontParameter.size = 20;
        fontParameter.color = Color.WHITE;
        fontRegular = fontGenerator.generateFont(fontParameter);

        fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("HackNerdFontMono-BoldItalic.ttf"));
        fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParameter.borderWidth = 0.1f;
        fontParameter.borderColor = Color.BLACK;
        fontParameter.size = 20;
        fontParameter.color = Color.WHITE;
        fontBoldItalic = fontGenerator.generateFont(fontParameter);

        fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("HackNerdFontMono-Bold.ttf"));
        fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParameter.borderWidth = 0.1f;
        fontParameter.borderColor = Color.BLACK;
        fontParameter.size = 20;
        fontParameter.color = Color.WHITE;
        fontBold = fontGenerator.generateFont(fontParameter);

        fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("HackNerdFontMono-Italic.ttf"));
        fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParameter.borderWidth = 0.1f;
        fontParameter.borderColor = Color.BLACK;
        fontParameter.size = 20;
        fontParameter.color = Color.WHITE;
        fontItalic = fontGenerator.generateFont(fontParameter);
        activeFont = fontRegular;
        startTime = 0L;

        boldButton = new Texture("bold.png");
        italicButton = new Texture("italic.png");
        underlineButton = new Texture("underline.png");

        bold = false;
        italic = false;
        underlined = false;

        touchPos = new Vector2();

        shapeRenderer = new ShapeRenderer();
        selecting = false;

        viewport = new FitViewport(8, 5);
        startX = 0;
        startY = 0;
        endX = 0;
        endY = 0;
        selectedEndIndex = -1;
        selectedStartIndex = -1;

        contentHeigth = 0;
        safeAreaTopRow = 0;
        cursorY = 0;
        cursorIndex = 0;
        isAnyKeyPressed= false;

        colSize = activeFont.getData().getGlyph('m').width + 1;
        rowSize = activeFont.getData().getGlyph('m').height + colSize;

        screenWidth = 640;
        screenHeight = 480;
        safeTextAreaX = 620;
        safeTextAreaY = 430;

        gapBuffer = new GapBuffer(1024);
        for (int i = 0; i < 10; i++) {
            gapBuffer.addChar('m', bold, italic, underlined);
        }

        topIndex = 0;
        isAnyKeyPressed = false;
        cursorLine=0;

        scrollBar = new ScrollBar(safeTextAreaX, 0, 20, safeTextAreaY);

    }

    private void handleShiftKeyInput() {
        if (gapBuffer.getCursor() < 0 || gapBuffer.getCursor() >= gapBuffer.getSize()) {
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            gapBuffer.moveCursorRight();
            updateSelectionIndices(1, -1);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            gapBuffer.moveCursorLeft();
            updateSelectionIndices(0, -1);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            int currIndex = gapBuffer.getCursor();
            gapBuffer.moveCursorUp();
            updateSelectionIndices(0, currIndex);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            gapBuffer.moveCursorDown();
            int currIndex = gapBuffer.getCursor();
            updateSelectionIndices(1, currIndex);
        }
    }

    private void handleRegularKeyInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            gapBuffer.moveCursorRight();
            isAnyKeyPressed = true;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            gapBuffer.moveCursorLeft();
            isAnyKeyPressed = true;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            gapBuffer.moveCursorUp();
            isAnyKeyPressed = true;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            gapBuffer.moveCursorDown();
            isAnyKeyPressed = true;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)) {
            handleBackspace();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            handleEnterKey();
        } else {
            handleCharacterInput();
        }
    }

    private void handleBackspace() {
        if (selectedStartIndex != -1 && selectedEndIndex != -1) {
            deleteSelecteds();
        } else {
            if (gapBuffer.getCursor() > 0) {
                gapBuffer.remove(gapBuffer.getCursor() - 1);
            }
        }
        isAnyKeyPressed = true;
    }

    private void handleEnterKey() {
        deleteSelecteds();
        gapBuffer.addLineBreak();
        if (cursorY < 20) safeAreaTopRow += rowSize;
        isAnyKeyPressed = true;
    }

    private void handleCharacterInput() {
        for (int key = Input.Keys.A; key <= Input.Keys.Z; key++) {
            if (Gdx.input.isKeyJustPressed(key)) {
                char letter = (char) ('a' + (key - Input.Keys.A));
                if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ||
                    Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT) ||
                    Gdx.input.isKeyPressed(Input.Keys.CAPS_LOCK)) {
                    letter = Character.toUpperCase(letter);
                }
                deleteSelecteds();
                gapBuffer.addChar(letter, bold, italic, underlined);
                if (cursorY < 20) safeAreaTopRow += rowSize;
                isAnyKeyPressed = true;
            }
        }
        handleNumberInput();
        handleSpecialCharacterInput();
    }

    private void handleNumberInput() {
        for (int key = Input.Keys.NUM_0; key <= Input.Keys.NUM_9; key++) {
            if (Gdx.input.isKeyJustPressed(key)) {
                char number = (char) ('0' + (key - Input.Keys.NUM_0));
                deleteSelecteds();
                gapBuffer.addChar(number, bold, italic, underlined);
                if (cursorY < 20) safeAreaTopRow += rowSize;
                isAnyKeyPressed = true;
            }
        }
    }

    private void handleSpecialCharacterInput() {
        Map<Integer, Character> specialChars = new HashMap<>();
        specialChars.put(Input.Keys.SPACE, ' ');
        specialChars.put(Input.Keys.PERIOD, '.');
        specialChars.put(Input.Keys.COMMA, ',');
        specialChars.put(Input.Keys.MINUS, '-');
        specialChars.put(Input.Keys.PLUS, '+');
        specialChars.put(Input.Keys.SLASH, '/');
        specialChars.put(Input.Keys.EQUALS, '=');
        specialChars.put(Input.Keys.SEMICOLON, ';');
        specialChars.put(Input.Keys.APOSTROPHE, '\'');
        specialChars.put(Input.Keys.BACKSLASH, '\\');
        specialChars.put(Input.Keys.LEFT_BRACKET, '[');
        specialChars.put(Input.Keys.RIGHT_BRACKET, ']');

        for (Map.Entry<Integer, Character> entry : specialChars.entrySet()) {
            if (Gdx.input.isKeyJustPressed(entry.getKey())) {
                deleteSelecteds();
                gapBuffer.addChar(entry.getValue(), bold, italic, underlined);
                if (cursorY < 20) safeAreaTopRow += rowSize;
                isAnyKeyPressed = true;
            }
        }
    }

    private void updateSelectionIndices(int rotation, int currentIndex) {
        if(rotation == 1){
            if (selectedStartIndex < 0) {
                selectedStartIndex = gapBuffer.getCursor();
            }
            selectedEndIndex = gapBuffer.getCursor();
        }else if (rotation == 0){
            if (selectedEndIndex < 0) {
                selectedEndIndex = gapBuffer.getCursor()+1;
            }
            selectedStartIndex = gapBuffer.getCursor()+1;
        }

        if(currentIndex != -1){
            if(rotation == 1){
                if (selectedStartIndex < 0) {
                    selectedStartIndex = gapBuffer.getCursor();
                }
                selectedEndIndex = currentIndex;
            }else if (rotation == 0){
                if (selectedEndIndex < 0) {
                    selectedEndIndex = gapBuffer.getCursor()+1;
                }
                selectedStartIndex = gapBuffer.getCursor()+1;
            }
        }
        selecting = false;
    }

    public void input() {
        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)) {
            handleShiftKeyInput();
        } else {
            handleRegularKeyInput();
        }

        if (Gdx.input.isTouched()) {
            isAnyKeyPressed = true;
        }
    }

    public void logic() {
        if (selectedStartIndex != selectedEndIndex && selecting) {
            for (int i = selectedStartIndex; i <= selectedEndIndex; i++) {
                gapBuffer.changeDecoration(i, bold, italic, underlined);
            }
        }

        if (lines == null) {
            lines = gapBuffer.getLines();
        }

        scrollBar.update(lines.size(), rowSize);

        if (isAnyKeyPressed) {
            selectedStartIndex = -1;
            selectedEndIndex = -1;
            lines = gapBuffer.getLines();
            isAnyKeyPressed = false;
        }

        if (cursorY-rowSize < 0) {
            topIndex++;
            cursorY += rowSize;
        } else if (cursorY-rowSize > safeTextAreaY - rowSize) {
            topIndex--;
            cursorY -= rowSize;
        }
    }

    public void draw() {
        ScreenUtils.clear(0.f, 0.f, 0f, 0f);

        // Selected text
        batch.begin();
        if (!selecting) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0, 0, 1, 0.5f);
            int y = safeTextAreaY - rowSize;
            int k = 0;
            for (int i = 0; i < lines.size(); i++) {
                for (int j = 0; j < lines.get(i).size(); j++) {
                    if (k >= selectedStartIndex && k <= selectedEndIndex) {
                        shapeRenderer.rect((j - 1) * colSize, y, colSize, rowSize);
                    }
                    k++;
                }
                y -= rowSize;
            }
            shapeRenderer.end();
        }
        batch.end();

        // Text
        batch.begin();
        int y = safeTextAreaY + topIndex * rowSize;
        int k = 0;
        int x = 0;
        boolean cursorDrawn;

        for (int i = 0; i < lines.size(); i++) {
            cursorDrawn = false;
            for (int j = 0; j < lines.get(i).size(); j++) {
                Node currNode = lines.get(i).get(j);
                x = j * colSize;

                // Font selection
                selectFont(currNode);

                // Draw text
                drawCharacter(currNode, x, y);

                if (k == gapBuffer.getCursor() && x != 0) {
                    drawCursor(x, y);
                    cursorDrawn = true;
                    cursorLine=i;
                }

                k++;
            }
            if (k == gapBuffer.getCursor() && !cursorDrawn) {
                drawCursor(x + colSize - 1, y);
                cursorLine=i;
            }
            y -= rowSize;
        }
        batch.end();

        // Scrollbar
        batch.begin();
        scrollBar.render(shapeRenderer);
        batch.end();

        // Icon background
        batch.begin();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(255f, 0.1f, 0.1f, 1);
        shapeRenderer.rect(0, screenHeight - 40, screenWidth, 40);
        shapeRenderer.end();
        batch.end();

        // Icons
        batch.begin();
        batch.draw(boldButton, (float) screenWidth / 4, screenHeight - 40, 40, 40);
        batch.draw(italicButton, (float) (screenWidth * 2) / 4, screenHeight - 40, 40, 40);
        batch.draw(underlineButton, (float) (screenWidth * 3) / 4, screenHeight - 40, 40, 40);
        batch.end();
    }

    private void selectFont(Node node) {
        if (node.isBold() && node.isItalic()) {
            activeFont = fontBoldItalic;
        } else if (node.isBold()) {
            activeFont = fontBold;
        } else if (node.isItalic()) {
            activeFont = fontItalic;
        } else {
            activeFont = fontRegular;
        }
    }

    private void drawCharacter(Node node, int x, int y) {
        activeFont.draw(batch, String.valueOf(node.getChar()), x, y);
        if (node.isUnderline()) {
            activeFont = fontRegular;
            activeFont.draw(batch, "_", x, y - 3);
        }
    }

    private void drawCursor(int x, int y) {
        activeFont.setColor(Color.RED);
        activeFont.draw(batch, "|", x - 1, y);
        activeFont.setColor(Color.WHITE);
        cursorY = y;
    }

    private void deleteSelecteds() {
        if (selectedStartIndex != -1 && selectedEndIndex != -1) {
            for (int i = selectedEndIndex; i >= selectedStartIndex; i--) {
                gapBuffer.remove(i - 1);
            }
            selectedStartIndex = -1;
            selectedEndIndex = -1;
        }
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        startX = screenX;
        startY = screenHeight - screenY;
        if (startX < screenWidth / 4 + 40 && startX > screenWidth / 4 && startY > screenHeight - 40 && startY < screenHeight) {
            bold = !bold;
        } else if (startX < screenWidth * 2 / 4 + 40 && startX > screenWidth * 2 / 4 && startY > screenHeight - 40 && startY < screenHeight) {
            italic = !italic;
        } else if (startX < screenWidth * 3 / 4 + 40 && startX > screenWidth * 3 / 4 && startY > screenHeight - 40 && startY < screenHeight) {
            underlined = !underlined;
        }
        moveCursorWithTouch();
        selecting = true;
        startTime = System.nanoTime();
        cursorIndex = gapBuffer.getCursor();
        if (scrollBar.hitTest(startX, startY)) {
            scrollBar.setDragging(true);
            return true;
        }
        return true;
    }

    private void moveCursorWithTouch() {
        float x = Gdx.input.getX();
        float y = Gdx.input.getY();
        viewport.unproject(touchPos);
        if(x > safeTextAreaX || y > safeTextAreaY || x<0 || y<0){
            return;
        }
        int col = (int) (x / colSize);
        int row = (int) (y / rowSize) - 2;

        int k = 0;
        for(int i = 0; i<gapBuffer.getLines().size(); i++){
            for(int j = 0; j<gapBuffer.getLines().get(i).size(); j++){
                if(i == row && j == col){
                    gapBuffer.moveCursor(k);
                    return;
                }
                k++;
            }
        }
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        moveCursorWithTouch();

        if (scrollBar.isDragging()) {
            scrollBar.setDragging(false);
            return true;
        }

        selecting = false;

        endX = screenX;
        endY = screenHeight - screenY;

            moveCursorWithTouch();

            if (startX < safeTextAreaX) {
                long endTime = System.nanoTime();
                double elapsedTimeInSeconds = (endTime - startTime) / 1_000_000_000.0;

                if (elapsedTimeInSeconds >= 0.15) {
                    selectedStartIndex = cursorIndex;
                    selectedEndIndex = gapBuffer.getCursor();
                    if (selectedStartIndex > selectedEndIndex) {
                        int tmp = selectedStartIndex;
                        selectedStartIndex = selectedEndIndex;
                        selectedEndIndex = tmp;
                    }
                }
            }

            selecting = false;
            endX = screenX;
            endY = screenHeight - screenY;
        return true;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (scrollBar.isDragging()) {
            if (scrollBar.handleDrag(startY, screenY, screenHeight)) {
                topIndex = (int) (scrollBar.getScrollPosition() * lines.size());
                startY = screenHeight - screenY;
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
