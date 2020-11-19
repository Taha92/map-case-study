package com.example.mapcasestudy.models;

public class BookingResponse {
    private String destination_coordinates;

    private String origin_coordinates;

    private String id;

    private String[] polyline;

    public String getDestination_coordinates ()
    {
        return destination_coordinates;
    }

    public void setDestination_coordinates (String destination_coordinates)
    {
        this.destination_coordinates = destination_coordinates;
    }

    public String getOrigin_coordinates ()
    {
        return origin_coordinates;
    }

    public void setOrigin_coordinates (String origin_coordinates)
    {
        this.origin_coordinates = origin_coordinates;
    }

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String[] getPolyline ()
    {
        return polyline;
    }

    public void setPolyline (String[] polyline)
    {
        this.polyline = polyline;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [destination_coordinates = "+destination_coordinates+", origin_coordinates = "+origin_coordinates+", id = "+id+", polyline = "+polyline+"]";
    }
}
