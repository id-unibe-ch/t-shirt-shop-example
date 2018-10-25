package com.vaadin.tshirtshop;

import com.github.karibu.testing.v10.MockVaadin;
import com.github.karibu.testing.v10.MockedUI;
import com.github.karibu.testing.v10.Routes;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.function.DeploymentConfiguration;
import com.vaadin.flow.server.ServiceException;
import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinServletService;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.server.startup.RouteRegistry;
import com.vaadin.flow.spring.SpringServlet;
import com.vaadin.flow.spring.SpringVaadinServletService;
import com.vaadin.tshirtshop.domain.TShirtOrder;
import com.vaadin.tshirtshop.domain.TShirtOrderRepository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

import static com.github.karibu.testing.v10.LocatorJ._click;
import static com.github.karibu.testing.v10.LocatorJ._get;
import static com.github.karibu.testing.v10.LocatorJ._setValue;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
@DirtiesContext
public class ApplicationTests {

    @Autowired
    private ApplicationContext ctx;

    @Autowired
    private TShirtOrderRepository repo;

    @Before
    public void setup() throws Exception {
        final RouteRegistry registry = new Routes().autoDiscoverViews("com.vaadin.tshirtshop").createRegistry();
        final SpringServlet servlet = new SpringServlet(ctx) {
            @Override
            protected VaadinServletService createServletService(DeploymentConfiguration deploymentConfiguration) throws ServiceException {
                final VaadinServletService service = new SpringVaadinServletService(this, deploymentConfiguration, ctx) {
                    @Override
                    protected boolean isAtmosphereAvailable() {
                        return false;
                    }

                    @Override
                    protected RouteRegistry getRouteRegistry() {
                        return registry;
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
        MockVaadin.setup(MockedUI::new, servlet);
    }

    @After
    public void tearDown() {
        MockVaadin.tearDown();
    }

    @Test
    public void smokeTest() {
        _setValue(_get(TextField.class, spec -> spec.withCaption("Name")), "Foo");
        _setValue(_get(TextField.class, spec -> spec.withCaption("Email")), "foo@bar.baz");
        _setValue(_get(ComboBox.class, spec -> spec.withCaption("T-shirt size")), "Small");
        _click(_get(Button.class, spec -> spec.withCaption("Place order")));

        final List<TShirtOrder> all = repo.findAll();
        assertEquals("orders=" + all, 1, all.size());
        assertEquals("Foo", all.get(0).getName());
    }
}
