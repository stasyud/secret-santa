# secret-santa

Internal project intended to generate list of assigned users for input list of users.
Written on groovy + spark.

To run project execute following commands from the root of the project:
```
gradle clean
gradle compileWithDependencies
```

Then you can run the result jar file by running following command
```
java -jar ./build/libs/secret-santa-all-1.0.jar
```

After successful launch, application will be available under URL http://localhost:8090/

Requirements: application requires java 8 (jre 1.8 or jdk 1.8) installed on your machine.
