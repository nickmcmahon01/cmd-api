package uk.gov.justice.digital.hmpps.cmd.api.controllers

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import uk.gov.justice.digital.hmpps.cmd.api.dto.ShiftDto
import uk.gov.justice.digital.hmpps.cmd.api.service.ShiftService
import java.time.LocalDate
import java.util.*

@Api(tags = ["shift controller"])
@RestController
@RequestMapping(produces = [APPLICATION_JSON_VALUE])
class ShiftController(private val shiftService: ShiftService) {

    @ApiOperation(value = "Retrieve all details for a user between two dates")
    @ApiResponses(value = [
        ApiResponse(code = 200, message = "OK")
    ])
    @GetMapping("/user/details")
    fun getShifts(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) from: Optional<LocalDate>,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) to: Optional<LocalDate>): ResponseEntity<Collection<ShiftDto>> {
        val result = shiftService.getDetailsForUser(from, to)
        return ResponseEntity.ok(result)
    }


}