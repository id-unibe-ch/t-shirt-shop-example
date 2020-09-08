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

import kotlin.jvm.functions.Function0;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

import static com.github.mvysny.kaributesting.v10.GridKt.*;
import static com.github.mvysny.kaributesting.v10.LocatorJ.*;
import static org.junit.Assert.assertEquals;

/**
 * Uses Karibu-Testing to test the app itself. First, {@link #routes} are auto-discovered;
 * then a specially mocked {@link MockSpringServlet} is used to setup Karibu-Testing
 * properly and allow Vaadin to work in mocked environment.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@WebAppConfiguration
@DirtiesContext
public class ApplicationTest {

    private static Routes routes;
    @BeforeAll
    public static void discoverRoutes() {
         routes = new Routes().autoDiscoverViews("com.vaadin.tshirtshop");
    }

    @Autowired
    private ApplicationContext ctx;

    @Autowired
    private TShirtOrderRepository repo;

    @BeforeEach
    public void setup() {
        final Function0<UI> uiFactory = UI::new;
        final SpringServlet servlet = new MockSpringServlet(routes, ctx, uiFactory);
        MockVaadin.setup(uiFactory, servlet);
        repo.deleteAll();
    }

    @AfterEach
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
        // initially the Grid is empty
        UI.getCurrent().navigate(ListOrdersView.class);
        expectRows(_get(Grid.class), 0);

        // test the Grid with a single row. First, create a dummy order
        final TShirtOrder order = new TShirtOrder();
        order.setName("Foo");
        order.setEmail("foo@bar.baz");
        order.setShirtSize("Small");
        repo.save(order);
        // reload the page in order to refresh the Grid
        UI.getCurrent().getPage().reload();
        // now assert that the Grid has one row
        expectRows(_get(Grid.class), 1);
        expectRow(_get(Grid.class), 0, "Foo", "foo@bar.baz", "Small", "Button[icon='vaadin:trash']");
    }
}
