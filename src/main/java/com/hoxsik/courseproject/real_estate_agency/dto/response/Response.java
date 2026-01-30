package com.hoxsik.courseproject.real_estate_agency.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Response {
    private boolean success;
    private HttpStatus status;
    private String response;
}
