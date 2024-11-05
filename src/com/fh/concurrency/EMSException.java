package com.fh.concurrency;

//Base exception class for EMS
public class EMSException extends Exception {
public EMSException(String message) {
    super(message);
}

public EMSException(String message, Throwable cause) {
    super(message, cause);
}
}

//File-related exceptions
class EMSFileNotFoundException extends EMSException {
public EMSFileNotFoundException(String message) {
    super(message);
}

public EMSFileNotFoundException(String message, Throwable cause) {
    super(message, cause);
}
}

class EMSFileReadException extends EMSException {
public EMSFileReadException(String message, Throwable cause) {
    super(message, cause);
}
}

//Regex or pattern-related exceptions
class EMSInvalidRegexException extends EMSException {
public EMSInvalidRegexException(String message, Throwable cause) {
    super(message, cause);
}
}

//Resource-related exceptions (for resource management issues)
class EMSResourceException extends EMSException {
public EMSResourceException(String message, Throwable cause) {
    super(message, cause);
}
}

