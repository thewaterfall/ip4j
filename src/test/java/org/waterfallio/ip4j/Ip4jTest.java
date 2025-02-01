package org.waterfallio.ip4j;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class Ip4jTest {
  @Mock
  private HttpServletRequest request;

  @Test
  void getIp_ReturnsNullWhenNoIpFound() {
    when(request.getHeader(any())).thenReturn(null);
    when(request.getRemoteAddr()).thenReturn(null);

    String clientIp = Ip4j.getIp(request);

    assertEquals(null, clientIp);
  }

  @Test
  void getIp_ReturnsNullWhenNoValidIpFound() {
    when(request.getHeader(any())).thenReturn("invalid-ip");
    when(request.getRemoteAddr()).thenReturn("invalid-ip");

    String clientIp = Ip4j.getIp(request);

    assertEquals(null, clientIp);
  }


  @Test
  void getIp_ReturnsClientIpFromXClientIp() {
    when(request.getHeader("X-Client-IP")).thenReturn("203.0.113.1");

    String clientIp = Ip4j.getIp(request);

    assertEquals("203.0.113.1", clientIp);
  }

  @Test
  void getIp_ReturnsRemoteAddrWhenNoHeadersPresent() {
    when(request.getHeader(any())).thenReturn(null);
    when(request.getRemoteAddr()).thenReturn("192.168.1.1");

    String clientIp = Ip4j.getIp(request);

    assertEquals("192.168.1.1", clientIp);
  }

  @Test
  void getIp_ReturnsFirstValidIpFromCustomHeaders() {
    when(request.getHeader("X-Ip")).thenReturn(null);
    when(request.getHeader("My-Ip")).thenReturn("192.168.1.1");

    String clientIp = Ip4j.getIp(request, "X-Ip", "My-Ip", "IPs");

    assertEquals("192.168.1.1", clientIp);
  }
} 
