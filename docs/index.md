EasyPipe
======================
Data streaming framework, built on top of **Spring Boot**. Concentrate on business aspect 
of your data, let **EasyPipe** do the rest. 

## How it Works


## Features
- implement all steps of your pipe separately;
- combine steps into EasyPipe;
- get control on your pipe out of the box;
- get main metrics of your pipe out of the box;

## Installation
Add EasyPipe dependency to you Spring Boot application.
```xml
<dependency>
    <groupId>com.altumpoint.easypipe</groupId>
    <artifactId>core</artifactId>
    <version>0.1.1</version>
</dependency>
```

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
