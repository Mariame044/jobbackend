package odk.apprenant.jobaventure_backend.service;

import odk.apprenant.jobaventure_backend.model.Trancheage;
import odk.apprenant.jobaventure_backend.repository.TrancheageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TrancheageService {

    @Autowired
    private TrancheageRepository trancheageRepository;


    public List<Trancheage> getAllTrancheages() {
        return trancheageRepository.findAll();
    }
    public Optional<Trancheage> getTrancheageById(Long id) {
        return trancheageRepository.findById(id);
    }
    public Trancheage createTrancheage(Trancheage trancheage) {
        return trancheageRepository.save(trancheage);
    }
    public void deleteTrancheageById(Long id) {
        trancheageRepository.deleteById(id);
    }
    public Optional<Trancheage> updateTrancheage(Long id, Trancheage newTrancheAge) {
        return trancheageRepository.findById(id).map(existingTrancheAge -> {
            existingTrancheAge.setAgeMin(newTrancheAge.getAgeMin());
            existingTrancheAge.setAgeMax(newTrancheAge.getAgeMax());
            existingTrancheAge.setDescription(newTrancheAge.getDescription());
            return trancheageRepository.save(existingTrancheAge);
        });
    }
}
