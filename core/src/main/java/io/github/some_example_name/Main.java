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

    GapBuffer gapBuffer;

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

        gapBuffer = new GapBuffer(1024);
        for(int i = 0; i < 49; i++){
            gapBuffer.addChar('m', 'n', font.getData().getGlyph('m').width);
        }
    }

    @Override
    public void render() {
        input();
        logic();
        draw();


    }
    private void input(){
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            gapBuffer.moveCursorRight();
        }else if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            gapBuffer.moveCursorLeft();
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
                gapBuffer.addChar(letter, 'n', font.getData().getGlyph(letter).width);
            }
        }
        for (int key = Input.Keys.NUM_0; key <= Input.Keys.NUM_9; key++) {
            if (Gdx.input.isKeyJustPressed(key)) {
                char number = (char) ('0' + (key - Input.Keys.NUM_0));
                gapBuffer.addChar(number, 'n', font.getData().getGlyph(number).width);
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
        for(int i = 0; i<gapBuffer.getSize()-1; i++){
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
                font.setColor(Color.RED);
                font.draw(batch, "|", currentX-font.getData().getGlyph(gapBuffer.getNode(i).data).width, currentY);
                font.setColor(Color.WHITE);
            }
            font.draw(batch, String.valueOf(gapBuffer.getNode(i).data), currentX -font.getData().getGlyph(gapBuffer.getNode(i).data).width, currentY);
        }
        batch.end();

    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
