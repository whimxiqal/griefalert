package com.minecraftonline.griefalert.api.profiles;

public class ProfileMalformedException extends IllegalStateException {


  public ProfileMalformedException() {
    super();
  }

  public ProfileMalformedException(String s) {
    super(s);
  }

  public ProfileMalformedException(String message, Throwable cause) {
    super(message, cause);
  }

  public ProfileMalformedException(Throwable cause) {
    super(cause);
  }
}
