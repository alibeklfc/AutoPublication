package com.example.softwareeng.services;

import com.example.softwareeng.model.Person;
import com.example.softwareeng.model.Publication;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
@Transactional
public class PersonRepository {
    @PersistenceContext
    private EntityManager em;

    @Autowired
    private JavaMailSender javaMailSender;

    public List<Person> getPeople() {
        return em.createQuery("SELECT c FROM Person c").getResultList();
    }

    public void addPerson(Person person) {
        String token = RandomStringUtils.randomAlphanumeric(17);
        person.setToken(token);
        em.persist(person);
    }


    public int blockPublication(Long personid, Long publid, String token) {
        Person p = getPerson(personid);
        if(!p.getToken().equals(token)){
            return 0;
        }
        List<Publication> publ = em.createQuery(
                "select p from Publication p where p.id=:publid").setParameter("publid", publid).getResultList();
        if(publ.size()==0){
            return 0;
        }
        Publication publication = publ.get(0);
        if(p.getPublications().contains(publication)){
            publication.getAuthorsblocked().add(p);
        }
        em.persist(publication);
        return 1;
    }

    public Person getPerson(Long personid) {
        List<Person> lp = em.createQuery(
                "select p from Person p where p.id =:id"
        ).setParameter("id", personid).getResultList();
        if(lp.size() == 1){
            return lp.get(0);
        }
        return null;
    }

    public int unBlockPublication(Long personid, Long publid, String token) {
        Person p = getPerson(personid);
        if(!p.getToken().equals(token)){
            return 0;
        }
        List<Publication> publ = em.createQuery(
                "select p from Publication p where p.id=:publid").setParameter("publid", publid).getResultList();
        if(publ.size()==0){
            return 0;
        }
        Publication publication = publ.get(0);
        if(p.getPublications().contains(publication)){
            publication.getAuthorsblocked().remove(p);
        }
        em.persist(publication);
        return 1;
    }

    public void sendEmail(Long personid){
        Person p = getPerson(personid);
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(p.getEmail());

        msg.setSubject("Your link for Baylor AutoPub modification page");
        msg.setText("http://localhost:4200/user/publication/" + + p.getId() + "/" + p.getToken());
        javaMailSender.send(msg);
    }
}
