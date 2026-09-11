package com.lucadani.netsims.editor;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TextDocument {
    @NotBlank(message = "Document ID cannot be blank")
    private String id;

    @NotBlank(message = "Document title is mandatory")
    private String title;

    private String content;

    private long version;

    @NotBlank(message = "Last modified date is mandatory")
    private LocalDateTime lastModified;
}
