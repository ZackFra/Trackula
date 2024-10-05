package com.trackula.track.model;

import org.springframework.data.annotation.Id;

public record Category(@Id Long id, String name) {

}
