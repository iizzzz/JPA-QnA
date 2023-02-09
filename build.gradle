plugins {
    id 'java'
    id 'org.springframework.boot' version '2.7.7'
    id 'io.spring.dependency-management' version '1.0.15.RELEASE'
    id 'org.asciidoctor.convert' version '1.5.8'
}

group = 'com'
version = '0.0.1-SNAPSHOT'
sourceCompatibility = '11'

configurations {
    compileOnly {
        extendsFrom annotationProcessor
    }
}

repositories {
    mavenCentral()
}

ext {
    set('snippetsDir', file("build/generated-snippets"))
}

dependencies {
    // Spring Web
    implementation 'org.springframework.boot:spring-boot-starter-web'
    // Lombok
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'
    // Spring Test
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    // Rest Docs
    testImplementation 'org.springframework.restdocs:spring-restdocs-mockmvc'
    // H2 DataBase
    runtimeOnly 'com.h2database:h2'
    // Spring Data JPA
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    // Validation
    implementation 'org.springframework.boot:spring-boot-starter-validation'
    // Mapstruct
    implementation 'org.mapstruct:mapstruct:1.5.1.Final'
    annotationProcessor 'org.mapstruct:mapstruct-processor:1.5.1.Final'
    // Spring Security
//    implementation 'org.springframework.boot:spring-boot-starter-security'
//    testImplementation 'org.springframework.security:spring-security-test'
    //JJWT
    implementation 'io.jsonwebtoken:jjwt-api:0.11.5'
    runtimeOnly 'io.jsonwebtoken:jjwt-impl:0.11.5'
    runtimeOnly 'io.jsonwebtoken:jjwt-jackson:0.11.5'
    // Gson
    implementation group: 'com.google.code.gson', name: 'gson', version: '2.8.9'
}

tasks.named('test') {
    outputs.dir snippetsDir
    useJUnitPlatform()
}

tasks.named('asciidoctor') {
    inputs.dir snippetsDir
    dependsOn test
}
