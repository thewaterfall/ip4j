package org.waterfallio.ip4j;


import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static org.waterfallio.ip4j.Extractors.*;

public class Ip4j {
  private static final List<Function<HttpServletRequest, String>> IP_EXTRACTORS = Arrays.asList(
      request -> extractIp("X-Client-IP", request),
      request -> extractFirstIp("X-Forwarded-For", request),
      request -> extractIp("CF-Connecting-IP", request), // Cloudflare
      request -> extractIp("Fastly-Client-Ip", request), // Fastly CDN
      request -> extractIp("True-Client-Ip", request), // Akamai/Cloudflare
      request -> extractIp("X-Real-IP", request), // Nginx/FastCGI
      request -> extractIp("X-Cluster-Client-IP", request), // Rackspace LB, Riverbed Stingray
      request -> extractFirstIp("X-Forwarded", request),
      request -> extractFirstIp("Forwarded-For", request),
      request -> extractForwardedIp("Forwarded", request),
      request -> extractIp("appengine-user-ip", request), // Google App Engine
      request -> extractIp("Cf-Pseudo-IPv4", request), // Cloudflare fallback
      request -> extractRemoteAddr(request)
  );

  /**
   * Extracts the client's IP address from the given HttpServletRequest and returns the first non-null and non-empty IP
   * address found. Return null if not found.
   *
   * <p>Sources of ip address:</p>
   * <ul>
   *   <li>X-Client-IP header</li>
   *   <li>X-Forwarded-For header (if multiple comma-separated IPs found - first one is returned)</li>
   *   <li>CF-Connecting-IP header</li>
   *   <li>Fastly-Client-Ip header</li>
   *   <li>True-Client-Ip header</li>
   *   <li>X-Real-IP header</li>
   *   <li>X-Cluster-Client-IP header</li>
   *   <li>X-Forwarded header (if multiple comma-separated IPs found - first one is returned)</li>
   *   <li>Forwarded-For header (if multiple comma-separated IPs found - first one is returned)</li>
   *   <li>Forwarded header (first "for" directive is used, if multiple comma-separated IPs found -
   *   first one is returned)</li>
   *   <li>appengine-user-ip header</li>
   *   <li>Cf-Pseudo-IPv4 header</li>
   *   <li>Remote address from the request</li>
   * </ul>
   *
   * @param request the HttpServletRequest from which to extract the IP address
   * @return the extracted IP address as a String, or null if no valid IP is found
   */
  public static String getIp(HttpServletRequest request) {
    for (Function<HttpServletRequest, String> headerProcessor : IP_EXTRACTORS) {
      String ip = headerProcessor.apply(request);

      if (ip != null && !ip.isEmpty()) {
        return ip;
      }
    }

    return null;
  }
}
