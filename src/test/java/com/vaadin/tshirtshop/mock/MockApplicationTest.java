package com.vaadin.tshirtshop.mock;

import static com.github.mvysny.kaributesting.v10.GridKt.expectRow;
import static com.github.mvysny.kaributesting.v10.GridKt.expectRows;
import static com.github.mvysny.kaributesting.v10.LocatorJ._click;
import static com.github.mvysny.kaributesting.v10.LocatorJ._get;
import static com.github.mvysny.kaributesting.v10.LocatorJ._setValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.github.mvysny.kaributesting.v10.MockVaadin;
import com.github.mvysny.kaributesting.v10.Routes;
import com.github.mvysny.kaributesting.v10.spring.MockSpringServlet;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.spring.SpringServlet;
import com.vaadin.tshirtshop.Application;
import com.vaadin.tshirtshop.ListOrdersView;
import com.vaadin.tshirtshop.TShirtService;
import com.vaadin.tshirtshop.domain.TShirtOrder;
import com.vaadin.tshirtshop.domain.TShirtOrderRepository;
import java.util.List;
import kotlin.jvm.functions.Function0;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Uses Karibu-Testing to test the app itself. First, {@link #routes} are auto-discovered;
 * then a specially mocked {@link MockSpringServlet} is used to setup Karibu-Testing
 * properly and allow Vaadin to work in mocked environment.
 */
@SpringBootTest
@DirtiesContext
@ActiveProfiles("test-karibu")
@ExtendWith(SpringExtension.class)
public class MockApplicationTest {

    private static Routes routes;
    @BeforeAll
    public static void discoverRoutes() {
         routes = new Routes().autoDiscoverViews("com.vaadin.tshirtshop");
    }

    @Autowired
    private ApplicationContext ctx;

    @Autowired
    private TShirtService tShirtService;

    @BeforeEach
    public void setup() {
        final Function0<UI> uiFactory = UI::new;
        final SpringServlet servlet = new MockSpringServlet(routes, ctx, uiFactory);
        MockVaadin.setup(uiFactory, servlet);
    }

    @AfterEach
    public void tearDown() {
        MockVaadin.tearDown();
    }

    @Test
    public void placeOrder() {
        final ArgumentCaptor<TShirtOrder> captor = ArgumentCaptor.forClass(TShirtOrder.class);

        _setValue(_get(TextField.class, spec -> spec.withCaption("Name")), "Foo");
        _setValue(_get(TextField.class, spec -> spec.withCaption("Email")), "foo@bar.baz");
        _setValue(_get(ComboBox.class, spec -> spec.withCaption("T-shirt size")), "Small");
        _click(_get(Button.class, spec -> spec.withCaption("Place order")));

        verify(tShirtService, times(1)).getSizes();
        verify(tShirtService, times(1)).placeOrder(captor.capture());
        assertEquals(captor.getValue().getName(), "Foo");
        assertEquals(captor.getValue().getEmail(), "foo@bar.baz");
        assertEquals(captor.getValue().getShirtSize(), "Small");
    }

}
