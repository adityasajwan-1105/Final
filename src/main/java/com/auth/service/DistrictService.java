package com.auth.service;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class DistrictService {
    
    // Uttarakhand districts with their center coordinates and radius
    private static final Map<String, District> DISTRICTS = new HashMap<>();
    
    static {
        // Initialize district data
        DISTRICTS.put("Tehri Garhwal", new District(30.3753, 78.4337, 0.5));
        DISTRICTS.put("Dehradun", new District(30.3165, 78.0322, 0.5));
        DISTRICTS.put("Haridwar", new District(29.9457, 78.1642, 0.5));
        DISTRICTS.put("Pauri Garhwal", new District(30.1469, 78.7808, 0.5));
        DISTRICTS.put("Rudraprayag", new District(30.2844, 78.9811, 0.5));
        DISTRICTS.put("Chamoli", new District(30.4227, 79.3286, 0.5));
        DISTRICTS.put("Pithoragarh", new District(29.5828, 80.2181, 0.5));
        DISTRICTS.put("Almora", new District(29.5892, 79.6467, 0.5));
        DISTRICTS.put("Nainital", new District(29.3919, 79.4542, 0.5));
        DISTRICTS.put("Udham Singh Nagar", new District(28.9610, 79.5152, 0.5));
        DISTRICTS.put("Champawat", new District(29.3362, 80.0911, 0.5));
        DISTRICTS.put("Bageshwar", new District(29.8362, 79.7713, 0.5));
        DISTRICTS.put("Uttarkashi", new District(30.7268, 78.4354, 0.5));
    }

    public String findDistrict(double latitude, double longitude) {
        String closestDistrict = null;
        double minDistance = Double.MAX_VALUE;

        for (Map.Entry<String, District> entry : DISTRICTS.entrySet()) {
            District district = entry.getValue();
            double distance = calculateDistance(latitude, longitude, 
                                             district.latitude, district.longitude);
            
            if (distance < district.radius && distance < minDistance) {
                minDistance = distance;
                closestDistrict = entry.getKey();
            }
        }

        return closestDistrict;
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Simple Euclidean distance calculation
        // For more accurate results, you might want to use Haversine formula
        return Math.sqrt(Math.pow(lat2 - lat1, 2) + Math.pow(lon2 - lon1, 2));
    }

    private static class District {
        double latitude;
        double longitude;
        double radius; // in degrees

        District(double latitude, double longitude, double radius) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.radius = radius;
        }
    }
} 