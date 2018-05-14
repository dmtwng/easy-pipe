EasyPipe
======================
Yet another data streaming engine, built on top of **Spring Boot**.

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
    <version>0.1.0</version>
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

Licensing
---------
**PLACE A COPY OF THE [APACHE LICENSE](http://emccode.github.io/sampledocs/LICENSE "LICENSE") FILE IN YOUR PROJECT**

Licensed under the Apache License, Version 2.0 (the â€œLicenseâ€); you may not use this file except in compliance with the License. You may obtain a copy of the License at <http://www.apache.org/licenses/LICENSE-2.0>

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an â€œAS ISâ€ BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
