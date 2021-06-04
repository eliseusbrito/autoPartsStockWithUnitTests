package com.eliseubrito.partstock.service;

import com.eliseubrito.partstock.dto.PartDTO;
import com.eliseubrito.partstock.entity.Part;
import com.eliseubrito.partstock.exception.PartAlreadyRegisteredException;
import com.eliseubrito.partstock.exception.PartNotFoundException;
import com.eliseubrito.partstock.exception.PartStockExceededException;
import com.eliseubrito.partstock.mapper.PartMapper;
import com.eliseubrito.partstock.repository.PartRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class PartService {

    private final PartRepository partRepository;
    private final PartMapper partMapper = PartMapper.INSTANCE;
    private final Integer quantidadeMinima = 0;

    public PartDTO createPart(PartDTO partDTO) throws PartAlreadyRegisteredException {
        verifyIfIsAlreadyRegistered(partDTO.getName());
        Part part = partMapper.toModel(partDTO);
        Part savedPart = partRepository.save(part);
        return partMapper.toDTO(savedPart);
    }

    public PartDTO findByName(String name) throws PartNotFoundException {
        Part foundPart = partRepository.findByName(name)
                .orElseThrow(() -> new PartNotFoundException(name));
        return partMapper.toDTO(foundPart);
    }

    public List<PartDTO> listAll() {
        return partRepository.findAll()
                .stream()
                .map(partMapper::toDTO)
                .collect(Collectors.toList());
    }

    public void deleteById(Long id) throws PartNotFoundException {
        verifyIfExists(id);
        partRepository.deleteById(id);
    }

    private void verifyIfIsAlreadyRegistered(String name) throws PartAlreadyRegisteredException {
        Optional<Part> optSavedPart = partRepository.findByName(name);
        if (optSavedPart.isPresent()) {
            throw new PartAlreadyRegisteredException(name);
        }
    }

    private Part verifyIfExists(Long id) throws PartNotFoundException {
        return partRepository.findById(id)
                .orElseThrow(() -> new PartNotFoundException(id));
    }

    public PartDTO increment(Long id, int quantityToIncrement) throws PartNotFoundException, PartStockExceededException {
        Part partToIncrementStock = verifyIfExists(id);
        int quantityAfterIncrement = quantityToIncrement + partToIncrementStock.getQuantity();
        if (quantityAfterIncrement <= partToIncrementStock.getMax()) {
            partToIncrementStock.setQuantity(partToIncrementStock.getQuantity() + quantityToIncrement);
            Part incrementedPartStock = partRepository.save(partToIncrementStock);
            return partMapper.toDTO(incrementedPartStock);
        }
        throw new PartStockExceededException(id, quantityToIncrement);
    }

    public PartDTO decrement(Long id, int quantityToDecrement) throws PartNotFoundException, PartStockExceededException {
        Part partToDecrementStock = verifyIfExists(id);
        int quantityAfterDecrement = quantityToDecrement - partToDecrementStock.getQuantity();
        if (quantityAfterDecrement <= quantidadeMinima) {
            partToDecrementStock.setQuantity(partToDecrementStock.getQuantity() - quantityToDecrement);
            Part decrementedPartStock = partRepository.save(partToDecrementStock);
            return partMapper.toDTO(decrementedPartStock);
        }
        throw new PartStockExceededException(id, quantityToDecrement);
    }

}
