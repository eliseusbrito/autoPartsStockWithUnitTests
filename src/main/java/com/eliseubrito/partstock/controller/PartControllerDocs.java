package com.eliseubrito.partstock.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import com.eliseubrito.partstock.dto.PartDTO;
import com.eliseubrito.partstock.exception.PartAlreadyRegisteredException;
import com.eliseubrito.partstock.exception.PartNotFoundException;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Api("Manages auto parts stock")
public interface PartControllerDocs {

    @ApiOperation(value = "Auto Part creation operation")
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Success auto part creation"),
            @ApiResponse(code = 400, message = "Missing required fields or wrong field range value.")
    })
    PartDTO createPart(PartDTO partDTO) throws PartAlreadyRegisteredException;

    @ApiOperation(value = "Returns auto part found by a given name")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Success auto part found in the system"),
            @ApiResponse(code = 404, message = "Auto Part with given name not found.")
    })
    PartDTO findByName(@PathVariable String name) throws PartNotFoundException;

    @ApiOperation(value = "Returns a list of all auto parts registered in the system")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "List of all auto parts registered in the system"),
    })
    List<PartDTO> listParts();

    @ApiOperation(value = "Delete a auto part found by a given valid Id")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Success auto part deleted in the system"),
            @ApiResponse(code = 404, message = "Auto Part with given id not found.")
    })
    void deleteById(@PathVariable Long id) throws PartNotFoundException;
}
