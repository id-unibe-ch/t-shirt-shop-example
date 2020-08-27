package com.vaadin.tshirtshop;

import com.github.mvysny.kaributesting.v10.MockVaadin;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.spring.SpringVaadinSession;
import kotlin.jvm.functions.Function0;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Martin Vysny <mavi@vaadin.com>
 */
public class MockSpringVaadinSession extends SpringVaadinSession {
    /**
     * We need to pretend that we have the UI lock during the duration of the test method, otherwise
     * Vaadin would complain that there is no session lock.
     * The easiest way is to simply always provide a locked lock :)
     */
    @NotNull
    private final ReentrantLock lock = new ReentrantLock();

    @NotNull
    private final Function0<UI> uiFactory;

    public MockSpringVaadinSession(@NotNull VaadinService service, @NotNull Function0<UI> uiFactory) {
        super(service);
        this.uiFactory = uiFactory;
        lock.lock();
    }

    @Override
    public Lock getLockInstance() {
        return lock;
    }

    @Override
    public void close() {
        super.close();
        MockVaadin.INSTANCE.afterSessionClose(this, uiFactory);
    }
}
