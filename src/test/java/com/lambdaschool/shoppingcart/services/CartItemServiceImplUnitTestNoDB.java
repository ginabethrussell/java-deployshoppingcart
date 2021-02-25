package com.lambdaschool.shoppingcart.services;

import com.lambdaschool.shoppingcart.ShoppingCartApplication;
import com.lambdaschool.shoppingcart.models.*;
import com.lambdaschool.shoppingcart.repository.CartItemRepository;
import com.lambdaschool.shoppingcart.repository.ProductRepository;
import com.lambdaschool.shoppingcart.repository.UserRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ShoppingCartApplication.class)
public class CartItemServiceImplUnitTestNoDB
{
    @Autowired
    private CartItemService cartItemService;

    @MockBean
    private UserRepository userrepos;

    @MockBean
    private ProductRepository prodrepos;

    @MockBean
    private CartItemRepository cartitemrepos;

    private List<Product> productList;
    private List<User> userList;
    private List<CartItem> cartItemList;

    @Before
    public void setUp() throws Exception
    {
        productList = new ArrayList<>();

        Product p1 = new Product();
        p1.setProductid(1);
        p1.setName("Pen");
        p1.setComments("Makes words");
        p1.setPrice(1.5);
        productList.add(p1);

        Product p2 = new Product();
        p1.setProductid(2);
        p1.setName("Pencil");
        p1.setComments("Erases words");
        p1.setPrice(1.0);
        productList.add(p2);

        Product p3 = new Product();
        p1.setProductid(3);
        p1.setName("Marker");
        p1.setComments("Permanent marks");
        p1.setPrice(2.0);
        productList.add(p3);

        userList = new ArrayList<>();

        Role r1 = new Role("admin");
        Role r2 = new Role("user");
        r1.setRoleid(1);
        r2.setRoleid(2);

        User u1 = new User("dexter",
            "password",
            "admin@lambdaschool.local",
            "I love bananas");
        u1.setUserid(10);
        u1.getRoles()
            .add(new UserRoles(u1,
                r1));
        u1.getRoles()
            .add(new UserRoles(u1,
                r2));

        User u2 = new User("mrpotatohead",
            "password",
            "yam@lambdaschool.local",
            "I yam in charge");
        u2.setUserid(20);
        u2.getRoles()
            .add(new UserRoles(u2,
                r2));

        CartItem c1 = new CartItem();
        c1.setUser(u1);
        c1.setProduct(p1);
        c1.setComments("order now");
        c1.setQuantity(1);

        u1.getCarts().add(c1);
        u2.getCarts().add(c1);

        userList.add(u1);
        userList.add(u2);

        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() throws Exception
    {
    }

    @Test
    public void addToCart()
    {

        CartItem cartItem = new CartItem();
        cartItem.setUser(userList.get(0));
        cartItem.setQuantity(1);
        cartItem.setComments("add");
        cartItem.setProduct(productList.get(0));

        Mockito.when(userrepos.findById(10L))
            .thenReturn(Optional.of(userList.get(0)));

        Mockito.when(prodrepos.findById(1L))
            .thenReturn(Optional.of(productList.get(0)));

        Mockito.when(cartitemrepos.findById(any(CartItemId.class)))
            .thenReturn(Optional.of(cartItem));

        Mockito.when(cartitemrepos.save(any(CartItem.class))).thenReturn(cartItem);

        CartItem rtnItem = cartItemService.addToCart(10,
            1,
            "add");

        assertEquals( 2, rtnItem.getQuantity());
        
    }

    @Test
    public void removeFromCart()
    {
    }
}