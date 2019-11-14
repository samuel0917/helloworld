# dockerfile-maven-plugin 使用

这是一个简单的Springboot示例,用于演示 [dockerfile-maven-plugin](https://github.com/spotify/dockerfile-maven) 把Maven项目生成Docker Image并Push到私有Registry。

com.spotify有两个插件,网上的资料大都基于老的docker-maven-plugin，在其主页上已经说明：

> 我们建议新项目使用dockerfile-maven。docker-maven-plugin将不再具有新功能或接受新功能的PR。但是，我们将继续提供BUG修复。
> 这个插件是Spotify用于从Java服务构建Docker Image的初始Maven插件。它最初创建于2014年，当时我们第一次开始尝试使用Docker。此插件能够根据pom.xml文件中的配置为您生成Dockerfile，用于FROM Image，使用ADD / COPY添加的资源等。随着时间的推移，我们已经意识到从Java项目构建Docker Image的最简单方法是让开发人员编写Dockerfile。这个插件围绕生成Dockerfiles，将项目目录复制到“staging”目录以用作Docker构建上下文等的行为最终导致了很多不必要的混淆，因为我们的用户引入了额外的抽象和需求用于配置Docker提供的功能。这导致了用于构建docker Image的第二个Maven插件，dockerfile-maven，我们认为这提供了一个更简单的使用Maven生成Docker Image的方法。

虽然说是更简单,但是实际使用中因为文档比较简略,还是有坑的,现记录如下。

1. 报错：
   `Could not acquire image ID or digest following build`
   在1.3.X版本会出现，更新到1.4.X后修复。
2. 报错：
   `Deployment failed: repository element was not specified in the POM inside distributionManagement element or in -DaltDeploymentRepository=id::layout::url parameter`

引入插件

```xml
<plugin>
 <groupId>org.apache.maven.plugins</groupId>
 <artifactId>maven-deploy-plugin</artifactId>
 <configuration>
 <skip>true</skip>
 </configuration> 
</plugin>
```

## dockerfile-maven-plugin Maven Goals：

Maven Goals

Goals available for this plugin:

| Goal               | Description                              | Default Phase |
| ------------------ | ---------------------------------------- | ------------- |
| `dockerfile:build` | Builds a Docker image from a Dockerfile. | package       |
| `dockerfile:tag`   | Tags a Docker image.                     | package       |
| `dockerfile:push`  | Pushes a Docker image to a repository.   | deploy        |

### Skip Docker Goals Bound to Maven Phases

You can pass options to maven to disable the docker goals.

| Maven Option            | What Does it Do?                                                | Default Value |
| ----------------------- | --------------------------------------------------------------- | ------------- |
| `dockerfile.skip`       | Disables the entire dockerfile plugin; all goals become no-ops. | false         |
| `dockerfile.build.skip` | Disables the build goal; it becomes a no-op.                    | false         |
| `dockerfile.tag.skip`   | Disables the tag goal; it becomes a no-op.                      | false         |
| `dockerfile.push.skip`  | Disables the push goal; it becomes a no-op.                     | false         |

如果package时不想打包Image可以这样:

`mvn clean package -Ddockerfile.skip`

## Configuration

### Build Phase

| Maven Option                      | What Does it Do?                                                                                       | Required | Default Value |
| --------------------------------- | ------------------------------------------------------------------------------------------------------ | -------- | ------------- |
| `dockerfile.contextDirectory`     | Directory containing the Dockerfile to build.                                                          | yes      | none          |
| `dockerfile.repository`           | The repository to name the built image                                                                 | no       | none          |
| `dockerfile.tag`                  | The tag to apply when building the Dockerfile, which is appended to the repository.                    | no       | latest        |
| `dockerfile.build.pullNewerImage` | Updates base images automatically.                                                                     | no       | true          |
| `dockerfile.build.noCache`        | Do not use cache when building the image.                                                              | no       | false         |
| `dockerfile.build.cacheFrom`      | Docker image used as cache-from. Pulled in advance if not exist locally or `pullNewerImage` is `false` | no       | none          |
| `dockerfile.buildArgs`            | Custom build arguments.                                                                                | no       | none          |
| `dockerfile.build.squash`         | Squash newly built layers into a single new layer (experimental API 1.25+).                            | no       | false         |

## 示例代码

POM

```xml
<project>
<properties>
 <java.version>1.8</java.version>
 <!--docker私服地址-->
 <docker.repostory>192.168.87.110:5000</docker.repostory>
  </properties>

  <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-deploy-plugin</artifactId>
                <configuration>
                    <skip>true</skip>
                </configuration>
            </plugin>
            <plugin>
                <groupId>com.spotify</groupId>
                <artifactId>dockerfile-maven-plugin</artifactId>
                <version>1.4.10</version>
                <executions>
                    <execution>
                        <id>default</id>
                        <goals>
                            <!--如果package时不想用docker打包,就注释掉这个goal-->
                            <goal>build</goal>
                            <goal>push</goal>
                        </goals>
                    </execution>
                </executions>
                <configuration>
                    <repository>${docker.repostory}/${project.artifactId}</repository>
                    <tag>${project.version}</tag>
                    <buildArgs>
                        <!--提供参数向Dockerfile传递-->            
                                    <JAR_FILE>target/${project.build.finalName}.jar</JAR_FILE>
                    </buildArgs>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
 
```

Dockerfile文件

```yml
FROM openjdk:8u191-jre-alpine3.9
ENTRYPOINT ["/usr/bin/java", "-jar", "/app.jar"]
ARG JAR_FILE
ADD ${JAR_FILE} /app.jar
EXPOSE 8080
```

打包并推送
`mvn deploy`

如果请确认能登录registry

```bash
cat ~/.docker/config.json

{
	"auths": {
		"192.168.87.110:5000": {
			"auth": "YWRtaW46JKDtaW4xMjM="
		}
	},
	"HttpHeaders": {
		"User-Agent": "Docker-Client/18.09.0 (linux)"
	}
}
```

如果没有，`docker login 192.168.87.110:5000`
手工这样是不方便的，如果配置处理，请参阅
[Authentication](https://github.com/spotify/dockerfile-maven/blob/master/docs/authentication.md)
可以在**maven settings.xml**或**pom.xml**中配置，如果要对settings.xml中的密码进行加密，请参阅[Password Encryption](https://maven.apache.org/guides/mini/guide-encryption.html) (从Version 1.4.3支持)

[Demo源码](https://gitee.com/fishdong1107/dockerfile-maven-plugin-demo.git)

