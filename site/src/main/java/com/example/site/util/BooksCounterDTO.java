package com.example.site.util;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class BooksCounterDTO {
    Long book_id;
    Integer counter;
}
