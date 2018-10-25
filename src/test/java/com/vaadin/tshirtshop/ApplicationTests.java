package com.vaadin.tshirtshop;

import com.github.karibu.mockhttp.MockContext;
import com.github.karibu.mockhttp.MockServletConfig;
import com.github.karibu.testing.v10.LocatorJ;
import com.github.karibu.testing.v10.MockVaadin;
import com.github.karibu.testing.v10.MockedUI;
import com.github.karibu.testing.v10.Routes;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.function.DeploymentConfiguration;
import com.vaadin.flow.server.ServiceException;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServlet;
import com.vaadin.flow.server.VaadinServletService;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.SpringServlet;
import com.vaadin.flow.spring.SpringVaadinServletService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.lang.reflect.Method;

import kotlin.jvm.functions.Function0;

import static com.github.karibu.testing.v10.LocatorJ._get;
import static com.github.karibu.testing.v10.LocatorJ._setValue;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
@DirtiesContext
public class ApplicationTests {

    @Autowired
    private ApplicationContext ctx;

    @Before
    public void setup() throws Exception {
        final SpringServlet servlet = new SpringServlet(ctx) {
            @Override
            protected VaadinServletService createServletService(DeploymentConfiguration deploymentConfiguration) throws ServiceException {
                final VaadinServletService service = new SpringVaadinServletService(this, deploymentConfiguration, ctx) {
                    @Override
                    protected boolean isAtmosphereAvailable() {
                        return false;
                    }

                    @Override
                    public String getMainDivId(VaadinSession session, VaadinRequest request) {
                        return "ROOT-1";
                    }
                };
                service.init();
                return service;
            }
        };
        final MockContext mockContext = new MockContext();
        servlet.init(new MockServletConfig(mockContext));
        VaadinService.setCurrent(servlet.getService());

        final Method createSession = MockVaadin.class.getDeclaredMethod("createSession", MockContext.class, VaadinServlet.class, Function0.class);
        createSession.setAccessible(true);
        createSession.invoke(MockVaadin.INSTANCE, mockContext, servlet, (Function0<UI>) () -> new MockedUI());
    }

    @After
    public void tearDown() {
        MockVaadin.tearDown();
    }

    @Test
    public void smokeTest() {
        _setValue(_get(TextField.class), "Foo");
    }
}
