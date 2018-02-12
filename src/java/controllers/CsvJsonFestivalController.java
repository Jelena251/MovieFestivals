/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import Model.Location;
import Model.Movie;
import beans.Festival;
import beans.Place;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import lombok.Data;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONObject;
import org.primefaces.model.UploadedFile;
import utils.CsvUtils;

/**
 *
 * @author user
 */
@ManagedBean(name = "fileFestivalController")
@Data
@SessionScoped
public class CsvJsonFestivalController implements Serializable {

    private int currentStep = 1;
    private String stepPage = "firstFileStep";
    private boolean complete = false;
    private UploadedFile festivalFile;
    private UploadedFile movieFile;
    private List<Festival> festivals;
    private List<Place> places;
    private List<Movie> movies;
    private final int NUMBER_OF_FEST_FIELDS = 5;
    private final int NUMBER_OF_PLACE_FIELDS = 2;
    private final int NUMBER_OF_MOVIE_FIELDS = 10;

    public void upload() throws IOException {
        if (festivalFile != null) {

            if (movieFile != null) {
                String content = movieFile.getInputstream().toString();
            } else {
                FacesMessage message = new FacesMessage("Failes", "Movie file is missing.!");
                FacesContext.getCurrentInstance().addMessage(null, message);
            }
            FacesMessage message = new FacesMessage("Succesful", festivalFile.getFileName() + " is uploaded.!");
            FacesContext.getCurrentInstance().addMessage(null, message);
        } else {
            FacesMessage message = new FacesMessage("Failed", " Festival file is missing!");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    public void uploadFestivals(FileUploadEvent event) {
        FacesMessage message;
        if (event.getFile() != null) {

            String type = event.getFile().getFileName().substring(
                    event.getFile().getFileName().length() - 3
            );
            switch (type) {
                case "csv":
                    message = parseCsvFestFile(event);
                    break;
                case "json":
                    message = parseJsonFestFile(event);
                    break;
                default:
                    message = new FacesMessage("Failed", "File is not parsed successfully.");
            }

        } else {
            message = new FacesMessage("Failed", "File is not parsed successfully.");
        }
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    private FacesMessage parseCsvFestFile(FileUploadEvent event) {
        FacesMessage message = null;
        String content = new String(event.getFile().getContents());
        if (parseFestivalFile(content)) {
            message = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");
        } else {
            message = new FacesMessage("Failed", "File is not parsed successfully.");
        }
        return message;
    }

    private FacesMessage parseJsonFestFile(FileUploadEvent event) {
        FacesMessage message = null;
        String content = new String(event.getFile().getContents());
        if (parseFestivalFileJson(content)) {
            message = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");
        } else {
            message = new FacesMessage("Failed", "File is not parsed successfully.");
        }
        return message;
    }

    public void uploadMovies(FileUploadEvent event) {
        FacesMessage message;
        if (event.getFile() != null) {

            String type = event.getFile().getFileName().substring(
                    event.getFile().getFileName().length() - 3
            );
            switch (type) {
                case "csv":
                    message = parseCsvMovieFile(event);
                    break;
                case "json":
                    message = parseJsonMovieFile(event);
                    break;
                default:
                    message = new FacesMessage("Failed", "File is not parsed successfully.");
            }

        } else {
            message = new FacesMessage("Failed", "File is not parsed successfully.");
        }
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    private FacesMessage parseCsvMovieFile(FileUploadEvent event) {
        FacesMessage message = null;
        String content = new String(event.getFile().getContents());
        if (parseMovieFile(content)) {
            message = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");
        } else {
            message = new FacesMessage("Failed", "File is not parsed successfully.");
        }
        return message;
    }

    private FacesMessage parseJsonMovieFile(FileUploadEvent event) {
        FacesMessage message = null;
        String content = new String(event.getFile().getContents());
        if (parseMovieFileJson(content)) {
            message = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");
        } else {
            message = new FacesMessage("Failed", "File is not parsed successfully.");
        }
        return message;
    }

    public void nextStep() {
        currentStep++;

        switch (currentStep) {
            case 1:
                stepPage = "firstFileStep";
                break;
            case 2:
                stepPage = "secondFileStep";
                break;
            case 3:
                stepPage = "thirdFileStep";
                complete = true;
                break;
            default:
                stepPage = "firstFileStep";
                complete = false;
                currentStep = 1;
                break;
        }
    }

    public void resetSteps() {
        stepPage = "firstFileStep";
        currentStep = 1;
        complete = false;
    }

    public void updateData() {

    }

    public boolean parseFestivalFile(String csvFile) {

        String[] files = csvFile.split("\\r?\\n-\\s+-\\s+-\\r?\\n");
        if (files.length != 2) {
            return false;
        }
        String festivalFile = files[0];
        String placesFile = files[1];
        if (!parsePlaces(placesFile)) {
            return false;
        }
        if (!parseFestivals(festivalFile)) {
            return false;
        }

        return true;
    }

    public boolean parseMovieFile(String movieFile) {
        movies = new ArrayList<>();
        String[] rows = movieFile.split("\\r?\\n");
        if (rows.length < 1) {
            return false;
        }
        List<String> firstRow = CsvUtils.parseLine(rows[0]);
        if (firstRow.size() < NUMBER_OF_MOVIE_FIELDS) {
            return false;
        }
        try {
            for (int i = 1; i < rows.length; i++) {
                List<String> data = CsvUtils.parseLine(rows[i]);

                if (data.size() < NUMBER_OF_MOVIE_FIELDS) {
                    return false;
                }
                try {
                    Integer.valueOf(data.get(2));
                    Integer.valueOf(data.get(7));
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
                movies.add(new Movie(data.get(0), data.get(1), data.get(2), data.get(3), data.get(4), data.get(5), data.get(6), data.get(7), data.get(8), data.get(9)));

            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean parseFestivals(String festivalFile) {
        festivals = new ArrayList<>();
        String[] rows = festivalFile.split("\\r?\\n");
        if (rows.length < 1) {
            return false;
        }
        List<String> firstRow = CsvUtils.parseLine(rows[0]);
        if (firstRow.size() < NUMBER_OF_FEST_FIELDS) {
            return false;
        }
        try {
            for (int i = 1; i < rows.length; i++) {
                List<String> data = CsvUtils.parseLine(rows[i]);

                if (data.size() < NUMBER_OF_FEST_FIELDS) {
                    return false;
                }
                Place place = getPlace(data.get(3));
                if (place != null) {
                    festivals.add(new Festival(data.get(0), data.get(1), data.get(2), place.getLocations(), data.get(4)));
                } else {
                    return false;
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private boolean parsePlaces(String placesFile) {
        places = new ArrayList<>();
        String[] rows = placesFile.split("\\r?\\n");
        if (rows.length < 1) {
            return false;
        }
        List<String> firstRow = CsvUtils.parseLine(rows[0]);
        if (firstRow.size() < NUMBER_OF_PLACE_FIELDS) {
            return false;
        }
        try {
            for (int i = 1; i < rows.length; i++) {
                List<String> data = CsvUtils.parseLine(rows[i]);
                if (data.size() < NUMBER_OF_PLACE_FIELDS) {
                    return false;
                }
                String placeName = data.get(0);
                String locationsString = data.get(1);
                Place place = getPlace(placeName);
                if (place != null) {
                    place.addLocation(locationsString);
                } else {
                    places.add(new Place(placeName, locationsString));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private Place getPlace(String name) {
        return this.places.stream()
                .filter(p -> name.equals(p.getName()))
                .findFirst()
                .orElse(null);
    }

    public boolean parseFestivalFileJson(String csvFile) {
        JSONObject jsonObj = new JSONObject(csvFile);
        JSONArray lokacije = jsonObj.getJSONArray("Loacations");
        if (populatePlaces(lokacije)) {
            JSONArray festivali = jsonObj.getJSONArray("Festivals");
            if (populateFestivals(festivali)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean parseMovieFileJson(String csvFile) {
        JSONObject jsonObj = new JSONObject(csvFile);
        JSONArray filmovi = jsonObj.getJSONArray("Movies");
        if (populateMovies(filmovi)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean populatePlaces(JSONArray lokacije) {
        try {
            places = new ArrayList<>();
            for (int i = 0; i < lokacije.length(); i++) {
                JSONObject placesObject = lokacije.getJSONObject(i);
                List<Location> locationsForPlace = new ArrayList<>();
                Place place = new Place(placesObject.get("Place").toString(), locationsForPlace);
                JSONArray locations = placesObject.getJSONArray("Location");
                for (int j = 0; j < locations.length(); i++) {
                    String[] locNameAndHall = locations.getJSONObject(j).getString("Name").split(",");
                    Location locObject = new Location(locNameAndHall[0], place);
                    if (locNameAndHall.length > 1) {
                        locObject.dodajSalu(locNameAndHall[1]);
                    }
                    locationsForPlace.add(locObject);
                }
                place.setLocations(locationsForPlace);
                places.add(place);
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private boolean populateFestivals(JSONArray festivali) {
        try {
            festivals = new ArrayList<>();
            for (int i = 0; i < festivali.length(); i++) {
                JSONObject fest = festivali.getJSONObject(i);
                Place place = getPlace(fest.getString("Place"));
                if (place == null) {
                    return false;
                }
                festivals.add(new Festival(fest.getString("Festival"), fest.getString("StartDate"), fest.getString("EndDate"),
                        place.getLocations(), fest.getString("About")));
            }

        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private boolean populateMovies(JSONArray filmovi) {
        try {
            movies = new ArrayList<>();
            for (int i = 0; i < filmovi.length(); i++) {
                JSONObject film = filmovi.getJSONObject(i);
                Integer.valueOf(film.getString("Year"));
                Integer.valueOf(film.getString("Runtime"));
                movies.add(new Movie(film.getString("Title"), film.getString("OriginalTitle"), film.getString("Year"),
                        film.getString("Summary"), film.getString("Director"),
                        film.getString("Stars"), film.getString("Runtime"),
                        film.getString("Country"), film.getString("Link1"), film.getString("Link2")));
            }

        } catch (Exception e) {
            return false;
        }
        return true;
    }

}
