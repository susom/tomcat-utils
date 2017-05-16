/*
 * Copyright 2017 The Board of Trustees of The Leland Stanford Junior University.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.susom.tomcat;

import java.io.FileReader;
import java.io.Reader;
import java.util.Properties;

/**
 * Alternative bootstrapping mechanism for Tomcat that can load properties
 * from a file and place them in the system properties. This helps avoid
 * the use of Docker environment variables for properties that might be
 * sensitive. To use it, pass -Dproperties=/some.properties on the command
 * line, while invoking this class as a substitute for the regular Tomcat
 * Bootstrap class (org.apache.catalina.startup.Bootstrap).
 *
 * @author garricko
 */
public class Bootstrap {
  public static void main(String[] args) {
    String filename = System.getProperty("properties");

    if (filename != null) {
      try (Reader reader = new FileReader(filename)) {
        Properties fileProperties = new Properties();
        fileProperties.load(reader);
        for (String key : fileProperties.stringPropertyNames()) {
          String value = fileProperties.getProperty(key);
          if (value == null || System.getProperty(key) != null) {
            continue;
          }
          System.setProperty(key, value);
        }
      } catch (Exception e) {
        e.printStackTrace();
        System.exit(1);
      }
    }

    org.apache.catalina.startup.Bootstrap.main(args);
  }
}
