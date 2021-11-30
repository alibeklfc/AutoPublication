package com.example.softwareeng.controller;

import com.example.softwareeng.model.Person;
import com.example.softwareeng.model.Publication;
import com.example.softwareeng.services.PersonRepository;
import com.example.softwareeng.services.PublicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin
public class PublicationController {
    private PublicationRepository publRepository;

    @Autowired
    public PublicationController(PublicationRepository publRepository){
        this.publRepository = publRepository;
    }

    @GetMapping("/publication")
    public ResponseEntity<Publication> publications() {
        return new ResponseEntity(publRepository.getPublications(), HttpStatus.OK);
    }

    @GetMapping("/publication/{personid}")
    public ResponseEntity<Publication> publicationsemanticFiltered(@PathVariable("personid") Long personid){
        return new ResponseEntity(publRepository.getPublications(personid),HttpStatus.OK);
    }

    @GetMapping("/publication/blocked/{personid}")
    public ResponseEntity<Publication> publicationsBlocked(@PathVariable("personid") Long personid){
        return new ResponseEntity(publRepository.getBlockedPublications(personid),HttpStatus.OK);
    }

    @GetMapping("/publication/notblocked/{personid}")
    public ResponseEntity<Publication> publicationsNotBlocked(@PathVariable("personid") Long personid){
        return new ResponseEntity(publRepository.getNotBlockedPublications(personid),HttpStatus.OK);
    }


    @PutMapping(value = "/populate")
    public ResponseEntity populateSemantic(@RequestParam(name = "personid") Long personid){
        List<Person> lp = publRepository.getPeople();
        for(Person p: lp){
            if(p.getId().equals(personid)){
                try {
                    publRepository.populate(p);
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        return new ResponseEntity(HttpStatus.OK);
    }
}
