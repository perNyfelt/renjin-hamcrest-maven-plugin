# renjin-hamcrest-maven-plugin
A maven plugin to execute R hamcrest tests using the Renjin ScriptEngine

It executes R Hamcrest test located in src/test/R dir.

In some regards it is not as advanced as the test plugin in the renjin-maven-plugin e.g.
tests are not forked and hence a severely misbehaving test could crash the build but
it does provide more developer friendly output saving you from having to analyze log output
in the test result files. Also print() in the test will write to the console just like a
junit test would do. 

To use it, build it with `mvn clean install` and then add the plugin to your pom as follows:  

````
    <build>
        <plugins>
            <plugin>
                <groupId>se.alipsa</groupId>
                <artifactId>renjin-hamcrest-maven-plugin</artifactId>
                <version>1.0-SNAPSHOT</version>
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
            </plugin>
        </plugins>
    </build>
````

### Configuration
- outputDirectory 
    - where the test logs will ge, default to "${project.build.outputDirectory}/renjin-hamcrest-test-reports"
- testSourceDirectory 
    - where the tests reside, defaults to "${project.basedir}/src/test/R
- skipTests
    - Whether to skip tests altogether, defaults to false  
- testFailureIgnore
    - Whether to halt the build on the first faiure encountered or not, defaults to false
    
Example of overriding a few parameters:
````
    <build>
        <plugins>
            <plugin>
                <groupId>se.alipsa</groupId>
                <artifactId>renjin-hamcrest-maven-plugin</artifactId>
                <version>1.0-SNAPSHOT</version>
                <configuration>
                    <outputDirectory>target/test-harness/project-to-test</outputDirectory>
                    <testSourceDirectory>R/test</testSourceDirectory>
                    <testFailureIgnore>true</testFailureIgnore>
                </configuration>
            </plugin>
        </plugins>
    </build>
````              

