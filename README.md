# renjin-test-maven-plugin
Previously called renjin-hamcrest-maven-plugin.
A maven plugin to execute R tests using the Renjin ScriptEngine

It executes R Hamcrest and/or Testthat test located in src/test/R dir per default.

Simple example test:
```r
library(hamcrest)

test.successTest <- function() {
  print("running success test")
  assertTrue(TRUE)
}
```
See [Using the hamcrest package to write unit tests](http://docs.renjin.org/en/latest/writing-renjin-extensions.html#using-the-hamcrest-package-to-write-unit-tests)
for more info about Renjin Hamcrest.


It executes R Hamcrest test located in src/test/R dir per default.
Testthat tests are also supported. 

In some regards it is not as advanced as the test plugin in the renjin-maven-plugin e.g.
tests are not forked and hence slightly slower; also a severely misbehaving test could crash the build but
it does provide more developer friendly output saving you from having to analyze log output
in the test result files. Also print() in the test will write to the console just like a
junit test would do. 

To use it, add the following to your maven build plugins section:

```xml
<plugins>
  <plugin>
    <groupId>se.alipsa</groupId>
    <artifactId>renjin-test-maven-plugin</artifactId>
    <version>1.3.6</version>
    <configuration>
      <testFailureIgnore>true</testFailureIgnore>
    </configuration>
    <executions>
      <execution>
        <phase>test</phase>
        <goals>
          <goal>testR</goal>
        </goals>
      </execution>
    </executions>
    <dependencies>
      <dependency>
        <groupId>org.renjin</groupId>
        <artifactId>renjin-script-engine</artifactId>
        <version>${renjin.version}</version>
        <exclusions>
          <!-- optional but needed if you use e.g.slf4j (then use the jcl bridge instead) -->
          <exclusion>
            <groupId>commons-logging</groupId>
            <artifactId>commons-logging</artifactId>
          </exclusion>
        </exclusions>
      </dependency>
      <dependency>
        <groupId>org.renjin</groupId>
        <artifactId>hamcrest</artifactId>
        <version>${renjin.version}</version>
      </dependency>
    </dependencies>
  </plugin>
</plugins>
```

To use the latest code, build it with `mvn clean install` and then add the plugin to your pom as follows:  

```xml
<plugins>
  <plugin>
    <groupId>se.alipsa</groupId>
    <artifactId>renjin-test-maven-plugin</artifactId>
    <!-- match the version with the version in the plugin pom -->
    <version>1.3.6</version>
    <configuration>
      <testFailureIgnore>true</testFailureIgnore>
    </configuration>
    <executions>
      <execution>
        <phase>test</phase>
        <goals>
          <goal>testR</goal>
        </goals>
      </execution>
    </executions>
    <dependencies>
      <dependency>
        <groupId>org.renjin</groupId>
        <artifactId>renjin-script-engine</artifactId>
        <version>${renjin.version}</version>
        <exclusions>
          <exclusion>
            <groupId>commons-logging</groupId>
            <artifactId>commons-logging</artifactId>
          </exclusion>
        </exclusions>
      </dependency>
      <dependency>
        <groupId>org.renjin</groupId>
        <artifactId>hamcrest</artifactId>
        <version>${renjin.version}</version>
      </dependency>
    </dependencies>
  </plugin>
</plugins>
```
Where ${renjin.version} is the version of Renjin you want to use e.g. 0.9.2719

### Configuration
- reportOutputDirectory 
    - where the test logs will be, default to "${project.build.directory}/renjin-test-reports"
- testSourceDirectory 
    - where the test sources reside, defaults to "${project.basedir}/src/test/R
- testResourceDirectory
    - where the test resources reside, defaults to "${project.basedir}/src/test/resources"   
- testOutputDirectory
    - where the tests will be executed from, defaults to "${project.build.testOutputDirectory}"   
- skipTests
    - Whether to skip tests altogether, defaults to false  
- testFailureIgnore
    - Whether to halt the build on the first failure encountered or not, defaults to false
- runSourceScriptsBeforeTests
    -Whether to run the R scripts in src/main/R prior to running tests (useful for non-package projects), 
    defaults for false.
- sourceDirectory
    - where the main R scripts are, defaults to "${project.basedir}/src/main/R"
- printSuccess
    - echo "Success" after each test is successful, defaults to false
- replaceStringsWhenCopy
    - replace string occurrences in the R scripts when they are copied from the src to target
    this is useful if you created a plugin where the same code should work in both GNU R and Renjin 
    but you want to publish the renjin version with a group name you own instead of org.renjin.cran
    and hence you need to change the call to library name in your tests
    to include the group name (see example below). You can have as many <property></property> 
    blocks as you want.
                   
Example of overriding a few parameters:
```xml
    <build>
        <plugins>
            <plugin>
                <groupId>se.alipsa</groupId>
                <artifactId>renjin-test-maven-plugin</artifactId>
                <version>1.3.6</version>
                <configuration>
                    <outputDirectory>target/test-harness/project-to-test</outputDirectory>
                    <testSourceDirectory>R/test</testSourceDirectory>
                    <testFailureIgnore>true</testFailureIgnore>
                    <replaceStringsWhenCopy>
                      <property>
                        <name>library(xmlr)</name>
                        <value>library('se.alipsa:xmlr')</value>
                      </property>
                    </replaceStringsWhenCopy>
                </configuration>
                <executions>
                  <execution>
                    <phase>test</phase>
                    <goals>
                      <goal>testR</goal>
                    </goals>
                  </execution>
                </executions>
                <dependencies>
                  <dependency>
                    <groupId>org.renjin</groupId>
                    <artifactId>renjin-script-engine</artifactId>
                    <version>${renjin.version}</version>
                    <exclusions>
                      <exclusion>
                        <groupId>commons-logging</groupId>
                        <artifactId>commons-logging</artifactId>
                      </exclusion>
                    </exclusions>
                  </dependency>
                  <dependency>
                    <groupId>org.renjin</groupId>
                    <artifactId>hamcrest</artifactId>
                    <version>${renjin.version}</version>
                  </dependency>
                </dependencies>
            </plugin>
        </plugins>
    </build>
```

# Generate a test report
You can use the surefire report plugin to generate a nice looking html report in the target/site dir.
Add something like the following to your maven pom:

```xml
<plugins>
    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-surefire-report-plugin</artifactId>
        <version>3.0.0-M5</version>
        <configuration>
          <title>R Tests Report</title>
          <outputName>test-report</outputName>
          <reportsDirectories>${project.build.directory}/renjin-test-reports</reportsDirectories>
          <linkXRef>false</linkXRef>
        </configuration>
        <executions>
          <execution>
            <phase>test</phase>
            <goals>
              <goal>report-only</goal>
            </goals>
          </execution>
        </executions>
    </plugin>
    <!-- the site plugin will create formatting stuff e.g. css etc. --> 
    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-site-plugin</artifactId>
        <version>3.9.1</version>
        <configuration>
          <generateReports>false</generateReports>
        </configuration>
        <executions>
          <execution>
            <phase>test</phase>
            <goals>
              <goal>site</goal>
            </goals>
          </execution>
        </executions>
    </plugin>
</plugins>
```      
Then run `mvn clean test site` or similar to generate the report

# Testthat support
This plugin was originally completely targeted for hamcrest but I noticed that
adding some level of support for testthat was easy so I decided to make this a 
a renjin test plugin instead and add support for testthat.

Testthat support is somewhat limited at this point. Basically i the plugin detects 
the testthat structure it will execute only the testthat.R file and leave it to the 
testthat package to handle. Since that is the case the reporting will not look wonderful - 
the plugin will think that only one testfile was executed. But the reason for this is that
users might put all sorts of thing in the testthat.R file needed for bootstrapping everything
so either execute this file only or run all the tests twice and have a nice report but terrible 
console output and unnecessary test executions. 
  
# Executing both hamcrest and testthat tests
If you have both in one project you need to add an additional execution target:
```xml   
<plugin>
<groupId>se.alipsa</groupId>
<artifactId>renjin-test-maven-plugin</artifactId>
<version>1.3.6</version>
<executions>
  <execution>
    <phase>test</phase>
    <id>testthat</id>
    <goals>
      <goal>testR</goal>
    </goals>
    <configuration>
      <testFailureIgnore>true</testFailureIgnore>
      <testSourceDirectory>${project.basedir}/tests</testSourceDirectory>
    </configuration>
  </execution>
  <execution>
    <id>hamcrest</id>
    <phase>test</phase>
    <goals>
      <goal>testR</goal>
    </goals>
    <configuration>
      <testFailureIgnore>true</testFailureIgnore>
      <testSourceDirectory>${project.basedir}/src/test/R</testSourceDirectory>
    </configuration>
  </execution>
</executions>
<dependencies>
  <dependency>
    <groupId>org.renjin</groupId>
    <artifactId>renjin-script-engine</artifactId>
    <version>${renjin.version}</version>
    <exclusions>
      <exclusion>
        <groupId>commons-logging</groupId>
        <artifactId>commons-logging</artifactId>
      </exclusion>
    </exclusions>
  </dependency>
  <dependency>
    <groupId>org.renjin</groupId>
    <artifactId>hamcrest</artifactId>
    <version>${renjin.version}</version>
  </dependency>
  <dependency>
    <groupId>org.renjin.cran</groupId>
    <artifactId>testthat</artifactId>
    <version>2.1.1-b2</version>
  </dependency>
  <dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-log4j12</artifactId>
    <version>1.7.30</version>
  </dependency>
  <dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>jcl-over-slf4j</artifactId>
    <version>1.7.30</version>
  </dependency>         
</dependencies>
</plugin>
``` 

# Version history

### 1.3.6
- Add option for filter copy (replace strings in the R code) for tests

### 1.3.5
- upgrade junit, commons-io, commons-lang3 and maven plugins
- add auto codescan

### 1.3.4
- copy full content of testResourceDirectory

### 1.3.3
- Minor fix: honor the -DskipTest as well as true property.

### 1.3.2
- fix bug where R files in test/resources was deleted from test-classes output dir. 
- Bump up ioutils version.

### 1.3.1
- Fix bug in the cleanup that was deleting everything which prevents java tests from working. Now only removes R and S files

### 1.3
- Added basic support for testthat.
- Renamed artifact to reflect change of purpose.
- Enhanced docs.

### 1.2
- move session creation to after classloader has been configured
- set working dir for src R scripts to where the script is located
- enable execution of src R code prior to tests to support non-package testing scenarios.
- make logging implementation classes provide
- skip execution if no test files found
- remove dependency on renjin core.
- bumped up dependency versions, fix failure if no R test sources exists

### 1.1
- Minor fixes such as removing some printlines and adding total time to console output.

### 1.0-final
initial release
