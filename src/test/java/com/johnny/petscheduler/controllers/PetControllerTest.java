package com.johnny.petscheduler.controllers;

import com.johnny.petscheduler.services.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PetController.class)
public class PetControllerTest
{
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PetService petService;


}
