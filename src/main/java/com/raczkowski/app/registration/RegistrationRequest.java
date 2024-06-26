package com.raczkowski.app.registration;

import lombok.*;

@Getter
@AllArgsConstructor
@ToString
public class RegistrationRequest {
    @NonNull
    private final String firstName;
    @NonNull
    private final String lastName;
    @NonNull
    private final String password;
    @NonNull
    private final String email;

    private final String description;

    private final String birthDate;

    private final String city;
}
