package odk.apprenant.jobaventure_backend.service;


import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import odk.apprenant.jobaventure_backend.model.FileInfo;
import odk.apprenant.jobaventure_backend.repository.FileInfoRepository;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class FileInfoService {
    private FileInfoRepository fileInfoRepository;

    @Transactional(Transactional.TxType.REQUIRED)
    public FileInfo creer(FileInfo fileInfo) {
        return this.fileInfoRepository.save(fileInfo);
    }
}
