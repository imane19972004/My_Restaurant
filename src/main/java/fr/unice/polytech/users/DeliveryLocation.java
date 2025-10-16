package fr.unice.polytech.users; // Assuming this package

public class DeliveryLocation {
    
    // Attributes from Class Diagram:
    private String name;
    private String address;
    private String city;
    private String zipCode;

    /**
     * Constructor for DeliveryLocation.
     */
    public DeliveryLocation(String name, String address, String city, String zipCode) {
        this.name = name;
        this.address = address;
        this.city = city;
        this.zipCode = zipCode;
    }
    
    // Getters
    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getZipCode() {
        return zipCode;
    }
    
    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    @Override
    public String toString() {
        return name + ": " + address + ", " + zipCode + " " + city;
    }
}