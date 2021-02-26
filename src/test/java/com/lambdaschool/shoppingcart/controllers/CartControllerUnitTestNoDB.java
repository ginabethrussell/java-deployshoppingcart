package com.lambdaschool.shoppingcart.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lambdaschool.shoppingcart.ShoppingCartApplication;
import com.lambdaschool.shoppingcart.models.*;
import com.lambdaschool.shoppingcart.services.CartItemService;
import com.lambdaschool.shoppingcart.services.UserService;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.StatusResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
@RunWith(SpringRunner.class)
@WithMockUser(username = "admin",
    roles = {"USER", "ADMIN"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
classes = ShoppingCartApplication.class)

@AutoConfigureMockMvc
public class CartControllerUnitTestNoDB
{

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @MockBean
    private CartItemService cartItemService;

    @MockBean
    private UserService userService;

    private List<Product> productList;
    private List<User> userList;

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

        User u1 = new User("admin",
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

        RestAssuredMockMvc.webAppContextSetup(webApplicationContext);

        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .apply(SecurityMockMvcConfigurers.springSecurity())
            .build();

    }

    @After
    public void tearDown() throws Exception
    {
    }

    @Test
    public void listCartItemsByAuthenticatedUser() throws Exception
    {
        String apiUrl = "/carts/";
        Mockito.when(userService.findByName("admin"))
            .thenReturn(userList.get(0));

        RequestBuilder rb = MockMvcRequestBuilders.get(apiUrl)
            .accept(MediaType.APPLICATION_JSON);
        MvcResult r = mockMvc.perform(rb)
            .andReturn();
        String tr = r.getResponse()
            .getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        String er = mapper.writeValueAsString(userList.get(0));

        System.out.println(tr);
        assertEquals(er,
            tr);
    }

    @Test
    public void addToCart() throws Exception
    {
        String apiUrl = "/carts/add/product/{productid}";
        Mockito.when(userService.findByName("admin"))
            .thenReturn(userList.get(0));

        CartItem cartItem = new CartItem();
        cartItem.setUser(userList.get(0));
        cartItem.setQuantity(1);
        cartItem.setComments("add");
        cartItem.setProduct(productList.get(0));

       Mockito.when(cartItemService.addToCart(userList.get(0).getUserid(), productList.get(0).getProductid(), "add"))
           .thenReturn(cartItem);

       RequestBuilder rb = MockMvcRequestBuilders.put(apiUrl, 1L)
           .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON);

       mockMvc.perform(rb)
            .andExpect(status().isOk());
       
    }


}