package pl.kurs.position.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.person.exception.PersonNotFound;
import pl.kurs.position.exception.PositionNotFound;
import pl.kurs.person.model.Employee;
import pl.kurs.position.model.Position;
import pl.kurs.position.model.command.CreatePositionCommand;
import pl.kurs.position.model.dto.PositionDto;
import pl.kurs.person.repository.EmployeeRepository;
import pl.kurs.position.repository.PositionRepository;

@Service
@RequiredArgsConstructor
public class PositionService {

    private final PositionRepository positionRepository;
    private final EmployeeRepository employeeRepository;

    @Transactional
    public void hireEmployee(int positionId, int employeeId) {
        Employee employee = employeeRepository.findEmployeeWithPositions(employeeId).orElseThrow(PersonNotFound::new);
        Position position = positionRepository.findByIdForUpdate(positionId).orElseThrow((PositionNotFound::new));

        if (position.getEmployee() != null) {
            throw new IllegalArgumentException("Position with ID " + positionId + " is assigned to another employee.");
        }

        Employee copy = new Employee(employee);
        position.hireEmployee(copy);
        employeeRepository.saveAndFlush(copy);
    }


    @Transactional
    public void dismissEmployee(int positionId, int employeeId) {
        Employee employee = employeeRepository.findEmployeeWithPositions(employeeId).orElseThrow(PersonNotFound::new);
        Position position = positionRepository.findByIdForUpdate(positionId).orElseThrow((PositionNotFound::new));

        validateDismiss(position, employeeId);
        position.dismissEmployee(employee);
        employeeRepository.saveAndFlush(employee);
    }

    @Transactional(readOnly = true)
    public Page<PositionDto> getPositionsForEmployeeId(int employeeId, Pageable pageable) {
        return positionRepository.findByEmployeeId(employeeId, pageable).map(PositionDto::toDto);
    }


    @Transactional
    public Position save(CreatePositionCommand command) {
        Position position = new Position(command.getName(), command.getDateFrom(), command.getDateTo(), command.getSalary());
        return positionRepository.saveAndFlush(position);
    }

    @Transactional(readOnly = true)
    public PositionDto findById(int id) {
        return PositionDto.toDto(positionRepository.findById(id).orElseThrow(PositionNotFound::new));
    }

    private void validateDismiss(Position position, int employeeId) {
        if (position.getEmployee() == null) {
            throw new IllegalArgumentException("Position was not assigned to any employee.");
        }
        if (position.getEmployee().getId() != employeeId) {
            throw new IllegalArgumentException("Given employee is not assigned to this position.");
        }
        if (position.getDateTo() != null) {
            throw new IllegalArgumentException("This position has already ended.");
        }
    }
}



