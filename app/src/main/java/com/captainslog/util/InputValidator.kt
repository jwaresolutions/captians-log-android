package com.captainslog.util

/**
 * Input validation utilities for Android app
 * Provides client-side validation to complement backend validation
 */
object InputValidator {
    
    /**
     * Validate required fields are present and not empty
     */
    fun validateRequired(data: Map<String, Any?>, requiredFields: List<String>): Boolean {
        return requiredFields.all { field ->
            val value = data[field]
            value != null && value.toString().isNotBlank()
        }
    }
    
    /**
     * Validate string length constraints
     */
    fun validateStringLength(
        value: String,
        fieldName: String,
        minLength: Int? = null,
        maxLength: Int? = null
    ): Boolean {
        if (minLength != null && value.length < minLength) {
            return false
        }
        if (maxLength != null && value.length > maxLength) {
            return false
        }
        return true
    }
    
    /**
     * Validate email format
     */
    fun validateEmail(email: String): Boolean {
        if (email.isBlank()) return false
        
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        return email.matches(emailRegex.toRegex())
    }
    
    /**
     * Validate number is within range
     */
    fun validateNumberRange(
        value: Double,
        fieldName: String,
        min: Double? = null,
        max: Double? = null
    ): Boolean {
        if (value.isNaN() || value.isInfinite()) {
            return false
        }
        
        if (min != null && value < min) {
            return false
        }
        if (max != null && value > max) {
            return false
        }
        return true
    }
    
    /**
     * Validate GPS coordinates
     */
    fun validateGPSCoordinates(latitude: Double, longitude: Double): Boolean {
        return validateNumberRange(latitude, "latitude", -90.0, 90.0) &&
               validateNumberRange(longitude, "longitude", -180.0, 180.0)
    }
    
    /**
     * Validate enum value is in allowed list
     */
    fun validateEnum(value: String, allowedValues: List<String>, fieldName: String): Boolean {
        return allowedValues.contains(value)
    }
    
    /**
     * Sanitize string by removing potentially dangerous characters
     */
    fun sanitizeString(input: String): String {
        return input.replace(Regex("[<>]"), "").trim()
    }
    
    /**
     * Validate boat data
     */
    fun validateBoatData(data: Map<String, Any?>): Boolean {
        val name = data["name"]?.toString()
        return name != null && validateStringLength(name, "name", 1, 100)
    }
    
    /**
     * Validate trip data
     */
    fun validateTripData(data: Map<String, Any?>): Boolean {
        val boatId = data["boatId"]?.toString()
        val startTime = data["startTime"]?.toString()
        val waterType = data["waterType"]?.toString()
        val role = data["role"]?.toString()
        
        // Required fields
        if (boatId.isNullOrBlank() || startTime.isNullOrBlank()) {
            return false
        }
        
        // Optional enum validation
        if (waterType != null && !validateEnum(waterType, listOf("inland", "coastal", "offshore"), "waterType")) {
            return false
        }
        
        if (role != null && !validateEnum(role, listOf("master", "mate", "operator", "deckhand", "engineer", "other"), "role")) {
            return false
        }
        
        return true
    }
    
    /**
     * Validate GPS point data
     */
    fun validateGPSPointData(data: Map<String, Any?>): Boolean {
        val latitude = data["latitude"] as? Double
        val longitude = data["longitude"] as? Double
        val timestamp = data["timestamp"]?.toString()
        val speed = data["speed"] as? Double
        val heading = data["heading"] as? Double
        
        // Required fields
        if (latitude == null || longitude == null || timestamp.isNullOrBlank()) {
            return false
        }
        
        // Validate coordinates
        if (!validateGPSCoordinates(latitude, longitude)) {
            return false
        }
        
        // Optional field validation
        if (speed != null && !validateNumberRange(speed, "speed", 0.0, 200.0)) {
            return false
        }
        
        if (heading != null && !validateNumberRange(heading, "heading", 0.0, 360.0)) {
            return false
        }
        
        return true
    }
}