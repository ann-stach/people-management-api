package pl.kurs.position.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.kurs.position.model.dto.PositionDto;
import pl.kurs.position.service.PositionService;

@RestController
@RequestMapping("/api/v1/positions")
@RequiredArgsConstructor
@Slf4j
public class PositionController {

    private final PositionService positionService;
    @PatchMapping("/{positionId}/employees/{employeeId}/hire")
    public ResponseEntity<PositionDto> hireEmployee(@PathVariable int positionId, @PathVariable int employeeId) {
        log.info("hireEmployee({}, {})", positionId, employeeId);
        positionService.hireEmployee(positionId, employeeId);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{positionId}/employees/{employeeId}/dismiss")
    public ResponseEntity<PositionDto> dismissEmployee(@PathVariable int positionId, @PathVariable int employeeId) {
        log.info("dismissEmployee({}, {})", positionId, employeeId);
        positionService.dismissEmployee(positionId, employeeId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/employees/{employeeId}")
    public ResponseEntity<Page<PositionDto>> getPositionsForEmployeeId(@PathVariable int employeeId, @PageableDefault Pageable pageable) {
        log.info("getPositionsForEmployee({},{}", employeeId, pageable);
        return ResponseEntity.ok(positionService.getPositionsForEmployeeId(employeeId, pageable));
    }

}
