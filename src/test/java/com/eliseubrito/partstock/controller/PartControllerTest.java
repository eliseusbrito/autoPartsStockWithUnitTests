package com.eliseubrito.partstock.controller;

import com.eliseubrito.partstock.builder.PartDTOBuilder;
import com.eliseubrito.partstock.dto.PartDTO;
import com.eliseubrito.partstock.dto.QuantityDTO;
import com.eliseubrito.partstock.exception.PartNotFoundException;
import com.eliseubrito.partstock.exception.PartStockExceededException;
import com.eliseubrito.partstock.service.PartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import java.util.Collections;

import static com.eliseubrito.partstock.utils.JsonConvertionUtils.asJsonString;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class PartControllerTest {

    private static final String PART_API_URL_PATH = "/api/v1/parts";
    private static final long VALID_PART_ID = 1L;
    private static final long INVALID_PART_ID = 2l;
    private static final String PART_API_SUBPATH_INCREMENT_URL = "/increment";
    private static final String PART_API_SUBPATH_DECREMENT_URL = "/decrement";

    private MockMvc mockMvc;

    @Mock
    private PartService partService;

    @InjectMocks
    private PartController partController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(partController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setViewResolvers((s, locale) -> new MappingJackson2JsonView())
                .build();
    }

    @Test
    void whenPOSTIsCalledThenAPartIsCreated() throws Exception {
        // given
        PartDTO partDTO = PartDTOBuilder.builder().build().toPartDTO();

        // when
        when(partService.createPart(partDTO)).thenReturn(partDTO);

        // then
        mockMvc.perform(post(PART_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(partDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(partDTO.getName())))
                .andExpect(jsonPath("$.brand", is(partDTO.getBrand())))
                .andExpect(jsonPath("$.type", is(partDTO.getType().toString())));
    }

    @Test
    void whenPOSTIsCalledWithoutRequiredFieldThenAnErrorIsReturned() throws Exception {
        // given
        PartDTO partDTO = PartDTOBuilder.builder().build().toPartDTO();
        partDTO.setBrand(null);

        // then
        mockMvc.perform(post(PART_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(partDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenGETIsCalledWithValidNameThenOkStatusIsReturned() throws Exception {
        // given
        PartDTO partDTO = PartDTOBuilder.builder().build().toPartDTO();

        //when
        when(partService.findByName(partDTO.getName())).thenReturn(partDTO);

        // then
        mockMvc.perform(MockMvcRequestBuilders.get(PART_API_URL_PATH + "/" + partDTO.getName())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(partDTO.getName())))
                .andExpect(jsonPath("$.brand", is(partDTO.getBrand())))
                .andExpect(jsonPath("$.type", is(partDTO.getType().toString())));
    }

    @Test
    void whenGETIsCalledWithoutRegisteredNameThenNotFoundStatusIsReturned() throws Exception {
        // given
        PartDTO partDTO = PartDTOBuilder.builder().build().toPartDTO();

        //when
        when(partService.findByName(partDTO.getName())).thenThrow(PartNotFoundException.class);

        // then
        mockMvc.perform(MockMvcRequestBuilders.get(PART_API_URL_PATH + "/" + partDTO.getName())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenGETListWithPartsIsCalledThenOkStatusIsReturned() throws Exception {
        // given
        PartDTO partDTO = PartDTOBuilder.builder().build().toPartDTO();

        //when
        when(partService.listAll()).thenReturn(Collections.singletonList(partDTO));

        // then
        mockMvc.perform(MockMvcRequestBuilders.get(PART_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is(partDTO.getName())))
                .andExpect(jsonPath("$[0].brand", is(partDTO.getBrand())))
                .andExpect(jsonPath("$[0].type", is(partDTO.getType().toString())));
    }

    @Test
    void whenGETListWithoutPartsIsCalledThenOkStatusIsReturned() throws Exception {
        // given
        PartDTO partDTO = PartDTOBuilder.builder().build().toPartDTO();

        //when
        when(partService.listAll()).thenReturn(Collections.singletonList(partDTO));

        // then
        mockMvc.perform(MockMvcRequestBuilders.get(PART_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void whenDELETEIsCalledWithValidIdThenNoContentStatusIsReturned() throws Exception {
        // given
        PartDTO partDTO = PartDTOBuilder.builder().build().toPartDTO();

        //when
        doNothing().when(partService).deleteById(partDTO.getId());

        // then
        mockMvc.perform(MockMvcRequestBuilders.delete(PART_API_URL_PATH + "/" + partDTO.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void whenDELETEIsCalledWithInvalidIdThenNotFoundStatusIsReturned() throws Exception {
        //when
        doThrow(PartNotFoundException.class).when(partService).deleteById(INVALID_PART_ID);

        // then
        mockMvc.perform(MockMvcRequestBuilders.delete(PART_API_URL_PATH + "/" + INVALID_PART_ID)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenPATCHIsCalledToIncrementDiscountThenOKstatusIsReturned() throws Exception {
        QuantityDTO quantityDTO = QuantityDTO.builder()
                .quantity(10)
                .build();

        PartDTO partDTO = PartDTOBuilder.builder().build().toPartDTO();
        partDTO.setQuantity(partDTO.getQuantity() + quantityDTO.getQuantity());

        when(partService.increment(VALID_PART_ID, quantityDTO.getQuantity())).thenReturn(partDTO);

        mockMvc.perform(MockMvcRequestBuilders.patch(PART_API_URL_PATH + "/" + VALID_PART_ID + PART_API_SUBPATH_INCREMENT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantityDTO))).andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(partDTO.getName())))
                .andExpect(jsonPath("$.brand", is(partDTO.getBrand())))
                .andExpect(jsonPath("$.type", is(partDTO.getType().toString())))
                .andExpect(jsonPath("$.quantity", is(partDTO.getQuantity())));
    }

    @Test
    void whenPATCHIsCalledToIncrementGreatherThanMaxThenBadRequestStatusIsReturned() throws Exception {
        QuantityDTO quantityDTO = QuantityDTO.builder()
                .quantity(30)
                .build();

        PartDTO partDTO = PartDTOBuilder.builder().build().toPartDTO();
        partDTO.setQuantity(partDTO.getQuantity() + quantityDTO.getQuantity());

        when(partService.increment(VALID_PART_ID, quantityDTO.getQuantity())).thenThrow(PartStockExceededException.class);

        mockMvc.perform(MockMvcRequestBuilders.patch(PART_API_URL_PATH + "/" + VALID_PART_ID + PART_API_SUBPATH_INCREMENT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantityDTO))).andExpect(status().isBadRequest());
    }

    @Test

    void whenPATCHIsCalledWithInvalidPartIdToIncrementThenNotFoundStatusIsReturned() throws Exception {
        QuantityDTO quantityDTO = QuantityDTO.builder()
                .quantity(30)
                .build();

        when(partService.increment(INVALID_PART_ID, quantityDTO.getQuantity())).thenThrow(PartNotFoundException.class);
        mockMvc.perform(MockMvcRequestBuilders.patch(PART_API_URL_PATH + "/" + INVALID_PART_ID + PART_API_SUBPATH_INCREMENT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantityDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void whenPATCHIsCalledToDecrementDiscountThenOKstatusIsReturned() throws Exception {
        QuantityDTO quantityDTO = QuantityDTO.builder()
                .quantity(5)
                .build();

        PartDTO partDTO = PartDTOBuilder.builder().build().toPartDTO();
        partDTO.setQuantity(partDTO.getQuantity() + quantityDTO.getQuantity());

        when(partService.decrement(VALID_PART_ID, quantityDTO.getQuantity())).thenReturn(partDTO);

        mockMvc.perform(MockMvcRequestBuilders.patch(PART_API_URL_PATH + "/" + VALID_PART_ID + PART_API_SUBPATH_DECREMENT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantityDTO))).andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(partDTO.getName())))
                .andExpect(jsonPath("$.brand", is(partDTO.getBrand())))
                .andExpect(jsonPath("$.type", is(partDTO.getType().toString())))
                .andExpect(jsonPath("$.quantity", is(partDTO.getQuantity())));
    }

    @Test
    void whenPATCHIsCalledToDEcrementLowerThanZeroThenBadRequestStatusIsReturned() throws Exception {
        QuantityDTO quantityDTO = QuantityDTO.builder()
                .quantity(60)
                .build();

        PartDTO partDTO = PartDTOBuilder.builder().build().toPartDTO();
        partDTO.setQuantity(partDTO.getQuantity() + quantityDTO.getQuantity());

        when(partService.decrement(VALID_PART_ID, quantityDTO.getQuantity())).thenThrow(PartStockExceededException.class);

        mockMvc.perform(MockMvcRequestBuilders.patch(PART_API_URL_PATH + "/" + VALID_PART_ID + PART_API_SUBPATH_DECREMENT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantityDTO))).andExpect(status().isBadRequest());
    }

    @Test
    void whenPATCHIsCalledWithInvalidPartIdToDecrementThenNotFoundStatusIsReturned() throws Exception {
        QuantityDTO quantityDTO = QuantityDTO.builder()
                .quantity(5)
                .build();

        when(partService.decrement(INVALID_PART_ID, quantityDTO.getQuantity())).thenThrow(PartNotFoundException.class);
        mockMvc.perform(MockMvcRequestBuilders.patch(PART_API_URL_PATH + "/" + INVALID_PART_ID + PART_API_SUBPATH_DECREMENT_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(quantityDTO)))
                .andExpect(status().isNotFound());
    }
}
