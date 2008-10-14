package org.toobsframework.pres.app.controller;

import org.toobsframework.pres.app.AppReader;

public interface IURLResolver {

  public static String DEFAULT_VIEW = "[default]";

  public IAppView resolve(AppReader appReader, String url, String method);

}
