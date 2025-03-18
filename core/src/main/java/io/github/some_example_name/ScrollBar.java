package io.github.some_example_name;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class ScrollBar {
    private final int x;
    private final int y;
    private final int width;
    private int height;
    private final int viewportHeight;
    private boolean isDragging;
    private float scrollPosition;

    public ScrollBar(int x, int y, int width, int viewportHeight) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.viewportHeight = viewportHeight;
        this.isDragging = false;
        this.scrollPosition = 0;
    }

    public void update(int totalLines, int rowSize) {
        int contentHeight = totalLines * rowSize;
        float ratio = (float) viewportHeight / contentHeight;
        height = ratio > 1 ? 0 : (int) (viewportHeight * ratio);
    }

    public void render(ShapeRenderer shapeRenderer) {
        if (height == 0) return;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.1f, 255f, 0.1f, 1);
        shapeRenderer.rect(x, y + (viewportHeight - height) * scrollPosition, width, height);
        shapeRenderer.end();
    }

    public boolean handleDrag(int startY, int currentY, int screenHeight) {
        if (!isDragging) return false;

        float deltaY = (screenHeight - currentY) - startY;
        float newPosition = scrollPosition + (deltaY / (viewportHeight - height));
        setScrollPosition(newPosition);
        return true;
    }

    public boolean hitTest(int testX, int testY) {
        int scrollBarY = (int) (y + (viewportHeight - height) * scrollPosition);
        return testX > x && testX < x + width &&
            testY >= scrollBarY && testY <= scrollBarY + height;
    }

    public void setDragging(boolean dragging) {
        isDragging = dragging;
    }

    public void setScrollPosition(float position) {
        scrollPosition = Math.max(0, Math.min(1, position));
    }

    public float getScrollPosition() {
        return scrollPosition;
    }

    public boolean isDragging() {
        return isDragging;
    }
}
