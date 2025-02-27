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

    int CurrentY;
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
        fontBoldItalic= fontGenerator.generateFont(fontParameter);

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
        activeFont = fontRegular ;
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
        CurrentY=safeTextAreaY;
        startX = 0;
        startY = 0;
        endX = 0;
        endY = 0;
        selectedEndIndex = -1;
        selectedStartIndex = -1;

        colSize = activeFont.getData().getGlyph('m').width+1;
        rowSize = activeFont.getData().getGlyph('m').height+colSize;

        screenWidth = 640;
        screenHeight = 480;
        safeTextAreaX = 620;
        safeTextAreaY = 430;

        gapBuffer = new GapBuffer(1024);
        for(int i = 0; i < 49; i++){
            gapBuffer.addChar('m', activeFont.getData().getGlyph('m').width, bold , italic, underlined);
        }


    }

    public void input(){
        boolean isAnyKeyPressed = false;
        System.out.println(bold + " " + italic + " " + underlined);

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
                        if (sum >= safeTextAreaX || gapBuffer.getNode(i).getChar() == '\n') {
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
                        if (sum >= safeTextAreaX || gapBuffer.getNode(i).getChar() == '\n') {
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
                            if (sum >= safeTextAreaX || gapBuffer.getNode(i).getChar() == '\n') {
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
                            if (sum >= safeTextAreaX || gapBuffer.getNode(i).getChar() == '\n') {
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
                gapBuffer.addChar('\n', 0, bold, italic, underlined);
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
                gapBuffer.addChar(letter,  activeFont.getData().getGlyph(letter).width, bold, italic, underlined);
                deleteSelecteds();
                isAnyKeyPressed = true;
            }
        }
        for (int key = Input.Keys.NUM_0; key <= Input.Keys.NUM_9; key++) {
            if (Gdx.input.isKeyJustPressed(key)) {
                char number = (char) ('0' + (key - Input.Keys.NUM_0));
                gapBuffer.addChar(number, activeFont.getData().getGlyph(number).width, bold, italic, underlined);
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
                gapBuffer.addChar(entry.getValue(), activeFont.getData().getGlyph(entry.getValue()).width, bold, italic, underlined);
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

    public void logic(){
        if(selectedStartIndex != selectedEndIndex && selecting){
            for(int i = selectedStartIndex; i<=selectedEndIndex; i++){
                gapBuffer.changeDecoration(i, bold, italic, underlined);
            }
        }
    }

    public void draw(){
        ScreenUtils.clear(0.f, 0.f, 0f, 0f);
        CurrentY=safeTextAreaY;

        int currentX=10;

        //icon background
        batch.begin();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(255f, 0.1f, 0.1f, 1);
        shapeRenderer.rect(0, screenHeight-40, screenWidth, 40);
        shapeRenderer.end();

        batch.end();

        //icons
        batch.begin();

        batch.draw(boldButton, screenWidth/4, screenHeight-40, 40, 40);
        batch.draw(italicButton, screenWidth*2/4, screenHeight-40, 40, 40);
        batch.draw(underlineButton, screenWidth*3/4, screenHeight-40, 40, 40);

        batch.end();

        //selected text
        batch.begin();
        if (!selecting) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0, 0, 1, 0.5f);

            if(selectedStartIndex != -1 && selectedEndIndex != -1) {
                int y = safeTextAreaY - 20;
                int tmp = selectedStartIndex * colSize;
                int x = tmp%safeTextAreaX-3;
                for (int i = selectedStartIndex; i <= selectedEndIndex; i++) {
                    while (tmp >= safeTextAreaX) {
                        y -= rowSize;
                        tmp -= safeTextAreaX;
                    }
                    if(x>=safeTextAreaX){
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


        //text
        batch.begin();

        for(int i = 0; i<gapBuffer.getSize(); i++){
            if(currentX > safeTextAreaX){
                CurrentY -= rowSize;
                currentX = 10;
            }

            Node currNode = gapBuffer.getNode(i);
            if(currNode.isBold() && currNode.isItalic()){
                activeFont = fontBoldItalic;
            }else if(currNode.isBold()){
                activeFont = fontBold;
            }else if(currNode.isItalic()){
                activeFont = fontItalic;
            }else{
                activeFont = fontRegular;
            }

            if(i == gapBuffer.getCursor()){
                activeFont.setColor(Color.RED);
                activeFont.draw(batch, "|", currentX - 1, CurrentY);
                activeFont.setColor(Color.WHITE);
            }
            if(currNode.getChar() == '\n'){
                CurrentY -= rowSize;
                currentX = 10;
            }
            activeFont.draw(batch, String.valueOf(currNode.getChar()), currentX, CurrentY);
            if(currNode.isUnderline()){
                activeFont=fontRegular;
                activeFont.draw(batch, "_", currentX, CurrentY-3);
            }

            currentX += colSize;
        }
        if(gapBuffer.getSize()== gapBuffer.getCursor()){
            activeFont.setColor(Color.RED);
            activeFont.draw(batch, "|", currentX - 1, CurrentY);
            activeFont.setColor(Color.WHITE);
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
        int x = screenX;
        int y = screenHeight - screenY;
        if (x < screenWidth / 4 + 40 && x > screenWidth / 4 && y > screenHeight - 40 && y < screenHeight) {
            bold = !bold;
        } else if (x < screenWidth * 2 / 4 + 40 && x > screenWidth * 2 / 4 && y > screenHeight - 40 && y < screenHeight) {
            italic = !italic;
        } else if (x < screenWidth * 3 / 4 + 40 && x > screenWidth * 3 / 4 && y > screenHeight - 40 && y < screenHeight) {
            underlined = !underlined;
        }
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
        int sumY = safeTextAreaY - 20;
        for(int i = 0; i<gapBuffer.getSize(); i++){
            sumX+=colSize;
            if(sumX >=safeTextAreaX || gapBuffer.getNode(i).getChar() == '\n'){
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

    @Override public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {return false;}
    @Override public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }
    @Override public boolean mouseMoved(int screenX, int screenY) {return false;}
    @Override public boolean keyDown(int keycode) { return false; }
    @Override public boolean keyUp(int keycode) { return false; }
    @Override public boolean keyTyped(char character) { return false; }
    @Override public boolean scrolled(float amountX, float amountY) { return false; }
}
