package com.transport.urbain.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for driver registration requests.
 * 
 * <p>Represents the request data required to register a new driver account.
 * Includes all passenger registration fields plus driver-specific information:
 * <ul>
 *   <li>Driver's license number</li>
 *   <li>License expiration date</li>
 *   <li>Vehicle information</li>
 * </ul>
 * 
 * <p>This DTO extends the validation rules from RegisterRequest with
 * additional driver-specific validations.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverRegisterRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$",
            message = "Password must contain at least one digit, one lowercase, one uppercase, and one special character")
    private String password;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 100, message = "First name must be between 2 and 100 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 100, message = "Last name must be between 2 and 100 characters")
    private String lastName;

    @NotBlank(message = "Phone number is required for drivers")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Phone number must be valid")
    private String phoneNumber;

    @NotBlank(message = "Driver's license number is required")
    @Size(min = 5, max = 50, message = "License number must be between 5 and 50 characters")
    private String licenseNumber;

    @NotBlank(message = "License expiration date is required")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Date must be in format YYYY-MM-DD")
    private String licenseExpirationDate;

    @Size(max = 200, message = "Additional information must not exceed 200 characters")
    private String additionalInfo;
}

