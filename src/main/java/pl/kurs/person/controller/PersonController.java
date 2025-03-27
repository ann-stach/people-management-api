package pl.kurs.person.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.kurs.person.model.command.CreatePersonCommand;
import pl.kurs.person.model.command.EditPersonCommand;
import pl.kurs.person.model.dto.PersonDto;
import pl.kurs.person.model.query.FindPersonQuery;
import pl.kurs.person.service.PersonService;

@RestController
@RequestMapping("/api/v1/people")
@RequiredArgsConstructor
@Slf4j
public class PersonController {

    private final PersonService personService;

    @PostMapping("/search")
    public Page<PersonDto> findAll(@RequestBody(required = false) FindPersonQuery query,
                                   @PageableDefault Pageable pageable) {
        if (query == null) {
            query = new FindPersonQuery();
        }
        return personService.findAll(query, pageable);
    }

    @PostMapping
    public ResponseEntity<PersonDto> addPerson(@Valid @RequestBody CreatePersonCommand command) {
        log.info("addPerson({})", command);
        return ResponseEntity.status(HttpStatus.CREATED).body(personService.createPerson(command));
    }

    @PatchMapping("/{personId}")
    public ResponseEntity<PersonDto> editPerson(@PathVariable int personId, @Valid @RequestBody EditPersonCommand command) {
        log.info("editPerson({}, {})", personId, command);
        return ResponseEntity.ok(personService.editPerson(personId, command));
    }


}
