package com.mihapetr.socialnetwork.cucumber;

import static io.cucumber.junit.platform.engine.Constants.*;
import org.junit.platform.suite.api.*;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("selected_features") // Path to feature files
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.mihapetr.socialnetwork.cucumber,com.mihapetr.socialnetwork.cucumber.stepdefs")
class CucumberIT {}
