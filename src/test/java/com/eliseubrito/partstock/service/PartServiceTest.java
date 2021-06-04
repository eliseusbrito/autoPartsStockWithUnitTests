package com.eliseubrito.partstock.service;

import com.eliseubrito.partstock.builder.PartDTOBuilder;
import com.eliseubrito.partstock.dto.PartDTO;
import com.eliseubrito.partstock.entity.Part;
import com.eliseubrito.partstock.exception.PartAlreadyRegisteredException;
import com.eliseubrito.partstock.exception.PartNotFoundException;
import com.eliseubrito.partstock.exception.PartStockExceededException;
import com.eliseubrito.partstock.mapper.PartMapper;
import com.eliseubrito.partstock.repository.PartRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PartServiceTest {

    private static final long INVALID_PART_ID = 1L;

    @Mock
    private PartRepository partRepository;

    private PartMapper partMapper = PartMapper.INSTANCE;

    @InjectMocks
    private PartService partService;

    @Test
    void whenPartInformedThenItShouldBeCreated() throws PartAlreadyRegisteredException {
        // given
        PartDTO expectedPartDTO = PartDTOBuilder.builder().build().toPartDTO();
        Part expectedSavedPart = partMapper.toModel(expectedPartDTO);

        // when
        when(partRepository.findByName(expectedPartDTO.getName())).thenReturn(Optional.empty());
        when(partRepository.save(expectedSavedPart)).thenReturn(expectedSavedPart);

        //then
        PartDTO createdPartDTO = partService.createPart(expectedPartDTO);

        assertThat(createdPartDTO.getId(), is(equalTo(expectedPartDTO.getId())));
        assertThat(createdPartDTO.getName(), is(equalTo(expectedPartDTO.getName())));
        assertThat(createdPartDTO.getQuantity(), is(equalTo(expectedPartDTO.getQuantity())));
    }

    @Test
    void whenAlreadyRegisteredPartInformedThenAnExceptionShouldBeThrown() {
        // given
        PartDTO expectedPartDTO = PartDTOBuilder.builder().build().toPartDTO();
        Part duplicatedPart = partMapper.toModel(expectedPartDTO);

        // when
        when(partRepository.findByName(expectedPartDTO.getName())).thenReturn(Optional.of(duplicatedPart));

        // then
        assertThrows(PartAlreadyRegisteredException.class, () -> partService.createPart(expectedPartDTO));
    }

    @Test
    void whenValidPartNameIsGivenThenReturnAPart() throws PartNotFoundException {
        // given
        PartDTO expectedFoundPartDTO = PartDTOBuilder.builder().build().toPartDTO();
        Part expectedFoundPart = partMapper.toModel(expectedFoundPartDTO);

        // when
        when(partRepository.findByName(expectedFoundPart.getName())).thenReturn(Optional.of(expectedFoundPart));

        // then
        PartDTO foundPartDTO = partService.findByName(expectedFoundPartDTO.getName());

        assertThat(foundPartDTO, is(equalTo(expectedFoundPartDTO)));
    }

    @Test
    void whenNotRegisteredPartNameIsGivenThenThrowAnException() {
        // given
        PartDTO expectedFoundPartDTO = PartDTOBuilder.builder().build().toPartDTO();

        // when
        when(partRepository.findByName(expectedFoundPartDTO.getName())).thenReturn(Optional.empty());

        // then
        assertThrows(PartNotFoundException.class, () -> partService.findByName(expectedFoundPartDTO.getName()));
    }

    @Test
    void whenListPartIsCalledThenReturnAListOfParts() {
        // given
        PartDTO expectedFoundPartDTO = PartDTOBuilder.builder().build().toPartDTO();
        Part expectedFoundPart = partMapper.toModel(expectedFoundPartDTO);

        //when
        when(partRepository.findAll()).thenReturn(Collections.singletonList(expectedFoundPart));

        //then
        List<PartDTO> foundListPartsDTO = partService.listAll();

        assertThat(foundListPartsDTO, is(not(empty())));
        assertThat(foundListPartsDTO.get(0), is(equalTo(expectedFoundPartDTO)));
    }

    @Test
    void whenListPartIsCalledThenReturnAnEmptyListOfParts() {
        //when
        when(partRepository.findAll()).thenReturn(Collections.EMPTY_LIST);

        //then
        List<PartDTO> foundListPartsDTO = partService.listAll();

        assertThat(foundListPartsDTO, is(empty()));
    }

    @Test
    void whenExclusionIsCalledWithValidIdThenAPartShouldBeDeleted() throws PartNotFoundException {
        // given
        PartDTO expectedDeletedPartDTO = PartDTOBuilder.builder().build().toPartDTO();
        Part expectedDeletedPart = partMapper.toModel(expectedDeletedPartDTO);

        // when
        when(partRepository.findById(expectedDeletedPartDTO.getId())).thenReturn(Optional.of(expectedDeletedPart));
        doNothing().when(partRepository).deleteById(expectedDeletedPartDTO.getId());

        // then
        partService.deleteById(expectedDeletedPartDTO.getId());

        verify(partRepository, times(1)).findById(expectedDeletedPartDTO.getId());
        verify(partRepository, times(1)).deleteById(expectedDeletedPartDTO.getId());
    }

    @Test
    void whenIncrementIsCalledThenIncrementPartStock() throws PartNotFoundException, PartStockExceededException {
        //given
        PartDTO expectedPartDTO = PartDTOBuilder.builder().build().toPartDTO();
        Part expectedPart = partMapper.toModel(expectedPartDTO);

        //when
        when(partRepository.findById(expectedPartDTO.getId())).thenReturn(Optional.of(expectedPart));
        when(partRepository.save(expectedPart)).thenReturn(expectedPart);

        int quantityToIncrement = 10;
        int expectedQuantityAfterIncrement = expectedPartDTO.getQuantity() + quantityToIncrement;

        // then
        PartDTO incrementedPartDTO = partService.increment(expectedPartDTO.getId(), quantityToIncrement);

        assertThat(expectedQuantityAfterIncrement, equalTo(incrementedPartDTO.getQuantity()));
        assertThat(expectedQuantityAfterIncrement, lessThan(expectedPartDTO.getMax()));
    }

    @Test
    void whenIncrementIsGreatherThanMaxThenThrowException() {
        PartDTO expectedPartDTO = PartDTOBuilder.builder().build().toPartDTO();
        Part expectedPart = partMapper.toModel(expectedPartDTO);

        when(partRepository.findById(expectedPartDTO.getId())).thenReturn(Optional.of(expectedPart));

        int quantityToIncrement = 80;
        assertThrows(PartStockExceededException.class, () -> partService.increment(expectedPartDTO.getId(), quantityToIncrement));
    }

    @Test
    void whenIncrementAfterSumIsGreatherThanMaxThenThrowException() {
        PartDTO expectedPartDTO = PartDTOBuilder.builder().build().toPartDTO();
        Part expectedPart = partMapper.toModel(expectedPartDTO);

        when(partRepository.findById(expectedPartDTO.getId())).thenReturn(Optional.of(expectedPart));

        int quantityToIncrement = 45;
        assertThrows(PartStockExceededException.class, () -> partService.increment(expectedPartDTO.getId(), quantityToIncrement));
    }

    @Test
    void whenIncrementIsCalledWithInvalidIdThenThrowException() {
        int quantityToIncrement = 10;

        when(partRepository.findById(INVALID_PART_ID)).thenReturn(Optional.empty());

        assertThrows(PartNotFoundException.class, () -> partService.increment(INVALID_PART_ID, quantityToIncrement));
    }

    @Test
    void whenDecrementIsCalledThenDecrementPartStock() throws PartNotFoundException, PartStockExceededException {
        PartDTO expectedPartDTO = PartDTOBuilder.builder().build().toPartDTO();
        Part expectedPart = partMapper.toModel(expectedPartDTO);

        when(partRepository.findById(expectedPartDTO.getId())).thenReturn(Optional.of(expectedPart));
        when(partRepository.save(expectedPart)).thenReturn(expectedPart);

        int quantityToDecrement = 5;
        int expectedQuantityAfterDecrement = expectedPartDTO.getQuantity() - quantityToDecrement;
        PartDTO incrementedPartDTO = partService.decrement(expectedPartDTO.getId(), quantityToDecrement);

        assertThat(expectedQuantityAfterDecrement, equalTo(incrementedPartDTO.getQuantity()));
        assertThat(expectedQuantityAfterDecrement, greaterThan(0));
    }

    @Test
    void whenDecrementIsCalledToEmptyStockThenEmptyPartStock() throws PartNotFoundException, PartStockExceededException {
        PartDTO expectedPartDTO = PartDTOBuilder.builder().build().toPartDTO();
        Part expectedPart = partMapper.toModel(expectedPartDTO);

        when(partRepository.findById(expectedPartDTO.getId())).thenReturn(Optional.of(expectedPart));
        when(partRepository.save(expectedPart)).thenReturn(expectedPart);

        int quantityToDecrement = 10;
        int expectedQuantityAfterDecrement = expectedPartDTO.getQuantity() - quantityToDecrement;
        PartDTO incrementedPartDTO = partService.decrement(expectedPartDTO.getId(), quantityToDecrement);

        assertThat(expectedQuantityAfterDecrement, equalTo(0));
        assertThat(expectedQuantityAfterDecrement, equalTo(incrementedPartDTO.getQuantity()));
    }

    @Test
    void whenDecrementIsLowerThanZeroThenThrowException() {
        PartDTO expectedPartDTO = PartDTOBuilder.builder().build().toPartDTO();
        Part expectedPart = partMapper.toModel(expectedPartDTO);

        when(partRepository.findById(expectedPartDTO.getId())).thenReturn(Optional.of(expectedPart));

        int quantityToDecrement = 80;
        assertThrows(PartStockExceededException.class, () -> partService.decrement(expectedPartDTO.getId(), quantityToDecrement));
    }

    @Test
    void whenDecrementIsCalledWithInvalidIdThenThrowException() {
        int quantityToDecrement = 10;

        when(partRepository.findById(INVALID_PART_ID)).thenReturn(Optional.empty());

        assertThrows(PartNotFoundException.class, () -> partService.decrement(INVALID_PART_ID, quantityToDecrement));
    }

}
