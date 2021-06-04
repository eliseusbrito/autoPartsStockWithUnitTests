package com.eliseubrito.partstock.controller;

import lombok.AllArgsConstructor;
import com.eliseubrito.partstock.dto.PartDTO;
import com.eliseubrito.partstock.dto.QuantityDTO;
import com.eliseubrito.partstock.exception.PartAlreadyRegisteredException;
import com.eliseubrito.partstock.exception.PartNotFoundException;
import com.eliseubrito.partstock.exception.PartStockExceededException;
import com.eliseubrito.partstock.service.PartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/parts")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class PartController implements PartControllerDocs {

    private final PartService partService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PartDTO createPart(@RequestBody @Valid PartDTO partDTO) throws PartAlreadyRegisteredException {
        return partService.createPart(partDTO);
    }

    @GetMapping("/{name}")
    public PartDTO findByName(@PathVariable String name) throws PartNotFoundException {
        return partService.findByName(name);
    }

    @GetMapping
    public List<PartDTO> listParts() {
        return partService.listAll();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long id) throws PartNotFoundException {
        partService.deleteById(id);
    }

    @PatchMapping("/{id}/increment")
    public PartDTO increment(@PathVariable Long id, @RequestBody @Valid QuantityDTO quantityDTO) throws PartNotFoundException, PartStockExceededException {
        return partService.increment(id, quantityDTO.getQuantity());
    }

    @PatchMapping("/{id}/decrement")
    public PartDTO decrement(@PathVariable Long id, @RequestBody @Valid QuantityDTO quantityDTO) throws PartNotFoundException, PartStockExceededException {
        return partService.decrement(id, quantityDTO.getQuantity());
    }

}
