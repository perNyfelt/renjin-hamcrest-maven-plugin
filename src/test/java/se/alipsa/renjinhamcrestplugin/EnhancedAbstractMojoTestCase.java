package se.alipsa.renjinhamcrestplugin;

import java.io.File;
import java.util.Arrays;

import org.apache.maven.DefaultMaven;
import org.apache.maven.Maven;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.DefaultMavenExecutionResult;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequestPopulator;
import org.apache.maven.execution.MavenExecutionResult;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingRequest;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.internal.impl.SimpleLocalRepositoryManagerFactory;
import org.eclipse.aether.repository.LocalRepository;
import org.junit.Ignore;

/** Based on APL code created by Alex Heneveld
 * https://github.com/ahgittin/license-audit-maven-plugin/blob/master/src/test/java/org/heneveld/maven/license_audit/BetterAbstractMojoTestCase.java
 */
@Ignore
public class EnhancedAbstractMojoTestCase extends AbstractMojoTestCase {

  protected MavenSession newMavenSession() {
    try {
      MavenExecutionRequest request = new DefaultMavenExecutionRequest();
      MavenExecutionResult result = new DefaultMavenExecutionResult();

      // populate sensible defaults, including repository basedir and remote repos
      MavenExecutionRequestPopulator populator;
      populator = getContainer().lookup( MavenExecutionRequestPopulator.class );
      populator.populateDefaults( request );

      // this is needed to allow java profiles to get resolved; i.e. avoid during project builds:
      request.setSystemProperties( System.getProperties() );

      // and this is needed so that the repo session in the maven session
      // has a repo manager, and it points at the local repo
      // (cf MavenRepositorySystemUtils.newSession() which is what is otherwise done)
      DefaultMaven maven = (DefaultMaven) getContainer().lookup( Maven.class );
      DefaultRepositorySystemSession repoSession =
          (DefaultRepositorySystemSession) maven.newRepositorySession( request );
      repoSession.setLocalRepositoryManager(
          new SimpleLocalRepositoryManagerFactory().newInstance(repoSession,
              new LocalRepository( request.getLocalRepository().getBasedir() ) ));

      @SuppressWarnings("deprecation")
      MavenSession session = new MavenSession( getContainer(),
          repoSession,
          request, result );
      return session;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /** Extends the super to use the new {@link #newMavenSession()} introduced here
   * which sets the defaults one expects from maven; the standard test case leaves a lot of things blank */
  @Override
  protected MavenSession newMavenSession(MavenProject project) {
    MavenSession session = newMavenSession();
    session.setCurrentProject( project );
    session.setProjects( Arrays.asList( project ) );
    return session;
  }

  /** As {@link #lookupConfiguredMojo(MavenProject, String)} but taking the pom file
   * and creating the {@link MavenProject}. */
  protected Mojo lookupConfiguredMojo(File pom, String goal) throws Exception {
    assertNotNull(pom);
    assertTrue(pom.exists());

    ProjectBuildingRequest buildingRequest = newMavenSession().getProjectBuildingRequest();
    ProjectBuilder projectBuilder = lookup(ProjectBuilder.class);
    MavenProject project = projectBuilder.build(pom, buildingRequest).getProject();

    RenjinHamcrestMojo plainMojo = (RenjinHamcrestMojo) lookupMojo(goal, pom);
    RenjinHamcrestMojo configuredMojo = (RenjinHamcrestMojo) lookupConfiguredMojo(project, goal);
    if (plainMojo.isTestFailureIgnore() == true) {
      configuredMojo.setTestFailureIgnore(true);
    }
    if (plainMojo.getReportOutputDirectory() != null) {
      configuredMojo.setReportOutputDirectory(plainMojo.getReportOutputDirectory());
    }
    if (plainMojo.isSkipTests() == true) {
      configuredMojo.setSkipTests(true);
    }
    if (plainMojo.getTestOutputDirectory() != null) {
      configuredMojo.setTestOutputDirectory(plainMojo.getTestOutputDirectory());
    }
    if (plainMojo.getTestSourceDirectory() != null) {
      configuredMojo.setTestSourceDirectory(plainMojo.getTestSourceDirectory());
    }
    return configuredMojo;
  }
}
