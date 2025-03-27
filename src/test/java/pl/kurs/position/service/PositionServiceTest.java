package pl.kurs.position.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import pl.kurs.position.exception.PositionNotFound;
import pl.kurs.person.model.Employee;
import pl.kurs.position.model.Position;
import pl.kurs.position.model.command.CreatePositionCommand;
import pl.kurs.position.model.dto.PositionDto;
import pl.kurs.person.repository.EmployeeRepository;
import pl.kurs.position.repository.PositionRepository;
import pl.kurs.position.service.PositionService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class PositionServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private PositionRepository positionRepository;

    @InjectMocks
    private PositionService positionService;
    @Test
    void shouldReturnPositionDtoWhenPositionExistsAndCallMethodsAsExpected() {
        int positionId = 1;

        Position position = mock(Position.class);
        PositionDto positionDto = mock(PositionDto.class);

        when(position.getId()).thenReturn(1);
        when(position.getDateFrom()).thenReturn(LocalDate.now());
        when(position.getDateTo()).thenReturn(LocalDate.now().plusDays(10));
        when(position.getName()).thenReturn("Software Engineer");
        when(position.getSalary()).thenReturn(5000);
        when(position.getEmployee()).thenReturn(null);

        when(positionRepository.findById(positionId)).thenReturn(Optional.of(position));
        try (MockedStatic<PositionDto> mockedPositionDto = Mockito.mockStatic(PositionDto.class)) {
            mockedPositionDto.when(() -> PositionDto.toDto(position)).thenReturn(positionDto);

            PositionDto result = positionService.findById(positionId);

            Assertions.assertNotNull(result);
            Assertions.assertEquals(positionDto, result);

            verify(positionRepository, times(1)).findById(positionId);
            mockedPositionDto.verify(() -> PositionDto.toDto(position), times(1));
        }
    }

    @Test
    void shouldThrowPositionNotFoundWhenPositionNotExistsAndCallMethodsAsExpected() {
        int positionId = 1;

        when(positionRepository.findById(positionId)).thenReturn(Optional.empty());

        Assertions.assertThrows(PositionNotFound.class, () -> positionService.findById(positionId));

        verify(positionRepository, times(1)).findById(positionId);
    }

    @Test
    void shouldSuccessfullyCreatePosition() {
        CreatePositionCommand command = mock(CreatePositionCommand.class);

        String positionName = "Software Engineer";
        LocalDate dateFrom = LocalDate.now();
        LocalDate dateTo = LocalDate.now().plusMonths(6);
        int salary = 6000;

        when(command.getName()).thenReturn(positionName);
        when(command.getDateFrom()).thenReturn(dateFrom);
        when(command.getDateTo()).thenReturn(dateTo);
        when(command.getSalary()).thenReturn(salary);

        Position positionToSave = new Position(positionName, dateFrom, dateTo, salary);

        when(positionRepository.saveAndFlush(any(Position.class))).thenReturn(positionToSave);

        Position result = positionService.save(command);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(positionToSave, result);

        ArgumentCaptor<Position> positionCaptor = ArgumentCaptor.forClass(Position.class);
        verify(positionRepository, times(1)).saveAndFlush(positionCaptor.capture());

        Position capturedPosition = positionCaptor.getValue();
        Assertions.assertEquals(positionToSave.getName(), capturedPosition.getName());
        Assertions.assertEquals(positionToSave.getDateFrom(), capturedPosition.getDateFrom());
        Assertions.assertEquals(positionToSave.getDateTo(), capturedPosition.getDateTo());
        Assertions.assertEquals(positionToSave.getSalary(), capturedPosition.getSalary());
    }

    @Test
    void shouldReturnPositionsForEmployeeIdAndCallMethodsAsExpected() {
        int employeeId = 1;
        Pageable pageable = mock(Pageable.class);

        Position position1 = new Position("Software Engineer", LocalDate.now(), LocalDate.now().plusMonths(6), 6000);
        Position position2 = new Position("Data Scientist", LocalDate.now(), LocalDate.now().plusMonths(12), 7000);
        List<Position> positions = List.of(position1, position2);

        Page<Position> positionPage = new PageImpl<>(positions);
        when(positionRepository.findByEmployeeId(employeeId, pageable)).thenReturn(positionPage);

        try (MockedStatic<PositionDto> mockedPositionDto = Mockito.mockStatic(PositionDto.class)) {
            PositionDto mockPositionDto1 = mock(PositionDto.class);
            PositionDto mockPositionDto2 = mock(PositionDto.class);
            mockedPositionDto.when(() -> PositionDto.toDto(position1)).thenReturn(mockPositionDto1);
            mockedPositionDto.when(() -> PositionDto.toDto(position2)).thenReturn(mockPositionDto2);

            Page<PositionDto> result = positionService.getPositionsForEmployeeId(employeeId, pageable);

            assertNotNull(result);
            assertEquals(positions.size(), result.getContent().size());

            verify(positionRepository, times(1)).findByEmployeeId(employeeId, pageable);

            mockedPositionDto.verify(() -> PositionDto.toDto(position1), times(1));
            mockedPositionDto.verify(() -> PositionDto.toDto(position2), times(1));
        }
    }

    @Test
    void shouldHireEmployeeToPositionAndSaveEmployeeToRepository() {
        int positionId = 1;
        int employeeId = 1;

        Position position = mock(Position.class);
        Employee employee = mock(Employee.class);

        when(employeeRepository.findEmployeeWithPositions(employeeId)).thenReturn(Optional.of(employee));
        when(positionRepository.findByIdForUpdate(positionId)).thenReturn(Optional.of(position));

        when(position.getEmployee()).thenReturn(null);

        positionService.hireEmployee(positionId, employeeId);

        verify(position, times(1)).hireEmployee(any(Employee.class));
        verify(employeeRepository, times(1)).saveAndFlush(any(Employee.class));
    }

    @Test
    void shouldThrowExceptionWhenPositionIsAlreadyAssignedToAnotherEmployeeAndDoNotSave() {
        int positionId = 1;
        int employeeId = 1;

        Employee employee = mock(Employee.class);
        when(employeeRepository.findEmployeeWithPositions(employeeId)).thenReturn(Optional.of(employee));

        Position position = mock(Position.class);
        Employee existingEmployee = mock(Employee.class);
        when(positionRepository.findByIdForUpdate(positionId)).thenReturn(Optional.of(position));
        when(position.getEmployee()).thenReturn(existingEmployee);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> positionService.hireEmployee(positionId, employeeId));

        assertEquals("Position with ID 1 is assigned to another employee.", exception.getMessage());

        verify(employeeRepository, never()).saveAndFlush(any(Employee.class));
    }
}

