/*
 * The MIT License (MIT)
 * Copyright (c) 2016 baoyongzhang <baoyz94@gmail.com>
 */
package com.baoyz.treasure

import com.android.build.gradle.AppExtension
import org.gradle.api.*
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.dsl.ArtifactHandler
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.artifacts.dsl.RepositoryHandler
import org.gradle.api.component.SoftwareComponentContainer
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.ConfigurableFileTree
import org.gradle.api.file.CopySpec
import org.gradle.api.file.DeleteSpec
import org.gradle.api.file.FileTree
import org.gradle.api.initialization.dsl.ScriptHandler
import org.gradle.api.invocation.Gradle
import org.gradle.api.logging.Logger
import org.gradle.api.logging.LoggingManager
import org.gradle.api.plugins.*
import org.gradle.api.resources.ResourceHandler
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.WorkResult
import org.gradle.process.ExecResult
import org.gradle.process.ExecSpec
import org.gradle.process.JavaExecSpec

/**
 * Created by baoyongzhang on 16/8/19.
 */
public class ProjectWrapper implements Project{

    private Project mProject;

    public ProjectWrapper(Project project) {
        mProject = project;
    }

    public AppExtension getAndroid(){
        return mProject.android;
    }

    public Project getRootProject() {
        return mProject.getRootProject();
    }

    @Incubating
    public SoftwareComponentContainer getComponents() {
        return mProject.getComponents();
    }

    public ProjectState getState() {
        return mProject.getState();
    }

    public String relativeProjectPath(String s) {
        return mProject.relativeProjectPath(s);
    }

    public File getProjectDir() {
        return mProject.getProjectDir();
    }

    public WorkResult copy(Closure closure) {
        return mProject.copy(closure);
    }

    public void configurations(Closure closure) {
        mProject.configurations(closure);
    }

    public ExecResult exec(Action<? super ExecSpec> action) {
        return mProject.exec(action);
    }

    @Incubating
    public PluginManager getPluginManager() {
        return mProject.getPluginManager();
    }

    public String getName() {
        return mProject.getName();
    }

    public Project evaluationDependsOn(String s) throws UnknownProjectException {
        return mProject.evaluationDependsOn(s);
    }

    public void artifacts(Closure closure) {
        mProject.artifacts(closure);
    }

    public AntBuilder createAntBuilder() {
        return mProject.createAntBuilder();
    }

    public void afterEvaluate(Closure closure) {
        mProject.afterEvaluate(closure);
    }

    public <T> NamedDomainObjectContainer<T> container(Class<T> aClass) {
        return mProject.container(aClass);
    }

    public ExtensionContainer getExtensions() {
        return mProject.getExtensions();
    }

    public Map<String, ?> getProperties() {
        return mProject.getProperties();
    }

    public FileTree zipTree(Object o) {
        return mProject.zipTree(o);
    }

    public ResourceHandler getResources() {
        return mProject.getResources();
    }

    public boolean hasProperty(String s) {
        return mProject.hasProperty(s);
    }

    public File getBuildDir() {
        return mProject.getBuildDir();
    }

    public void repositories(Closure closure) {
        mProject.repositories(closure);
    }

    public FileTree tarTree(Object o) {
        return mProject.tarTree(o);
    }

    public void apply(Action<? super ObjectConfigurationAction> action) {
        mProject.apply(action);
    }

    public <T> NamedDomainObjectContainer<T> container(Class<T> aClass, Closure closure) {
        return mProject.container(aClass, closure);
    }

    public ExecResult javaexec(Action<? super JavaExecSpec> action) {
        return mProject.javaexec(action);
    }

    public void subprojects(Closure closure) {
        mProject.subprojects(closure);
    }

    public CopySpec copySpec(Closure closure) {
        return mProject.copySpec(closure);
    }

    public URI uri(Object o) {
        return mProject.uri(o);
    }

    public String relativePath(Object o) {
        return mProject.relativePath(o);
    }

    public String getDescription() {
        return mProject.getDescription();
    }

    public void setVersion(Object o) {
        mProject.setVersion(o);
    }

    public File mkdir(Object o) {
        return mProject.mkdir(o);
    }

    public void allprojects(Closure closure) {
        mProject.allprojects(closure);
    }

    public ScriptHandler getBuildscript() {
        return mProject.getBuildscript();
    }

    public ConfigurableFileTree fileTree(Map<String, ?> map) {
        return mProject.fileTree(map);
    }

    public ArtifactHandler getArtifacts() {
        return mProject.getArtifacts();
    }

    public AntBuilder getAnt() {
        return mProject.getAnt();
    }

    public Set<Project> getSubprojects() {
        return mProject.getSubprojects();
    }

    public CopySpec copySpec(Action<? super CopySpec> action) {
        return mProject.copySpec(action);
    }

    public boolean delete(Object... objects) {
        return mProject.delete(objects);
    }

    @Override
    WorkResult delete(Action<? super DeleteSpec> action) {
        return project.delete(action);
    }

    public ConfigurationContainer getConfigurations() {
        return mProject.getConfigurations();
    }

    public Project getParent() {
        return mProject.getParent();
    }

    public String absoluteProjectPath(String s) {
        return mProject.absoluteProjectPath(s);
    }

    public Gradle getGradle() {
        return mProject.getGradle();
    }

    public Project project(String s) throws UnknownProjectException {
        return mProject.project(s);
    }

    public void afterEvaluate(Action<? super Project> action) {
        mProject.afterEvaluate(action);
    }

