package com.stemist.simulation.Menu;

import com.badlogic.gdx.Screen;
import com.stemist.simulation.MainWindow;

public class MenuScreen implements Screen {

    // To change screens
    MainWindow main;

    // For button interaction
    MenuHud hud;

    // Constructor
    public MenuScreen(MainWindow main) {
        // For changing screens
        this.main = main;

        // Add hud
        hud = new MenuHud(main.batch);

    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {

        // Draw hud
        hud.draw();

    }

    @Override
    public void resize(int width, int height) {
        hud.viewport.update(width, height);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
    }
    
}
