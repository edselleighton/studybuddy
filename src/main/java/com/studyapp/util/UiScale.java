package com.studyapp.util;

import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Screen;

public final class UiScale {
    public static final String TITLE_FONT = "Georgia";
    public static final String UI_FONT = "Segoe UI";

    private static final double BASE_WIDTH = 1920.0;
    private static final double BASE_HEIGHT = 1080.0;
    private static final double MIN_SCALE = 0.85;
    private static final double MAX_SCALE = 1.25;

    private UiScale() {
    }

    public static double scale() {
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        double widthScale = bounds.getWidth() / BASE_WIDTH;
        double heightScale = bounds.getHeight() / BASE_HEIGHT;
        double scale = Math.min(widthScale, heightScale);
        return Math.max(MIN_SCALE, Math.min(MAX_SCALE, scale));
    }

    public static double size(double value) {
        return Math.round(value * scale());
    }

    public static Font font(String family, double size) {
        return Font.font(family, size(size));
    }

    public static Font font(String family, FontWeight weight, double size) {
        return Font.font(family, weight, size(size));
    }

    public static Font titleFont(double size) {
        return font(TITLE_FONT, size);
    }

    public static Font headingFont(double size) {
        return font(UI_FONT, FontWeight.SEMI_BOLD, size);
    }

    public static Font bodyFont(double size) {
        return font(UI_FONT, size);
    }

    public static Font buttonFont(double size) {
        return font(UI_FONT, FontWeight.SEMI_BOLD, size);
    }

    public static Font emphasisFont(double size) {
        return font(UI_FONT, FontWeight.BOLD, size);
    }

    public static String uiFontCss(double size) {
        return "-fx-font-family: '" + UI_FONT + "'; -fx-font-size: " + size(size) + "px;";
    }

    public static String buttonFontCss(double size) {
        return uiFontCss(size) + " -fx-font-weight: bold;";
    }

    public static Insets insets(double all) {
        return new Insets(size(all));
    }

    public static Insets insets(double top, double right, double bottom, double left) {
        return new Insets(size(top), size(right), size(bottom), size(left));
    }
}

