package io.github.some_example_name;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

import java.sql.Time;
import java.util.*;
;


public class TextEditor implements InputProcessor {
    private SpriteBatch batch;
    private FreeTypeFontGenerator fontGenerator;
    private FreeTypeFontGenerator.FreeTypeFontParameter fontParameter;
    private BitmapFont font;
    ShapeRenderer shapeRenderer;

    boolean selecting;

    Vector2 touchPos;

    FitViewport viewport;

    GapBuffer gapBuffer;

    int CurrentY;
    int startX;
    int startY;
    int endX;
    int endY;
    int selectedStartIndex;
    int selectedEndIndex;
    int colSize;
    int rowSize;
    int CursorX;
    int CursorY;
    Long startTime;

    public TextEditor(SpriteBatch batch) {
        this.batch = batch;
        fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("HackNerdFontMono-Regular.ttf"));
        fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParameter.borderWidth = 0.1f;
        fontParameter.borderColor = Color.BLACK;
        fontParameter.size = 20;
        fontParameter.color = Color.WHITE;
        font = fontGenerator.generateFont(fontParameter);
        startTime = 0L;

        touchPos = new Vector2();

        shapeRenderer = new ShapeRenderer();
        selecting = false;

        viewport = new FitViewport(8, 5);
        CurrentY=460;
        startX = 0;
        startY = 0;
        endX = 0;
        endY = 0;
        selectedEndIndex = -1;
        selectedStartIndex = -1;

        CursorX=10;
        CursorY=460;

        colSize = font.getData().getGlyph('m').width+1;
        rowSize = font.getData().getGlyph('m').height+colSize;

