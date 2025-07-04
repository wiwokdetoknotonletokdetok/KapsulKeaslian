package org.gaung.wiwokdetok.kapsulkeaslian.validation;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class PaginationValidator {

    public void validatePageAndSizeNumber(int page, int size) {
        if (size < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Size harus dimulai dari 1");
        }

        if (page < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page harus dimulai dari 1");
        }
    }

    public void validatePageBounds(int page, Page<?> resultPage) {
        if (resultPage.getTotalPages() > 0 && page > resultPage.getTotalPages()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page melebihi jumlah halaman yang tersedia");
        }
    }
}
