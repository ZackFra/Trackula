package com.trackula.track.model;

import org.springframework.data.annotation.Id;

public record Authorities(@Id Long id, String authority, String username) {
}
