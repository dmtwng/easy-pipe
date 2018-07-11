EasyPipe is a data streaming library, which handles all important aspects of each pipeline and provide possibility to 
build pipelines fast and easy.

<br>

---

![Spring](/img/spring.png) | Built on top of SpringBoot, so you can use all the advantages of Spring ecosystem.

---

Concentrate on the business goals and implementing of particular streaming steps. | ![Spring](/img/objective.png)

---

![Spring](/img/puzzles.png) | Easily connect implemented steps and build data streaming pipeline.

---

Get comfortable control of your pipes to have possibility to run and stop via HTTP. | ![Spring](/img/control.png)

---

![Spring](/img/monitoring.png) | Get all important metrics of your data streaming pipeline and separate steps out of the box. Easily export it to your favorite monitoring system.

---

## How it works
![EasyPipe Architecture](/img/architecture.png)
 - Container with pipelines (`PipesManager`) runs within [Spring Boot](https://spring.io/projects/spring-boot) application, 
 so you can use all benefits of **Spring Container** in your pipelines.
 - `PipesManager` could contain several pipelines withing one application, each of them should have
 an uniq identifier.
 - `PipesManager` exposes **HTTP endpoints** to manage pipelines. It provides commands such as run or stop
 pipeline, get status. 
 - Each pipeline consists of several stages (`PipeStage`), each stage is a wrapper for one `Component`.
 - `PipeStage` is a part of EasyPipe application, and provides management of messages sequence and its
 stream between `Components` 
 - `PipeStage`, using [Micrometer](https://micrometer.io/), collects the main measures of streaming system
 for each step of pipeline, such as messages count, execution time etc. **Micrometer** provides integration
 with the most monitoring systems.
 - `Components` is the ones, who represent the business logic of each data streaming pipeline. There are a few 
 types of components: `Source`, `Processor`, `Destination`.

## Installation
Add EasyPipe dependency to your Spring Boot application.
```xml
<dependency>
    <groupId>com.altumpoint.easypipe</groupId>
    <artifactId>core</artifactId>
    <version>0.1.1</version>
</dependency>
```

Add Bintray repository to your project.
```xml
<repositories>
    <repository>
        <snapshots>
            <enabled>false</enabled>
        </snapshots>
        <id>bintray-dmytro-winged-maven</id>
        <name>bintray</name>
        <url>https://dl.bintray.com/dmytro-winged/maven</url>
    </repository>
</repositories>
```

Project on [Bintray](https://bintray.com/dmytro-winged/maven/easy-pipe)

## Usage Instructions
Build your EasyPipe with builder and add to application context with `EasyPipeComponent` annotation.
```java
    @Autowired
    @EasyPipeComponent("doubles-stream")
    public EasyPipe doublesStream(
            PipeBuilder pipeBuilder,
            DoublesConsumer doublesConsumer,
            PercentsTransformer percentsTransformer,
            LogsPublisher logsPublisher
    ) {
        return pipeBuilder
                .startPipe("doubles-stream", doublesConsumer)
                .addTransformer("d-transformer", percentsTransformer)
                .addPublisher(logsPublisher)
                .build();
    }
```


## License
EasyPipe is Open Source software released under the
[Apache 2.0 license](http://www.apache.org/licenses/LICENSE-2.0.html).
