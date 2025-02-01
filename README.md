# Ip4J

Ip4J is a small, simple and zero dependencies library for retrieving a request IP address from an
`HttpServletRequest`. It supports various headers commonly used in web applications to forward client IP addresses.

## Features

- Extracts IP addresses from multiple sources:
  - `X-Client-IP`: The IP address of the client as identified by the application.
  - `X-Forwarded-For`: A standard header used to identify the originating IP address of a client connecting through a proxy.
  - `CF-Connecting-IP`: The IP address of the client connecting to a Cloudflare server.
  - `Fastly-Client-Ip`: The IP address of the client connecting to a Fastly CDN.
  - `True-Client-Ip`: The original IP address of the client when using Akamai or Cloudflare.
  - `X-Real-IP`: The IP address of the client as forwarded by Nginx or FastCGI.
  - `X-Cluster-Client-IP`: The IP address of the client connecting through a Rackspace load balancer or Riverbed Stingray.
  - `X-Forwarded`: A general header that can contain multiple directives, including the client's IP address.
  - `Forwarded-For`: A header that can contain multiple comma-separated IPs, returning the first valid one.
  - `Forwarded`: A general header that can contain multiple directives, including the client's IP address.
  - `appengine-user-ip`: The IP address of the user connecting to Google App Engine.
  - `Cf-Pseudo-IPv4`: A fallback header used by Cloudflare to provide a pseudo IPv4 address.
  - `request.getRemoteAddr()`: The IP address from the request itself if no other headers are present.

## Installation
Ip4j can be easily installed using JitPack, see Gradle and Maven examples below.

### Gradle
Add the following to your build.gradle file:

```
repositories {
    mavenCentral()
    maven { url "https://jitpack.io" }
}

dependencies {
    implementation 'com.github.thewaterfall:ip4j:1.0.0'
}
```

### Maven
Add the following to your pom.xml file:

```
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>com.github.thewaterfall</groupId>
        <artifactId>ip4j</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>
```

## Usage 

```
Ip4j.getIp(request); 
```
