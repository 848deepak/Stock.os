package com.inventory;

import com.inventory.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class BasicSanityTest {

    @Test
    void mathSanity_shouldPass() {
        assertEquals(4, 2 + 2);
    }

    @Test
    void tokenExtraction_shouldHandleBearerPrefix() {
        JwtTokenProvider provider = new JwtTokenProvider();
        assertEquals("abc", provider.getTokenFromRequest("Bearer abc"));
        assertFalse(provider.getTokenFromRequest("xyz").startsWith("Bearer "));
    }
}
