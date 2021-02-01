package org.example.server.web;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StompPrincipalTest {

    @Test
    void testGetName() {
        assertNotNull(new StompPrincipal("Name").getName());
        assertNull(new StompPrincipal(null).getName());
    }
}