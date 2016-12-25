package ua.tifoha.maven;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo (name = "merge", defaultPhase = LifecyclePhase.PACKAGE)
public class MergerMojo extends AbstractMojo {
	@Parameter (defaultValue = "${project.build.directory}/classes", required = true)
	private File buildDirectory;

//	@Parameter (property = "project.basedir", defaultValue = "${project.basedir}", required = true)
//	private File baseDir;

	@Parameter (defaultValue = "true")
	private boolean deleteSourceFiles;

	private static Properties loadProperties(Path path) {
		try (InputStream inputStream = Files.newInputStream(path);) {
			Properties properties = new Properties();
			properties.load(inputStream);
			return properties;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static Path getBasePath(Path path) {
		Path fileName = path.getFileName();
		String[] parts = fileName.toString().split("\\.");
		if (parts.length >= 3 && (isLocalProperties(parts[0]) || isTemplateProperties(parts[0]))) {
			String baseFileNameString = Stream.of(parts).skip(1).collect(Collectors.joining("."));
			return path.getParent().resolve(baseFileNameString);
		}
		return path;
	}

	private static boolean isLocalProperties(String fileName) {
		return isMatch("local", fileName);
	}

	private static boolean isTemplateProperties(String fileName) {
		return isMatch("template", fileName);
	}


	private static boolean isMatch(String prefix, String fileName) {
		String s = fileName.toLowerCase();
		return s.startsWith(prefix.toLowerCase());
	}

	public void execute() throws MojoExecutionException {
		Log logger = getLog();
		Pattern pattern = Pattern.compile(".+\\.properties");
		Predicate<Path> propertiesPredicate = path -> pattern.matcher(path.toString()).matches();
		Path buildPath = buildDirectory.toPath();
		logger.debug("read directory:" + buildPath);
		try {
			Map<Path, List<Path>> propGroups = Files.walk(buildPath)
													.filter(propertiesPredicate)
													.collect(Collectors.groupingBy(MergerMojo::getBasePath));

			for (Map.Entry<Path, List<Path>> pathListEntry : propGroups.entrySet()) {
				Path mainProperty = pathListEntry.getKey();
				logger.debug("processing property file:" + mainProperty);
				Properties properties = new Properties();
				pathListEntry.getValue().stream()
							 .filter(path -> isTemplateProperties(path.getFileName().toString()))
							 .peek(path -> logger.debug("applying templete:" + path))
							 .map(MergerMojo::loadProperties)
							 .forEach(properties::putAll);

				if (Files.exists(mainProperty)) {
					logger.debug("applying main file:" + mainProperty);
					properties.putAll(loadProperties(mainProperty));
				}

				pathListEntry.getValue().stream()
							 .filter(path -> isLocalProperties(path.getFileName().toString()))
							 .peek(path -> logger.debug("applying local properties:" + path))
							 .map(MergerMojo::loadProperties)
							 .forEach(properties::putAll);

				try (OutputStream outputStream = Files.newOutputStream(mainProperty)) {
					properties.store(outputStream, "Generated by 'property-merger-maven-plugin'");
				}

				if (deleteSourceFiles) {
					pathListEntry.getValue().stream()
								 .filter((path) -> !mainProperty.equals(path))
								 .forEach(path -> {
									 try {
										 Files.deleteIfExists(path);
									 } catch (IOException e) {
										 logger.warn("Cannot delete file: " + path);
									 }
								 });
				}
			}
		} catch (IOException e) {
			throw new MojoExecutionException("Cannot process file", e);
		}
	}
}
