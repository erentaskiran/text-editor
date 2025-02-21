package io.github.some_example_name;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

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
