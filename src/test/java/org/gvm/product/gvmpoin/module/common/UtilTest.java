package org.gvm.product.gvmpoin.module.common;

import java.util.Arrays;
import org.gvm.product.gvmpoin.util.HashUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({HashUtil.class})
public class UtilTest {

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    PowerMockito.mockStatic(HashUtil.class);
  }

  @Test
  public void testGenerateNewPin() {
    String expectedPin = Arrays.toString(new char[6]);
    Mockito.when(HashUtil.generateNewPassword()).thenReturn(expectedPin);

    String actualPin = HashUtil.generateNewPassword();
    Assert.assertTrue(expectedPin.length() == actualPin.length());
  }

  @Test
  public void testGenerateNewPinIsInteger() {
    String expectedPin = "987654";
    Mockito.when(HashUtil.generateNewPassword()).thenReturn(expectedPin);

    Integer actualPin = Integer.parseInt(HashUtil.generateNewPassword());
    Assert.assertTrue(actualPin.getClass() == Integer.class);
  }

  @Test(expected = NumberFormatException.class)
  public void testGenerateNewPinWhenValueIsNotInteger() {
    String expectedPin = "987654a";
    Mockito.when(HashUtil.generateNewPassword()).thenReturn(expectedPin);

    Integer.parseInt(HashUtil.generateNewPassword());
  }

}
