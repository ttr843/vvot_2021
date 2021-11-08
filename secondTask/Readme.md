# SecondTask


## Getting Started


#### 1. Set up credentials file

Win: username/.aws

Unix: $HOME

#### 2. Create Cloud-function

##### settings :

1. Lang: Java
   
2. RAM : 1024MB
   
3. connection timeout : 120 sec.

#### 3. Set up credentials in FaceService.java (accessKey, secretKey)

#### 4. Run this command to package Jar file

```bash
./mvn clean compile package  
```

#### 5. Put jar file into function

#### 6. Create object storage trigger with function

#### 7. Create queue 

##### settings : 

1. name : faceQueue

#### 8.Put some jpeg file into bucket




