package com.servicerca;
import com.servicerca.service.CryptoService;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
class CryptoServiceTest { @Test void encryptsNonDeterministically(){var c=new CryptoService("0123456789abcdef0123456789abcdef");var a=c.encrypt("30111222");var b=c.encrypt("30111222");assertThat(a).isNotEqualTo("30111222").isNotEqualTo(b);}}
