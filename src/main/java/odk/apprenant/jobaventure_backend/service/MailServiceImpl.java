package odk.apprenant.jobaventure_backend.service;


import lombok.NoArgsConstructor;
import odk.apprenant.jobaventure_backend.model.Email;
import odk.apprenant.jobaventure_backend.repository.EmailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@NoArgsConstructor
public class MailServiceImpl implements MailService {

    private EmailRepository emailRepository;

    @Autowired
    public MailServiceImpl(EmailRepository emailRepository) {
        this.emailRepository = emailRepository;
    }

    @Override
    public Email Creermail(Email email) {
        return emailRepository.save(email);
    }


}
