package com.inventory.util;

import javax.swing.*;
import java.awt.*;

public class DarkModeUtil {
    public static void enableDarkMode() {
        try {
            UIManager.setLookAndFeel("com.formdev.flatlaf.FlatDarkLaf");
        } catch (Exception e) {
            // fallback to default
        }
    }
}
