package com.masferrer.models.dtos;

import org.springframework.web.multipart.MultipartFile;
import lombok.Data;

@Data
public class EmailDTO {
    private String to; //para quien
    private String subject; //asunto
    private String text; //cuerpo del mensaje
    private MultipartFile attachment;
}
