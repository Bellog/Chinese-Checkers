package org.example.server;

import org.apache.catalina.core.ApplicationContext;
import org.example.server.replay.ReplayServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ApplicationTest {

    @Mock
    private ReplayServer replay;

    @Mock
    private Server server;

    @Mock
    private ApplicationContext context;

    @InjectMocks
    private Application app;

    /*
    @BeforeEach
    public void setup() {
        context = mock(ApplicationContext.class);
        MockitoAnnotations.initMocks(this);
        doReturn(Server.class).when(context)
                .getBean(any(Class.class));
    }

     */

    @Test
    void testStartReplay() {
    }

    /*
    @Test
    void testStartServer() {
        server = mock(Server.class);
        context = mock(ApplicationContext.class);
        MockitoAnnotations.initMocks(this);
        doReturn(server).when(context)
                .getBean(any(Class.class));

        InputStream sysInBackup = System.in; // backup System.in to restore it later
        ByteArrayInputStream in = new ByteArrayInputStream(("1" + System.lineSeparator() + "2").getBytes());
        System.setIn(in);
        // do your thing

        // optionally, reset System.in to its original
        System.setIn(sysInBackup);
    }

     */
}