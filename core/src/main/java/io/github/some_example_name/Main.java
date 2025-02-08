package io.github.some_example_name;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.HashMap;
import java.util.Map;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private SpriteBatch batch;

    private FreeTypeFontGenerator fontGenerator;
    private FreeTypeFontGenerator.FreeTypeFontParameter fontParameter;
    private BitmapFont font;

    FitViewport viewport;

    GapBuffer<Character> gapBuffer;

    int CurrentY;
    int charSpace;
    int space;

    @Override
    public void create() {
        batch = new SpriteBatch();
        fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("Arial.ttf"));
        fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParameter.borderWidth = 0.1f;
        fontParameter.borderColor = Color.BLACK;
        fontParameter.size = 12;
        fontParameter.color = Color.WHITE;
        font = fontGenerator.generateFont(fontParameter);

        viewport = new FitViewport(8, 5);
        CurrentY=460;
        charSpace=font.getData().getGlyph('m').width;
        space=14;

        gapBuffer = new GapBuffer<Character>();
        for(int i = 0; i < 50; i++){
            gapBuffer.add('m');
        }
        gapBuffer.moveCursor(12);

    }

    @Override
    public void render() {
        input();
        logic();
        draw();


    }
    private void input(){
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            gapBuffer.cursorMoveRight();
        }else if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            gapBuffer.cursorMoveLeft();
        }else if(Gdx.input.isKeyJustPressed(Input.Keys.UP)){
                int cursor = gapBuffer.getCursor();
                int newIndex = (cursor*space-620)/space;
                gapBuffer.moveCursor(newIndex);

        }else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)){
            int cursor = gapBuffer.getCursor();
            int newIndex = (cursor*space+620)/space;
            gapBuffer.moveCursor(newIndex+1);

        }else if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)){
            if(gapBuffer.getCursor()>0){
                gapBuffer.remove(gapBuffer.getCursor()-1);
                gapBuffer.cursorMoveLeft();
            }
        }

        for (int key = Input.Keys.A; key <= Input.Keys.Z; key++) {
            if (Gdx.input.isKeyJustPressed(key)) {
                char letter = (char) ('a' + (key - Input.Keys.A)); // Küçük harf dönüşümü
                if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ||
                    Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT) ||
                    Gdx.input.isKeyPressed(Input.Keys.CAPS_LOCK)) {
                    letter = Character.toUpperCase(letter); // Büyük harf dönüşümü
                }
                gapBuffer.add(gapBuffer.getCursor(), letter);
                gapBuffer.cursorMoveRight();
            }
        }
        for (int key = Input.Keys.NUM_0; key <= Input.Keys.NUM_9; key++) {
            if (Gdx.input.isKeyJustPressed(key)) {
                char number = (char) ('0' + (key - Input.Keys.NUM_0));
                gapBuffer.add(gapBuffer.getCursor(), number);
                gapBuffer.cursorMoveRight();
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
                gapBuffer.add(gapBuffer.getCursor(), entry.getValue());
                gapBuffer.cursorMoveRight();
            }
        }

    }

    private void logic(){
    }

    private void draw(){
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        int currentY=460;
        CurrentY=currentY;
        int currentX=0;
        boolean isXfull=false;

        batch.begin();
        for(int i = 0; i<gapBuffer.size(); i++){
            if(currentX > 620){
                isXfull=true;
            }
            if (isXfull) {
                currentY -= 20;
                isXfull = false;
                currentX = 0;
            }
            currentX+=space;
            if(i == gapBuffer.getCursor()){
                font.draw(batch, "|", currentX-font.getData().getGlyph(gapBuffer.get(i)).width-space/4, currentY);
            }
            font.draw(batch, String.valueOf(gapBuffer.get(i)), currentX -font.getData().getGlyph(gapBuffer.get(i)).width, currentY);
        }
        batch.end();

    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
