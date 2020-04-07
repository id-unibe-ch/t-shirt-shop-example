package com.vaadin.tshirtshop;

import com.github.mvysny.kaributesting.v10.*;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.SpringServlet;
import com.vaadin.tshirtshop.domain.TShirtOrder;
import com.vaadin.tshirtshop.domain.TShirtOrderRepository;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

import static com.github.mvysny.kaributesting.v10.GridKt.*;
import static com.github.mvysny.kaributesting.v10.LocatorJ.*;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
@DirtiesContext
public class ApplicationTests {

    private static Routes routes;
    @BeforeClass
    public static void discoverRoutes() {
         routes = new Routes().autoDiscoverViews("com.vaadin.tshirtshop");
    }

    @Autowired
    private ApplicationContext ctx;

    @Autowired
    private TShirtOrderRepository repo;

    @Before
    public void setup() throws Exception {
        final SpringServlet servlet = new MockSpringServlet(routes, ctx);
        MockVaadin.setup(MockedUI::new, servlet);
        repo.deleteAll();
    }

    @After
    public void tearDown() {
        MockVaadin.tearDown();
    }

    @Test
    public void placeOrder() {
        _setValue(_get(TextField.class, spec -> spec.withCaption("Name")), "Foo");
        _setValue(_get(TextField.class, spec -> spec.withCaption("Email")), "foo@bar.baz");
        _setValue(_get(ComboBox.class, spec -> spec.withCaption("T-shirt size")), "Small");
        _click(_get(Button.class, spec -> spec.withCaption("Place order")));

        final List<TShirtOrder> all = repo.findAll();
        assertEquals("orders=" + all, 1, all.size());
        assertEquals("Foo", all.get(0).getName());
    }

    @Test
    public void listOrders() {
        UI.getCurrent().navigate(ListOrdersView.class);
        expectRows(_get(Grid.class), 0);
    }
}
