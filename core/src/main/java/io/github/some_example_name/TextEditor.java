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

    int contentHeigth;
    int scrollBarHeight;
    int scrollbarY;

    boolean underlined;
    boolean bold;
    boolean italic;


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
        scrollBarHeight = 0;
        safeAreaTopRow = 0;
        cursorY = 0;
        cursorIndex = 0;

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


    }

    private void handleShiftKeyInput() {
        if (gapBuffer.getCursor() < 0 || gapBuffer.getCursor() >= gapBuffer.getSize()) {
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            gapBuffer.moveCursorRight();
            updateSelectionIndices();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            gapBuffer.moveCursorLeft();
            updateSelectionIndices();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            gapBuffer.moveCursorUp();
            updateSelectionIndices();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            gapBuffer.moveCursorDown();
            updateSelectionIndices();
        }
    }

    private void handleRegularKeyInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            gapBuffer.moveCursorRight();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            gapBuffer.moveCursorLeft();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            gapBuffer.moveCursorUp();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            gapBuffer.moveCursorDown();
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
    }

    private void handleEnterKey() {
        deleteSelecteds();
        gapBuffer.addChar('\n', bold, italic, underlined);
        if (cursorY < 20) safeAreaTopRow += rowSize;
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
                gapBuffer.addChar(letter, bold, italic, underlined);
                deleteSelecteds();
                if (cursorY < 20) safeAreaTopRow += rowSize;
            }
        }
        handleNumberInput();
        handleSpecialCharacterInput();
    }

    private void handleNumberInput() {
        for (int key = Input.Keys.NUM_0; key <= Input.Keys.NUM_9; key++) {
            if (Gdx.input.isKeyJustPressed(key)) {
                char number = (char) ('0' + (key - Input.Keys.NUM_0));
                gapBuffer.addChar(number, bold, italic, underlined);
                deleteSelecteds();
                if (cursorY < 20) safeAreaTopRow += rowSize;
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
                gapBuffer.addChar(entry.getValue(), bold, italic, underlined);
                deleteSelecteds();
                if (cursorY < 20) safeAreaTopRow += rowSize;
            }
        }
    }

    private void updateSelectionIndices() {
        if (selectedStartIndex < 0) {
            selectedStartIndex = gapBuffer.getCursor();
        }
        selectedEndIndex = gapBuffer.getCursor();
        selecting = false;
    }

    public void input() {
        boolean isAnyKeyPressed = false;

        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)) {
            handleShiftKeyInput();
        } else {
            handleRegularKeyInput();
        }

        if (Gdx.input.isTouched()) {
            isAnyKeyPressed = true;
        }
        if (isAnyKeyPressed) {
            selectedStartIndex = 1;
            selectedEndIndex = -1;
        }
    }
    public void logic() {
        if (selectedStartIndex != selectedEndIndex && selecting) {
            for (int i = selectedStartIndex; i <= selectedEndIndex; i++) {
                gapBuffer.changeDecoration(i, bold, italic, underlined);
            }
        }
        if (contentHeigth <= 0) {
            contentHeigth = 1;
        }
        float tmp = (float) contentHeigth / (safeTextAreaY - rowSize);
        if (tmp > 1) {
            scrollBarHeight = (int) (safeTextAreaY / tmp);
        } else {
            scrollBarHeight = safeTextAreaY;
        }
    }

    public void draw() {
        ScreenUtils.clear(0.f, 0.f, 0f, 0f);
        List<List<Node>> lines = gapBuffer.getLines();

        //selected text
        batch.begin();
        if (!selecting) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0, 0, 1, 0.5f);
            int y = safeTextAreaY - rowSize;
            int k = 0;
            for (int i = 0; i <lines.size(); i++) {
                for (int j = 0; j < lines.get(i).size(); j++) {
                    if (k>=selectedStartIndex && k<=selectedEndIndex) {
                        shapeRenderer.rect((j-1) * colSize, y, colSize, rowSize);
                    }
                    k++;
                }
                y -= rowSize;
            }

            shapeRenderer.end();
        }
        batch.end();


        //text
        batch.begin();
        int y = safeTextAreaY;
        int k = 0;
        for (int i = 0; i <lines.size(); i++) {
            for (int j = 0; j < lines.get(i).size(); j++) {
                Node currNode = lines.get(i).get(j);
                int x = j*colSize;

                //font selection
                if (currNode.isBold() && currNode.isItalic()) {
                    activeFont = fontBoldItalic;
                } else if (currNode.isBold()) {
                    activeFont = fontBold;
                } else if (currNode.isItalic()) {
                    activeFont = fontItalic;
                } else {
                    activeFont = fontRegular;
                }

                //draw text
                if (k == gapBuffer.getCursor()) {
                    activeFont.setColor(Color.RED);
                    activeFont.draw(batch, "|", x - 1, y);
                    activeFont.setColor(Color.WHITE);
                    cursorY = y;
                }
                activeFont.draw(batch, String.valueOf(currNode.getChar()), x, y);
                if (currNode.isUnderline()) {
                    activeFont = fontRegular;
                    activeFont.draw(batch, "_", x, y - 3);
                }

                k++;
            }
            y -= rowSize;
        }
        batch.end();

        //scrollbar
        batch.begin();
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.1f, 255f, 0.1f, 1);
        int tmp = cursorY - scrollBarHeight / 2;
        if (tmp < 0) {
            scrollbarY = 0;
        } else if (tmp > safeTextAreaY / 2) {
            scrollbarY = safeTextAreaY - scrollBarHeight;
        } else {
            scrollbarY = tmp;
        }
        shapeRenderer.rect(safeTextAreaX, scrollbarY, 20, scrollBarHeight);
        shapeRenderer.end();

        batch.end();

        //icon background
        batch.begin();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(255f, 0.1f, 0.1f, 1);
        shapeRenderer.rect(0, screenHeight - 40, screenWidth, 40);
        shapeRenderer.end();

        batch.end();

        //icons
        batch.begin();

        batch.draw(boldButton, (float) screenWidth / 4, screenHeight - 40, 40, 40);
        batch.draw(italicButton, (float) (screenWidth * 2) / 4, screenHeight - 40, 40, 40);
        batch.draw(underlineButton, (float) (screenWidth * 3) / 4, screenHeight - 40, 40, 40);

        batch.end();

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

    private void moveScrollbarWithTouch() {
        if (startX < safeTextAreaX) {
            return;
        }
        if (startY > endY) {
            System.out.println("scrolling down");
            scrollbarY += 40;
            while (scrollbarY + scrollBarHeight < safeTextAreaY && startY > scrollbarY + scrollBarHeight) {
                scrollbarY++;
            }
        } else {
            System.out.println("scrolling up");
            while (scrollbarY > 0 && startY < scrollbarY) {
                scrollbarY--;
            }
        }
        System.out.println(startX + " " + startY);

    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
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

        moveScrollbarWithTouch();
        return true;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
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