        gapBuffer = new GapBuffer(1024);
        for(int i = 0; i < 49; i++){
            gapBuffer.addChar('m', 'n', font.getData().getGlyph('m').width);
        }


    }

    public void input(){
        boolean isAnyKeyPressed = false;

        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)){
            if(gapBuffer.getCursor() < 0 || gapBuffer.getCursor() >= gapBuffer.getSize()){
                return;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) && (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT))) {
                gapBuffer.moveCursorRight();

                if (selectedStartIndex<0){
                    selectedStartIndex = gapBuffer.getCursor();
                }
                selectedEndIndex = gapBuffer.getCursor();
                selecting=false;
            }else if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)&& (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT))) {
                if (selectedEndIndex<0){
                    selectedEndIndex = gapBuffer.getCursor();
                }
                selectedStartIndex = gapBuffer.getCursor();
                gapBuffer.moveCursorLeft();

                selecting=false;
            }else if(Gdx.input.isKeyJustPressed(Input.Keys.UP)&& (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT))){
                int cursor = gapBuffer.getCursor();
                int sum=0;
                int tmp = -1;

                if(cursor < gapBuffer.getSize() && cursor >= 0) {
                    if (selectedEndIndex < 0) {
                        tmp = gapBuffer.getCursor();
                    }
                    for (int i = cursor; i >= 0; i--) {
                        sum += colSize;
                        if (sum >= 620 || gapBuffer.getNode(i).getChar() == '\n') {
                            gapBuffer.moveCursor(i);
                            selectedStartIndex = gapBuffer.getCursor();
                            selectedEndIndex = tmp;
                            break;
                        }
                    }
                }
                selecting=false;
            }else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)&& (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT))){
                int cursor = gapBuffer.getCursor();
                int sum=0;
                int tmp = -1;
                if(cursor < gapBuffer.getSize() && cursor >= 0) {
                    if (selectedStartIndex < 0) {
                        tmp = gapBuffer.getCursor();
                    }
                    for (int i = cursor; i < gapBuffer.getSize(); i++) {
                        sum += colSize;
                        if (sum >= 620 || gapBuffer.getNode(i).getChar() == '\n') {
                            gapBuffer.moveCursor(i);
                            selectedEndIndex = gapBuffer.getCursor();
                            selectedStartIndex = tmp;
                            break;
                        }
                    }
                }
                selecting = false;
            }

        }else {
            if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
                gapBuffer.moveCursorRight();
                isAnyKeyPressed = true;
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
                gapBuffer.moveCursorLeft();
                isAnyKeyPressed = true;
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
                int cursor = gapBuffer.getCursor();
                int sum = 0;
                if(cursor < gapBuffer.getSize() && cursor >= 0) {
                    if (gapBuffer.getNode(cursor).getChar() == '\n' && cursor > 0) {
                        gapBuffer.moveCursorLeft();
                    } else {
                        for (int i = cursor; i >= 0; i--) {
                            sum += colSize;
                            if (sum >= 620 || gapBuffer.getNode(i).getChar() == '\n') {
                                gapBuffer.moveCursor(i);
                                break;
                            }
                        }
                    }
                    isAnyKeyPressed = true;
                }

            } else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
                int cursor = gapBuffer.getCursor();
                int sum = 0;
                if(cursor < gapBuffer.getSize() && cursor >= 0) {
                    if (gapBuffer.getNode(cursor).getChar() == '\n' && cursor > 0) {
                        gapBuffer.moveCursorRight();
                    } else {
                        for (int i = cursor; i < gapBuffer.getSize(); i++) {
                            sum += colSize;
                            if (sum >= 620 || gapBuffer.getNode(i).getChar() == '\n') {
                                gapBuffer.moveCursor(i);
                                break;
                            }
                        }
                    }
                }
                isAnyKeyPressed = true;

            } else if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)) {
                if(selectedStartIndex != -1 && selectedEndIndex != -1) {
                    deleteSelecteds();
                }else{
                    if (gapBuffer.getCursor() > 0) {
                        gapBuffer.remove(gapBuffer.getCursor() - 1);
                    }
                }
                isAnyKeyPressed = true;
            } else if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                deleteSelecteds();
                gapBuffer.addChar('\n', 'n', 0);
            }
        }

        for (int key = Input.Keys.A; key <= Input.Keys.Z; key++) {
            if (Gdx.input.isKeyJustPressed(key)) {
                char letter = (char) ('a' + (key - Input.Keys.A));
                if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ||
                    Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT) ||
                    Gdx.input.isKeyPressed(Input.Keys.CAPS_LOCK)) {
                    letter = Character.toUpperCase(letter);
                }
                gapBuffer.addChar(letter, 'n', font.getData().getGlyph(letter).width);
                deleteSelecteds();
                isAnyKeyPressed = true;
            }
        }
        for (int key = Input.Keys.NUM_0; key <= Input.Keys.NUM_9; key++) {
            if (Gdx.input.isKeyJustPressed(key)) {
                char number = (char) ('0' + (key - Input.Keys.NUM_0));
                gapBuffer.addChar(number, 'n', font.getData().getGlyph(number).width);
                deleteSelecteds();
                isAnyKeyPressed = true;
            }
        }

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
                gapBuffer.addChar(entry.getValue(), 'n', font.getData().getGlyph(entry.getValue()).width);
                deleteSelecteds();
                isAnyKeyPressed = true;
            }
        }

        if (Gdx.input.isTouched()) {
            isAnyKeyPressed = true;
        }
        if (isAnyKeyPressed){
            selectedStartIndex = -1;
            selectedEndIndex = -1;
        }
    }

    public void draw(){
        ScreenUtils.clear(0.f, 0.f, 0f, 0f);
        CurrentY=460;

        int currentX=10;
        batch.begin();
        if (!selecting) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0, 0, 1, 0.5f);

            if(selectedStartIndex != -1 && selectedEndIndex != -1) {
                int y = 440;
                int tmp = selectedStartIndex * colSize;
                int x = tmp%620-3;
                for (int i = selectedStartIndex; i <= selectedEndIndex; i++) {
                    while (tmp >= 620) {
                        y -= rowSize;
                        tmp -= 620;
                    }
                    if(x>=620){
                        x=10;
                    }
                    shapeRenderer.rect(x, y, colSize, rowSize);
                    tmp += colSize;
                    x+=colSize;
                }
            }

            shapeRenderer.end();
        }
        batch.end();

        batch.begin();

        for(int i = 0; i<gapBuffer.getSize(); i++){
            if(currentX > 620){
                CurrentY -= rowSize;
                currentX = 10;
            }

            Node currNode = gapBuffer.getNode(i);

            if(i == gapBuffer.getCursor()){
                CursorY = CurrentY;
                CursorX = currentX;
                font.setColor(Color.RED);
                font.draw(batch, "|", currentX - 1, CurrentY);
                font.setColor(Color.WHITE);
            }
            if(currNode.getChar() == '\n'){
                CurrentY -= rowSize;
                currentX = 10;
            }
            font.draw(batch, String.valueOf(currNode.getChar()), currentX, CurrentY);

            currentX += colSize;
        }
        if(gapBuffer.getSize()== gapBuffer.getCursor()){
            font.setColor(Color.RED);
            font.draw(batch, "|", currentX - 1, CurrentY);
            font.setColor(Color.WHITE);
        }

        batch.end();

    }

    private void deleteSelecteds(){
        if(selectedStartIndex != -1 && selectedEndIndex != -1){
            for (int i = selectedEndIndex; i >= selectedStartIndex; i--){
                gapBuffer.remove(i-1);
            }
            System.out.println();
            selectedStartIndex = -1;
            selectedEndIndex = -1;
        }
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        moveCursorWithTouch();
        selecting = true;
        startTime = System.nanoTime();
        startX = gapBuffer.getCursor();
        return true;
    }

    private void moveCursorWithTouch(){
        float x =Gdx.input.getX();
        float y = 480-Gdx.input.getY();
        viewport.unproject(touchPos);
        int sumX = 10;
        int sumY = 440;
        for(int i = 0; i<gapBuffer.getSize(); i++){
            sumX+=colSize;
            if(sumX >=620 || gapBuffer.getNode(i).getChar() == '\n'){
                sumY-=20;
                sumX=10;
            }

            if (Math.abs(sumX-x)<20 && Math.abs(sumY-y)<20){
                gapBuffer.moveCursor(i);
            }
        }
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        moveCursorWithTouch();
        long endTime = System.nanoTime();
        double elapsedTimeInSeconds = (endTime - startTime) / 1_000_000_000.0;

        if (elapsedTimeInSeconds >= 0.15) {
            selectedStartIndex = startX;
            selectedEndIndex = gapBuffer.getCursor();
            if(selectedStartIndex>selectedEndIndex){
                int tmp = selectedStartIndex;
                selectedStartIndex = selectedEndIndex;
                selectedEndIndex = tmp;
            }
        }
        selecting = false;
        return true;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override public boolean keyDown(int keycode) { return false; }
    @Override public boolean keyUp(int keycode) { return false; }
    @Override public boolean keyTyped(char character) { return false; }
    @Override public boolean scrolled(float amountX, float amountY) { return false; }
}
