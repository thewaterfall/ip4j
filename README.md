# Ip4j

[![](https://jitpack.io/v/thewaterfall/ip4j.svg)](https://jitpack.io/#thewaterfall/ip4j)

Ip4j is a small, simple and zero dependencies Java 11+ library for retrieving a request IP address from an
`HttpServletRequest`. It supports various headers commonly used in web applications to forward client IP addresses.

## Features

Extracts IP address from multiple sources by the following order (higher to lower priority):
- `Custom user headers` (optional user-provided headers)
- `X-Client-IP header`
- `X-Forwarded-For header` (if multiple comma-separated IPs found - first one is returned)
- `CF-Connecting-IP header` (Cloudflare)
- `Fastly-Client-Ip header` (Fastly CDN)
- `True-Client-Ip header` (Akamai, Cloudflare)
- `X-Real-IP header` (Nginx, FastCGI)
- `X-Cluster-Client-IP header` (Rackspace, Riverbed Stingray)
- `X-Forwarded header` (if multiple comma-separated IPs found - first one is returned)
- `Forwarded-For header` (if multiple comma-separated IPs found - first one is returned)
- `Forwarded header` (first "for" directive is used, if multiple comma-separated IPs found - first one is returned)
- `appengine-user-ip header` (Google App Engine)
- `Cf-Pseudo-IPv4 header` (Cloudflare fallback)
- `request.getRemoteAddr()`

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
String ip = Ip4j.getIp(request); 

// With custom headers
String ip = Ip4j.getIp(request, "X-Ip");
String ip = Ip4j.getIp(request, "X-Ip", "My-Ip", "IPs"); 
```
