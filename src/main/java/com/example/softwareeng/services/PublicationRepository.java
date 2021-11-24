package com.example.softwareeng.services;

import com.example.softwareeng.model.Person;
import com.example.softwareeng.model.Publication;
import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

@Repository
@Transactional
public class PublicationRepository {
    @PersistenceContext
    private EntityManager em;

    public Object getPublications(Long personid) {
        for(Person p: getPeople()){
            if(p.getId() == personid){
                Query query = em.createQuery("SELECT c FROM Publication c where :person member of c.authors");
                query.setParameter("person", p);
                return query.getResultList();
            }
        }
        return null;
    }
//    people from database.
    
    public List<Person> getPeople() {
        return em.createQuery("SELECT c FROM Person c").getResultList();
    }

    public List<Publication> getPublications() {
        return em.createQuery("SELECT c FROM Publication c").getResultList();
    }

    public List<Publication> getPublicationsByDOI(String doi) {
        Query query = em.createQuery("SELECT c FROM Publication c where c.doi = :doi");
        query.setParameter("doi", doi).getResultList();
        return query.getResultList();

    }

    public void populate(Person p) throws IOException, JSONException {
        String url = p.getUrl();
        //String url = "https://api.semanticscholar.org/graph/v1/author/1682740/papers?fields=externalIds,url,title,abstract,venue,year,citationCount,authors&limit=999";
        JSONObject json = new JSONObject(IOUtils.toString(new URL(url), Charset.forName("UTF-8")));
        JSONArray array = json.getJSONArray("data");
        for(int i = 0 ; i < array.length() ; i++){
            try {
                JSONObject externalIds = array.getJSONObject(i).getJSONObject("externalIds");
                String doi = externalIds.getString("DOI");
                List<Publication> temp = getPublicationsByDOI(doi);
                if (temp.size() != 0){
                    Publication pub = getPublicationsByDOI(doi).get(0);
                    pub.getAuthors().add(p);
                    em.persist(pub);
                    continue;
                }
            }catch (Exception exception){
            }
            Publication publ = new Publication();
            if(array.getJSONObject(i).getString("abstract").length() > 7999){
                continue;
            }
            try{
                publ.setTitle(array.getJSONObject(i).getString("title"));
                publ.setDescription(array.getJSONObject(i).getString("abstract"));
                publ.setVenue(array.getJSONObject(i).getString("venue"));
                publ.setCitations(Integer.parseInt(array.getJSONObject(i).getString("citationCount")));}
            catch (Exception ex){
                continue;
            }
            JSONArray authorsArr = array.getJSONObject(i).getJSONArray("authors");
            StringBuilder sb = new StringBuilder();
            for(int j = 0; j < authorsArr.length(); j++){
                sb.append(authorsArr.getJSONObject(j).get("name"));
                if(j == 50){
                    break;
                }
                if(j != authorsArr.length() - 1){
                    sb.append(", ");
                }
            }
            try{
                publ.setListofauthors(sb.toString());
            }catch (Exception ex){
                continue;
            }
            JSONObject externalIds = array.getJSONObject(i).getJSONObject("externalIds");
            try{
                String type = "application/x-bibtex";
                publ.setDoi(externalIds.getString("DOI").toString());
                CloseableHttpClient httpClient = HttpClients.createDefault();
                HttpGet request = new HttpGet("http://dx.doi.org/"+externalIds.getString("DOI").toString());
                request.addHeader("Accept", type);
                try (CloseableHttpResponse response = httpClient.execute(request)) {
                    HttpEntity entity = response.getEntity();
                    Header headers = entity.getContentType();
                    if (entity != null) {
                        // return it as a String
                        String result = EntityUtils.toString(entity);
                        System.out.println(result.length());
                        publ.setBibtex(result);
                        result.replace("\r", "");
                        result.replace("\t", "");
                        String[] parts = result.split("\n");
                        for(int k = 0; k< parts.length; k++){
                            //System.out.println(parts[k]);
                            if(parts[k].contains("year = ")){
                                publ.setYear(Integer.parseInt(parts[k].replaceAll("[^0-9]","")));
                            }else if (parts[k].contains("publisher = ")){
                                String temp[] = parts[k].split("publisher = ");
                                String str = temp[1].replace("{", "").replace("}", "");

                                publ.setPublisher(str.substring(0, str.length() - 1));
                            }else if (parts[k].contains("pages = ")){
                                String temp[] = parts[k].split("pages = ");
                                String str = temp[1].replace("{", "").replace("}", "").replace("--", "-");
                                publ.setPages(str.substring(0, str.length() - 1));
                            }else if (parts[k].contains("volume = ")){
                                String temp[] = parts[k].split("volume = ");
                                String str = temp[1].replace("{", "").replace("}", "");
                                publ.setVolume(str.substring(0, str.length() - 1));
                            }else if (parts[k].contains("number = ")){
                                String temp[] = parts[k].split("number = ");
                                String str = temp[1].replace("{", "").replace("}", "");
                                publ.setNumber(str.substring(0, str.length() - 1));
                            }
                        }
                    }
                }
            }catch(Exception exception){
                System.err.println(exception);
            }
            publ.getAuthors().add(p);
            em.persist(publ);
        }
    }


    public List<Publication> getBlockedPublications(Long personid) {
        Query query = em.createQuery("SELECT c.blockedPublications FROM Person c where c.id = :personid");
        query.setParameter("personid", personid).getResultList();
        return query.getResultList();
    }


    public List<Publication> getNotBlockedPublications(Long personid) {
        Query query = em.createQuery("SELECT c.publications FROM Person c where c.id = :personid");
        query.setParameter("personid", personid).getResultList();
        List<Publication> publ = query.getResultList();
        List<Publication> blockedPubl = getBlockedPublications(personid);
        publ.removeAll(blockedPubl);
        return publ;
    }
}
