package com.example.softwareeng.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Publication {
    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    @Size(max = 750)
    @Column( length = 750 )
    private String title;

    @NotNull
    @Size(max = 255)
    @Column( length = 255 )
    private String doi;

    @NotNull
    @Column( length = 8000 )
    private String description;

    @NotNull
    @Column( length = 2000 )
    private String venue;

    @NotNull
    @Column( length = 2004 )
    private String listofauthors;

    @NotNull
    private int citations;

    @NotNull
    private int year;

    @NotNull
    @Column( length = 2005 )
    private String publisher;

    @NotNull
    @Column( length = 100 )
    private String pages;

    @NotNull
    @Column( length = 100 )
    private String volume;

    @NotNull
    @Column( length = 100 )
    private String number;

    @NotNull
    @Column( length = 8000 )
    private String bibtex;

    @Column(length = 255)
    private String link;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "BLOCKED_PUBLICATIONS_PERSON",
            joinColumns = { @JoinColumn(name = "PUBLICATION_ID", referencedColumnName = "ID") }, //do not forget referencedColumnName if name is different
            inverseJoinColumns = { @JoinColumn(name = "PERSON_ID", referencedColumnName = "ID") })
    //annotation bellow is just for Jackson serialization in controller
    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id")
    @JsonIdentityReference(alwaysAsId=true)
    private Set<Person> authorsblocked = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "PUBLICATIONS_PERSON",
            joinColumns = { @JoinColumn(name = "PUBLICATION_ID", referencedColumnName = "ID") }, //do not forget referencedColumnName if name is different
            inverseJoinColumns = { @JoinColumn(name = "PERSON_ID", referencedColumnName = "ID") })
    //annotation bellow is just for Jackson serialization in controller
    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id")
    @JsonIdentityReference(alwaysAsId=true)
    private Set<Person> authors = new HashSet<>();


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getListofauthors() {
        return listofauthors;
    }

    public void setListofauthors(String listofauthors) {
        this.listofauthors = listofauthors;
    }

    public int getCitations() {
        return citations;
    }

    public void setCitations(int citations) {
        this.citations = citations;
    }

    public String getBibtex() {
        return bibtex;
    }

    public void setBibtex(String bibtex) {
        this.bibtex = bibtex;
    }

    public Set<Person> getAuthors() {
        return authors;
    }

    public void setAuthorsSemantic(Set<Person> authorsSemantic) {
        this.authors = authorsSemantic;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getPages() {
        return pages;
    }

    public void setPages(String pages) {
        this.pages = pages;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Set<Person> getAuthorsblocked() {
        return authorsblocked;
    }

    public void setAuthorsblocked(Set<Person> authorsblocked) {
        this.authorsblocked = authorsblocked;
    }

    public void setAuthors(Set<Person> authors) {
        this.authors = authors;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
