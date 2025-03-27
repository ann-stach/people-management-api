package pl.kurs._import.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs._import.exception.StatusNotFound;
import pl.kurs._import.model.ImportStatus;
import pl.kurs._import.repository.ImportStatusRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ImportStatusService {

    private final ImportStatusRepository importStatusRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateToProcessing(int id) {
        ImportStatus toUpdate = importStatusRepository.findById(id).orElseThrow(StatusNotFound::new);
        toUpdate.setStartDate(LocalDateTime.now());
        toUpdate.setStatus(ImportStatus.Status.PROCESSING);
        importStatusRepository.saveAndFlush(toUpdate);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateProgress(int id, int progress) {
        ImportStatus toUpdate = importStatusRepository.findById(id).orElseThrow(StatusNotFound::new);
        toUpdate.setProcessed(progress);
        importStatusRepository.saveAndFlush(toUpdate);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateToSuccess(int id, int processed) {
        ImportStatus toUpdate = importStatusRepository.findById(id).orElseThrow(StatusNotFound::new);
        toUpdate.setProcessed(processed);
        toUpdate.setStatus(ImportStatus.Status.SUCCESS);
        importStatusRepository.saveAndFlush(toUpdate);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateToFailed(int id, int processed) {
        ImportStatus toUpdate = importStatusRepository.findById(id).orElseThrow(StatusNotFound::new);
        toUpdate.setStatus(ImportStatus.Status.FAILED);
        toUpdate.setProcessed(processed);
        importStatusRepository.saveAndFlush(toUpdate);
    }

}