    public Project getProject() {
        return mProject.getProject();
    }

    public Task task(String s) throws InvalidUserDataException {
        return mProject.task(s);
    }

    public Logger getLogger() {
        return mProject.getLogger();
    }

    public void buildscript(Closure closure) {
        mProject.buildscript(closure);
    }

    public CopySpec copySpec() {
        return mProject.copySpec();
    }

    public DependencyHandler getDependencies() {
        return mProject.getDependencies();
    }

    public ConfigurableFileCollection files(Object... objects) {
        return mProject.files(objects);
    }

    public Project findProject(String s) {
        return mProject.findProject(s);
    }

    public Iterable<?> configure(Iterable<?> iterable, Closure closure) {
        return mProject.configure(iterable, closure);
    }

    public void apply(Map<String, ?> map) {
        mProject.apply(map);
    }

    public Task task(String s, Closure closure) {
        return mProject.task(s, closure);
    }

    public ExecResult exec(Closure closure) {
        return mProject.exec(closure);
    }

    public Project project(String s, Closure closure) {
        return mProject.project(s, closure);
    }

    public <T> NamedDomainObjectContainer<T> container(Class<T> aClass, NamedDomainObjectFactory<T> namedDomainObjectFactory) {
        return mProject.container(aClass, namedDomainObjectFactory);
    }

    public void setDefaultTasks(List<String> list) {
        mProject.setDefaultTasks(list);
    }

    public void setDescription(String s) {
        mProject.setDescription(s);
    }

    public Task task(Map<String, ?> map, String s) throws InvalidUserDataException {
        return mProject.task(map, s);
    }

    public AntBuilder ant(Closure closure) {
        return mProject.ant(closure);
    }

    public Map<Project, Set<Task>> getAllTasks(boolean b) {
        return mProject.getAllTasks(b);
    }

    public File file(Object o, PathValidation pathValidation) throws InvalidUserDataException {
        return mProject.file(o, pathValidation);
    }

    public File file(Object o) {
        return mProject.file(o);
    }

    public Object getGroup() {
        return mProject.getGroup();
    }

    public Object getStatus() {
        return mProject.getStatus();
    }

    public TaskContainer getTasks() {
        return mProject.getTasks();
    }

    public Convention getConvention() {
        return mProject.getConvention();
    }

    public <T> Iterable<T> configure(Iterable<T> iterable, Action<? super T> action) {
        return mProject.configure(iterable, action);
    }

    public WorkResult copy(Action<? super CopySpec> action) {
        return mProject.copy(action);
    }

    public File getBuildFile() {
        return mProject.getBuildFile();
    }

    public int depthCompare(Project project) {
        return mProject.depthCompare(project);
    }

    public ConfigurableFileCollection files(Object o, Closure closure) {
        return mProject.files(o, closure);
    }

    public int compareTo(Project o) {
        return mProject.compareTo(o);
    }

    public Object configure(Object o, Closure closure) {
        return mProject.configure(o, closure);
    }

    public int getDepth() {
        return mProject.getDepth();
    }

    public void setStatus(Object o) {
        mProject.setStatus(o);
    }

    public void evaluationDependsOnChildren() {
        mProject.evaluationDependsOnChildren();
    }

    public Object property(String s) throws MissingPropertyException {
        return mProject.property(s);
    }

    @Override
    Object findProperty(String s) {
        return mProject.findProject(s);
    }

    public Map<String, Project> getChildProjects() {
        return mProject.getChildProjects();
    }

    public ConfigurableFileTree fileTree(Object o, Closure closure) {
        return mProject.fileTree(o, closure);
    }

    public Object getVersion() {
        return mProject.getVersion();
    }

    public RepositoryHandler getRepositories() {
        return mProject.getRepositories();
    }

    public ExecResult javaexec(Closure closure) {
        return mProject.javaexec(closure);
    }

    public void allprojects(Action<? super Project> action) {
        mProject.allprojects(action);
    }

    public LoggingManager getLogging() {
        return mProject.getLogging();
    }

    public Task task(Map<String, ?> map, String s, Closure closure) {
        return mProject.task(map, s, closure);
    }

    public List<String> getDefaultTasks() {
        return mProject.getDefaultTasks();
    }

    public void setProperty(String s, Object o) throws MissingPropertyException {
        mProject.setProperty(s, o);
    }

    public void beforeEvaluate(Action<? super Project> action) {
        mProject.beforeEvaluate(action);
    }

    public void setBuildDir(Object o) {
        mProject.setBuildDir(o);
    }

    public File getRootDir() {
        return mProject.getRootDir();
    }

    public String getPath() {
        return mProject.getPath();
    }

    public Set<Project> getAllprojects() {
        return mProject.getAllprojects();
    }

    public void setGroup(Object o) {
        mProject.setGroup(o);
    }

    public void apply(Closure closure) {
        mProject.apply(closure);
    }

    public Set<Task> getTasksByName(String s, boolean b) {
        return mProject.getTasksByName(s, b);
    }

    public void defaultTasks(String... strings) {
        mProject.defaultTasks(strings);
    }

    public void subprojects(Action<? super Project> action) {
        mProject.subprojects(action);
    }

    public void dependencies(Closure closure) {
        mProject.dependencies(closure);
    }

    public ConfigurableFileTree fileTree(Object o) {
        return mProject.fileTree(o);
    }

    public void beforeEvaluate(Closure closure) {
        mProject.beforeEvaluate(closure);
    }

    public PluginContainer getPlugins() {
        return mProject.getPlugins();
    }
}
