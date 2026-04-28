<%--
Copyright (C) 2025-2026, AdaCore
SPDX-License-Identifier: GPL-3.0-or-later

Template file to generate a Maven project Java bindings.
--%>
@import com.adacore.gnatpolyglot.proxy.Proxy
@import com.adacore.gnatpolyglot.proxy2java.JavaAPI

@param JavaAPI api
@param Proxy proxy

<project>

    <modelVersion>4.0.0</modelVersion>

    <groupId>com.adacore</groupId>
    <artifactId>${proxy.name.toLower()}</artifactId>
    <version>0.1</version>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <build>
        <plugins>

            <plugin>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
                <configuration>
                    <compilerArgs>
                        <arg>-Xlint:all</arg>
                    </compilerArgs>
                    <release>24</release>
                </configuration>
            </plugin>

            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-shade-plugin</artifactId>
                <version>3.5.2</version>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals>
                            <goal>shade</goal>
                        </goals>
                        <configuration>
                            <finalName>${proxy.name.toLower()}</finalName>
                            <outputDirectory>
                                ${"${project.build.directory}"}/lib
                            </outputDirectory>
                            <filters>
                                <filter>
                                    <artifact>*:*</artifact>
                                    <excludes>
                                        <exclude>META-INF/*.SF</exclude>
                                        <exclude>META-INF/*.DSA</exclude>
                                        <exclude>META-INF/*.RSA</exclude>
                                    </excludes>
                                </filter>
                            </filters>
                        </configuration>
                    </execution>
                </executions>
            </plugin>

        </plugins>
    </build>

    <dependencies>
        <dependency>
            <groupId>com.adacore.gnatpolyglot.runtime</groupId>
            <artifactId>proxy2java</artifactId>
            <version>0.1</version>
        </dependency>

        <dependency>
            <groupId>com.adacore.gnatpolyglot.runtime</groupId>
            <artifactId>ada2java</artifactId>
            <version>0.1</version>
        </dependency>
    </dependencies>

</project>

