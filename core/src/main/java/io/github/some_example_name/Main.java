package io.github.some_example_name;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private TextEditor textEditor;


    @Override
    public void create() {
        batch = new SpriteBatch();
        textEditor = new TextEditor(batch);
        Gdx.input.setInputProcessor(textEditor);
    }

    @Override
    public void render() {
        input();
        logic();
        draw();


    }

    private void input(){
        textEditor.input();
    }

    private void logic(){

    }

    private void draw(){
        textEditor.draw();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
