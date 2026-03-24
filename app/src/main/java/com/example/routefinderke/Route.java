package com.example.routefinderke;

import java.io.Serializable;
import java.util.List;

public class Route implements Serializable {
    private String routeNumber;
    private String startPoint;
    private String destination;
    private List<String> stops;
    private String fareRange;
    private int imageResourceId;
    private boolean isFavorite;
    private String county;
    private double startLat;
    private double startLng;
    private double destLat;
    private double destLng;
    private String paymentTill; // Added for automatic payment logic

    public Route(String routeNumber, String startPoint, String destination, List<String> stops, String fareRange, int imageResourceId, String county, double startLat, double startLng, double destLat, double destLng, String paymentTill) {
        this.routeNumber = routeNumber;
        this.startPoint = startPoint;
        this.destination = destination;
        this.stops = stops;
        this.fareRange = fareRange;
        this.imageResourceId = imageResourceId;
        this.isFavorite = false;
        this.county = county;
        this.startLat = startLat;
        this.startLng = startLng;
        this.destLat = destLat;
        this.destLng = destLng;
        this.paymentTill = paymentTill;
    }

    public String getRouteNumber() { return routeNumber; }
    public String getStartPoint() { return startPoint; }
    public String getDestination() { return destination; }
    public List<String> getStops() { return stops; }
    public String getFareRange() { return fareRange; }
    public int getImageResourceId() { return imageResourceId; }
    public String getCounty() { return county; }
    public double getStartLat() { return startLat; }
    public double getStartLng() { return startLng; }
    public double getDestLat() { return destLat; }
    public double getDestLng() { return destLng; }
    public String getPaymentTill() { return paymentTill; }
    
    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }
}
