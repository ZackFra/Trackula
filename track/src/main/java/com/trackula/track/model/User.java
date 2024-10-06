package com.trackula.track.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table(name="app_user")
public record User(@Id String username, String password, Boolean enabled) {
}
