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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;;


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
    int space;
    int startX;
    int startY;
    int endX;
    int endY;
    ArrayList<Integer> selectedLines;
    HashSet<Integer> currentLines;

    public TextEditor(SpriteBatch batch) {
        this.batch = batch;
        fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("Arial.ttf"));
        fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParameter.borderWidth = 0.1f;
        fontParameter.borderColor = Color.BLACK;
        fontParameter.size = 12;
        fontParameter.color = Color.WHITE;
        font = fontGenerator.generateFont(fontParameter);

        touchPos = new Vector2();

        shapeRenderer = new ShapeRenderer();
        selecting = false;
        selectedLines = new ArrayList<>();

        viewport = new FitViewport(8, 5);
        CurrentY=460;
        space = 1;
        startX = 0;
        startY = 0;
        endX = 0;
        endY = 0;

        gapBuffer = new GapBuffer(1024);
        for(int i = 0; i < 49; i++){
            gapBuffer.addChar('m', 'n', font.getData().getGlyph('m').width);
        }

        currentLines = new HashSet<>();

    }

    public void input(){
        boolean isAnyKeyPressed = false;
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            gapBuffer.moveCursorRight();
            isAnyKeyPressed = true;
        }else if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            gapBuffer.moveCursorLeft();
            isAnyKeyPressed = true;
        }else if(Gdx.input.isKeyJustPressed(Input.Keys.UP)){
            int cursor = gapBuffer.getCursor();
            int sum=0;
            if(gapBuffer.getNode(cursor).getChar()=='\n' && cursor>0){
                gapBuffer.moveCursorLeft();
            }else{
                for(int i = cursor; i >= 0; i--) {
                    sum+=space + gapBuffer.getNode(i).getCharLength();
                    if(sum>=620 || gapBuffer.getNode(i).getChar() == '\n'){
                        gapBuffer.moveCursor(i);
                        break;
                    }
                }
            }
            isAnyKeyPressed = true;

        }else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)){
            int cursor = gapBuffer.getCursor();
            int sum=0;
            if(gapBuffer.getNode(cursor).getChar()=='\n' && cursor>0){
                gapBuffer.moveCursorRight();
            }else {

                for (int i = cursor; i < gapBuffer.getSize(); i++) {
                    sum += space + gapBuffer.getNode(i).getCharLength();
                    if (sum >= 620 || gapBuffer.getNode(i).getChar() == '\n') {
                        gapBuffer.moveCursor(i);
                        break;
                    }
                }
            }
            isAnyKeyPressed = true;

        }else if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)){
            if(gapBuffer.getCursor()>0){
                gapBuffer.remove(gapBuffer.getCursor()-1);
            }
            isAnyKeyPressed = true;
        }else if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)){
            gapBuffer.addChar('\n', 'n',0);
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
                isAnyKeyPressed = true;
            }
        }
        for (int key = Input.Keys.NUM_0; key <= Input.Keys.NUM_9; key++) {
            if (Gdx.input.isKeyJustPressed(key)) {
                char number = (char) ('0' + (key - Input.Keys.NUM_0));
                gapBuffer.addChar(number, 'n', font.getData().getGlyph(number).width);
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
                isAnyKeyPressed = true;
            }
        }

        if (Gdx.input.isTouched()) {
            float x =Gdx.input.getX();
            float y = 480-Gdx.input.getY();
            viewport.unproject(touchPos);
            int sumX = 10;
            int sumY = 440;
            for(int i = 0; i<gapBuffer.getSize(); i++){
                sumX+=gapBuffer.getNode(i).getCharLength() + space;
                if(sumX >=620 || gapBuffer.getNode(i).getChar() == '\n'){
                    sumY-=20;
                    sumX=10;
                }

                if (Math.abs(sumX-x)<20 && Math.abs(sumY-y)<20){
                    gapBuffer.moveCursor(i);
                }
            }
            isAnyKeyPressed = true;
        }
        updateCurrentLines();
        if(isAnyKeyPressed){
            selectedLines.clear();
        }
    }

    private void updateCurrentLines(){
        currentLines.clear();
        int sumX = 10;
        int sumY = 460;
        for(int i = 0; i<gapBuffer.getSize(); i++){
            sumX+=gapBuffer.getNode(i).getCharLength() + space;
            if(sumX >=620 || gapBuffer.getNode(i).getChar() == '\n'){
                sumY-=20;
                sumX=10;
            }
            currentLines.add(sumY);
        }
    }

    public void draw(){
        ScreenUtils.clear(0.f, 0.f, 0f, 0f);
        CurrentY=460;

        int currentX=10;
        boolean isXfull=false;
        batch.begin();
        if (!selecting) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            int lenSelectedLines = selectedLines.size();
            if(lenSelectedLines==0){
                shapeRenderer.setColor(0, 0, 0, 0f);
            }else{
                shapeRenderer.setColor(0, 0, 1, 0.5f);
            }
            if(lenSelectedLines>1){
                if(currentLines.contains(selectedLines.get(0))){
                shapeRenderer.rect(startX,  selectedLines.get(0)-20, 620, 20);
                }
                for(int i = 1; i<lenSelectedLines-1; i++){
                    if(currentLines.contains(selectedLines.get(i))){
                        shapeRenderer.rect(10,  selectedLines.get(i) -20, 620, 20);
                    }
                }
                if(currentLines.contains(selectedLines.get(lenSelectedLines-1))){
                    shapeRenderer.rect(10,  selectedLines.get(lenSelectedLines-1)-20, endX, 20);
                }
            }else{
                float highlightX = Math.min(startX, endX);
                float highlightWidth = Math.abs(startX - endX);
                shapeRenderer.rect(highlightX, CurrentY - 20, highlightWidth, 20);
            }

            shapeRenderer.end();
        }
        batch.end();

        batch.begin();



        for(int i = 0; i<gapBuffer.getSize(); i++){
            if(currentX > 620){
                isXfull=true;
            }
            if (isXfull) {
                CurrentY -= 20;
                isXfull = false;
                currentX = 10;
            }

            Node currNode = gapBuffer.getNode(i);

            if(i == gapBuffer.getCursor()){
                font.setColor(Color.RED);
                font.draw(batch, "|", currentX - space, CurrentY);
                font.setColor(Color.WHITE);
            }
            if(currNode.getChar() == '\n'){
                CurrentY -= 20;
                currentX = 10;
            }
            font.draw(batch, String.valueOf(currNode.getChar()), currentX, CurrentY);

            currentX += currNode.getCharLength() + space;
        }
        if(gapBuffer.getSize()== gapBuffer.getCursor()){
            font.setColor(Color.RED);
            font.draw(batch, "|", currentX - space, CurrentY);
            font.setColor(Color.WHITE);
        }

        batch.end();

    }
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        selectedLines.clear();
        startX = screenX;
        startY = 480-screenY;
        selecting = true;
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        endX = screenX;
        endY = 480-screenY;

        for(int i = startY+ (20 -startY%20); i>= endY+(20 -endY%20); i-=20){
            if(i>460){
                continue;
            }
            selectedLines.add(i);
        }
        System.out.println(selectedLines);
        selecting = false;
        System.out.println("Selection from (" + startX + ", " + startY + ") to (" + endX + ", " + endY + ")");
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
