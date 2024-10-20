package edu.school21.tanks.app;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

public class PortValidator implements IParameterValidator {

    private static final int MIN_PORT_VALUE = 0;
    private static final int MIN_PUBLIC_PORT_VALUE = 1024;
    private static final int MAX_PORT_VALUE = 65535;

    @Override
    public void validate(String name, String value) throws ParameterException {
        int port = Integer.parseInt(value);
        if ((port < MIN_PORT_VALUE) || (port > MAX_PORT_VALUE)) {
            throw new ParameterException(
                "Parameter " + name + " is out of ports range:[" +
                MIN_PORT_VALUE + " ; " + MAX_PORT_VALUE + "]\n"
                + "Value = " + value);
        }
        if (port < MIN_PUBLIC_PORT_VALUE) {
            throw new ParameterException("Port " + value + " is system port.\n"
                                         + "Try a value greater than " +
                                         (MIN_PUBLIC_PORT_VALUE - 1) + ".\n");
        }
    }
}
