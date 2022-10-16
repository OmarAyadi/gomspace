package com.example.gomspace.repo;

import org.springframework.data.domain.PageRequest;

public interface UtilMock {
    PageRequest VALID_PAGINATION_MOCK = PageRequest.of(0, 20);
}
