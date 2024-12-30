package dev.mdalvz.documentgeneratorcommon.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@Getter
@AllArgsConstructor
public enum DocumentInputType {

  HTML("html"),

  TGZ("tgz");

  private final @NonNull String extension;

}
