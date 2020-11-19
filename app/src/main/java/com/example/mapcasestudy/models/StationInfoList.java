package com.example.mapcasestudy.models;

public class StationInfoList {

    private String center_coordinates;

    private String count;

    private String name;

    private String id;

    public String getCenter_coordinates ()
    {
        return center_coordinates;
    }

    public void setCenter_coordinates (String center_coordinates)
    {
        this.center_coordinates = center_coordinates;
    }

    public String getCount ()
    {
        return count;
    }

    public void setCount (String count)
    {
        this.count = count;
    }

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [center_coordinates = "+center_coordinates+", count = "+count+", name = "+name+", id = "+id+"]";
    }
}
